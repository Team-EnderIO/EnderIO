package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class VoidSeedItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER =
        (stack, v) -> new StrictFluidHandlerItemStack(EIODataComponents.ITEM_FLUID_CONTENT, stack, 3400, EIOTags.Fluids.EXPERIENCE); //0-10 levels

    public VoidSeedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null && capability.getFluidInTank(0).getAmount() < ExperienceUtil.EXP_TO_FLUID) {
            return InteractionResultHolder.fail(stack);
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        IFluidHandlerItem capability = pStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (pLivingEntity instanceof Player player && capability != null && capability.getFluidInTank(0).getAmount() > ExperienceUtil.EXP_TO_FLUID) {
            FluidStack result = capability.drain((capability.getFluidInTank(0).getAmount() / ExperienceUtil.EXP_TO_FLUID) * ExperienceUtil.EXP_TO_FLUID, IFluidHandler.FluidAction.EXECUTE);
            player.giveExperiencePoints(result.getAmount() / ExperienceUtil.EXP_TO_FLUID);
        }
        return pStack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
    }

    @Override //TODO nice color?
    public int getBarColor(ItemStack stack) {
        return 0xcfff18;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            return Math.round(fluidHandler.getFluidInTank(0).getAmount() * 13f / fluidHandler.getTankCapacity(0));
        }

        return 0;
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        var fluidHandler = pStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            return fluidHandler.getFluidInTank(0).getAmount() == fluidHandler.getTankCapacity(0);
        }
        return super.isFoil(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, context, components, flag);

        var tankCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (tankCap != null) {
            Component postFix = tankCap.getFluidInTank(0).isEmpty() ? EIOFluids.XP_JUICE.get().getDescription() : tankCap.getFluidInTank(0).getHoverName();
            components.add(Component.literal(tankCap.getFluidInTank(0).getAmount() + " / " + tankCap.getTankCapacity(0) + " ").append(postFix));
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_344979_) {
        return (int) (1.6F * 20.0F); //default food time
    }

    @SubscribeEvent
    static void collectEXP(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        if (!(stack.getItem() instanceof VoidSeedItem)) {
            stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        }

        if (stack.getItem() instanceof VoidSeedItem) {
            IFluidHandler cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (cap != null && event.getOrb().getValue() > 0 && cap.getFluidInTank(0).getAmount() < cap.getTankCapacity(0)) {
                int exp = event.getOrb().getValue() * ExperienceUtil.EXP_TO_FLUID;
                int amount = cap.fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), exp), IFluidHandler.FluidAction.EXECUTE);
                event.getOrb().value = exp - amount;
            }
        }
    }
}

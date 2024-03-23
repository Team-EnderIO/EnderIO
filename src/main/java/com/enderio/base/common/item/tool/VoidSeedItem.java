package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.attachment.IStrictItemFluidHandlerConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidSeedItem extends Item implements IStrictItemFluidHandlerConfig {

    public static ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER =
        (stack, v) -> stack.getData(EIOAttachments.ITEM_STRICT_FLUID);

    public VoidSeedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability != null && capability.getFluidInTank(0).getAmount() > ExperienceUtil.EXP_TO_FLUID) {
            FluidStack result = capability.drain((capability.getFluidInTank(0).getAmount() / ExperienceUtil.EXP_TO_FLUID) * ExperienceUtil.EXP_TO_FLUID, IFluidHandler.FluidAction.EXECUTE);
            pPlayer.giveExperiencePoints(result.getAmount() / ExperienceUtil.EXP_TO_FLUID);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
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

    @Override //TODO config
    public int getFluidCapacity() {
        return 3400; //0-10 levels
    }

    @Override
    public Predicate<Fluid> getFluidFilter() {
        return f -> f.is(EIOTags.Fluids.EXPERIENCE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);

        var tankCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (tankCap != null) {
            Component postFix = tankCap.getFluidInTank(0).isEmpty() ? EIOFluids.XP_JUICE.get().getDescription() : tankCap.getFluidInTank(0).getDisplayName();
            components.add(Component.literal(tankCap.getFluidInTank(0).getAmount() + " / " + tankCap.getTankCapacity(0) + " ").append(postFix));
        }
    }

    @SubscribeEvent
    static void collectEXP(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(InteractionHand.OFF_HAND);
        IFluidHandler cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (stack.getItem() instanceof VoidSeedItem && cap != null && event.getOrb().getValue() > 0) {
            if (cap.getFluidInTank(0).getAmount() < cap.getTankCapacity(0)) {
                int exp = event.getOrb().getValue() * ExperienceUtil.EXP_TO_FLUID;
                int amount = cap.fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), exp), IFluidHandler.FluidAction.EXECUTE);
                event.getOrb().value = exp - amount;
            }
        }
    }
}

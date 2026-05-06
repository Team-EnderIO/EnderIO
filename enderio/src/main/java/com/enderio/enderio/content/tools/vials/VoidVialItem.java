package com.enderio.enderio.content.tools.vials;

import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.tools.ToolsLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.google.common.primitives.Ints;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

@EventBusSubscriber
public class VoidVialItem extends Item implements ICustomCreativeTabEntries {

    public static final ResourceLocation FILLED_MODEL_PROPERTY = EnderIO.rl("void_vial_filled");

    public static final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER = (stack, v) -> new StrictFluidHandlerItemStack(
        () -> EIODataComponents.ITEM_FLUID_CONTENT, stack, Ints.saturatedCast(ExperienceUtil.getFluidFromLevel(10)), Tags.Fluids.EXPERIENCE);

    public VoidVialItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        var filledStack = new ItemStack(this);
        var fluidHandler = filledStack.getCapability(Capabilities.FluidHandler.ITEM);
        fluidHandler.fill(new FluidStack(EIOFluids.XP_JUICE.source(), Integer.MAX_VALUE), IFluidHandler.FluidAction.EXECUTE);
        output.accept(filledStack);
    }

    public static boolean isFilled(ItemStack stack) {
        var fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        return fluidHandler != null && !fluidHandler.getFluidInTank(0).isEmpty();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null || capability.getFluidInTank(0).getAmount() < ExperienceUtil.EXP_TO_FLUID) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (!(livingEntity instanceof Player player) || capability == null) {
            return stack;
        }

        int amount = capability.getFluidInTank(0).getAmount();
        if (amount < ExperienceUtil.EXP_TO_FLUID) {
            return stack;
        }

        FluidStack result = capability.drain((amount / ExperienceUtil.EXP_TO_FLUID) * ExperienceUtil.EXP_TO_FLUID, IFluidHandler.FluidAction.EXECUTE);
        player.giveExperiencePoints(result.getAmount() / ExperienceUtil.EXP_TO_FLUID);
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isFilled(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // Color from XP Juice texture
        return 0xFF66FF03;
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
    public boolean isFoil(ItemStack stack) {
        return isFilled(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(ToolsLang.VOID_VIAL_HINT);

        var tankCap = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (tankCap != null) {
            FluidStack storedFluid = tankCap.getFluidInTank(0);
            int amount = tankCap.getFluidInTank(0).getAmount();
            int capacity = tankCap.getTankCapacity(0);

            if (amount > 0) {
                tooltipComponents.add(TooltipUtil.styledWithArgs(EIOCommonLang.FLUID_TANK_TOOLTIP, amount, capacity, storedFluid.getHoverName()));

                var xp = ExperienceUtil.getLevelFromFluidWithLeftover(amount);
                tooltipComponents.add(TooltipUtil.styledWithArgs(ToolsLang.VOID_VIAL_STORED_EXPERIENCE, xp.level(), xp.experience()));
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return (int) (1.6F * 20.0F); // default food time
    }

    @SubscribeEvent
    public static void collectEXP(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();

        ItemStack stack = player.getOffhandItem();
        if (!(stack.getItem() instanceof VoidVialItem)) {
            stack = player.getMainHandItem();
        }

        if (stack.getItem() instanceof VoidVialItem) {
            IFluidHandler cap = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if (cap != null && event.getOrb().getValue() > 0 && cap.getFluidInTank(0).getAmount() < cap.getTankCapacity(0)) {
                int orbFluidVolume = event.getOrb().getValue() * ExperienceUtil.EXP_TO_FLUID;

                int simulatedFilled = cap.fill(new FluidStack(EIOFluids.XP_JUICE.source(), orbFluidVolume), IFluidHandler.FluidAction.SIMULATE);

                // Ensure we're aligning to the EXP_TO_FLUID boundary
                int amountToFill = simulatedFilled - (simulatedFilled % ExperienceUtil.EXP_TO_FLUID);
                if (amountToFill <= 0) {
                    return;
                }

                cap.fill(new FluidStack(EIOFluids.XP_JUICE.source(), amountToFill), IFluidHandler.FluidAction.EXECUTE);

                int newOrbVolume = (orbFluidVolume - amountToFill);
                event.getOrb().value = newOrbVolume / ExperienceUtil.EXP_TO_FLUID;
            }
        }
    }
}

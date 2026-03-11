package com.enderio.enderio.content.tools;

import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class LevitationStaffItem extends PoweredToggledItem {

    public static final ICapabilityProvider FLUID_HANDLER_PROVIDER = (stack,
            v) -> new StrictFluidHandlerItemStack(() -> EIODataComponents.ITEM_FLUID_CONTENT, stack, 1000,
                    EIOTags.Fluids.STAFF_OF_LEVITY_FUEL);

    public LevitationStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    protected int getEnergyUse() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_ENERGY_USE.get();
    }

    @Override
    public int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_MAX_ENERGY.get();
    }

    @Override
    protected boolean hasCharge(ItemStack stack) {
        if (!super.hasCharge(stack)) {
            return false;
        }

        var fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (fluidHandler != null) {
            // TODO: Config for consumption amount
            return !fluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE).isEmpty();
        }

        return false;
    }

    @Override
    protected void consumeCharge(ItemStack stack) {
        super.consumeCharge(stack);

        var fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (fluidHandler != null) {
            // TODO: Config for consumption amount
            fluidHandler.drain(1, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected void setFullCharge(ItemStack stack) {
        super.setFullCharge(stack);

        var fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (fluidHandler != null) {
            if (fluidHandler instanceof StrictFluidHandlerItemStack strictFluidHandlerItemStack) {
                strictFluidHandlerItemStack.setFluid(
                        new FluidStack(EIOFluids.VAPOR_OF_LEVITY.source(), fluidHandler.getTankCapacity(0)));
            }
        }
    }

    @Override
    protected void onTickWhenActive(Player player, ItemStack stack, Level level, Entity entity, int slotId,
            boolean isSelected) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 1));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}

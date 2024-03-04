package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.core.common.attachment.IStrictItemFluidHandlerConfig;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.function.Predicate;

public class LevitationStaffItem extends PoweredToggledItem implements IStrictItemFluidHandlerConfig {

    public static final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER
        = (stack, v) -> stack.getData(EIOAttachments.ITEM_STRICT_FLUID);

    public LevitationStaffItem(Properties pProperties) {
        super(pProperties);
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
    protected boolean hasCharge(ItemStack pStack) {
        if (!super.hasCharge(pStack)) {
            return false;
        }

        var fluidHandler = pStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            //TODO: Config for consumption amount
            return !fluidHandler.drain(1, IFluidHandler.FluidAction.SIMULATE).isEmpty();
        }

        return false;
    }

    @Override
    protected void consumeCharge(ItemStack pStack) {
        super.consumeCharge(pStack);

        var fluidHandler = pStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            //TODO: Config for consumption amount
            fluidHandler.drain(1, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    protected void setFullCharge(ItemStack pStack) {
        super.setFullCharge(pStack);

        var fluidHandler = pStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            if (fluidHandler instanceof StrictFluidHandlerItemStack strictFluidHandlerItemStack) {
                strictFluidHandlerItemStack.setFluid(new FluidStack(EIOFluids.VAPOR_OF_LEVITY.getSource(), fluidHandler.getTankCapacity(0)));
            }
        }
    }

    @Override
    protected void onTickWhenActive(Player player, ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 1));
    }

    @Override
    public int getFluidCapacity() {
        // TODO: Config
        return 1000;
    }

    @Override
    public Predicate<Fluid> getFluidFilter() {
        return f -> f.is(EIOTags.Fluids.STAFF_OF_LEVITY_FUEL);
    }
}

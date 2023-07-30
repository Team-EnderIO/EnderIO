package com.enderio.base.common.item.tool;

import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.AcceptingFluidItemHandler;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class LevitationStaffItem extends PoweredToggledItem {
    public LevitationStaffItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected int getEnergyUse() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_ENERGY_USE.get();
    }

    @Override
    protected int getMaxEnergy() {
        return BaseConfig.COMMON.ITEMS.LEVITATION_STAFF_MAX_ENERGY.get();
    }

    @Override
    protected boolean hasCharge(ItemStack pStack) {
        //TODO: Config for consumption amount
        return getTankCap(pStack).map(handler -> !handler.drain(1, IFluidHandler.FluidAction.SIMULATE).isEmpty()).orElse(false) && super.hasCharge(pStack);
    }

    @Override
    protected void consumeCharge(ItemStack pStack) {
        super.consumeCharge(pStack);
        // TODO: Consumption config
        getTankCap(pStack).ifPresent(handler -> handler.drain(1, IFluidHandler.FluidAction.EXECUTE));
    }

    @Override
    protected void setFullCharge(ItemStack pStack) {
        super.setFullCharge(pStack);
        getTankCap(pStack).ifPresent(handler -> {
            if (handler instanceof AcceptingFluidItemHandler fluidHandler) {
                fluidHandler.setFluid(new FluidStack(EIOFluids.VAPOR_OF_LEVITY.get(), handler.getTankCapacity(0)));
            }
        });
    }

    @Override
    protected void onTickWhenActive(Player player, ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 1));
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(ForgeCapabilities.FLUID_HANDLER_ITEM,
            new AcceptingFluidItemHandler(stack, 1000, EIOTags.Fluids.STAFF_OF_LEVITY_FUEL).getCapability(
                ForgeCapabilities.FLUID_HANDLER_ITEM));
        return super.initCapabilities(stack, nbt, provider);
    }

    private Optional<IFluidHandlerItem> getTankCap(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
    }
}

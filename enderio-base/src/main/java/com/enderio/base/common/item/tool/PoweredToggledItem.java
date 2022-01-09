package com.enderio.base.common.item.tool;

import com.enderio.base.client.renderer.item.ItemBarRenderer;
import com.enderio.base.client.tooltip.IAdvancedTooltipProvider;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.capability.IMultiCapabilityItem;
import com.enderio.base.common.capability.MultiCapabilityProvider;
import com.enderio.base.common.capability.toggled.Toggled;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.EnergyUtil;
import com.enderio.base.common.util.TooltipUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class PoweredToggledItem extends Item implements IMultiCapabilityItem, IAdvancedTooltipProvider {

    public PoweredToggledItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    protected abstract void onTickWhenActive(Player player, @Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId,
        boolean pIsSelected);

    protected abstract int getEnergyUse();

    protected abstract int getMaxEnergy();

    protected void enable(ItemStack stack) {
        Toggled.setEnabled(stack, true);
    }

    protected void disable(ItemStack stack) {
        Toggled.setEnabled(stack, false);
    }

    protected boolean hasCharge(ItemStack pStack) {
        return EnergyUtil.extractEnergy(pStack, getEnergyUse(), true) > 0;
    }

    protected void consumeCharge(ItemStack pStack) {
        EnergyUtil.extractEnergy(pStack, getEnergyUse(), false);
    }

    protected void setFullCharge(ItemStack pStack) {
        EnergyUtil.setFull(pStack);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return Toggled.isEnabled(pStack);
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab pCategory, @Nonnull NonNullList<ItemStack> pItems) {
        if (allowdedIn(pCategory)) {
            ItemStack is = new ItemStack(this);
            pItems.add(is.copy());

            setFullCharge(is);
            pItems.add(is);
        }
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        if (pPlayer.isCrouching()) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            if (Toggled.isEnabled(stack)) {
                disable(stack);
            } else if (hasCharge(stack)) {
                enable(stack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof Player player) {
            if (Toggled.isEnabled(pStack)) {
                if (hasCharge(pStack)) {
                    consumeCharge(pStack);
                    onTickWhenActive(player, pStack, pLevel, pEntity, pSlotId, pIsSelected);
                } else {
                    disable(pStack);
                }
            }
        }
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.TOGGLED, LazyOptional.of(Toggled::new));
        provider.addSerialized("Energy", CapabilityEnergy.ENERGY, LazyOptional.of(() -> new EnergyStorage(getMaxEnergy())));
        return provider;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, true);
        }
        return oldStack.getItem() != newStack.getItem() || Toggled.isEnabled(oldStack) != Toggled.isEnabled(newStack);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return pStack
            .getCapability(CapabilityEnergy.ENERGY)
            .map(energyStorage -> Math.round(energyStorage.getEnergyStored() * 13.0F / energyStorage.getMaxEnergyStored()))
            .orElse(0);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return ItemBarRenderer.ENERGY_BAR_RGB;
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @org.jetbrains.annotations.Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d",EnergyUtil.getEnergyStored(itemStack)) + "/" +  String.format("%,d",EnergyUtil.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOLang.ENERGY_AMOUNT, energy));
    }
}

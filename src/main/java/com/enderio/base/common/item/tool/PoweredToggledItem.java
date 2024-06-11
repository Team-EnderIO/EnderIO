package com.enderio.base.common.item.tool;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public abstract class PoweredToggledItem extends Item implements AdvancedTooltipProvider, CreativeTabVariants {

    public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER =
        (stack, v) -> new ComponentEnergyStorage(stack, EIODataComponents.ENERGY.get(), ((PoweredToggledItem)stack.getItem()).getMaxEnergy());

    public PoweredToggledItem(Properties pProperties) {
        super(pProperties
            .stacksTo(1)
            .component(EIODataComponents.TOGGLED, false));
    }

    protected abstract void onTickWhenActive(Player player, ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId,
        boolean pIsSelected);

    protected abstract int getMaxEnergy();

    protected abstract int getEnergyUse();

    protected boolean isEnabled(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(EIODataComponents.TOGGLED));
    }

    protected void enable(ItemStack stack) {
        stack.set(EIODataComponents.TOGGLED, true);
    }

    protected void disable(ItemStack stack) {
        stack.set(EIODataComponents.TOGGLED, false);
    }

    protected boolean hasCharge(ItemStack pStack) {
        return ItemStackEnergy.extractEnergy(pStack, getEnergyUse(), true) > 0;
    }

    protected void consumeCharge(ItemStack pStack) {
        ItemStackEnergy.extractEnergy(pStack, getEnergyUse(), false);
    }

    protected void setFullCharge(ItemStack pStack) {
        ItemStackEnergy.setFull(pStack);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isEnabled(pStack);
    }

    public static ItemStack getCharged(PoweredToggledItem item) {
        ItemStack is = new ItemStack(item);
        item.setFullCharge(is);
        return is;
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);
        modifier.accept(getCharged(this));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer.isCrouching()) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            if (isEnabled(stack)) {
                disable(stack);
            } else if (hasCharge(stack)) {
                enable(stack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof Player player) {
            if (isEnabled(pStack)) {
                if (hasCharge(pStack)) {
                    consumeCharge(pStack);
                    onTickWhenActive(player, pStack, pLevel, pEntity, pSlotId, pIsSelected);
                } else {
                    disable(pStack);
                }
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (slotChanged) {
            return super.shouldCauseReequipAnimation(oldStack, newStack, true);
        }
        return oldStack.getItem() != newStack.getItem() || isEnabled(oldStack) != isEnabled(newStack);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        var energyStorage = pStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage != null) {
            return Math.round(energyStorage.getEnergyStored() * 13.0F / energyStorage.getMaxEnergyStored());
        }

        return 0;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return EnergyBarDecorator.BAR_COLOR;
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @org.jetbrains.annotations.Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/" +  String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOLang.ENERGY_AMOUNT, energy));
    }
}

package com.enderio.base.common.item.tool;

import com.enderio.base.common.capability.EnergyStorageItemStack;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.item.ITabVariants;
import com.enderio.core.common.util.EnergyUtil;
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
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public abstract class PoweredToggledItem extends Item implements IAdvancedTooltipProvider, ITabVariants {

    // TODO: Attachment for this; this should have a custom energy storage that somehow determines the capacity.
    public static final ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER =
        (stack, v) -> new EnergyStorageItemStack(stack, ((PoweredToggledItem)stack.getItem()).getMaxEnergy());

    public PoweredToggledItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    protected abstract void onTickWhenActive(Player player, ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId,
        boolean pIsSelected);

    protected abstract int getEnergyUse();

    protected abstract int getMaxEnergy();

    protected boolean isEnabled(ItemStack stack) {
        return stack.getData(EIOAttachments.TOGGLED);
    }

    protected void enable(ItemStack stack) {
        stack.setData(EIOAttachments.TOGGLED, true);
    }

    protected void disable(ItemStack stack) {
        stack.setData(EIOAttachments.TOGGLED, false);
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
        String energy = String.format("%,d",EnergyUtil.getEnergyStored(itemStack)) + "/" +  String.format("%,d",EnergyUtil.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOLang.ENERGY_AMOUNT, energy));
    }
}

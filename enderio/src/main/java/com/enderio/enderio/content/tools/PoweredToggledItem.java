package com.enderio.enderio.content.tools;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.ComponentEnergyStorage;

import java.util.List;

public abstract class PoweredToggledItem extends Item implements AdvancedTooltipProvider, ICustomCreativeTabEntries {

    public static final ICapabilityProvider ENERGY_STORAGE_PROVIDER =
        (stack, v) -> new ComponentEnergyStorage(stack, EIODataComponents.ENERGY, ((PoweredToggledItem)stack.getItem()).getMaxEnergy());

    public PoweredToggledItem(Properties properties) {
        super(properties
            .stacksTo(1)
            .component(EIODataComponents.TOGGLED, false));
    }

    protected abstract void onTickWhenActive(Player player, ItemStack stack, Level level, Entity entity, int slotId,
        boolean isSelected);

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

    protected boolean hasCharge(ItemStack stack) {
        return ItemStackEnergy.extractEnergy(stack, getEnergyUse(), true) > 0;
    }

    protected void consumeCharge(ItemStack stack) {
        ItemStackEnergy.extractEnergy(stack, getEnergyUse(), false);
    }

    protected void setFullCharge(ItemStack stack) {
        ItemStackEnergy.setFull(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isEnabled(stack);
    }

    public static ItemStack getCharged(PoweredToggledItem item) {
        ItemStack is = new ItemStack(item);
        item.setFullCharge(is);
        return is;
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(getCharged(this));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isCrouching()) {
            ItemStack stack = player.getItemInHand(usedHand);
            if (isEnabled(stack)) {
                disable(stack);
            } else if (hasCharge(stack)) {
                enable(stack);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            if (isEnabled(stack)) {
                if (hasCharge(stack)) {
                    consumeCharge(stack);
                    onTickWhenActive(player, stack, level, entity, slotId, isSelected);
                } else {
                    disable(stack);
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
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energyStorage = stack.getCapability(ForgeCapabilities.ENERGY);
        if (energyStorage != null) {
            return Math.round(energyStorage.getEnergyStored() * 13.0F / energyStorage.getMaxEnergyStored());
        }

        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return EnergyBarDecorator.BAR_COLOR;
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @org.jetbrains.annotations.Nullable Player player, List<Component> tooltips) {
        String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/" +  String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
        tooltips.add(TooltipUtil.styledWithArgs(EIOCommonLang.ENERGY_AMOUNT, energy));
    }
}

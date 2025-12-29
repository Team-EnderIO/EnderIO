package com.enderio.enderio.content.tools;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PoweredToggledItem extends Item implements AdvancedTooltipProvider, ICustomCreativeTabEntries {

    public static final ICapabilityProvider<ItemStack, ItemAccess, EnergyHandler> ENERGY_STORAGE_PROVIDER =
        (stack, itemAccess) -> new ItemAccessEnergyHandler(itemAccess, EIODataComponents.ENERGY, ((PoweredToggledItem)stack.getItem()).getMaxEnergy());

    public PoweredToggledItem(Properties pProperties) {
        super(pProperties
            .stacksTo(1)
            .component(EIODataComponents.TOGGLED, false));
    }

    protected abstract void onTickWhenActive(Player player, ItemStack stack, Level level, @Nullable EquipmentSlot slot);

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
        return ItemStackEnergy.hasEnergy(pStack, getEnergyUse());
    }

    protected void consumeCharge(ItemStack pStack) {
        try (Transaction transaction = Transaction.openRoot()) {
            ItemStackEnergy.extractEnergy(pStack, getEnergyUse(), transaction);
            transaction.commit();
        }
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
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(getCharged(this));
    }

    @Override
    public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
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
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (entity instanceof Player player) {
            if (isEnabled(stack)) {
                if (hasCharge(stack)) {
                    consumeCharge(stack);
                    onTickWhenActive(player, stack, level, slot);
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
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
        if (energyStorage != null) {
            return Math.round(energyStorage.getAmountAsInt() * 13.0F / energyStorage.getCapacityAsInt());
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
        tooltips.add(TooltipUtil.styledWithArgs(EIOCommonLang.ENERGY_AMOUNT, energy));
    }
}

package com.enderio.enderio.content.armory;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.armory.capacitor.CapacitorSocket;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.EquipmentTickHook;
import com.enderio.enderio.api.armory.upgrades.hooks.general.ModifyAttributesHook;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class ArmoryEventHooks {
    // This is the one point every stack's modifiers pass through, whether they come from the attribute_modifiers component (tools) or
    // Item#getDefaultAttributeModifiers (armor).
    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        ItemDarkSteelUpgrades upgrades = stack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null || upgrades.upgrades().isEmpty()) {
            return;
        }

        if (!(stack.getItem() instanceof DarkSteelUpgradableItem upgradableItem)
            || !upgradableItem.availableHookTypes().contains(DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES)) {
            return;
        }

        ModifyAttributesHook.modifyAttributes(upgrades, DarkSteelHelper.getHookContext(stack), event);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        // Hooks run on the server only, anything they change (i.e. mob effects) is synced by vanilla.
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) {
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof DarkSteelUpgradableItem upgradableItem)
                || !upgradableItem.availableHookTypes().contains(DarkSteelUpgradeHookTypes.EQUIPMENT_TICK)) {
                continue;
            }

            // Only tick items in the slot they're intended for, i.e. not armor held in a hand.
            if (entity.getEquipmentSlotForItem(stack) != slot) {
                continue;
            }

            EquipmentTickHook.onEquipmentTick(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), entity, slot);
        }
    }

    /**
     * Bundle-style interaction for socketing and removing a capacitor.
     */
    @SubscribeEvent
    public static void onItemStackedOnOther(ItemStackedOnOtherEvent event) {
        ItemStack target = event.getStackedOnItem();
        // Sockets are per-stack, so a stack of several would duplicate or lose capacitors.
        if (target.getCount() != 1) {
            return;
        }

        CapacitorSocket socket = target.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (socket == null) {
            return;
        }

        Slot slot = event.getSlot();
        Player player = event.getPlayer();
        if (!slot.allowModification(player)) {
            return;
        }

        ItemStack carried = event.getCarriedItem();
        if (event.getClickAction() == ClickAction.PRIMARY) {
            if (!socket.getCapacitor().isEmpty() || !CapacitorSocket.isCapacitor(carried)) {
                return;
            }

            socket.setCapacitor(carried.split(1));
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
        } else {
            ItemStack capacitor = socket.getCapacitor();
            if (!carried.isEmpty() || capacitor.isEmpty()) {
                return;
            }

            event.getCarriedSlotAccess().set(capacitor.copy());
            socket.setCapacitor(ItemStack.EMPTY);
            player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
        }

        slot.setChanged();
        event.setCanceled(true);
    }
}

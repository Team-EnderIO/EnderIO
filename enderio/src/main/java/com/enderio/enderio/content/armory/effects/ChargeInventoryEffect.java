package com.enderio.enderio.content.armory.effects;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.EquipmentTickHook;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Charges items carried by the wearer while the upgraded item is equipped and {@link #condition()} is active.
 * Each interval, {@code energy_per_tick * interval} is shared between the target items in order (armor, main hand, off hand, then the rest of the
 * inventory), so the total generated never exceeds that amount however many items there are.
 * <p>
 * Targets are chosen with {@link #armor()}, {@link #mainHand()}, {@link #offHand()} and {@link #inventory()}, and can be further filtered by
 * {@link #items()}. {@link #inventory()} covers the player's whole inventory, and only applies to players.
 */
public class ChargeInventoryEffect extends DarkSteelUpgradeEffect implements EquipmentTickHook {

    public static final MapCodec<ChargeInventoryEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("energy_per_tick").forGetter(ChargeInventoryEffect::energyPerTick),
            Codec.intRange(1, 1200).optionalFieldOf("interval", 20).forGetter(ChargeInventoryEffect::interval),
            Codec.BOOL.optionalFieldOf("armor", false).forGetter(ChargeInventoryEffect::armor),
            Codec.BOOL.optionalFieldOf("main_hand", false).forGetter(ChargeInventoryEffect::mainHand),
            Codec.BOOL.optionalFieldOf("off_hand", false).forGetter(ChargeInventoryEffect::offHand),
            Codec.BOOL.optionalFieldOf("inventory", false).forGetter(ChargeInventoryEffect::inventory),
            TagKey.codec(Registries.ITEM).optionalFieldOf("items").forGetter(ChargeInventoryEffect::items),
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(ChargeInventoryEffect::condition)
        ).apply(inst, ChargeInventoryEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.EQUIPMENT_TICK);

    private final int energyPerTick;
    private final int interval;
    private final boolean armor;
    private final boolean mainHand;
    private final boolean offHand;
    private final boolean inventory;
    private final Optional<TagKey<Item>> items;
    private final EffectCondition condition;

    public ChargeInventoryEffect(int energyPerTick, int interval, boolean armor, boolean mainHand, boolean offHand, boolean inventory,
        Optional<TagKey<Item>> items, EffectCondition condition) {
        this.energyPerTick = energyPerTick;
        this.interval = interval;
        this.armor = armor;
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.inventory = inventory;
        this.items = items;
        this.condition = condition;
    }

    public int energyPerTick() {
        return energyPerTick;
    }

    public int interval() {
        return interval;
    }

    public boolean armor() {
        return armor;
    }

    public boolean mainHand() {
        return mainHand;
    }

    public boolean offHand() {
        return offHand;
    }

    public boolean inventory() {
        return inventory;
    }

    public Optional<TagKey<Item>> items() {
        return items;
    }

    public EffectCondition condition() {
        return condition;
    }

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return HOOKS;
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    @Override
    public void onEquipmentTick(DarkSteelHookContext context, LivingEntity entity, EquipmentSlot slot) {
        if (entity.tickCount % interval != 0 || !condition.isActive(context.itemStack(), entity.level(), entity)) {
            return;
        }

        int remaining = (int) Math.min(Integer.MAX_VALUE, (long) energyPerTick * interval);
        for (ItemStack target : collectTargets(entity)) {
            if (items.isPresent() && !target.is(items.get())) {
                continue;
            }

            var energyStorage = target.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energyStorage == null || !energyStorage.canReceive()) {
                continue;
            }

            remaining -= energyStorage.receiveEnergy(remaining, false);
            if (remaining <= 0) {
                return;
            }
        }
    }

    private List<ItemStack> collectTargets(LivingEntity entity) {
        // Identity set, as the same stack can be reached through more than one target (e.g. the selected hotbar slot and the main hand).
        Set<ItemStack> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        List<ItemStack> targets = new ArrayList<>();

        if (armor) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                if (equipmentSlot.isArmor()) {
                    addTarget(entity.getItemBySlot(equipmentSlot), seen, targets);
                }
            }
        }

        if (mainHand) {
            addTarget(entity.getMainHandItem(), seen, targets);
        }

        if (offHand) {
            addTarget(entity.getOffhandItem(), seen, targets);
        }

        if (inventory && entity instanceof Player player) {
            Inventory playerInventory = player.getInventory();
            for (int i = 0; i < playerInventory.getContainerSize(); i++) {
                addTarget(playerInventory.getItem(i), seen, targets);
            }
        }

        return targets;
    }

    private static void addTarget(ItemStack stack, Set<ItemStack> seen, List<ItemStack> targets) {
        if (!stack.isEmpty() && seen.add(stack)) {
            targets.add(stack);
        }
    }
}

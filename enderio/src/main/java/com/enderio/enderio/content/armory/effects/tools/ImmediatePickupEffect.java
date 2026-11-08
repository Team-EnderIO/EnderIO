package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCost;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCostInstance;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber
public class ImmediatePickupEffect extends DarkSteelUpgradeEffect {

    // TODO: Need a way to require energy from an effect. Might be nicer than the JSON saying it needs a cap.

    public static final MapCodec<ImmediatePickupEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(EffectCost.CODEC.fieldOf("cost_per_item").forGetter(ImmediatePickupEffect::costPerItem))
        .apply(inst, ImmediatePickupEffect::new));

    private final EffectCost costPerItem;

    public ImmediatePickupEffect(EffectCost costPerItem) {
        this.costPerItem = costPerItem;
    }

    public EffectCost costPerItem() {
        return costPerItem;
    }

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return Collections.emptyList();
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player)) {
            return;
        }

        // The event's tool is a snapshot taken before the block was mined, so costs (i.e. durability) must be taken
        // from the live stack. If the tool broke or was swapped during the break, don't redirect anything.
        ItemStack tool = player.getMainHandItem();
        if (!ItemStack.isSameItem(tool, event.getTool())) {
            return;
        }

        directItems(event.getLevel(), player, tool, event.getDrops());
    }

    @SubscribeEvent
    public static void onEntityDrops(LivingDropsEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack weapon = event.getSource().getWeaponItem();
        if (weapon == null) {
            return;
        }

        directItems(player.serverLevel(), player, weapon, event.getDrops());
    }

    /**
     * Moves drops straight into the player's inventory before they are spawned into the world.
     * Mirrors {@link ItemEntity#playerTouch}, except the pickup delay is ignored as this is the point of the upgrade.
     * Any drops that are fully picked up are removed from {@code drops}; partially picked up drops have their stack shrunk.
     */
    private static void directItems(ServerLevel level, ServerPlayer player, ItemStack tool, Collection<ItemEntity> drops) {
        ItemDarkSteelUpgrades upgrades = tool.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return;
        }

        List<ImmediatePickupEffect> effects = upgrades.getAllEffectsOfType(ImmediatePickupEffect.class);
        if (effects.isEmpty()) {
            return;
        }

        boolean pickedUpAny = false;
        Iterator<ItemEntity> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ItemEntity drop = iterator.next();
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            var result = EventHooks.fireItemPickupPre(drop, player).canPickup();
            if (result.isFalse()) {
                continue;
            }

            // Respect item targeting unless the event forced the pickup.
            if (!result.isTrue() && drop.getTarget() != null && !drop.getTarget().equals(player.getUUID())) {
                continue;
            }

            EffectCostInstance cost = findAffordableCost(level, player, tool, effects);
            if (cost == null) {
                // Nothing is affordable, and no costs are taken during this loop unless an item is picked up.
                // Costs are re-sampled per drop though, so a cheaper sample may still be affordable for later drops.
                continue;
            }

            ItemStack originalCopy = stack.copy();
            if (!player.getInventory().add(stack)) {
                continue;
            }

            int pickedUp = originalCopy.getCount() - stack.getCount();
            if (pickedUp <= 0) {
                continue;
            }

            cost.take(level, player, tool);
            EventHooks.fireItemPickupPost(drop, player, originalCopy);

            player.awardStat(Stats.ITEM_PICKED_UP.get(originalCopy.getItem()), pickedUp);
            player.onItemPickup(drop);
            pickedUpAny = true;

            if (stack.isEmpty()) {
                iterator.remove();
            }

            // The cost may have broken the tool, in which case we're done.
            if (tool.isEmpty()) {
                break;
            }
        }

        // The entities never reach the client, so the client won't play the usual pickup sound. Play it once from here instead.
        if (pickedUpAny) {
            var random = player.getRandom();
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS,
                0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
    }

    // Find the first affordable effect cost, if any.
    @Nullable
    private static EffectCostInstance findAffordableCost(ServerLevel level, ServerPlayer player, ItemStack tool,
        List<ImmediatePickupEffect> effects) {
        for (ImmediatePickupEffect effect : effects) {
            EffectCostInstance cost = effect.costPerItem().createInstance(level, player, tool);
            if (cost.canAfford(level, player, tool)) {
                return cost;
            }
        }

        return null;
    }
}

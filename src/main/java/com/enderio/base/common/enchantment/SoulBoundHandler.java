package com.enderio.base.common.enchantment;

import com.enderio.base.common.init.EIOEnchantments;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber
public class SoulBoundHandler {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long RESTORE_CHECK_INTERVAL_MS = 1000;
    private static final int SYNC_STOP_RESTORE_DELAY_TICKS = 120;
    private static final String SYNC_START_EVENT_CLASS = "net.pawjwp.sync.api.event.PlayerSyncEvents$StartSyncing";
    private static final String SYNC_STOP_EVENT_CLASS = "net.pawjwp.sync.api.event.PlayerSyncEvents$StopSyncing";
    private static final Map<UUID, Integer> syncRestoreReadyTicks = new HashMap<>();
    private static final Set<UUID> syncingPlayers = new HashSet<>();
    private static long lastRestoreCheck = 0;
    private static boolean syncHandlersRegistered = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void deathHandler(LivingDropsEvent event) {
        if (event.getEntity() == null || event.getEntity() instanceof FakePlayer || event.isCanceled()) {
            return;
        }
        if (event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        tryRegisterSyncHandlers();

        List<ItemStack> soulItems = new ArrayList<>();
        Iterator<ItemEntity> iter = event.getDrops().iterator();
        while (iter.hasNext()) {
            ItemEntity ei = iter.next();
            ItemStack item = ei.getItem();
            if (isSoulBound(item)) {
                soulItems.add(item.copy());
                iter.remove();
            }
        }

        if (soulItems.isEmpty()) {
            return;
        }

        LOGGER.info("SoulBound: captured {} items for player {}", soulItems.size(), player.getGameProfile().getName());
        SoulBoundSavedData.get(player.level()).storeItems(player, soulItems);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void reviveHandler(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        Player newPlayer = event.getEntity();
        if (newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }

        if (restorePendingItems(newPlayer, "Clone")) {
            return;
        }

        LOGGER.info("SoulBound: Clone fallback - scanning original inventory");
        copySoulBoundItems(event.getOriginal().getInventory(), newPlayer);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void respawnHandler(PlayerEvent.PlayerRespawnEvent event) {
        if (isSyncing(event.getEntity())) {
            return;
        }
        restorePendingItems(event.getEntity(), "Respawn");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void loginHandler(PlayerEvent.PlayerLoggedInEvent event) {
        restorePendingItems(event.getEntity(), "Login");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void changedDimensionHandler(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (isSyncing(event.getEntity())) {
            return;
        }
        restorePendingItems(event.getEntity(), "ChangedDimension");
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastRestoreCheck < RESTORE_CHECK_INTERVAL_MS) {
            return;
        }
        lastRestoreCheck = now;

        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }

        for (var serverPlayer : server.getPlayerList().getPlayers()) {
            if (isSyncLoaded()) {
                Integer readyTick = syncRestoreReadyTicks.get(serverPlayer.getUUID());
                if (readyTick == null || serverPlayer.tickCount < readyTick) {
                    continue;
                }
                if (!isReadyForRestore(serverPlayer)) {
                    continue;
                }
                restorePendingItems(serverPlayer, "SyncStopDelayed");
                syncRestoreReadyTicks.remove(serverPlayer.getUUID());
                continue;
            }

            restorePendingItems(serverPlayer, "ServerTick");
        }
    }

    private static void syncStartHandler(Event event) {
        if (!SYNC_START_EVENT_CLASS.equals(event.getClass().getName()) || !(event instanceof PlayerEvent playerEvent)) {
            return;
        }

        Player player = playerEvent.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        syncingPlayers.add(player.getUUID());

        List<ItemStack> pending = SoulBoundSavedData.get(player.level()).takeItems(player);
        if (pending == null) {
            return;
        }

        try {
            Method getTargetState = event.getClass().getMethod("getTargetState");
            Object targetState = getTargetState.invoke(event);
            if (targetState == null) {
                SoulBoundSavedData.get(player.level()).storeItems(player, pending);
                return;
            }

            Method getInventory = targetState.getClass().getMethod("getInventory");
            Object targetInventory = getInventory.invoke(targetState);
            if (!(targetInventory instanceof Container container)) {
                SoulBoundSavedData.get(player.level()).storeItems(player, pending);
                LOGGER.warn("SoulBound: Sync target inventory was not a Minecraft container for player {}", player.getGameProfile().getName());
                return;
            }

            List<ItemStack> leftovers = insertItems(container, pending);
            if (!leftovers.isEmpty()) {
                SoulBoundSavedData.get(player.level()).storeItems(player, leftovers);
                LOGGER.warn("SoulBound: SyncStart staged {} items for player {}, {} stacks left for delayed restore",
                    pending.size(), player.getGameProfile().getName(), leftovers.size());
                return;
            }

            LOGGER.info("SoulBound: SyncStart staged {} items in target shell for player {}", pending.size(), player.getGameProfile().getName());
        } catch (ReflectiveOperationException e) {
            SoulBoundSavedData.get(player.level()).storeItems(player, pending);
            LOGGER.warn("SoulBound: failed to stage items in Sync target shell for player {}", player.getGameProfile().getName(), e);
        }
    }

    private static void syncStopHandler(Event event) {
        if (!SYNC_STOP_EVENT_CLASS.equals(event.getClass().getName()) || !(event instanceof PlayerEvent playerEvent)) {
            return;
        }

        Player player = playerEvent.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        syncingPlayers.remove(player.getUUID());
        syncRestoreReadyTicks.put(player.getUUID(), player.tickCount + SYNC_STOP_RESTORE_DELAY_TICKS);
        LOGGER.info("SoulBound: Sync stop detected for player {}, queued pending restore", player.getGameProfile().getName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void tryRegisterSyncHandlers() {
        if (syncHandlersRegistered || !isSyncLoaded()) {
            return;
        }

        try {
            Class<? extends Event> syncStartEvent = Class.forName(SYNC_START_EVENT_CLASS).asSubclass(Event.class);
            Class<? extends Event> syncStopEvent = Class.forName(SYNC_STOP_EVENT_CLASS).asSubclass(Event.class);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, (Class) syncStartEvent,
                (java.util.function.Consumer<Event>) SoulBoundHandler::syncStartHandler);
            MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, (Class) syncStopEvent,
                (java.util.function.Consumer<Event>) SoulBoundHandler::syncStopHandler);
            syncHandlersRegistered = true;
            LOGGER.info("SoulBound: registered Sync restore hooks");
        } catch (ClassNotFoundException ignored) {
            syncHandlersRegistered = true;
            LOGGER.warn("SoulBound: Sync is loaded but sync event classes were not found");
        }
    }

    private static boolean isSyncLoaded() {
        return ModList.get().isLoaded("sync");
    }

    private static boolean restorePendingItems(Player player, String source) {
        if (!isReadyForRestore(player)) {
            return false;
        }

        List<ItemStack> pending = SoulBoundSavedData.get(player.level()).takeItems(player);
        if (pending == null) {
            return false;
        }

        LOGGER.info("SoulBound: {} restored {} items for player {}", source, pending.size(), player.getGameProfile().getName());
        restoreItems(player, pending);
        return true;
    }

    private static boolean isSyncing(Player player) {
        return isSyncLoaded() && syncingPlayers.contains(player.getUUID());
    }

    private static boolean isReadyForRestore(Player player) {
        return !player.level().isClientSide
            && player.isAlive()
            && !player.isDeadOrDying()
            && player.getHealth() > 0.0F
            && player.deathTime == 0;
    }

    private static void restoreItems(Player player, List<ItemStack> items) {
        for (ItemStack stack : items) {
            if (!player.addItem(stack)) {
                player.drop(stack, false, false);
            }
        }
    }

    private static List<ItemStack> insertItems(Container container, List<ItemStack> items) {
        List<ItemStack> leftovers = new ArrayList<>();
        for (ItemStack stack : items) {
            ItemStack remaining = stack.copy();
            mergeIntoExistingStacks(container, remaining);
            moveIntoEmptySlots(container, remaining);
            if (!remaining.isEmpty()) {
                leftovers.add(remaining.copy());
            }
        }
        container.setChanged();
        return leftovers;
    }

    private static void mergeIntoExistingStacks(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            ItemStack existing = container.getItem(slot);
            if (existing.isEmpty() || !ItemStack.isSameItemSameTags(existing, stack)) {
                continue;
            }

            int maxStackSize = Math.min(existing.getMaxStackSize(), container.getMaxStackSize());
            int transferable = Math.min(stack.getCount(), maxStackSize - existing.getCount());
            if (transferable <= 0) {
                continue;
            }

            existing.grow(transferable);
            stack.shrink(transferable);
        }
    }

    private static void moveIntoEmptySlots(Container container, ItemStack stack) {
        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            if (!container.getItem(slot).isEmpty()) {
                continue;
            }

            int transferable = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), container.getMaxStackSize()));
            ItemStack moved = stack.copy();
            moved.setCount(transferable);
            container.setItem(slot, moved);
            stack.shrink(transferable);
        }
    }

    private static void copySoulBoundItems(Inventory inventory, Player newPlayer) {
        for (ItemStack item : inventory.items) {
            if (isSoulBound(item) && !item.isEmpty()) {
                ItemStack stack = item.copy();
                if (!newPlayer.addItem(stack)) {
                    newPlayer.drop(stack, false, false);
                }
            }
        }
        for (ItemStack armor : inventory.armor) {
            if (isSoulBound(armor) && !armor.isEmpty()) {
                ItemStack stack = armor.copy();
                if (!newPlayer.addItem(stack)) {
                    newPlayer.drop(stack, false, false);
                }
            }
        }
        for (ItemStack offhand : inventory.offhand) {
            if (isSoulBound(offhand) && !offhand.isEmpty()) {
                ItemStack stack = offhand.copy();
                if (!newPlayer.addItem(stack)) {
                    newPlayer.drop(stack, false, false);
                }
            }
        }
    }

    public static boolean isSoulBound(ItemStack item) {
        return item.getEnchantmentLevel(EIOEnchantments.SOULBOUND.get()) > 0;
    }
}

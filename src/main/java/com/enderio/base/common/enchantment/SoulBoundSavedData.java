package com.enderio.base.common.enchantment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SoulBoundSavedData extends SavedData {

    private static final String DATA_NAME = "enderio_soulbound";
    private static final String ENTRIES = "Entries";
    private static final String UUID_KEY = "UUID";
    private static final String ITEMS = "Items";

    private final Map<UUID, List<ItemStack>> pendingItems = new HashMap<>();

    public SoulBoundSavedData() {}

    public SoulBoundSavedData(CompoundTag nbt) {
        ListTag entries = nbt.getList(ENTRIES, Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            CompoundTag entryTag = (CompoundTag) entry;
            UUID uuid = entryTag.getUUID(UUID_KEY);
            ListTag itemsTag = entryTag.getList(ITEMS, Tag.TAG_COMPOUND);
            List<ItemStack> items = new ArrayList<>();
            for (Tag itemTag : itemsTag) {
                ItemStack stack = ItemStack.of((CompoundTag) itemTag);
                if (!stack.isEmpty()) {
                    items.add(stack);
                }
            }
            if (!items.isEmpty()) {
                pendingItems.put(uuid, items);
            }
        }
    }

    public static SoulBoundSavedData get(Level level) {
        if (level.isClientSide) {
            throw new IllegalStateException("SoulBoundSavedData accessed on client side");
        }

        MinecraftServer server = level.getServer();
        if (server == null) {
            throw new IllegalStateException("SoulBoundSavedData accessed without a server");
        }

        return get(server);
    }

    public static SoulBoundSavedData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(SoulBoundSavedData::new, SoulBoundSavedData::new, DATA_NAME);
    }

    public void storeItems(Player player, List<ItemStack> items) {
        List<ItemStack> storedItems = new ArrayList<>();
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                storedItems.add(stack.copy());
            }
        }
        if (storedItems.isEmpty()) {
            return;
        }

        pendingItems.computeIfAbsent(player.getUUID(), uuid -> new ArrayList<>()).addAll(storedItems);
        setDirty();
    }

    public List<ItemStack> takeItems(Player player) {
        List<ItemStack> items = pendingItems.remove(player.getUUID());
        if (items != null) {
            setDirty();
        }
        return items;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag entries = new ListTag();
        for (Map.Entry<UUID, List<ItemStack>> entry : pendingItems.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID(UUID_KEY, entry.getKey());
            ListTag itemsTag = new ListTag();
            for (ItemStack stack : entry.getValue()) {
                itemsTag.add(stack.save(new CompoundTag()));
            }
            entryTag.put(ITEMS, itemsTag);
            entries.add(entryTag);
        }
        nbt.put(ENTRIES, entries);
        return nbt;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}

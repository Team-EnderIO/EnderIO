package com.enderio.enderio.content.machines.capacitor_bank;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CapacitorBankManager {
    private static final Map<UUID, CapacitorSyncData> DATA = new HashMap<>();

    private CapacitorBankManager() {

    }

    public static void addData(UUID uuid, long storedEnergy, long capacity, long added, long send, List<BlockPos> nodes) {
        DATA.put(uuid, new CapacitorSyncData(storedEnergy, capacity, added, send, nodes));
    }

    public static void removeData(UUID uuid) {
        DATA.remove(uuid);
    }

    @Nullable
    public static CapacitorSyncData getData(UUID uuid) {
        return DATA.get(uuid);
    }

    public record CapacitorSyncData(long storedEnergy, long capacity, long added, long send, List<BlockPos> nodes) {}
}

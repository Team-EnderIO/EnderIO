package com.enderio.enderio.content.machines.capacitor_bank.rework;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CapacitorBankManager {
    public static final CapacitorBankManager INSTANCE = new CapacitorBankManager();

    private static final Map<UUID, CapacitorSyncData> DATA = new HashMap<>();

    private CapacitorBankManager() {

    }

    public static void addData(UUID uuid, CapacitorSyncData data) {
        DATA.put(uuid, data);
    }

    @Nullable
    public static CapacitorSyncData getData(UUID uuid) {
        return DATA.get(uuid);
    }

    public record CapacitorSyncData(long storedEnergy, long capacity, List<BlockPos> nodes) {}
}

package com.enderio.base.common.event;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class EIOChestLootEvent extends Event implements IModBusEvent {

    private final String lootTableName;

    private final LootPool.Builder lootPoolBuilder;

    public EIOChestLootEvent(String lootTableName, LootPool.Builder lootPoolBuilder) {
        this.lootTableName = lootTableName;
        this.lootPoolBuilder = lootPoolBuilder;
    }

    public String getLootTableName() {
        return lootTableName;
    }

    public void add(LootPoolEntryContainer.Builder<?> entriesBuilder) {
        lootPoolBuilder.add(entriesBuilder);
    }
}

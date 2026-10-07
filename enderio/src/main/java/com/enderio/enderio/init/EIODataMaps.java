package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.foundation.datamap.FarmSoul;
import com.enderio.enderio.foundation.datamap.SolarSoul;
import com.enderio.enderio.foundation.datamap.SpawnerSoul;
import com.enderio.enderio.foundation.datamap.RangeExtender;
import com.enderio.enderio.foundation.datamap.VatReagent;
import com.enderio.enderio.foundation.datamap.EngineSoul;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Map;

@EventBusSubscriber
public class EIODataMaps {

    public static final DataMapType<EntityType<?>, EngineSoul> ENGINE_SOUL = DataMapType
        .builder(EnderIO.id("engine_soul"), Registries.ENTITY_TYPE, EngineSoul.CODEC)
        // Used in the GUI, so we sync it
        .synced(EngineSoul.CODEC, true)
        .build();

    public static final DataMapType<EntityType<?>, FarmSoul> FARM_SOUL = DataMapType
        .builder(EnderIO.id("farm_soul"), Registries.ENTITY_TYPE, FarmSoul.CODEC)
        .build();

    public static final DataMapType<EntityType<?>, SolarSoul> SOLAR_SOUL = DataMapType
        .builder(EnderIO.id("solar_soul"), Registries.ENTITY_TYPE, SolarSoul.CODEC)
        .build();

    public static final DataMapType<EntityType<?>, SpawnerSoul> SPAWNER_SOUL = DataMapType
        .builder(EnderIO.id("spawner_soul"), Registries.ENTITY_TYPE, SpawnerSoul.CODEC)
        .build();

    public static final AdvancedDataMapType<Item, Map<TagKey<Item>, Double>, DataMapValueRemover.Default<Map<TagKey<Item>, Double>, Item>> VAT_REAGENT =
        AdvancedDataMapType
            .builder(EnderIO.id("vat_reagent"), Registries.ITEM, VatReagent.CODEC)
            .synced(VatReagent.CODEC, true)
            .build();

    @SubscribeEvent
    public static void registerDataMap(RegisterDataMapTypesEvent event) {
        event.register(VAT_REAGENT);
        event.register(RangeExtender.DATA_MAP);
        event.register(GrindingBallData.DATA_MAP_TYPE);
    }
}

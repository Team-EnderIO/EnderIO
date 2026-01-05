package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerLootModifier;
import com.enderio.enderio.content.capacitors.SetLootCapacitorFunction;
import com.enderio.enderio.content.paint.CopyPaintFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("unused")
public class EIOLootModifiers {
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister
            .create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnderIO.MOD_ID);

    private static final DeferredRegister<MapCodec<? extends LootItemCondition>> CONDITIONS = DeferredRegister
            .create(Registries.LOOT_CONDITION_TYPE, EnderIO.MOD_ID);
    private static final DeferredRegister<MapCodec<? extends LootItemFunction>> FUNCTIONS = DeferredRegister
            .create(Registries.LOOT_FUNCTION_TYPE, EnderIO.MOD_ID);

    // public static DeferredHolder<MapCodec<? extends IGlobalLootModifier>,
    // MapCodec<AutoSmeltModifier>> AUTO_SMELT =
    // SERIALIZERS.register("auto_smelt", () -> AutoSmeltModifier.CODEC);

    public static DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BrokenSpawnerLootModifier>> BROKEN_SPAWNER_SERIALIZER = SERIALIZERS
            .register("broken_spawner", () -> BrokenSpawnerLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SetLootCapacitorFunction>> SET_LOOT_CAPACITOR = FUNCTIONS
            .register("set_loot_capacitor", () -> SetLootCapacitorFunction.CODEC);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<CopyPaintFunction>> COPY_PAINT = FUNCTIONS
            .register("copy_paint", () -> CopyPaintFunction.CODEC);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        CONDITIONS.register(bus);
        FUNCTIONS.register(bus);
    }
}

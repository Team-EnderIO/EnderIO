package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.enchantment.AutoSmeltModifier;
import com.enderio.base.common.loot.BrokenSpawnerLootModifier;
import com.enderio.base.common.loot.ChestLootModifier;
import com.enderio.base.common.loot.SetLootCapacitorFunction;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class EIOLootModifiers {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnderIO.MODID);

    private static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EnderIO.MODID);
    private static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, EnderIO.MODID);

    public static RegistryObject<Codec<AutoSmeltModifier>> AUTO_SMELT = SERIALIZERS.register("auto_smelt", AutoSmeltModifier.CODEC);
    public static RegistryObject<Codec<BrokenSpawnerLootModifier>> BROKEN_SPAWNER_SERIALIZER = SERIALIZERS.register("broken_spawner", BrokenSpawnerLootModifier.CODEC);
    public static RegistryObject<Codec<ChestLootModifier>> CHEST_LOOT = SERIALIZERS.register("chest_loot", ChestLootModifier.CODEC);

    public static RegistryObject<LootItemFunctionType> SET_LOOT_CAPACITOR = FUNCTIONS.register("set_loot_capacitor", () -> new LootItemFunctionType(new SetLootCapacitorFunction.Serializer()));

    public static void register() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(eventbus);
        CONDITIONS.register(eventbus);
        FUNCTIONS.register(eventbus);
    }
}

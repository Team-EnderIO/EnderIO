package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.enderio.base.common.loot.BrokenSpawnerLootModifier;
import com.enderio.base.common.loot.CapacitorLootModifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class EIOLootModifiers {
    private final static DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, EnderIO.MODID);

    private final static DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, EnderIO.MODID);

    public static RegistryObject<Codec<CapacitorLootModifier>> CAPACITOR_SERIALIZER = SERIALIZERS.register("capacitor_loot", CapacitorLootModifier.CODEC);
    public static RegistryObject<Codec<BrokenSpawnerLootModifier>> BROKEN_SPAWNER_SERIALIZER = SERIALIZERS.register("broken_spawner", BrokenSpawnerLootModifier.CODEC);
    public static RegistryObject<Codec<DirectUpgradeLootModifier>> DIRECT_UPGRADE_SERIALIZER = SERIALIZERS.register("direct_upgrade", DirectUpgradeLootModifier.CODEC);

    public static RegistryObject<LootItemConditionType> DIRECT_UPGRADE_CONDITION = CONDITIONS.register("has_direct_upgrade", () -> DirectUpgradeLootCondition.HAS_DIRECT_UPGRADE);

    public static void register() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(eventbus);
        CONDITIONS.register(eventbus);
    }
}

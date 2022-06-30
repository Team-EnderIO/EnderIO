package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.base.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.enderio.base.common.loot.BrokenSpawnerLootModifier;
import com.enderio.base.common.loot.CapacitorLootModifier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class EIOLootModifiers {
    private final static DeferredRegister<GlobalLootModifierSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, EnderIO.MODID);

    private final static DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, EnderIO.MODID);

    public static RegistryObject<CapacitorLootModifier.Serializer> CAPACITOR_SERIALIZER = SERIALIZERS.register("capacitor_loot", CapacitorLootModifier.Serializer::new);
    public static RegistryObject<BrokenSpawnerLootModifier.Serializer> BROKEN_SPAWNER_SERIALIZER = SERIALIZERS.register("broken_spawner", BrokenSpawnerLootModifier.Serializer::new);
    public static RegistryObject<DirectUpgradeLootModifier.Serializer> DIRECT_UPGRADE_SERIALIZER = SERIALIZERS.register("direct_upgrade", DirectUpgradeLootModifier.Serializer::new);

    public static RegistryObject<LootItemConditionType> DIRECT_UPGRADE_CONDITION = CONDITIONS.register("has_direct_upgrade", () -> DirectUpgradeLootCondition.HAS_DIRECT_UPGRADE);

    public static void register() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(eventbus);
        CONDITIONS.register(eventbus);
    }
}

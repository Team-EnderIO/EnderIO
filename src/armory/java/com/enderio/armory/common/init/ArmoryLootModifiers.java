package com.enderio.armory.common.init;

import com.enderio.EnderIO;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("unused")
public class ArmoryLootModifiers {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnderIO.MODID);

    private static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EnderIO.MODID);
    private static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, EnderIO.MODID);

    public static DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<DirectUpgradeLootModifier>> DIRECT_UPGRADE_SERIALIZER =
        SERIALIZERS.register("direct_upgrade", DirectUpgradeLootModifier.CODEC);

    public static DeferredHolder<LootItemConditionType, LootItemConditionType> DIRECT_UPGRADE_CONDITION =
        CONDITIONS.register("has_direct_upgrade", () -> DirectUpgradeLootCondition.HAS_DIRECT_UPGRADE);

    public static void register(IEventBus eventbus) {
        SERIALIZERS.register(eventbus);
        CONDITIONS.register(eventbus);
        FUNCTIONS.register(eventbus);
    }
}

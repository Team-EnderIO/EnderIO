package com.enderio.armory.common.init;

import com.enderio.EnderIO;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("unused")
public class ArmoryLootModifiers {
    private static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, EnderIO.MODID);

    private static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, EnderIO.MODID);
    private static final DeferredRegister<LootItemFunctionType> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, EnderIO.MODID);

    public static RegistryObject<Codec<DirectUpgradeLootModifier>> DIRECT_UPGRADE_SERIALIZER = SERIALIZERS.register("direct_upgrade", DirectUpgradeLootModifier.CODEC);

    public static RegistryObject<LootItemConditionType> DIRECT_UPGRADE_CONDITION = CONDITIONS.register("has_direct_upgrade", () -> DirectUpgradeLootCondition.HAS_DIRECT_UPGRADE);

    public static void register() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        SERIALIZERS.register(eventbus);
        CONDITIONS.register(eventbus);
        FUNCTIONS.register(eventbus);
    }
}

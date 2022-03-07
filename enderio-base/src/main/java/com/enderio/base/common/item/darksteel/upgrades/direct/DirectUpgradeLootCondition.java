package com.enderio.base.common.item.darksteel.upgrades.direct;

import com.enderio.base.EnderIO;
import com.enderio.base.common.capability.darksteel.DarkSteelUpgradeable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DirectUpgradeLootCondition implements LootItemCondition {

    public static LootItemConditionType HAS_DIRECT_UPGRADE;

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        //force static init
    }

    // LootItemConditionType has no event, but it works on any.
    @SubscribeEvent
    public static void register(RegistryEvent.Register<RecipeSerializer<?>> event) {
        HAS_DIRECT_UPGRADE = Registry.register(Registry.LOOT_CONDITION_TYPE, EnderIO.loc("has_direct_upgrade"),
            new LootItemConditionType(new InnerSerializer()));
    }

    @Override
    public LootItemConditionType getType() {
        return HAS_DIRECT_UPGRADE;
    }

    @Override
    public boolean test(LootContext context) {
        if(!context.hasParam(LootContextParams.TOOL) || !context.hasParam(LootContextParams.THIS_ENTITY)) {
            return false;
        }
        return DarkSteelUpgradeable.hasUpgrade(context.getParam(LootContextParams.TOOL), DirectUpgrade.NAME)
            && context.getParam(LootContextParams.THIS_ENTITY) instanceof Player;
    }

    private static class InnerSerializer implements Serializer<DirectUpgradeLootCondition> {

        @Override
        public void serialize(JsonObject pJson, DirectUpgradeLootCondition pValue, JsonSerializationContext pSerializationContext) {
        }

        @Override
        public DirectUpgradeLootCondition deserialize(JsonObject pJson, JsonDeserializationContext pSerializationContext) {
            return new DirectUpgradeLootCondition();
        }
    }
}

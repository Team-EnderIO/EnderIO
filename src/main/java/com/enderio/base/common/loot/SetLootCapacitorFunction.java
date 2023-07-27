package com.enderio.base.common.loot;

import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOLootModifiers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetLootCapacitorFunction extends LootItemConditionalFunction {
    private final NumberProvider range;

    SetLootCapacitorFunction(LootItemCondition[] conditions, NumberProvider range) {
        super(conditions);
        this.range = range;
    }

    @Override
    public LootItemFunctionType getType() {
        return EIOLootModifiers.SET_LOOT_CAPACITOR.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        stack.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
            if (cap instanceof LootCapacitorData lootCap) {
                lootCap.setBase(range.getFloat(context));
                lootCap.addNewModifier(CapacitorUtil.getRandomModifier(context.getRandom()), range.getFloat(context));

                // 15% chance of a secondary modifier
                if (context.getRandom().nextFloat() < 0.15f) {
                    lootCap.addModifier(CapacitorUtil.getRandomModifier(context.getRandom()), range.getFloat(context));
                }

                // 2% change of a third
                if (context.getRandom().nextFloat() < 0.02f) {
                    lootCap.addModifier(CapacitorUtil.getRandomModifier(context.getRandom()), range.getFloat(context));
                }
            }
        });
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> setLootCapacitor(NumberProvider range) {
        return simpleBuilder((conditions) -> new SetLootCapacitorFunction(conditions, range));
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetLootCapacitorFunction> {

        @Override
        public void serialize(JsonObject json, SetLootCapacitorFunction value, JsonSerializationContext serializationContext) {
            super.serialize(json, value, serializationContext);
            json.add("range", serializationContext.serialize(value.range));
        }

        @Override
        public SetLootCapacitorFunction deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            NumberProvider range = GsonHelper.getAsObject(object, "range", deserializationContext, NumberProvider.class);
            return new SetLootCapacitorFunction(conditions, range);
        }
    }
}

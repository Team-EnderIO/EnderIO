package com.enderio.base.common.loot;

import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOLootModifiers;
import com.google.common.base.Suppliers;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;
import java.util.function.Supplier;

public class SetLootCapacitorFunction extends LootItemConditionalFunction {

    public static final Supplier<Codec<SetLootCapacitorFunction>> CODEC = Suppliers.memoize(() ->
        RecordCodecBuilder.create(inst -> commonFields(inst)
            .and(NumberProviders.CODEC.fieldOf("range").forGetter(m -> m.range))
            .apply(inst, SetLootCapacitorFunction::new)));

    private final NumberProvider range;

    SetLootCapacitorFunction(List<LootItemCondition> conditions, NumberProvider range) {
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
}

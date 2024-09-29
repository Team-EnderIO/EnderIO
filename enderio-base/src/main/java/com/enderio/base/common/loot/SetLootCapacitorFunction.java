package com.enderio.base.common.loot;

import com.enderio.base.api.capacitor.CapacitorData;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOLootModifiers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetLootCapacitorFunction extends LootItemConditionalFunction {

    public static final MapCodec<SetLootCapacitorFunction> CODEC =
        RecordCodecBuilder.mapCodec(inst -> commonFields(inst)
            .and(NumberProviders.CODEC.fieldOf("range").forGetter(m -> m.range))
            .apply(inst, SetLootCapacitorFunction::new));

    private final NumberProvider range;

    SetLootCapacitorFunction(List<LootItemCondition> conditions, NumberProvider range) {
        super(conditions);
        this.range = range;
    }

    @Override
    public LootItemFunctionType<SetLootCapacitorFunction> getType() {
        return EIOLootModifiers.SET_LOOT_CAPACITOR.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        float base = range.getFloat(context);
        Map<CapacitorModifier, Float> modifiers = new HashMap<>();

        modifiers.put(CapacitorModifier.getRandomModifier(context.getRandom()), range.getFloat(context));

        // 15% chance of a secondary modifier
        if (context.getRandom().nextFloat() < 0.15f) {
            modifiers.put(CapacitorModifier.getRandomModifier(context.getRandom()), range.getFloat(context));
        }

        // 2% change of a third
        if (context.getRandom().nextFloat() < 0.02f) {
            modifiers.put(CapacitorModifier.getRandomModifier(context.getRandom()), range.getFloat(context));
        }

        stack.set(EIODataComponents.CAPACITOR_DATA, new CapacitorData(base, modifiers));
        return stack;
    }

    public static Builder<?> setLootCapacitor(NumberProvider range) {
        return simpleBuilder((conditions) -> new SetLootCapacitorFunction(conditions, range));
    }
}

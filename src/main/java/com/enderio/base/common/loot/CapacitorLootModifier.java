package com.enderio.base.common.loot;

import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOItems;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class CapacitorLootModifier extends LootModifier {
    public static final Supplier<Codec<CapacitorLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).and(
        inst.group(
            Codec.FLOAT.fieldOf("min").forGetter(m -> m.min),
            Codec.FLOAT.fieldOf("max").forGetter(m -> m.max)
        )).apply(inst, CapacitorLootModifier::new)));

    /**
     * The minimum base value
     */
    private final float min;
    /**
     * The maximum base value
     */
    private final float max;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected CapacitorLootModifier(LootItemCondition[] conditionsIn, float min, float max) {
        super(conditionsIn);
        this.min = min;
        this.max = max;
    }

    /**
     * Makes a loot capacitor with random stats and adds it to the loot.
     */
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack capacitor = new ItemStack(EIOItems.LOOT_CAPACITOR.get());
        capacitor.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
            if (cap instanceof LootCapacitorData lootCap) {
                lootCap.setBase(getModifierValue(context.getRandom(), min, max));
                lootCap.addNewModifier(CapacitorUtil.getRandomModifier(context.getRandom()), getModifierValue(context.getRandom(), min, max));

                // 15% chance of a secondary modifier
                if (context.getRandom().nextFloat() < 0.15f) {
                    lootCap.addModifier(CapacitorUtil.getRandomModifier(context.getRandom()), getModifierValue(context.getRandom(), min, max));
                }

                // 2% change of a third
                if (context.getRandom().nextFloat() < 0.02f) {
                    lootCap.addModifier(CapacitorUtil.getRandomModifier(context.getRandom()), getModifierValue(context.getRandom(), min, max));
                }
            }
        });
        generatedLoot.add(capacitor);
        return generatedLoot;
    }

    private float getModifierValue(RandomSource random, float min, float max) {
        float mean = min + (max - min) * 0.5f;
        float stdDev = (max - min) * 0.25f;
        return Math.max(min, Math.min(max, (float) random.nextGaussian() * stdDev + mean));
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

}

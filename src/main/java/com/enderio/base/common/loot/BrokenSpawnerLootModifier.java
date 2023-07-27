package com.enderio.base.common.loot;

import com.enderio.api.capability.StoredEntityData;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.item.misc.BrokenSpawnerItem;
import com.enderio.base.common.tag.EIOTags;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class BrokenSpawnerLootModifier extends LootModifier {
    public static final Supplier<Codec<BrokenSpawnerLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, BrokenSpawnerLootModifier::new)));

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    public BrokenSpawnerLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockEntity entity = context.getParam(LootContextParams.BLOCK_ENTITY);
        if (entity instanceof SpawnerBlockEntity spawnerBlockEntity) {
            if (!context.getParam(LootContextParams.TOOL).is(EIOTags.Items.BROKEN_SPAWNER_BLACKLIST)) {
                if (context.getRandom().nextFloat() < BaseConfig.COMMON.BLOCKS.BROKEN_SPAWNER_DROP_CHANCE.get()) {
                    BaseSpawner spawner = spawnerBlockEntity.getSpawner();
                    CompoundTag entityTag = spawner.nextSpawnData.getEntityToSpawn();

                    if (entityTag.contains(StoredEntityData.KEY_ID)) {
                        ResourceLocation type = new ResourceLocation(entityTag.getString(StoredEntityData.KEY_ID));
                        ItemStack brokenSpawner = BrokenSpawnerItem.forType(type);
                        generatedLoot.add(brokenSpawner);
                    }
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}

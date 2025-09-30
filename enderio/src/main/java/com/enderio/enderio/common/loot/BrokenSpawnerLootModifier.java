package com.enderio.enderio.common.loot;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.common.config.BaseConfig;
import com.enderio.enderio.common.item.misc.BrokenSpawnerItem;
import com.enderio.enderio.common.tag.EIOTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class BrokenSpawnerLootModifier extends LootModifier {
    public static final MapCodec<BrokenSpawnerLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, BrokenSpawnerLootModifier::new));

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

                    // TODO: should we be copying the entire entity tag?
                    if (entityTag.contains(Entity.ID_TAG)) {
                        ResourceLocation type = ResourceLocation.parse(entityTag.getString(Entity.ID_TAG));
                        ItemStack brokenSpawner = BrokenSpawnerItem.forSoul(Soul.of(type));
                        generatedLoot.add(brokenSpawner);
                    }
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}

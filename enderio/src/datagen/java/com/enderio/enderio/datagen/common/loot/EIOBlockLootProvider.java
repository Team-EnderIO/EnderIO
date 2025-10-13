package com.enderio.enderio.datagen.common.loot;

import com.enderio.enderio.content.paint.CopyPaintFunction;
import com.enderio.enderio.content.paint.block.PaintedSlabBlock;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class EIOBlockLootProvider extends BlockLootSubProvider {
    public EIOBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return EIOBlocks.BLOCKS.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
    }

    @Override
    protected void generate() {
        // Alloys
        dropSelf(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.DARK_STEEL_BLOCK.get());
        dropSelf(EIOBlocks.SOULARIUM_BLOCK.get());
        dropSelf(EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        dropSelf(EIOBlocks.VOID_CHASSIS.get());
        dropSelf(EIOBlocks.ENSOULED_CHASSIS.get());

        // Dark Steel Building Blocks
        dropSelf(EIOBlocks.DARK_STEEL_LADDER.get());
        dropSelf(EIOBlocks.DARK_STEEL_BARS.get());
        add(EIOBlocks.DARK_STEEL_DOOR.get(), createDoorTable(EIOBlocks.DARK_STEEL_DOOR.get()));
        dropSelf(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        dropSelf(EIOBlocks.END_STEEL_BARS.get());
        dropSelf(EIOBlocks.REINFORCED_OBSIDIAN.get());

        // Painted blocks
        for (var pair : EIOBlocks.PAINTED_BLOCKS) {
            dropPainted(pair.left().get());
        }

        // Resetting Levers
        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            dropSelf(lever.get());
        }

        // Glass blocks
        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var glassBlock : glassBlocks.getAllBlocks().toList()) {
                dropSelf(glassBlock.get());
            }
        }

        // Miscellaneous
        dropSelf(EIOBlocks.SOUL_CHAIN.get());
        dropSelf(EIOBlocks.ENDERMAN_HEAD.get());
        dropSelf(EIOBlocks.INDUSTRIAL_INSULATION.get());
    }

    private void dropPainted(Block block) {
        if (block instanceof PaintedSlabBlock) {
            add(block, createPaintedSlabTable(block));
        } else if (block instanceof DoorBlock) {
            // Not actually used yet, but can't hurt :)
            add(block, createPaintedDoorTable(block));
        } else {
            add(block, createPaintedTable(block));
        }
    }

    private LootTable.Builder createPaintedTable(Block item) {
        return LootTable
            .lootTable()
            .withPool(this.applyExplosionCondition(item, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item).apply(CopyPaintFunction.copyPrimary()))));
    }

    private LootTable.Builder createPaintedSlabTable(Block item) {
        return LootTable
            .lootTable()
            .withPool(this.applyExplosionCondition(item, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item).apply(CopyPaintFunction.copyPrimary()))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(item).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.TOP))))))
            .withPool(this.applyExplosionCondition(item, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item).apply(CopyPaintFunction.copySecondary()))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(item).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.BOTTOM))))));
    }

    private LootTable.Builder createPaintedDoorTable(Block item) {
        return LootTable
            .lootTable()
            .withPool(this.applyExplosionCondition(item, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(item).apply(CopyPaintFunction.copyPrimary()))
                .when(InvertedLootItemCondition.invert(new LootItemBlockStatePropertyCondition.Builder(item).setProperties(
                    StatePropertiesPredicate.Builder.properties().hasProperty(DoorBlock.HALF, DoubleBlockHalf.LOWER))))));
    }
}

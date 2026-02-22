package com.enderio.enderio.datagen.common.loot;

import com.enderio.enderio.content.paint.CopyPaintFunction;
import com.enderio.enderio.content.paint.block.PaintedSlabBlock;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFeatureFlags;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class EIOBlockLootProvider extends BlockLootSubProvider {
    public EIOBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlagSet.of(FeatureFlags.VANILLA, EIOFeatureFlags.ENDERFACE, EIOFeatureFlags.NIARD), registries);
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
        dropSelf(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        dropSelf(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
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
        
        // Pressure plates
        dropSelf(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get());
        dropSelf(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get());

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

        // Machine Blocks
        addMachineLoot();
    }

    private void addMachineLoot() {
        // Fluid Tanks
        copyComponents(EIOBlocks.FLUID_TANK.get());
        copyComponents(EIOBlocks.PRESSURIZED_FLUID_TANK.get());

        // Enchanter
        copyComponents(EIOBlocks.ENCHANTER.get());

        // Enderface
        copyComponents(EIOBlocks.ENDERFACE.get());

        // Progress Machines
        copyComponents(EIOBlocks.ALLOY_SMELTER.get());
        copyComponents(EIOBlocks.PAINTING_MACHINE.get());
        copyComponents(EIOBlocks.WIRELESS_CHARGER.get());
        copyComponents(EIOBlocks.STIRLING_GENERATOR.get());
        copyComponents(EIOBlocks.SAG_MILL.get());
        copyComponents(EIOBlocks.SLICE_AND_SPLICE.get());
        copyComponents(EIOBlocks.IMPULSE_HOPPER.get());
        copyComponents(EIOBlocks.SOUL_BINDER.get());
        copyComponents(EIOBlocks.CRAFTER.get());
        copyComponents(EIOBlocks.DRAIN.get());
        copyComponents(EIOBlocks.SOUL_ENGINE.get());

        // Machines
        copyComponents(EIOBlocks.WIRED_CHARGER.get());

        // Powered Spawner (with soul component)
        copyStandardComponentsWith(EIOBlocks.POWERED_SPAWNER.get(), EIODataComponents.SOUL);

        // Vacuum Machines
        copyComponents(EIOBlocks.VACUUM_CHEST.get());
        copyComponents(EIOBlocks.XP_VACUUM.get());

        // Travel Anchors
        copyComponents(EIOBlocks.TRAVEL_ANCHOR.get());
        add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get(), createPaintedTable(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get()));

        // Solar Panels
//        for (var solarPanel : EIOBlocks.SOLAR_PANELS.values()) {
//            copyComponents(solarPanel.get());
//        }

        // Capacitor Banks
//        for (var capacitorBank : EIOBlocks.CAPACITOR_BANKS.values()) {
//            copyComponents(capacitorBank.get());
//        }

        // Niard
        copyComponents(EIOBlocks.NIARD.get());

        // VAT
        copyComponents(EIOBlocks.VAT.get());

        // Obelisks
        copyComponents(EIOBlocks.XP_OBELISK.get());
        copyComponents(EIOBlocks.FARMING_STATION.get());
        copyComponents(EIOBlocks.INHIBITOR_OBELISK.get());
        copyComponents(EIOBlocks.AVERSION_OBELISK.get());
        copyComponents(EIOBlocks.RELOCATOR_OBELISK.get());
        copyComponents(EIOBlocks.ATTRACTOR_OBELISK.get());
        copyComponents(EIOBlocks.WEATHER_OBELISK.get());

        // Mind Killer, Block Detector, Wireless Antennas, Creative Power - no special loot
        dropSelf(EIOBlocks.MIND_KILLER.get());
        dropSelf(EIOBlocks.BLOCK_DETECTOR.get());
        dropSelf(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get());
        dropSelf(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());
//        dropSelf(EIOBlocks.CREATIVE_POWER.get());
    }

    private void copyComponents(Block block) {
        add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(
                    CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY))))); //TODO 1.21.11 does it copy all?
    }

    private void copyStandardComponentsWith(Block block, net.minecraft.core.component.DataComponentType<?> componentType) {
        add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                    .include(componentType)
                    .include(EIODataComponents.ENERGY)
                    .include(EIODataComponents.IO_CONFIG)
                    .include(EIODataComponents.REDSTONE_CONTROL)
                    .include(DataComponents.CONTAINER)
                ))));
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

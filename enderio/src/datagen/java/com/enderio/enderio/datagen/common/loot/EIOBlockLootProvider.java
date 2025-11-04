package com.enderio.enderio.datagen.common.loot;

import com.enderio.enderio.content.paint.CopyPaintFunction;
import com.enderio.enderio.content.paint.block.PaintedSlabBlock;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFeatureFlags;
import com.enderio.enderio.init.MachineBlocks;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
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
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class EIOBlockLootProvider extends BlockLootSubProvider {
    public EIOBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlagSet.of(FeatureFlags.VANILLA, EIOFeatureFlags.ENDERFACE, EIOFeatureFlags.NIARD, EIOFeatureFlags.FARMING_STATION), registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        var eioBlocks = EIOBlocks.BLOCKS.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
        var machineBlocks = MachineBlocks.BLOCKS.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
        return java.util.stream.Stream.concat(eioBlocks.stream(), machineBlocks.stream()).toList();
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

        // Machine Blocks
        addMachineLoot();
    }

    private void addMachineLoot() {
        // Fluid Tanks
        copyComponents(MachineBlocks.FLUID_TANK.get());
        copyComponents(MachineBlocks.PRESSURIZED_FLUID_TANK.get());

        // Enchanter
        copyComponents(MachineBlocks.ENCHANTER.get());

        // Enderface
        copyComponents(MachineBlocks.ENDERFACE.get());

        // Progress Machines
        copyComponents(MachineBlocks.ALLOY_SMELTER.get());
        copyComponents(MachineBlocks.PAINTING_MACHINE.get());
        copyComponents(MachineBlocks.WIRELESS_CHARGER.get());
        copyComponents(MachineBlocks.STIRLING_GENERATOR.get());
        copyComponents(MachineBlocks.SAG_MILL.get());
        copyComponents(MachineBlocks.SLICE_AND_SPLICE.get());
        copyComponents(MachineBlocks.IMPULSE_HOPPER.get());
        copyComponents(MachineBlocks.SOUL_BINDER.get());
        copyComponents(MachineBlocks.CRAFTER.get());
        copyComponents(MachineBlocks.DRAIN.get());
        copyComponents(MachineBlocks.SOUL_ENGINE.get());

        // Machines
        copyComponents(MachineBlocks.WIRED_CHARGER.get());

        // Powered Spawner (with soul component)
        copyStandardComponentsWith(MachineBlocks.POWERED_SPAWNER.get(), EIODataComponents.SOUL);

        // Vacuum Machines
        copyComponents(MachineBlocks.VACUUM_CHEST.get());
        copyComponents(MachineBlocks.XP_VACUUM.get());

        // Travel Anchors
        copyComponents(MachineBlocks.TRAVEL_ANCHOR.get());
        add(MachineBlocks.PAINTED_TRAVEL_ANCHOR.get(), createPaintedTable(MachineBlocks.PAINTED_TRAVEL_ANCHOR.get()));

        // Solar Panels
        for (var solarPanel : MachineBlocks.SOLAR_PANELS.values()) {
            copyComponents(solarPanel.get());
        }

        // Capacitor Banks
        for (var capacitorBank : MachineBlocks.CAPACITOR_BANKS.values()) {
            copyComponents(capacitorBank.get());
        }

        // Niard
        copyComponents(MachineBlocks.NIARD.get());

        // VAT
        copyComponents(MachineBlocks.VAT.get());

        // Obelisks
        copyComponents(MachineBlocks.XP_OBELISK.get());
        copyComponents(MachineBlocks.FARMING_STATION.get());
        copyComponents(MachineBlocks.INHIBITOR_OBELISK.get());
        copyComponents(MachineBlocks.AVERSION_OBELISK.get());
        copyComponents(MachineBlocks.RELOCATOR_OBELISK.get());
        copyComponents(MachineBlocks.ATTRACTOR_OBELISK.get());
        copyComponents(MachineBlocks.WEATHER_OBELISK.get());

        // Mind Killer, Block Detector, Wireless Antennas, Creative Power - no special loot
        dropSelf(MachineBlocks.MIND_KILLER.get());
        dropSelf(MachineBlocks.BLOCK_DETECTOR.get());
        dropSelf(MachineBlocks.WIRELESS_CHARGER_ANTENNA.get());
        dropSelf(MachineBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());
        dropSelf(MachineBlocks.CREATIVE_POWER.get());
    }

    private void copyComponents(Block block) {
        add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)))));
    }

    private void copyStandardComponentsWith(Block block, net.minecraft.core.component.DataComponentType<?> componentType) {
        add(block, LootTable
            .lootTable()
            .withPool(new LootPool.Builder().add(
                LootItem.lootTableItem(block).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
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

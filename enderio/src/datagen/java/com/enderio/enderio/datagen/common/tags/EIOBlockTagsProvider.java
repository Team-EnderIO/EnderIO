package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.BlockIds;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EIOBlockTagsProvider extends BlockTagsProvider {

    public EIOBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EnderIOAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.CLIMBABLE).add(EIOBlocks.DARK_STEEL_LADDER.key());
        tag(BlockTags.DOORS).add(EIOBlocks.DARK_STEEL_DOOR.key());
        tag(BlockTags.TRAPDOORS).add(EIOBlocks.DARK_STEEL_TRAPDOOR.key());
        tag(BlockTags.WITHER_IMMUNE).add(EIOBlocks.REINFORCED_OBSIDIAN.key());
        tag(Tags.BlockItems.CHAINS.block()).add(EIOBlocks.SOUL_CHAIN.key());
        tag(Tags.Blocks.SKULLS)
            .add(EIOBlocks.ENDERMAN_HEAD.key())
            .add(EIOBlocks.WALL_ENDERMAN_HEAD.key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.key())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.key())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.key())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.key())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.key())
            .add(EIOBlocks.DARK_STEEL_BLOCK.key())
            .add(EIOBlocks.SOULARIUM_BLOCK.key())
            .add(EIOBlocks.END_STEEL_BLOCK.key())
            .add(EIOBlocks.VOID_CHASSIS.key())
            .add(EIOBlocks.ENSOULED_CHASSIS.key())
            .add(EIOBlocks.DARK_STEEL_LADDER.key())
            .add(EIOBlocks.DARK_STEEL_BARS.key())
            .add(EIOBlocks.DARK_STEEL_DOOR.key())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.key())
            .add(EIOBlocks.END_STEEL_BARS.key())
            .add(EIOBlocks.REINFORCED_OBSIDIAN.key())
            .add(EIOBlocks.CONDUIT_BUNDLE.key())
            .add(EIOBlocks.SOUL_CHAIN.key())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.key())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.key())
            .add(EIOBlocks.ENCHANTER.key())
            .add(EIOBlocks.ENDERFACE.key())
            .add(EIOBlocks.ALLOY_SMELTER.key())
            .add(EIOBlocks.PAINTING_MACHINE.key())
            .add(EIOBlocks.WIRELESS_CHARGER.key())
            .add(EIOBlocks.STIRLING_GENERATOR.key())
            .add(EIOBlocks.SAG_MILL.key())
            .add(EIOBlocks.SLICE_AND_SPLICE.key())
            .add(EIOBlocks.IMPULSE_HOPPER.key())
            .add(EIOBlocks.SOUL_BINDER.key())
            .add(EIOBlocks.CRAFTER.key())
            .add(EIOBlocks.DRAIN.key())
            .add(EIOBlocks.WIRED_CHARGER.key())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.key())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.key())
            .add(EIOBlocks.POWERED_SPAWNER.key())
            .add(EIOBlocks.MIND_KILLER.key())
            .add(EIOBlocks.VACUUM_CHEST.key())
            .add(EIOBlocks.XP_VACUUM.key())
            .add(EIOBlocks.TRAVEL_ANCHOR.key())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.key())
            .add(EIOBlocks.SOUL_ENGINE.key())
            .add(EIOBlocks.NIARD.key())
            .add(EIOBlocks.VAT.key())
            .add(EIOBlocks.XP_OBELISK.key())
            .add(EIOBlocks.FARMING_STATION.key())
            .add(EIOBlocks.INHIBITOR_OBELISK.key())
            .add(EIOBlocks.AVERSION_OBELISK.key())
            .add(EIOBlocks.RELOCATOR_OBELISK.key())
            .add(EIOBlocks.ATTRACTOR_OBELISK.key())
            .add(EIOBlocks.WEATHER_OBELISK.key());
        
        // Solar Panels and Capacitor Banks
        var solarPanels = EIOBlocks.SOLAR_PANELS.values()
            .stream()
            .sorted(Comparator.comparing(DeferredHolder::key))
            .toList();

        for (var solarPanel : solarPanels) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(solarPanel.key());
            tag(BlockTags.NEEDS_IRON_TOOL).add(solarPanel.key());
        }

        var capacitorBanks = EIOBlocks.CAPACITOR_BANKS.values()
            .stream()
            .sorted(Comparator.comparing(DeferredHolder::key))
            .toList();

        for (var capacitorBank : capacitorBanks) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(capacitorBank.key());
        }

        // Blocks that need stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.key())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.key())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.key())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.key())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.key())
            .add(EIOBlocks.DARK_STEEL_BLOCK.key())
            .add(EIOBlocks.SOULARIUM_BLOCK.key())
            .add(EIOBlocks.END_STEEL_BLOCK.key())
            .add(EIOBlocks.VOID_CHASSIS.key())
            .add(EIOBlocks.ENSOULED_CHASSIS.key());

        // Iron tools
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(EIOBlocks.DARK_STEEL_LADDER.key())
            .add(EIOBlocks.DARK_STEEL_BARS.key())
            .add(EIOBlocks.DARK_STEEL_DOOR.key())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.key())
            .add(EIOBlocks.END_STEEL_BARS.key())
            .add(EIOBlocks.SOUL_CHAIN.key())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.key())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.key())
            .add(EIOBlocks.ENCHANTER.key())
            .add(EIOBlocks.ENDERFACE.key())
            .add(EIOBlocks.ALLOY_SMELTER.key())
            .add(EIOBlocks.PAINTING_MACHINE.key())
            .add(EIOBlocks.WIRELESS_CHARGER.key())
            .add(EIOBlocks.STIRLING_GENERATOR.key())
            .add(EIOBlocks.SAG_MILL.key())
            .add(EIOBlocks.SLICE_AND_SPLICE.key())
            .add(EIOBlocks.IMPULSE_HOPPER.key())
            .add(EIOBlocks.SOUL_BINDER.key())
            .add(EIOBlocks.CRAFTER.key())
            .add(EIOBlocks.DRAIN.key())
            .add(EIOBlocks.WIRED_CHARGER.key())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.key())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.key())
            .add(EIOBlocks.POWERED_SPAWNER.key())
            .add(EIOBlocks.MIND_KILLER.key())
            .add(EIOBlocks.VACUUM_CHEST.key())
            .add(EIOBlocks.XP_VACUUM.key())
            .add(EIOBlocks.TRAVEL_ANCHOR.key())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.key())
            .add(EIOBlocks.SOUL_ENGINE.key())
            .add(EIOBlocks.NIARD.key())
            .add(EIOBlocks.VAT.key())
            .add(EIOBlocks.XP_OBELISK.key())
            .add(EIOBlocks.FARMING_STATION.key())
            .add(EIOBlocks.INHIBITOR_OBELISK.key())
            .add(EIOBlocks.AVERSION_OBELISK.key())
            .add(EIOBlocks.RELOCATOR_OBELISK.key())
            .add(EIOBlocks.ATTRACTOR_OBELISK.key())
            .add(EIOBlocks.WEATHER_OBELISK.key());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(EIOBlocks.REINFORCED_OBSIDIAN.key());

        tag(Tags.BlockItems.STORAGE_BLOCKS.block())
            .addTag(EIOTags.BlockItems.BLOCKS_CONDUCTIVE_ALLOY.block())
            .addTag(EIOTags.BlockItems.BLOCKS_DARK_STEEL.block())
            .addTag(EIOTags.BlockItems.BLOCKS_END_STEEL.block())
            .addTag(EIOTags.BlockItems.BLOCKS_ENERGETIC_ALLOY.block())
            .addTag(EIOTags.BlockItems.BLOCKS_PULSATING_ALLOY.block())
            .addTag(EIOTags.BlockItems.BLOCKS_REDSTONE_ALLOY.block())
            .addTag(EIOTags.BlockItems.BLOCKS_SOULARIUM.block())
            .addTag(EIOTags.BlockItems.BLOCKS_VIBRANT_ALLOY.block());

        tag(EIOTags.Blocks.BLOCKS_TELEPORTATION)
            .add(EIOBlocks.TRAVEL_ANCHOR.key())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.key());

        tag(EIOTags.Blocks.REDSTONE_CONNECTABLE)
            .add(BlockItemIds.PISTON.block())
            .add(BlockItemIds.STICKY_PISTON.block())
            .add(BlockItemIds.REDSTONE_LAMP.block())
            .add(BlockItemIds.NOTE_BLOCK.block())
            .add(BlockItemIds.DISPENSER.block())
            .add(BlockItemIds.DROPPER.block())
            .add(BlockItemIds.POWERED_RAIL.block())
            .add(BlockItemIds.ACTIVATOR_RAIL.block())
            .add(BlockIds.MOVING_PISTON)
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.UNAFFECTED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.EXPOSED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.WEATHERED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.OXIDIZED).block())
            .add(BlockItemIds.COPPER_BULB.waxed().pick(WeatheringCopper.WeatherState.UNAFFECTED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.EXPOSED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.WEATHERED).block())
            .add(BlockItemIds.COPPER_BULB.weathering().pick(WeatheringCopper.WeatherState.OXIDIZED).block())
            .add(BlockItemIds.COPPER_BULB.waxed().pick(WeatheringCopper.WeatherState.UNAFFECTED).block())
            .add(BlockItemIds.COPPER_BULB.waxed().pick(WeatheringCopper.WeatherState.EXPOSED).block())
            .add(BlockItemIds.COPPER_BULB.waxed().pick(WeatheringCopper.WeatherState.WEATHERED).block())
            .add(BlockItemIds.COPPER_BULB.waxed().pick(WeatheringCopper.WeatherState.OXIDIZED).block())
            .add(BlockItemIds.CRAFTER.block())
            .addTag(BlockTags.DOORS)
            .addTag(BlockTags.TRAPDOORS)
            .addTag(BlockItemTags.REDSTONE_ORES.block());

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(EIOBlocks.CONDUIT_BUNDLE.key());

        // Alloys
        tag(EIOTags.BlockItems.BLOCKS_ENERGETIC_ALLOY.block()).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_VIBRANT_ALLOY.block()).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_REDSTONE_ALLOY.block()).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_CONDUCTIVE_ALLOY.block()).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_PULSATING_ALLOY.block()).add(EIOBlocks.PULSATING_ALLOY_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_DARK_STEEL.block()).add(EIOBlocks.DARK_STEEL_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_SOULARIUM.block()).add(EIOBlocks.SOULARIUM_BLOCK.key());
        tag(EIOTags.BlockItems.BLOCKS_END_STEEL.block()).add(EIOBlocks.END_STEEL_BLOCK.key());

        addPaintedBlocks();
        addGlassBlocks();
        addMachineBlockTags();

        // Pressure plates
        tag(BlockTags.PRESSURE_PLATES)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.key());

        // Only EIO metal plates require stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.key())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.key())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.key());

        tag(EIOTags.Blocks.CROPS_WITH_STEM)
            .add(BlockItemIds.MELON.block())
            .add(BlockItemIds.PUMPKIN.block());

        tag(BlockTags.FIRE).add(EIOBlocks.COLD_FIRE.key());
    }

    private void addMachineBlockTags() {
        // Mind Killer tag
        tag(EIOTags.Blocks.MIND_KILLER).add(EIOBlocks.MIND_KILLER.key());
        
        // Range Extender tags
        tag(EIOTags.Blocks.RANGE_EXTENDER)
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.key())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.key());
        
        // Block Detector (mineable with pickaxe but no special tool requirement)
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(EIOBlocks.BLOCK_DETECTOR.key());
    }

    private void addPaintedBlocks() {
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(EIOBlocks.PAINTED_FENCE.key())
            .add(EIOBlocks.PAINTED_FENCE_GATE.key())
            .add(EIOBlocks.PAINTED_STAIRS.key())
            .add(EIOBlocks.PAINTED_CRAFTING_TABLE.key())
            .add(EIOBlocks.PAINTED_TRAPDOOR.key())
            .add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.key())
            .add(EIOBlocks.PAINTED_SLAB.key());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(EIOBlocks.PAINTED_SAND.key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.PAINTED_REDSTONE_BLOCK.key())
            .add(EIOBlocks.PAINTED_WALL.key());

        tag(BlockTags.WOODEN_FENCES).add(EIOBlocks.PAINTED_FENCE.key());
        tag(BlockTags.FENCE_GATES).add(EIOBlocks.PAINTED_FENCE_GATE.key());
        tag(BlockTags.SAND).add(EIOBlocks.PAINTED_SAND.key());
        tag(BlockTags.WOODEN_STAIRS).add(EIOBlocks.PAINTED_STAIRS.key());
        tag(BlockTags.WOODEN_TRAPDOORS).add(EIOBlocks.PAINTED_TRAPDOOR.key());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.key());
        tag(BlockTags.WOODEN_SLABS).add(EIOBlocks.PAINTED_SLAB.key());
        tag(BlockTags.WALLS).add(EIOBlocks.PAINTED_WALL.key());
    }

    private void addGlassBlocks() {
        var fusedQuartzTag = tag(EIOTags.BlockItems.FUSED_QUARTZ.block());
        var enlightenedFusedQuartzTag = tag(EIOTags.BlockItems.ENLIGHTENED_FUSED_QUARTZ.block());
        var darkFusedQuartzTag = tag(EIOTags.BlockItems.DARK_FUSED_QUARTZ.block());
        var clearGlassTag = tag(EIOTags.BlockItems.CLEAR_GLASS.block());

        var glassBlockCollections = EIOBlocks.GLASS_BLOCKS.entrySet()
            .stream()
            .sorted(Comparator.comparing(a -> a.getKey().glassName()))
            .map(Map.Entry::getValue)
            .toList();

        for (var glassBlocks : glassBlockCollections) {
            var glassItems = new ArrayList<>(glassBlocks.getAllBlocks()
                .sorted(Comparator.comparing(DeferredHolder::key))
                .toList());

            for (var blockHolder : glassItems) {
                if (blockHolder.get().glassIdentifier().explosionResistance()) {
                    fusedQuartzTag.add(blockHolder.key());

                    if (blockHolder.get().glassIdentifier().lighting() == GlassLighting.EMITTING) {
                        enlightenedFusedQuartzTag.add(blockHolder.key());
                    }

                    if (blockHolder.get().glassIdentifier().lighting() == GlassLighting.BLOCKING) {
                        darkFusedQuartzTag.add(blockHolder.key());
                    }
                } else {
                    clearGlassTag.add(blockHolder.key());
                }
            }
        }
    }
}

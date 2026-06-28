package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.references.BlockIds;
import net.minecraft.references.BlockItemId;
import net.minecraft.references.BlockItemIds;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.block.Blocks;
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
        tag(BlockTags.CLIMBABLE).add(EIOBlocks.DARK_STEEL_LADDER.getKey());
        tag(BlockTags.DOORS).add(EIOBlocks.DARK_STEEL_DOOR.getKey());
        tag(BlockTags.TRAPDOORS).add(EIOBlocks.DARK_STEEL_TRAPDOOR.getKey());
        tag(BlockTags.WITHER_IMMUNE).add(EIOBlocks.REINFORCED_OBSIDIAN.getKey());
        tag(Tags.Blocks.CHAINS).add(EIOBlocks.SOUL_CHAIN.getKey());
        tag(Tags.Blocks.SKULLS)
            .add(EIOBlocks.ENDERMAN_HEAD.getKey())
            .add(EIOBlocks.WALL_ENDERMAN_HEAD.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.DARK_STEEL_BLOCK.getKey())
            .add(EIOBlocks.SOULARIUM_BLOCK.getKey())
            .add(EIOBlocks.END_STEEL_BLOCK.getKey())
            .add(EIOBlocks.VOID_CHASSIS.getKey())
            .add(EIOBlocks.ENSOULED_CHASSIS.getKey())
            .add(EIOBlocks.DARK_STEEL_LADDER.getKey())
            .add(EIOBlocks.DARK_STEEL_BARS.getKey())
            .add(EIOBlocks.DARK_STEEL_DOOR.getKey())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.getKey())
            .add(EIOBlocks.END_STEEL_BARS.getKey())
            .add(EIOBlocks.REINFORCED_OBSIDIAN.getKey())
            .add(EIOBlocks.CONDUIT_BUNDLE.getKey())
            .add(EIOBlocks.SOUL_CHAIN.getKey())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.getKey())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.getKey())
            .add(EIOBlocks.ENCHANTER.getKey())
            .add(EIOBlocks.ENDERFACE.getKey())
            .add(EIOBlocks.ALLOY_SMELTER.getKey())
            .add(EIOBlocks.PAINTING_MACHINE.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER.getKey())
            .add(EIOBlocks.STIRLING_GENERATOR.getKey())
            .add(EIOBlocks.SAG_MILL.getKey())
            .add(EIOBlocks.SLICE_AND_SPLICE.getKey())
            .add(EIOBlocks.IMPULSE_HOPPER.getKey())
            .add(EIOBlocks.SOUL_BINDER.getKey())
            .add(EIOBlocks.CRAFTER.getKey())
            .add(EIOBlocks.DRAIN.getKey())
            .add(EIOBlocks.WIRED_CHARGER.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.getKey())
            .add(EIOBlocks.POWERED_SPAWNER.getKey())
            .add(EIOBlocks.MIND_KILLER.getKey())
            .add(EIOBlocks.VACUUM_CHEST.getKey())
            .add(EIOBlocks.XP_VACUUM.getKey())
            .add(EIOBlocks.TRAVEL_ANCHOR.getKey())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.getKey())
            .add(EIOBlocks.SOUL_ENGINE.getKey())
            .add(EIOBlocks.NIARD.getKey())
            .add(EIOBlocks.VAT.getKey())
            .add(EIOBlocks.XP_OBELISK.getKey())
            .add(EIOBlocks.FARMING_STATION.getKey())
            .add(EIOBlocks.INHIBITOR_OBELISK.getKey())
            .add(EIOBlocks.AVERSION_OBELISK.getKey())
            .add(EIOBlocks.RELOCATOR_OBELISK.getKey())
            .add(EIOBlocks.ATTRACTOR_OBELISK.getKey())
            .add(EIOBlocks.WEATHER_OBELISK.getKey());
        
        // Solar Panels and Capacitor Banks
        var solarPanels = EIOBlocks.SOLAR_PANELS.values()
            .stream()
            .sorted(Comparator.comparing(DeferredHolder::getKey))
            .toList();

        for (var solarPanel : solarPanels) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(solarPanel.getKey());
            tag(BlockTags.NEEDS_IRON_TOOL).add(solarPanel.getKey());
        }

//        var capacitorBanks = EIOBlocks.CAPACITOR_BANKS.values()
//            .stream()
//            .sorted(Comparator.comparing(DeferredHolder::getKey))
//            .toList();

//        for (var capacitorBank : capacitorBanks) {
//            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(capacitorBank.getKey());
//        }

        // Blocks that need stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.getKey())
            .add(EIOBlocks.DARK_STEEL_BLOCK.getKey())
            .add(EIOBlocks.SOULARIUM_BLOCK.getKey())
            .add(EIOBlocks.END_STEEL_BLOCK.getKey())
            .add(EIOBlocks.VOID_CHASSIS.getKey())
            .add(EIOBlocks.ENSOULED_CHASSIS.getKey());

        // Iron tools
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(EIOBlocks.DARK_STEEL_LADDER.getKey())
            .add(EIOBlocks.DARK_STEEL_BARS.getKey())
            .add(EIOBlocks.DARK_STEEL_DOOR.getKey())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.getKey())
            .add(EIOBlocks.END_STEEL_BARS.getKey())
            .add(EIOBlocks.SOUL_CHAIN.getKey())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.getKey())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.getKey())
            .add(EIOBlocks.ENCHANTER.getKey())
            .add(EIOBlocks.ENDERFACE.getKey())
            .add(EIOBlocks.ALLOY_SMELTER.getKey())
            .add(EIOBlocks.PAINTING_MACHINE.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER.getKey())
            .add(EIOBlocks.STIRLING_GENERATOR.getKey())
            .add(EIOBlocks.SAG_MILL.getKey())
            .add(EIOBlocks.SLICE_AND_SPLICE.getKey())
            .add(EIOBlocks.IMPULSE_HOPPER.getKey())
            .add(EIOBlocks.SOUL_BINDER.getKey())
            .add(EIOBlocks.CRAFTER.getKey())
            .add(EIOBlocks.DRAIN.getKey())
            .add(EIOBlocks.WIRED_CHARGER.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.getKey())
            .add(EIOBlocks.POWERED_SPAWNER.getKey())
            .add(EIOBlocks.MIND_KILLER.getKey())
            .add(EIOBlocks.VACUUM_CHEST.getKey())
            .add(EIOBlocks.XP_VACUUM.getKey())
            .add(EIOBlocks.TRAVEL_ANCHOR.getKey())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.getKey())
            .add(EIOBlocks.SOUL_ENGINE.getKey())
            .add(EIOBlocks.NIARD.getKey())
            .add(EIOBlocks.VAT.getKey())
            .add(EIOBlocks.XP_OBELISK.getKey())
            .add(EIOBlocks.FARMING_STATION.getKey())
            .add(EIOBlocks.INHIBITOR_OBELISK.getKey())
            .add(EIOBlocks.AVERSION_OBELISK.getKey())
            .add(EIOBlocks.RELOCATOR_OBELISK.getKey())
            .add(EIOBlocks.ATTRACTOR_OBELISK.getKey())
            .add(EIOBlocks.WEATHER_OBELISK.getKey());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(EIOBlocks.REINFORCED_OBSIDIAN.getKey());

        tag(Tags.Blocks.STORAGE_BLOCKS)
            .addTag(EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY)
            .addTag(EIOTags.Blocks.BLOCKS_DARK_STEEL)
            .addTag(EIOTags.Blocks.BLOCKS_END_STEEL)
            .addTag(EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY)
            .addTag(EIOTags.Blocks.BLOCKS_PULSATING_ALLOY)
            .addTag(EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY)
            .addTag(EIOTags.Blocks.BLOCKS_SOULARIUM)
            .addTag(EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY);

        tag(EIOTags.Blocks.BLOCKS_TELEPORTATION)
            .add(EIOBlocks.TRAVEL_ANCHOR.getKey())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.getKey());

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

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(EIOBlocks.CONDUIT_BUNDLE.getKey());

        // Alloys
        tag(EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_PULSATING_ALLOY).add(EIOBlocks.PULSATING_ALLOY_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_DARK_STEEL).add(EIOBlocks.DARK_STEEL_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_SOULARIUM).add(EIOBlocks.SOULARIUM_BLOCK.getKey());
        tag(EIOTags.Blocks.BLOCKS_END_STEEL).add(EIOBlocks.END_STEEL_BLOCK.getKey());

        addPaintedBlocks();
        addGlassBlocks();
        addMachineBlockTags();

        // Pressure plates
        tag(BlockTags.PRESSURE_PLATES)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.getKey());

        // Only EIO metal plates require stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.getKey());

        tag(EIOTags.Blocks.CROPS_WITH_STEM)
            .add(BlockItemIds.MELON.block())
            .add(BlockItemIds.PUMPKIN.block());
    }

    private void addMachineBlockTags() {
        // Mind Killer tag
        tag(EIOTags.Blocks.MIND_KILLER).add(EIOBlocks.MIND_KILLER.getKey());
        
        // Range Extender tags
        tag(EIOTags.Blocks.RANGE_EXTENDER)
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.getKey())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.getKey());
        
        // Block Detector (mineable with pickaxe but no special tool requirement)
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(EIOBlocks.BLOCK_DETECTOR.getKey());
    }

    private void addPaintedBlocks() {
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(EIOBlocks.PAINTED_FENCE.getKey())
            .add(EIOBlocks.PAINTED_FENCE_GATE.getKey())
            .add(EIOBlocks.PAINTED_STAIRS.getKey())
            .add(EIOBlocks.PAINTED_CRAFTING_TABLE.getKey())
            .add(EIOBlocks.PAINTED_TRAPDOOR.getKey())
            .add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.getKey())
            .add(EIOBlocks.PAINTED_SLAB.getKey());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(EIOBlocks.PAINTED_SAND.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.PAINTED_REDSTONE_BLOCK.getKey())
            .add(EIOBlocks.PAINTED_WALL.getKey());

        tag(BlockTags.WOODEN_FENCES).add(EIOBlocks.PAINTED_FENCE.getKey());
        tag(BlockTags.FENCE_GATES).add(EIOBlocks.PAINTED_FENCE_GATE.getKey());
        tag(BlockTags.SAND).add(EIOBlocks.PAINTED_SAND.getKey());
        tag(BlockTags.WOODEN_STAIRS).add(EIOBlocks.PAINTED_STAIRS.getKey());
        tag(BlockTags.WOODEN_TRAPDOORS).add(EIOBlocks.PAINTED_TRAPDOOR.getKey());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.getKey());
        tag(BlockTags.WOODEN_SLABS).add(EIOBlocks.PAINTED_SLAB.getKey());
        tag(BlockTags.WALLS).add(EIOBlocks.PAINTED_WALL.getKey());
    }

    private void addGlassBlocks() {
        var fusedQuartzTag = tag(EIOTags.Blocks.FUSED_QUARTZ);
        var enlightenedFusedQuartzTag = tag(EIOTags.Blocks.ENLIGHTENED_FUSED_QUARTZ);
        var darkFusedQuartzTag = tag(EIOTags.Blocks.DARK_FUSED_QUARTZ);
        var clearGlassTag = tag(EIOTags.Blocks.CLEAR_GLASS);

        var glassBlockCollections = EIOBlocks.GLASS_BLOCKS.entrySet()
            .stream()
            .sorted(Comparator.comparing(a -> a.getKey().glassName()))
            .map(Map.Entry::getValue)
            .toList();

        for (var glassBlocks : glassBlockCollections) {
            var glassItems = new ArrayList<>(glassBlocks.getAllBlocks()
                .sorted(Comparator.comparing(DeferredHolder::getKey))
                .toList());

            for (var blockHolder : glassItems) {
                if (blockHolder.get().glassIdentifier().explosionResistance()) {
                    fusedQuartzTag.add(blockHolder.getKey());

                    if (blockHolder.get().glassIdentifier().lighting() == GlassLighting.EMITTING) {
                        enlightenedFusedQuartzTag.add(blockHolder.getKey());
                    }

                    if (blockHolder.get().glassIdentifier().lighting() == GlassLighting.BLOCKING) {
                        darkFusedQuartzTag.add(blockHolder.getKey());
                    }
                } else {
                    clearGlassTag.add(blockHolder.getKey());
                }
            }
        }
    }
}

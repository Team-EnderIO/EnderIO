package com.enderio.enderio.datagen.common.tags;

import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EIOBlockTagsProvider extends BlockTagsProvider {

    public EIOBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EnderIOAPI.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.CLIMBABLE).add(EIOBlocks.DARK_STEEL_LADDER.get());
        tag(BlockTags.DOORS).add(EIOBlocks.DARK_STEEL_DOOR.get());
        tag(BlockTags.TRAPDOORS).add(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        tag(BlockTags.WITHER_IMMUNE).add(EIOBlocks.REINFORCED_OBSIDIAN.get());
        tag(Tags.Blocks.CHAINS).add(EIOBlocks.SOUL_CHAIN.get());
        tag(Tags.Blocks.SKULLS)
            .add(EIOBlocks.ENDERMAN_HEAD.get())
            .add(EIOBlocks.WALL_ENDERMAN_HEAD.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.get())
            .add(EIOBlocks.DARK_STEEL_BLOCK.get())
            .add(EIOBlocks.SOULARIUM_BLOCK.get())
            .add(EIOBlocks.END_STEEL_BLOCK.get())
            .add(EIOBlocks.VOID_CHASSIS.get())
            .add(EIOBlocks.ENSOULED_CHASSIS.get())
            .add(EIOBlocks.DARK_STEEL_LADDER.get())
            .add(EIOBlocks.DARK_STEEL_BARS.get())
            .add(EIOBlocks.DARK_STEEL_DOOR.get())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.get())
            .add(EIOBlocks.END_STEEL_BARS.get())
            .add(EIOBlocks.REINFORCED_OBSIDIAN.get())
            .add(EIOBlocks.CONDUIT_BUNDLE.get())
            .add(EIOBlocks.SOUL_CHAIN.get())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.get())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.get())
            .add(EIOBlocks.ENCHANTER.get())
            .add(EIOBlocks.ENDERFACE.get())
            .add(EIOBlocks.ALLOY_SMELTER.get())
            .add(EIOBlocks.PAINTING_MACHINE.get())
            .add(EIOBlocks.WIRELESS_CHARGER.get())
            .add(EIOBlocks.STIRLING_GENERATOR.get())
            .add(EIOBlocks.SAG_MILL.get())
            .add(EIOBlocks.SLICE_AND_SPLICE.get())
            .add(EIOBlocks.IMPULSE_HOPPER.get())
            .add(EIOBlocks.SOUL_BINDER.get())
            .add(EIOBlocks.CRAFTER.get())
            .add(EIOBlocks.DRAIN.get())
            .add(EIOBlocks.WIRED_CHARGER.get())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get())
            .add(EIOBlocks.POWERED_SPAWNER.get())
            .add(EIOBlocks.MIND_KILLER.get())
            .add(EIOBlocks.VACUUM_CHEST.get())
            .add(EIOBlocks.XP_VACUUM.get())
            .add(EIOBlocks.TRAVEL_ANCHOR.get())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get())
            .add(EIOBlocks.SOUL_ENGINE.get())
            .add(EIOBlocks.NIARD.get())
            .add(EIOBlocks.VAT.get())
            .add(EIOBlocks.XP_OBELISK.get())
            .add(EIOBlocks.FARMING_STATION.get())
            .add(EIOBlocks.INHIBITOR_OBELISK.get())
            .add(EIOBlocks.AVERSION_OBELISK.get())
            .add(EIOBlocks.RELOCATOR_OBELISK.get())
            .add(EIOBlocks.ATTRACTOR_OBELISK.get())
            .add(EIOBlocks.WEATHER_OBELISK.get());
        
        // Solar Panels and Capacitor Banks
        var solarPanels = EIOBlocks.SOLAR_PANELS.values()
            .stream()
            .sorted(Comparator.comparing(DeferredHolder::getKey))
            .toList();

        for (var solarPanel : solarPanels) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(solarPanel.get());
            tag(BlockTags.NEEDS_IRON_TOOL).add(solarPanel.get());
        }

        var capacitorBanks = EIOBlocks.CAPACITOR_BANKS.values()
            .stream()
            .sorted(Comparator.comparing(DeferredHolder::getKey))
            .toList();

        for (var capacitorBank : capacitorBanks) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(capacitorBank.get());
        }

        // Blocks that need stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get())
            .add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get())
            .add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get())
            .add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get())
            .add(EIOBlocks.PULSATING_ALLOY_BLOCK.get())
            .add(EIOBlocks.DARK_STEEL_BLOCK.get())
            .add(EIOBlocks.SOULARIUM_BLOCK.get())
            .add(EIOBlocks.END_STEEL_BLOCK.get())
            .add(EIOBlocks.VOID_CHASSIS.get())
            .add(EIOBlocks.ENSOULED_CHASSIS.get());

        // Iron tools
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(EIOBlocks.DARK_STEEL_LADDER.get())
            .add(EIOBlocks.DARK_STEEL_BARS.get())
            .add(EIOBlocks.DARK_STEEL_DOOR.get())
            .add(EIOBlocks.DARK_STEEL_TRAPDOOR.get())
            .add(EIOBlocks.END_STEEL_BARS.get())
            .add(EIOBlocks.SOUL_CHAIN.get())
            // Machine Blocks
            .add(EIOBlocks.FLUID_TANK.get())
            .add(EIOBlocks.PRESSURIZED_FLUID_TANK.get())
            .add(EIOBlocks.ENCHANTER.get())
            .add(EIOBlocks.ENDERFACE.get())
            .add(EIOBlocks.ALLOY_SMELTER.get())
            .add(EIOBlocks.PAINTING_MACHINE.get())
            .add(EIOBlocks.WIRELESS_CHARGER.get())
            .add(EIOBlocks.STIRLING_GENERATOR.get())
            .add(EIOBlocks.SAG_MILL.get())
            .add(EIOBlocks.SLICE_AND_SPLICE.get())
            .add(EIOBlocks.IMPULSE_HOPPER.get())
            .add(EIOBlocks.SOUL_BINDER.get())
            .add(EIOBlocks.CRAFTER.get())
            .add(EIOBlocks.DRAIN.get())
            .add(EIOBlocks.WIRED_CHARGER.get())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get())
            .add(EIOBlocks.POWERED_SPAWNER.get())
            .add(EIOBlocks.MIND_KILLER.get())
            .add(EIOBlocks.VACUUM_CHEST.get())
            .add(EIOBlocks.XP_VACUUM.get())
            .add(EIOBlocks.TRAVEL_ANCHOR.get())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get())
            .add(EIOBlocks.SOUL_ENGINE.get())
            .add(EIOBlocks.NIARD.get())
            .add(EIOBlocks.VAT.get())
            .add(EIOBlocks.XP_OBELISK.get())
            .add(EIOBlocks.FARMING_STATION.get())
            .add(EIOBlocks.INHIBITOR_OBELISK.get())
            .add(EIOBlocks.AVERSION_OBELISK.get())
            .add(EIOBlocks.RELOCATOR_OBELISK.get())
            .add(EIOBlocks.ATTRACTOR_OBELISK.get())
            .add(EIOBlocks.WEATHER_OBELISK.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(EIOBlocks.REINFORCED_OBSIDIAN.get());

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
            .add(EIOBlocks.TRAVEL_ANCHOR.get())
            .add(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get());

        tag(EIOTags.Blocks.REDSTONE_CONNECTABLE)
            .add(Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.REDSTONE_LAMP, Blocks.NOTE_BLOCK, Blocks.DISPENSER,
                Blocks.DROPPER, Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.MOVING_PISTON,
                Blocks.COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB,
                Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB,
                Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.CRAFTER)
            .addTags(BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.REDSTONE_ORES);

        tag(Tags.Blocks.RELOCATION_NOT_SUPPORTED).add(EIOBlocks.CONDUIT_BUNDLE.get());

        // Alloys
        tag(EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY).add(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY).add(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY).add(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY).add(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_PULSATING_ALLOY).add(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_DARK_STEEL).add(EIOBlocks.DARK_STEEL_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_SOULARIUM).add(EIOBlocks.SOULARIUM_BLOCK.get());
        tag(EIOTags.Blocks.BLOCKS_END_STEEL).add(EIOBlocks.END_STEEL_BLOCK.get());

        addPaintedBlocks();
        addGlassBlocks();
        addMachineBlockTags();

        // Pressure plates
        tag(BlockTags.PRESSURE_PLATES)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get());

        // Only EIO metal plates require stone tools
        tag(BlockTags.NEEDS_STONE_TOOL)
            .add(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get())
            .add(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get())
            .add(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get());

        tag(EIOTags.Blocks.CROPS_WITH_STEM)
            .add(Blocks.MELON)
            .add(Blocks.PUMPKIN);

        tag(BlockTags.FIRE).add(EIOBlocks.COLD_FIRE.get());
    }

    private void addMachineBlockTags() {
        // Mind Killer tag
        tag(EIOTags.Blocks.MIND_KILLER).add(EIOBlocks.MIND_KILLER.get());
        
        // Range Extender tags
        tag(EIOTags.Blocks.RANGE_EXTENDER)
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get())
            .add(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());
        
        // Block Detector (mineable with pickaxe but no special tool requirement)
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(EIOBlocks.BLOCK_DETECTOR.get());
    }

    private void addPaintedBlocks() {
        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(EIOBlocks.PAINTED_FENCE.get())
            .add(EIOBlocks.PAINTED_FENCE_GATE.get())
            .add(EIOBlocks.PAINTED_STAIRS.get())
            .add(EIOBlocks.PAINTED_CRAFTING_TABLE.get())
            .add(EIOBlocks.PAINTED_TRAPDOOR.get())
            .add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get())
            .add(EIOBlocks.PAINTED_SLAB.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(EIOBlocks.PAINTED_SAND.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EIOBlocks.PAINTED_REDSTONE_BLOCK.get())
            .add(EIOBlocks.PAINTED_WALL.get());

        tag(BlockTags.WOODEN_FENCES).add(EIOBlocks.PAINTED_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(EIOBlocks.PAINTED_FENCE_GATE.get());
        tag(BlockTags.SAND).add(EIOBlocks.PAINTED_SAND.get());
        tag(BlockTags.WOODEN_STAIRS).add(EIOBlocks.PAINTED_STAIRS.get());
        tag(BlockTags.WOODEN_TRAPDOORS).add(EIOBlocks.PAINTED_TRAPDOOR.get());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get());
        tag(BlockTags.WOODEN_SLABS).add(EIOBlocks.PAINTED_SLAB.get());
        tag(BlockTags.WALLS).add(EIOBlocks.PAINTED_WALL.get());
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
                .map(DeferredHolder::get)
                .toList());

            for (var block : glassItems) {
                if (block.glassIdentifier().explosionResistance()) {
                    fusedQuartzTag.add(block);

                    if (block.glassIdentifier().lighting() == GlassLighting.EMITTING) {
                        enlightenedFusedQuartzTag.add(block);
                    }

                    if (block.glassIdentifier().lighting() == GlassLighting.BLOCKING) {
                        darkFusedQuartzTag.add(block);
                    }
                } else {
                    clearGlassTag.add(block);
                }
            }
        }
    }
}

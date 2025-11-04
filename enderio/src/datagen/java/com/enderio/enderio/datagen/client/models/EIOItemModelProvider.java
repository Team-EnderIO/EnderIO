package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.data.model.item.FacadeItemModelBuilder;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Locale;
import java.util.Objects;

public class EIOItemModelProvider extends ItemModelProvider {
    public EIOItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnderIO.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Alloys
        basicItem(EIOItems.COPPER_ALLOY_INGOT.get());
        basicItem(EIOItems.ENERGETIC_ALLOY_INGOT.get());
        basicItem(EIOItems.VIBRANT_ALLOY_INGOT.get());
        basicItem(EIOItems.REDSTONE_ALLOY_INGOT.get());
        basicItem(EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        basicItem(EIOItems.PULSATING_ALLOY_INGOT.get());
        basicItem(EIOItems.DARK_STEEL_INGOT.get());
        basicItem(EIOItems.SOULARIUM_INGOT.get());
        basicItem(EIOItems.END_STEEL_INGOT.get());

        basicItem(EIOItems.COPPER_ALLOY_NUGGET.get());
        basicItem(EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        basicItem(EIOItems.VIBRANT_ALLOY_NUGGET.get());
        basicItem(EIOItems.REDSTONE_ALLOY_NUGGET.get());
        basicItem(EIOItems.CONDUCTIVE_ALLOY_NUGGET.get());
        basicItem(EIOItems.PULSATING_ALLOY_NUGGET.get());
        basicItem(EIOItems.DARK_STEEL_NUGGET.get());
        basicItem(EIOItems.SOULARIUM_NUGGET.get());
        basicItem(EIOItems.END_STEEL_NUGGET.get());

        // Grinding Balls
        basicItem(EIOItems.COPPER_ALLOY_BALL.get());
        basicItem(EIOItems.ENERGETIC_ALLOY_BALL.get());
        basicItem(EIOItems.VIBRANT_ALLOY_BALL.get());
        basicItem(EIOItems.REDSTONE_ALLOY_BALL.get());
        basicItem(EIOItems.CONDUCTIVE_ALLOY_BALL.get());
        basicItem(EIOItems.PULSATING_ALLOY_BALL.get());
        basicItem(EIOItems.DARK_STEEL_BALL.get());
        basicItem(EIOItems.SOULARIUM_BALL.get());
        basicItem(EIOItems.END_STEEL_BALL.get());

        // Crafting Components
        basicItem(EIOItems.SILICON.get());
        basicItem(EIOItems.GRAINS_OF_INFINITY.get());
        basicItem(EIOItems.INFINITY_ROD.get());
        basicItem(EIOItems.CONDUIT_BINDER_COMPOSITE.get());
        basicItem(EIOItems.CONDUIT_BINDER.get());

        basicItem(EIOItems.GEAR_IRON.get());
        basicItem(EIOItems.GEAR_ENERGIZED.get());
        basicItem(EIOItems.GEAR_VIBRANT.get());
        basicItem(EIOItems.GEAR_DARK_STEEL.get());

        basicItem(EIOItems.ZOMBIE_ELECTRODE.get());
        basicItem(EIOItems.Z_LOGIC_CONTROLLER.get());
        withExistingParent(EIOItems.FRANK_N_ZOMBIE.getId().toString(), EIOItems.Z_LOGIC_CONTROLLER.getId());
        basicItem(EIOItems.ENDER_RESONATOR.get());
        withExistingParent(EIOItems.SENTIENT_ENDER.getId().toString(), EIOItems.ENDER_RESONATOR.getId());
        basicItem(EIOItems.SKELETAL_CONTRACTOR.get());
        basicItem(EIOItems.GUARDIAN_DIODE.get());
        basicItem(EIOItems.SUSPICIOUS_SEED.get());

        // Capacitors
        basicItem(EIOItems.BASIC_CAPACITOR.get());
        basicItem(EIOItems.DOUBLE_LAYER_CAPACITOR.get());
        basicItem(EIOItems.OCTADIC_CAPACITOR.get());
        basicItem(EIOItems.LOOT_CAPACITOR.get()); // TODO: Multiple variants of loot capacitor.

        // Crystals
        basicItem(EIOItems.PULSATING_CRYSTAL.get());
        basicItem(EIOItems.VIBRANT_CRYSTAL.get());
        basicItem(EIOItems.ENDER_CRYSTAL.get());
        basicItem(EIOItems.ENTICING_CRYSTAL.get());
        basicItem(EIOItems.WEATHER_CRYSTAL.get());
        basicItem(EIOItems.PRESCIENT_CRYSTAL.get());

        // Powders and Fragments
        basicItem(EIOItems.FLOUR.get());
        basicItem(EIOItems.POWDERED_COAL.get());
        basicItem(EIOItems.POWDERED_IRON.get());
        basicItem(EIOItems.POWDERED_GOLD.get());
        basicItem(EIOItems.POWDERED_COPPER.get());
        basicItem(EIOItems.POWDERED_TIN.get());
        basicItem(EIOItems.POWDERED_ENDER_PEARL.get());
        basicItem(EIOItems.POWDERED_OBSIDIAN.get());
        basicItem(EIOItems.POWDERED_COBALT.get());
        basicItem(EIOItems.POWDERED_LAPIS_LAZULI.get());
        basicItem(EIOItems.POWDERED_QUARTZ.get());
        basicItem(EIOItems.PRESCIENT_POWDER.get());
        basicItem(EIOItems.VIBRANT_POWDER.get());
        basicItem(EIOItems.PULSATING_POWDER.get());
        basicItem(EIOItems.ENDER_CRYSTAL_POWDER.get());
        basicItem(EIOItems.PHOTOVOLTAIC_COMPOSITE.get());
        basicItem(EIOItems.SOUL_POWDER.get());
        basicItem(EIOItems.CONFUSION_POWDER.get());
        basicItem(EIOItems.WITHERING_POWDER.get());

        // Dyes
        basicItem(EIOItems.DYE_GREEN.get());
        basicItem(EIOItems.DYE_BROWN.get());
        basicItem(EIOItems.DYE_BLACK.get());

        // Misc Materials
        basicItem(EIOItems.PHOTOVOLTAIC_PLATE.get());
        basicItem(EIOItems.NUTRITIOUS_STICK.get());
        basicItem(EIOItems.PLANT_MATTER_GREEN.get());
        basicItem(EIOItems.PLANT_MATTER_BROWN.get());
        basicItem(EIOItems.GLIDER_WING.get());
        basicItem(EIOItems.ANIMAL_TOKEN.get());
        basicItem(EIOItems.MONSTER_TOKEN.get());
        basicItem(EIOItems.PLAYER_TOKEN.get());
        basicItem(EIOItems.CAKE_BASE.get());
        basicItem(EIOItems.BLACK_PAPER.get());
        basicItem(EIOItems.CLAYED_GLOWSTONE.get());
        basicItem(EIOItems.NETHERCOTTA.get());
        fakeBlock(EIOItems.BROKEN_SPAWNER.get());

        // Gliders
        basicItem(EIOItems.GLIDER.get());

        // Fun
        // TODO: Should we generate enderios model?
        basicItem(EIOItems.ENDERIOS.get())
            .override()
            .predicate(EnderiosItem.INVERTED_PROPERTY, 1)
            .model(basicItem(EnderIO.rl("soiredne")))
            .end();

        // Tools
        basicItem(EIOItems.SOUL_VIAL.get())
            .override()
            .predicate(SoulVialItem.FILLED_MODEL_PROPERTY, 1)
            .model(basicItem(EnderIO.rl("soul_vial_filled")))
            .end();

        basicItem(EIOItems.VOID_VIAL.get());
        basicItem(EIOItems.YETA_WRENCH.get());
        basicItem(EIOItems.COORDINATE_SELECTOR.get());
        basicItem(EIOItems.LOCATION_PRINTOUT.get());
        basicItem(EIOItems.LEVITATION_STAFF.get());
        basicItem(EIOItems.TRAVEL_STAFF.get());
        basicItem(EIOItems.ELECTROMAGNET.get());
        basicItem(EIOItems.COLD_FIRE_IGNITER.get());

        getBuilder(EIOItems.CONDUIT_PROBE.getId().toString())
            .parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", EnderIO.rl("item/conduit_probe_probe"))
                .override()
                .predicate(ConduitProbeItem.PROBE_STATE_PREDICATE, 1)
                .model(basicItem(EnderIO.rl("conduit_probe_copy")));

        // Filters
        basicItem(EIOItems.BASIC_ITEM_FILTER.get());
        basicItem(EIOItems.BIG_ITEM_FILTER.get());
        basicItem(EIOItems.ADVANCED_ITEM_FILTER.get());
        basicItem(EIOItems.BIG_ADVANCED_ITEM_FILTER.get());

        basicItem(EIOItems.BASIC_FLUID_FILTER.get());

        basicItem(EIOItems.BASIC_SOUL_FILTER.get());

        basicItem(EIOItems.REDSTONE_FILTER_BASE.get());
        basicItem(EIOItems.NOT_FILTER.get());
        basicItem(EIOItems.OR_FILTER.get());
        basicItem(EIOItems.AND_FILTER.get());
        basicItem(EIOItems.NOR_FILTER.get());
        basicItem(EIOItems.NAND_FILTER.get());
        basicItem(EIOItems.XOR_FILTER.get());
        basicItem(EIOItems.XNOR_FILTER.get());
        basicItem(EIOItems.TLATCH_FILTER.get());
        basicItem(EIOItems.COUNT_FILTER.get());
        basicItem(EIOItems.SENSOR_FILTER.get());
        basicItem(EIOItems.TIMER_FILTER.get());

        // Conduit facades
        getBuilder(EIOItems.CONDUIT_FACADE.getId().toString())
            .customLoader(FacadeItemModelBuilder::begin)
            .model(EIOItems.CONDUIT_FACADE.getId().getPath());

        getBuilder(EIOItems.TRANSPARENT_CONDUIT_FACADE.getId().toString())
            .customLoader(FacadeItemModelBuilder::begin)
            .model(EIOItems.TRANSPARENT_CONDUIT_FACADE.getId().getPath());

        getBuilder(EIOItems.HARDENED_CONDUIT_FACADE.getId().toString())
            .customLoader(FacadeItemModelBuilder::begin)
            .model(EIOItems.HARDENED_CONDUIT_FACADE.getId().getPath());

        getBuilder(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.getId().toString())
            .customLoader(FacadeItemModelBuilder::begin)
            .model(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.getId().getPath());

        // Creative Tab Icon
        basicItem(EIOItems.CREATIVE_ICON.get());

        // region Blocks

        // Alloys
        simpleBlockItem(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        simpleBlockItem(EIOBlocks.DARK_STEEL_BLOCK.get());
        simpleBlockItem(EIOBlocks.SOULARIUM_BLOCK.get());
        simpleBlockItem(EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        simpleBlockItem(EIOBlocks.VOID_CHASSIS.get());
        simpleBlockItem(EIOBlocks.ENSOULED_CHASSIS.get());

        // Dark Steel Building Blocks
        flatBlockItem(EIOBlocks.DARK_STEEL_LADDER.get());
        flatBlockItem(EIOBlocks.DARK_STEEL_BARS.get());
        basicItem(EIOBlocks.DARK_STEEL_DOOR.asItem());
        simpleBlockItem(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        withExistingParent(EIOBlocks.DARK_STEEL_TRAPDOOR.getId().toString(), modLoc("block/dark_steel_trapdoor_bottom"));
        flatBlockItem(EIOBlocks.END_STEEL_BARS.get());
        simpleBlockItem(EIOBlocks.REINFORCED_OBSIDIAN.get());

        // Painted Blocks
        for (var pair : EIOBlocks.PAINTED_BLOCKS) {
            simpleBlockItem(pair.left().get());
        }

        // Resetting Levers
        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            withExistingParent(lever.getId().toString(), mcLoc("item/lever"));
        }

        // Glass Blocks
        var fusedQuartzModel = modLoc("block/fused_quartz");
        var clearGlassModel = modLoc("block/clear_glass");

        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var block : glassBlocks.getAllBlocks().toList()) {
                withExistingParent(block.getId().toString(), block.get().glassIdentifier().explosionResistance() ? fusedQuartzModel : clearGlassModel);
            }
        }

        // Miscellaneous
        basicItem(EIOBlocks.SOUL_CHAIN.asItem());
        withExistingParent(EIOBlocks.ENDERMAN_HEAD.getId().toString(), mcLoc("item/template_skull"));
        simpleBlockItem(EIOBlocks.INDUSTRIAL_INSULATION.get());

        // Pressure Plates
        simpleBlockItem(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        simpleBlockItem(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get());
        simpleBlockItem(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        simpleBlockItem(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get());

        // Silent variants that wrap vanilla blocks — point items at the vanilla block models
        withExistingParent(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.getId().toString(), mcLoc("item/oak_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.getId().toString(), mcLoc("item/acacia_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.getId().toString(), mcLoc("item/dark_oak_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.getId().toString(), mcLoc("item/spruce_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.getId().toString(), mcLoc("item/birch_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.getId().toString(), mcLoc("item/jungle_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.getId().toString(), mcLoc("item/crimson_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.getId().toString(), mcLoc("item/warped_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.getId().toString(), mcLoc("item/stone_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.getId().toString(), mcLoc("item/polished_blackstone_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.getId().toString(), mcLoc("item/heavy_weighted_pressure_plate"));
        withExistingParent(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.getId().toString(), mcLoc("item/light_weighted_pressure_plate"));

        // Machine Blocks
        addMachineItemModels();

        // endregion
    }

    private void addMachineItemModels() {
        // Fluid Tanks - no item model needed (empty)
        
        // Enchanter
        simpleBlockItem(EIOBlocks.ENCHANTER.get());

        // Enderface
        simpleBlockItem(EIOBlocks.ENDERFACE.get());

        // Progress Machines
        simpleBlockItem(EIOBlocks.ALLOY_SMELTER.get());
        simpleBlockItem(EIOBlocks.PAINTING_MACHINE.get());
        simpleBlockItem(EIOBlocks.WIRELESS_CHARGER.get());
        simpleBlockItem(EIOBlocks.STIRLING_GENERATOR.get());
        simpleBlockItem(EIOBlocks.SAG_MILL.get());
        simpleBlockItem(EIOBlocks.SLICE_AND_SPLICE.get());
        simpleBlockItem(EIOBlocks.IMPULSE_HOPPER.get());
        simpleBlockItem(EIOBlocks.SOUL_BINDER.get());
        simpleBlockItem(EIOBlocks.CRAFTER.get());
        simpleBlockItem(EIOBlocks.DRAIN.get());
        simpleBlockItem(EIOBlocks.POWERED_SPAWNER.get());
        simpleBlockItem(EIOBlocks.SOUL_ENGINE.get());

        // Machines
        simpleBlockItem(EIOBlocks.WIRED_CHARGER.get());

        // Wireless Antennas
        simpleBlockItem(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get());
        simpleBlockItem(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());

        // Creative Power
        simpleBlockItem(EIOBlocks.CREATIVE_POWER.get());

        // Mind Killer
        simpleBlockItem(EIOBlocks.MIND_KILLER.get());

        // Vacuum Machines
        simpleBlockItem(EIOBlocks.VACUUM_CHEST.get());
        simpleBlockItem(EIOBlocks.XP_VACUUM.get());

        // Travel Anchors
        simpleBlockItem(EIOBlocks.TRAVEL_ANCHOR.get());
        simpleBlockItem(EIOBlocks.PAINTED_TRAVEL_ANCHOR.get());

        // Solar Panels
        for (var entry : EIOBlocks.SOLAR_PANELS.entrySet()) {
            String tierName = entry.getKey().name().toLowerCase(Locale.ROOT);
            withExistingParent(entry.getValue().getId().toString(), EnderIO.rl("item/photovoltaic_cell"))
                .texture("side", "block/" + tierName + "_side")
                .texture("panel", "block/" + tierName + "_top");
        }

        // Capacitor Banks - no item model needed (empty)

        // Niard
        simpleBlockItem(EIOBlocks.NIARD.get());

        // VAT
        simpleBlockItem(EIOBlocks.VAT.get());

        // Block Detector
        simpleBlockItem(EIOBlocks.BLOCK_DETECTOR.get());

        // Obelisks
        simpleBlockItem(EIOBlocks.XP_OBELISK.get());
        simpleBlockItem(EIOBlocks.FARMING_STATION.get());
        simpleBlockItem(EIOBlocks.INHIBITOR_OBELISK.get());
        simpleBlockItem(EIOBlocks.AVERSION_OBELISK.get());
        simpleBlockItem(EIOBlocks.RELOCATOR_OBELISK.get());
        simpleBlockItem(EIOBlocks.ATTRACTOR_OBELISK.get());
        simpleBlockItem(EIOBlocks.WEATHER_OBELISK.get());
    }

    private ItemModelBuilder fakeBlock(ItemLike item) {
        return fakeBlock(BuiltInRegistries.ITEM.getKey(item.asItem()));
    }

    private ItemModelBuilder fakeBlock(ResourceLocation item) {
        return withExistingParent(item.toString(), mcLoc("block/cube_all"))
            .texture("all", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public ItemModelBuilder flatBlockItem(Block item) {
        return flatBlockItem(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(item)));
    }

    public ItemModelBuilder flatBlockItem(ResourceLocation block) {
        return this.getBuilder(block.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(block.getNamespace(), "block/" + block.getPath()));
    }
}

package com.enderio.enderio.datagen.client.models;

import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlock;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.misc_blocks.ResettingLeverBlock;
import com.enderio.enderio.content.paint.block.PaintedStairBlock;
import com.enderio.enderio.datagen.client.models.block.ConduitModelBuilder;
import com.enderio.enderio.datagen.client.models.block.EIOBlockState;
import com.enderio.enderio.datagen.client.models.block.PaintedBlockModelBuilder;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.Locale;

public class EIOBlockStateProvider extends ModelProvider {
    public EIOBlockStateProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Alloys
        simpleBlock(blockModels, EIOBlocks.COPPER_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.DARK_STEEL_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.SOULARIUM_BLOCK.get());
        simpleBlock(blockModels, EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        simpleTranslucentBlock(blockModels, EIOBlocks.VOID_CHASSIS.get());
        simpleTranslucentBlock(blockModels, EIOBlocks.ENSOULED_CHASSIS.get());


        // Dark Steel Building Blocks
        blockModels.createNonTemplateHorizontalBlock(EIOBlocks.DARK_STEEL_LADDER.get());
        blockModels.registerSimpleFlatItemModel(EIOBlocks.DARK_STEEL_LADDER.get());

    }

    private void simpleBlock(BlockModelGenerators blockModels, Block block) {
        ResourceLocation resourcelocation = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(block), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourcelocation));
    }

    private void simpleTranslucentBlock(BlockModelGenerators blockModels, Block block) {
        var CUBE_TRANS = ExtendedModelTemplateBuilder.of(ModelTemplates.CUBE_ALL).renderType("translucent").build();
        ResourceLocation resourcelocation = CUBE_TRANS.create(block, TextureMapping.cube(block), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, resourcelocation));
    }

    public void createBars(BlockModelGenerators blockModels, Block block) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block, "_post_ends");
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block, "_post");
        ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(block, "_cap");
        ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(block, "_cap_alt");
        ResourceLocation resourcelocation4 = ModelLocationUtils.getModelLocation(block, "_side");
        ResourceLocation resourcelocation5 = ModelLocationUtils.getModelLocation(block, "_side_alt");
        blockModels.blockStateOutput
            .accept(
                MultiPartGenerator.multiPart(block)
                    .with(Variant.variant().with(VariantProperties.MODEL, resourcelocation))
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation1)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, true)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation2)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, true)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, true)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation3)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, true),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation4))
                    .with(
                        Condition.condition().term(BlockStateProperties.EAST, true),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation5))
                    .with(
                        Condition.condition().term(BlockStateProperties.WEST, true),
                        Variant.variant().with(VariantProperties.MODEL, resourcelocation5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
            );
        blockModels.registerSimpleFlatItemModel(block);
    }


    @Override
    protected void registerStatesAndModels() {

        paneBlockWithRenderType(EIOBlocks.DARK_STEEL_BARS.get(), blockTexture(EIOBlocks.DARK_STEEL_BARS.get()), blockTexture(EIOBlocks.DARK_STEEL_BARS.get()),
            "cutout_mipped");
        doorBlockWithRenderType(EIOBlocks.DARK_STEEL_DOOR.get(), modLoc("block/dark_steel_door_bottom"), modLoc("block/dark_steel_door_top"),
            "cutout");
        trapdoorBlockWithRenderType(EIOBlocks.DARK_STEEL_TRAPDOOR.get(), blockTexture(EIOBlocks.DARK_STEEL_TRAPDOOR.get()), true, "cutout");
        paneBlockWithRenderType(EIOBlocks.END_STEEL_BARS.get(), blockTexture(EIOBlocks.END_STEEL_BARS.get()), blockTexture(EIOBlocks.END_STEEL_BARS.get()),
            "cutout_mipped");
        simpleBlock(EIOBlocks.REINFORCED_OBSIDIAN.get());

        // Painted Blocks
        for (var pair : EIOBlocks.PAINTED_BLOCKS) {
            Block block = pair.left().get();
            Direction itemTextureDirection = Direction.NORTH;

            if (block instanceof PaintedStairBlock) {
                itemTextureDirection = Direction.WEST;
            }

            simpleBlock(pair.left().get(), models().getBuilder(name(pair.left().get()))
                .customLoader(PaintedBlockModelBuilder::begin)
                .reference(pair.right())
                .itemTextureRotation(itemTextureDirection)
                .end());
        }

        // Resetting Levers
        var baseModel = models().getExistingFile(mcLoc("block/lever"));
        var onModel = models().getExistingFile(mcLoc("block/lever_on"));

        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            VariantBlockStateBuilder vb = getVariantBuilder(lever.get());

            vb.forAllStates(blockState -> {
                ModelFile.ExistingModelFile model = blockState.getValue(ResettingLeverBlock.POWERED) ? onModel : baseModel;
                int rotationX =
                    blockState.getValue(LeverBlock.FACE) == AttachFace.CEILING ? 180 : blockState.getValue(LeverBlock.FACE) == AttachFace.WALL ? 90 : 0;
                Direction f = blockState.getValue(LeverBlock.FACING);
                int rotationY = f.get2DDataValue() * 90;
                if (blockState.getValue(LeverBlock.FACE) != AttachFace.CEILING) {
                    rotationY = (rotationY + 180) % 360;
                }
                return new ConfiguredModel[] { new ConfiguredModel(model, rotationX, rotationY, false) };
            });
        }

        // Glass Blocks
        ModelFile fusedQuartzModel = models().getExistingFile(modLoc("block/fused_quartz"));
        ModelFile clearGlassModel = models().getExistingFile(modLoc("block/clear_glass"));

        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var block : glassBlocks.getAllBlocks().toList()) {
                simpleBlock(block.get(), block.get().glassIdentifier().explosionResistance() ? fusedQuartzModel : clearGlassModel);
            }
        }

        // Miscellaneous
        simpleBlock(EIOBlocks.CONDUIT_BUNDLE.get(), models().getBuilder(EIOBlocks.CONDUIT_BUNDLE.getId().toString()).customLoader(ConduitModelBuilder::begin).end());
        chainBlock(EIOBlocks.SOUL_CHAIN.get());

        // This generates the models used for the cold fire blockstat ein our resources.
        // TODO: Generate the blockstate file :P
        String[] toCopy = { "fire_floor0", "fire_floor1", "fire_side0", "fire_side1", "fire_side_alt0", "fire_side_alt1", "fire_up0", "fire_up1",
            "fire_up_alt0", "fire_up_alt1" };

        for (String name : toCopy) {
            models().withExistingParent(name, mcLoc(name)).renderType("cutout");
        }

        simpleBlock(EIOBlocks.ENDERMAN_HEAD.get(), models().getExistingFile(mcLoc("block/skull")));
        simpleBlock(EIOBlocks.WALL_ENDERMAN_HEAD.get(), models().getExistingFile(mcLoc("block/skull")));
        simpleBlock(EIOBlocks.INDUSTRIAL_INSULATION.get());

        // Pressure Plates
        eioPressurePlateBlock(EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        eioPressurePlateBlock(EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get());
        eioPressurePlateBlock(EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        eioPressurePlateBlock(EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get());
        // Silent variants wrapping vanilla blocks
        silentPressurePlateBlock(EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get(), Blocks.OAK_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get(), Blocks.ACACIA_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get(), Blocks.DARK_OAK_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get(), Blocks.SPRUCE_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get(), Blocks.BIRCH_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get(), Blocks.JUNGLE_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get(), Blocks.CRIMSON_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get(), Blocks.WARPED_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get(), Blocks.STONE_PRESSURE_PLATE);
        silentPressurePlateBlock(EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(), Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        // Silent weighted
        silentWeightedPressurePlateBlock(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get(), (WeightedPressurePlateBlock) Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        silentWeightedPressurePlateBlock(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get(), (WeightedPressurePlateBlock) Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);

        registerMachineBlocks();
        registerFluidBlocks();
    }

    private void registerMachineBlocks() {
        // Fluid Tanks
        fluidTankBlock(EIOBlocks.FLUID_TANK.get());
        pressurizedFluidTankBlock(EIOBlocks.PRESSURIZED_FLUID_TANK.get());

        // Enchanter
        machineBlock(EIOBlocks.ENCHANTER.get());

        // Enderface
        simpleBlock(EIOBlocks.ENDERFACE.get(),
            models().cubeAll("enderface", EnderIO.rl("block/enderface")).renderType("translucent"));

        // Progress Machines
        progressMachineBlock(EIOBlocks.ALLOY_SMELTER.get());
        progressMachineBlock(EIOBlocks.PAINTING_MACHINE.get());
        progressMachineBlock(EIOBlocks.WIRELESS_CHARGER.get());
        progressMachineBlock(EIOBlocks.STIRLING_GENERATOR.get());
        progressMachineBlock(EIOBlocks.SAG_MILL.get());
        progressMachineBlock(EIOBlocks.SLICE_AND_SPLICE.get());
        progressMachineBlock(EIOBlocks.IMPULSE_HOPPER.get());
        progressMachineBlock(EIOBlocks.SOUL_BINDER.get());
        progressMachineBlock(EIOBlocks.CRAFTER.get());
        progressMachineBlock(EIOBlocks.DRAIN.get());
        progressMachineBlock(EIOBlocks.POWERED_SPAWNER.get());
        progressMachineBlock(EIOBlocks.SOUL_ENGINE.get());

        // Machines
        machineBlock(EIOBlocks.WIRED_CHARGER.get());

        // Wireless Antennas
        simpleBlockWithExistingModel(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get());
        simpleBlockWithExistingModel(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());

        // Creative Power
        simpleBlock(EIOBlocks.CREATIVE_POWER.get());

        // Mind Killer
        simpleBlockWithExistingModel(EIOBlocks.MIND_KILLER.get());

        // Vacuum Machines
        simpleBlockWithExistingModel(EIOBlocks.VACUUM_CHEST.get());
        simpleBlockWithExistingModel(EIOBlocks.XP_VACUUM.get());

        // Travel Anchors
        simpleBlockWithExistingModel(EIOBlocks.TRAVEL_ANCHOR.get());
        EIOBlockState.paintedBlock("painted_travel_anchor", this, EIOBlocks.PAINTED_TRAVEL_ANCHOR.get(),
            Blocks.DIRT, null);

        // Solar Panels
        for (var entry : EIOBlocks.SOLAR_PANELS.entrySet()) {
            solarPanelBlock(entry.getValue().get(), entry.getKey());
        }

        // Capacitor Banks
        for (var capacitorBank : EIOBlocks.CAPACITOR_BANKS.values()) {
            simpleBlockWithExistingModel(capacitorBank.get());
        }

        // Niard
        machineBlock(EIOBlocks.NIARD.get());

        // VAT
        machineBlock(EIOBlocks.VAT.get());

        // Block Detector
        simpleBlockWithExistingModel(EIOBlocks.BLOCK_DETECTOR.get());

        // Obelisks
        simpleBlockWithExistingModel(EIOBlocks.XP_OBELISK.get());
        simpleBlockWithExistingModel(EIOBlocks.FARMING_STATION.get());
        simpleBlockWithExistingModel(EIOBlocks.INHIBITOR_OBELISK.get());
        simpleBlockWithExistingModel(EIOBlocks.AVERSION_OBELISK.get());
        simpleBlockWithExistingModel(EIOBlocks.RELOCATOR_OBELISK.get());
        simpleBlockWithExistingModel(EIOBlocks.ATTRACTOR_OBELISK.get());
        simpleBlockWithExistingModel(EIOBlocks.WEATHER_OBELISK.get());
    }

    private void registerFluidBlocks() {
        var water = models().getExistingFile(mcLoc("block/water"));
        for (var fluidBlock : EIOFluids.FLUIDS.blocksRegister().getEntries()) {
            simpleBlock(fluidBlock.get(), water);
        }
    }

    private void ladderBlock(Block block) {
        horizontalBlock(block, models()
            .withExistingParent(name(block), mcLoc("block/ladder"))
            .renderType(mcLoc("cutout_mipped"))
            .texture("particle", blockTexture(block))
            .texture("texture", blockTexture(block)));
    }

    private void chainBlock(ChainBlock block) {
        var model = models()
            .withExistingParent(name(block), mcLoc("block/chain"))
            .renderType(mcLoc("cutout_mipped"))
            .texture("particle", blockTexture(block))
            .texture("all", blockTexture(block));

        axisBlock(block, model, model);
    }

    private void fluidTankBlock(Block block) {
        String name = name(block);
        ModelFile model = models()
            .getBuilder(name)
            .customLoader(CompositeModelBuilder::begin)
            .child("tank",
                ModelHelper.getExistingAsBuilder(models(),
                    EnderIO.rl(String.format("block/%s_body", name))))
            .child("overlay", ModelHelper.getExistingAsBuilder(models(), EnderIO.rl("block/io_overlay")))
            .end()
            .texture("particle", EnderIO.rl("block/machine_side"));
        horizontalBlock(block, model);
    }

    private void pressurizedFluidTankBlock(Block block) {
        String name = name(block);
        ModelFile model = models()
            .withExistingParent(name, mcLoc("block/block"))
            .customLoader(CompositeModelBuilder::begin)
            .child("tank",
                ModelHelper.getExistingAsBuilder(models(),
                    EnderIO.rl(String.format("block/%s_body", name))))
            .child("overlay", ModelHelper.getExistingAsBuilder(models(), EnderIO.rl("block/io_overlay")))
            .end()
            .texture("particle", EnderIO.rl("block/machine_side"));
        horizontalBlock(block, model);
    }

    private void machineBlock(Block block) {
        String ns = key(block).getNamespace();
        String path = key(block).getPath();
        ModelFile model = wrapMachineModel(block,
            ResourceLocation.fromNamespaceAndPath(ns, "block/" + path));
        getVariantBuilder(block)
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    private void progressMachineBlock(Block block) {
        String ns = key(block).getNamespace();
        String path = key(block).getPath();
        var unpowered = ResourceLocation.fromNamespaceAndPath(ns, "block/" + path);
        var powered = ResourceLocation.fromNamespaceAndPath(ns, "block/" + path + "_active");

        var unpoweredModel = wrapMachineModel(block, unpowered);
        var poweredModel = wrapMachineModel(block, powered);
        getVariantBuilder(block)
            .forAllStates(state -> ConfiguredModel.builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? poweredModel : unpoweredModel)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    private void solarPanelBlock(SolarPanelBlock block, SolarPanelTier tier) {
        String name = name(block);
        String tierName = tier.name().toLowerCase(Locale.ROOT);

        var baseModel = models()
            .withExistingParent(name + "_base", EnderIO.rl("block/photovoltaic_cell_base"))
            .texture("panel", "block/" + tierName + "_top")
            .texture("side", "block/" + tierName + "_side");
        var sideModel = models()
            .withExistingParent(name + "_side", EnderIO.rl("block/photovoltaic_cell_side"))
            .texture("side", "block/" + tierName + "_side");
        var cornerModel = models()
            .withExistingParent(name + "_corner", EnderIO.rl("block/photovoltaic_cell_corner"))
            .texture("side", "block/" + tierName + "_side");

        var builder = getMultipartBuilder(block);
        builder.part().modelFile(baseModel).addModel();
        builder.part().modelFile(sideModel).addModel().condition(SolarPanelBlock.NORTH, true);
        builder.part().modelFile(sideModel).rotationY(90).addModel().condition(SolarPanelBlock.EAST, true);
        builder.part().modelFile(sideModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH, true);
        builder.part().modelFile(sideModel).rotationY(270).addModel().condition(SolarPanelBlock.WEST, true);
        builder.part().modelFile(cornerModel).addModel().condition(SolarPanelBlock.NORTH_EAST, true);
        builder.part().modelFile(cornerModel).rotationY(90).addModel().condition(SolarPanelBlock.SOUTH_EAST, true);
        builder.part().modelFile(cornerModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH_WEST, true);
        builder.part().modelFile(cornerModel).rotationY(270).addModel().condition(SolarPanelBlock.NORTH_WEST, true);
    }

    private void eioPressurePlateBlock(Block block) {
        String name = name(block);
        ResourceLocation texture;
        if (name.startsWith("silent_")) {
            texture = EnderIO.rl("block/" + name.substring(7));
        } else {
            texture = blockTexture(block);
        }

        ModelFile down = models().withExistingParent(name + "_down", mcLoc("block/pressure_plate_down")).texture("texture", texture);
        ModelFile up = models().withExistingParent(name, mcLoc("block/pressure_plate_up")).texture("texture", texture);
        VariantBlockStateBuilder vb = getVariantBuilder(block);
        vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(down));
        vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(up));
    }

    private void silentPressurePlateBlock(Block block, Block vanillaBlock) {
        ResourceLocation upModelLoc = BuiltInRegistries.BLOCK.getKey(vanillaBlock);
        ResourceLocation downModelLoc = ResourceLocation.fromNamespaceAndPath(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");
        VariantBlockStateBuilder vb = getVariantBuilder(block);
        vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(models().getExistingFile(downModelLoc)));
        vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(models().getExistingFile(upModelLoc)));
    }

    private void silentWeightedPressurePlateBlock(Block block, WeightedPressurePlateBlock vanillaBlock) {
        ResourceLocation upModelLoc = BuiltInRegistries.BLOCK.getKey(vanillaBlock);
        ResourceLocation downModelLoc = ResourceLocation.fromNamespaceAndPath(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");
        getVariantBuilder(block).forAllStates(blockState -> {
            if (blockState.getValue(net.minecraft.world.level.block.WeightedPressurePlateBlock.POWER) == 0) {
                return new ConfiguredModel[] { new ConfiguredModel(models().getExistingFile(upModelLoc)) };
            }
            return new ConfiguredModel[] { new ConfiguredModel(models().getExistingFile(downModelLoc)) };
        });
    }

    private ModelFile wrapMachineModel(Block block, ResourceLocation model) {
        String blockName = name(block);
        return models()
            .withExistingParent(model.getPath() + "_combined", mcLoc("block/block"))
            .texture("particle",
                blockName.equals("enchanter") ? EnderIO.rl("block/dark_steel_pressure_plate")
                    : ResourceLocation.fromNamespaceAndPath(model.getNamespace(),
                    "block/" + blockName + "_front"))
            .customLoader(CompositeModelBuilder::begin)
            .child("machine", ModelHelper.getExistingAsBuilder(models(), model))
            .child("overlay", ModelHelper.getExistingAsBuilder(models(), EnderIO.rl("block/io_overlay")))
            .end();
    }

    private void simpleBlockWithExistingModel(Block block) {
        simpleBlock(block, models().getExistingFile(EnderIO.rl("block/" + name(block))));
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}

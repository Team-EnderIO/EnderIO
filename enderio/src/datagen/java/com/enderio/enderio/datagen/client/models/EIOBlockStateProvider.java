package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBlockStateModel;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlock;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelTier;
import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlock;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import net.neoforged.neoforge.client.model.generators.loaders.CompositeModelBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.stream.Stream;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public class EIOBlockStateProvider extends ModelProvider {
    public EIOBlockStateProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    public String getName() {
        return "Ender IO Block Model Definitions";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Alloys
        blockModels.createTrivialCube(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.DARK_STEEL_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.SOULARIUM_BLOCK.get());
        blockModels.createTrivialCube(EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        simpleTranslucentBlock(blockModels, EIOBlocks.VOID_CHASSIS.get());
        simpleTranslucentBlock(blockModels, EIOBlocks.ENSOULED_CHASSIS.get());


        // Dark Steel Building Blocks
        blockModels.createNonTemplateHorizontalBlock(EIOBlocks.DARK_STEEL_LADDER.get());
        blockModels.registerSimpleFlatItemModel(EIOBlocks.DARK_STEEL_LADDER.get());
        createBars(blockModels, EIOBlocks.DARK_STEEL_BARS.get());
        createBars(blockModels, EIOBlocks.END_STEEL_BARS.get());
        blockModels.createDoor(EIOBlocks.DARK_STEEL_DOOR.get());
        blockModels.createTrapdoor(EIOBlocks.DARK_STEEL_TRAPDOOR.get());
        blockModels.createTrivialCube(EIOBlocks.REINFORCED_OBSIDIAN.get());

        for (var lever : EIOBlocks.RESETTING_LEVERS) {
            createLever(blockModels, lever.get());
        }

        // Glass Blocks
        ResourceLocation fusedQuartzModel = EnderIO.rl("block/fused_quartz");
        ResourceLocation clearGlassModel = EnderIO.rl("block/clear_glass");

        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var block : glassBlocks.getAllBlocks().toList()) {
                simpleBlockWithModel(blockModels, block.get(), block.get().glassIdentifier().explosionResistance() ? fusedQuartzModel : clearGlassModel);
            }
        }

        // Miscellaneous
        blockModels.createAxisAlignedPillarBlockCustomModel(EIOBlocks.SOUL_CHAIN.get(),  plainVariant(ModelLocationUtils.getModelLocation(EIOBlocks.SOUL_CHAIN.get())));
        blockModels.registerSimpleFlatItemModel(EIOBlocks.SOUL_CHAIN.get());

        ResourceLocation resourcelocation = ModelLocationUtils.decorateItemModelLocation("template_skull");
        blockModels.createHead(EIOBlocks.ENDERMAN_HEAD.get(), EIOBlocks.WALL_ENDERMAN_HEAD.get(), EnderSkullBlock.EIOSkulls.ENDERMAN, resourcelocation);
        blockModels.createTrivialCube(EIOBlocks.INDUSTRIAL_INSULATION.get());

        // Pressure Plates
        pressurePlate(blockModels, EIOBlocks.DARK_STEEL_PRESSURE_PLATE.get());
        pressurePlate(blockModels, EIOBlocks.SILENT_DARK_STEEL_PRESSURE_PLATE.get());
        pressurePlate(blockModels, EIOBlocks.SOULARIUM_PRESSURE_PLATE.get());
        pressurePlate(blockModels, EIOBlocks.SILENT_SOULARIUM_PRESSURE_PLATE.get());
        // Silent variants wrapping vanilla blocks
        silentPressurePlate(blockModels, EIOBlocks.SILENT_OAK_PRESSURE_PLATE.get(), Blocks.OAK_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_ACACIA_PRESSURE_PLATE.get(), Blocks.ACACIA_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_DARK_OAK_PRESSURE_PLATE.get(), Blocks.DARK_OAK_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_SPRUCE_PRESSURE_PLATE.get(), Blocks.SPRUCE_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_BIRCH_PRESSURE_PLATE.get(), Blocks.BIRCH_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_JUNGLE_PRESSURE_PLATE.get(), Blocks.JUNGLE_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_CRIMSON_PRESSURE_PLATE.get(), Blocks.CRIMSON_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_WARPED_PRESSURE_PLATE.get(), Blocks.WARPED_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_STONE_PRESSURE_PLATE.get(), Blocks.STONE_PRESSURE_PLATE);
        silentPressurePlate(blockModels, EIOBlocks.SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(), Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        // Silent weighted
        blockModels.createWeightedPressurePlate(EIOBlocks.SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE.get(), Blocks.IRON_BLOCK);
        blockModels.createWeightedPressurePlate(EIOBlocks.SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE.get(), Blocks.GOLD_BLOCK);

        registerMachineBlocks(blockModels);
        registerFluidBlocks(blockModels);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(EIOBlocks.CONDUIT_BUNDLE.get(),
            MultiVariant.of(new CustomBlockStateModelBuilder.Simple(ConduitBlockStateModel.Unbaked.INSTANCE))));

    }

    private void simpleBlockWithModel(BlockModelGenerators blockModels, Block block, ResourceLocation resourcelocation) {
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, plainVariant(resourcelocation)));
    }

    private void simpleTranslucentBlock(BlockModelGenerators blockModels, Block block) {
        var CUBE_TRANS = TexturedModel.createDefault(TextureMapping::cube, ModelTemplates.CUBE_ALL.extend().renderType("translucent").build());
        blockModels.createTrivialBlock(block, CUBE_TRANS);
    }

    public void createBars(BlockModelGenerators blockModels, Block block) {
        //TODO rendertype
        MultiVariant multivariant = plainVariant(ModelLocationUtils.getModelLocation(block, "_post_ends"));
        MultiVariant multivariant1 = plainVariant(ModelLocationUtils.getModelLocation(block, "_post"));
        MultiVariant multivariant2 = plainVariant(ModelLocationUtils.getModelLocation(block, "_cap"));
        MultiVariant multivariant3 = plainVariant(ModelLocationUtils.getModelLocation(block, "_cap_alt"));
        MultiVariant multivariant4 = plainVariant(ModelLocationUtils.getModelLocation(block, "_side"));
        MultiVariant multivariant5 = plainVariant(ModelLocationUtils.getModelLocation(block, "_side_alt"));
        blockModels.blockStateOutput
            .accept(
                MultiPartGenerator.multiPart(block)
                    .with(multivariant)
                    .with(
                        condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        multivariant1
                    )
                    .with(
                        condition()
                            .term(BlockStateProperties.NORTH, true)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        multivariant2
                    )
                    .with(
                        condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, true)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        multivariant2.with(Y_ROT_90)
                    )
                    .with(
                        condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, true)
                            .term(BlockStateProperties.WEST, false),
                        multivariant3
                    )
                    .with(
                        condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, true),
                        multivariant3.with(Y_ROT_90)
                    )
                    .with(condition().term(BlockStateProperties.NORTH, true), multivariant4)
                    .with(condition().term(BlockStateProperties.EAST, true), multivariant4.with(Y_ROT_90))
                    .with(condition().term(BlockStateProperties.SOUTH, true), multivariant5)
                    .with(condition().term(BlockStateProperties.WEST, true), multivariant5.with(Y_ROT_90))
            );
        blockModels.registerSimpleFlatItemModel(block);
    }

    public void createLever(BlockModelGenerators blockModels, Block block) {
        MultiVariant multivariant = plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER));
        MultiVariant multivariant1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on"));
        blockModels.registerSimpleFlatItemModel(block);
        blockModels.blockStateOutput
            .accept(
                MultiVariantGenerator.dispatch(block)
                    .with(createBooleanModelDispatch(BlockStateProperties.POWERED, multivariant, multivariant1))
                    .with(
                        PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                            .select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180))
                            .select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270))
                            .select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180)
                            .select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90))
                            .select(AttachFace.FLOOR, Direction.NORTH, NOP)
                            .select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90)
                            .select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180)
                            .select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270)
                            .select(AttachFace.WALL, Direction.NORTH, X_ROT_90)
                            .select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90))
                            .select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180))
                            .select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270))
                    )
            );
    }

    public void pressurePlate(BlockModelGenerators blockModelGenerators, Block block) {
        String name = name(block);
        ResourceLocation texture;
        if (name.startsWith("silent_")) {
            texture = EnderIO.rl("block/" + name.substring(7));
        } else {
            texture = ModelLocationUtils.getModelLocation(block);
        }
        ResourceLocation resourcelocation = ModelTemplates.PRESSURE_PLATE_UP.create(block, TextureMapping.cube(texture), blockModelGenerators.modelOutput);
        ResourceLocation resourcelocation1 = ModelTemplates.PRESSURE_PLATE_DOWN.create(block, TextureMapping.cube(texture), blockModelGenerators.modelOutput);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(block, plainVariant(resourcelocation), plainVariant(resourcelocation1)));
    }

    public void silentPressurePlate(BlockModelGenerators blockModelGenerators, Block block, Block vanilla) {
        ResourceLocation texture = ModelLocationUtils.getModelLocation(vanilla);
        ResourceLocation resourcelocation = ModelTemplates.PRESSURE_PLATE_UP.create(block, TextureMapping.cube(texture), blockModelGenerators.modelOutput);
        ResourceLocation resourcelocation1 = ModelTemplates.PRESSURE_PLATE_DOWN.create(block, TextureMapping.cube(texture), blockModelGenerators.modelOutput);
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(block, plainVariant(resourcelocation), plainVariant(resourcelocation1)));
    }

//    @Override
//    protected void registerStatesAndModels() {
//
//        // Painted Blocks
//        for (var pair : EIOBlocks.PAINTED_BLOCKS) {
//            Block block = pair.left().get();
//            Direction itemTextureDirection = Direction.NORTH;
//
//            if (block instanceof PaintedStairBlock) {
//                itemTextureDirection = Direction.WEST;
//            }
//
//            simpleBlock(pair.left().get(), models().getBuilder(name(pair.left().get()))
//                .customLoader(PaintedBlockModelBuilder::begin)
//                .reference(pair.right())
//                .itemTextureRotation(itemTextureDirection)
//                .end());
//        }
//
//        // Miscellaneous
//        simpleBlock(EIOBlocks.CONDUIT_BUNDLE.get(), models().getBuilder(EIOBlocks.CONDUIT_BUNDLE.getId().toString()).customLoader(ConduitModelBuilder::begin).end());
//
//        // This generates the models used for the cold fire blockstat ein our resources.
//        // TODO: Generate the blockstate file :P
//        String[] toCopy = { "fire_floor0", "fire_floor1", "fire_side0", "fire_side1", "fire_side_alt0", "fire_side_alt1", "fire_up0", "fire_up1",
//            "fire_up_alt0", "fire_up_alt1" };
//
//        for (String name : toCopy) {
//            models().withExistingParent(name, mcLoc(name)).renderType("cutout");
//        }
//    }

    private void registerMachineBlocks(BlockModelGenerators blockModels) {
        // Fluid Tanks
        fluidTankBlock(blockModels, EIOBlocks.FLUID_TANK.get());
        fluidTankBlock(blockModels, EIOBlocks.PRESSURIZED_FLUID_TANK.get());

        // Enchanter
        machineBlock(blockModels, EIOBlocks.ENCHANTER.get());

        // Enderface
//        simpleBlock(EIOBlocks.ENDERFACE.get(),
//            models().cubeAll("enderface", EnderIO.rl("block/enderface")).renderType("translucent"));

        // Progress Machines
        progressMachineBlock(blockModels, EIOBlocks.ALLOY_SMELTER.get());
        progressMachineBlock(blockModels, EIOBlocks.PAINTING_MACHINE.get());
        progressMachineBlock(blockModels, EIOBlocks.WIRELESS_CHARGER.get());
        progressMachineBlock(blockModels, EIOBlocks.STIRLING_GENERATOR.get());
        progressMachineBlock(blockModels, EIOBlocks.SAG_MILL.get());
        progressMachineBlock(blockModels, EIOBlocks.SLICE_AND_SPLICE.get());
        progressMachineBlock(blockModels, EIOBlocks.IMPULSE_HOPPER.get());
        progressMachineBlock(blockModels, EIOBlocks.SOUL_BINDER.get());
        progressMachineBlock(blockModels, EIOBlocks.CRAFTER.get());
        progressMachineBlock(blockModels, EIOBlocks.DRAIN.get());
        progressMachineBlock(blockModels, EIOBlocks.POWERED_SPAWNER.get());
        progressMachineBlock(blockModels, EIOBlocks.SOUL_ENGINE.get());

        // Machines
        machineBlock(blockModels, EIOBlocks.WIRED_CHARGER.get());

        // Wireless Antennas
        blockModels.createNonTemplateModelBlock(EIOBlocks.WIRELESS_CHARGER_ANTENNA.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.WIRELESS_CHARGER_ANTENNA_ADVANCED.get());

        // Creative Power
        blockModels.createTrivialCube(EIOBlocks.CREATIVE_POWER.get());

        // Mind Killer
        blockModels.createNonTemplateModelBlock(EIOBlocks.MIND_KILLER.get());

        // Vacuum Machines
        blockModels.createNonTemplateModelBlock(EIOBlocks.VACUUM_CHEST.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.XP_VACUUM.get());

        // Travel Anchors
        blockModels.createNonTemplateModelBlock(EIOBlocks.TRAVEL_ANCHOR.get());
//        EIOBlockState.paintedBlock("painted_travel_anchor", this, EIOBlocks.PAINTED_TRAVEL_ANCHOR.get(),
//            Blocks.DIRT, null);

        // Solar Panels
        for (var entry : EIOBlocks.SOLAR_PANELS.entrySet()) {
            solarPanelBlock(entry.getValue().get(), entry.getKey());
        }

        // Capacitor Banks
        for (var capacitorBank : EIOBlocks.CAPACITOR_BANKS.values()) {
            blockModels.createNonTemplateModelBlock(capacitorBank.get());
        }

        // Niard
        machineBlock(blockModels, EIOBlocks.NIARD.get());

        // VAT
        machineBlock(blockModels, EIOBlocks.VAT.get());

        // Block Detector
        blockModels.createNonTemplateModelBlock(EIOBlocks.BLOCK_DETECTOR.get());

        // Obelisks
        blockModels.createNonTemplateModelBlock(EIOBlocks.XP_OBELISK.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.FARMING_STATION.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.INHIBITOR_OBELISK.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.AVERSION_OBELISK.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.RELOCATOR_OBELISK.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.ATTRACTOR_OBELISK.get());
        blockModels.createNonTemplateModelBlock(EIOBlocks.WEATHER_OBELISK.get());
    }

    private void registerFluidBlocks(BlockModelGenerators blockModels) {
        for (var fluidBlock : EIOFluids.FLUIDS.blocksRegister().getEntries()) {
            blockModels.createNonTemplateModelBlock(fluidBlock.get(), Blocks.WATER);
        }
    }

    private void fluidTankBlock(BlockModelGenerators blockModelGenerators, Block block) {
        String name = name(block);
        ResourceLocation tank =  EnderIO.rl(String.format("block/%s_body", name));
        ResourceLocation overlay =  EnderIO.rl("block/io_overlay");

        blockModelGenerators.blockStateOutput.accept(
            MultiPartGenerator.multiPart(block)
                .with(plainVariant(tank))
                .with(plainVariant(overlay)));
    }

    private void machineBlock(BlockModelGenerators blockModelGenerators, Block block) {
        ResourceLocation model = wrapMachineModel(blockModelGenerators, block, ModelLocationUtils.getModelLocation(block));
        MultiVariantGenerator.dispatch(block, plainVariant(model))
            .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
    }

    private void progressMachineBlock(BlockModelGenerators blockModelGenerators, Block block) {
        String ns = key(block).getNamespace();
        String path = key(block).getPath();
        var powered = ResourceLocation.fromNamespaceAndPath(ns, "block/" + path + "_active");

        var unpoweredModel = wrapMachineModel(blockModelGenerators, block, ModelLocationUtils.getModelLocation(block));
        var poweredModel = wrapMachineModel(blockModelGenerators, block, powered);
        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(createBooleanModelDispatch(ProgressMachineBlock.POWERED, plainVariant(poweredModel), plainVariant(unpoweredModel)))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }

    private void solarPanelBlock(SolarPanelBlock block, SolarPanelTier tier) {
//        String name = name(block);
//        String tierName = tier.name().toLowerCase(Locale.ROOT);
//
//        var baseModel = models()
//            .withExistingParent(name + "_base", EnderIO.rl("block/photovoltaic_cell_base"))
//            .texture("panel", "block/" + tierName + "_top")
//            .texture("side", "block/" + tierName + "_side");
//        var sideModel = models()
//            .withExistingParent(name + "_side", EnderIO.rl("block/photovoltaic_cell_side"))
//            .texture("side", "block/" + tierName + "_side");
//        var cornerModel = models()
//            .withExistingParent(name + "_corner", EnderIO.rl("block/photovoltaic_cell_corner"))
//            .texture("side", "block/" + tierName + "_side");
//
//        var builder = getMultipartBuilder(block);
//        builder.part().modelFile(baseModel).addModel();
//        builder.part().modelFile(sideModel).addModel().condition(SolarPanelBlock.NORTH, true);
//        builder.part().modelFile(sideModel).rotationY(90).addModel().condition(SolarPanelBlock.EAST, true);
//        builder.part().modelFile(sideModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH, true);
//        builder.part().modelFile(sideModel).rotationY(270).addModel().condition(SolarPanelBlock.WEST, true);
//        builder.part().modelFile(cornerModel).addModel().condition(SolarPanelBlock.NORTH_EAST, true);
//        builder.part().modelFile(cornerModel).rotationY(90).addModel().condition(SolarPanelBlock.SOUTH_EAST, true);
//        builder.part().modelFile(cornerModel).rotationY(180).addModel().condition(SolarPanelBlock.SOUTH_WEST, true);
//        builder.part().modelFile(cornerModel).rotationY(270).addModel().condition(SolarPanelBlock.NORTH_WEST, true);
    }

    private ResourceLocation wrapMachineModel(BlockModelGenerators blockModelGenerators, Block block, ResourceLocation model) {
        return ExtendedModelTemplateBuilder.builder()
            .customLoader(CompositeModelBuilder::new, builder -> {
                builder.child("machine", model);
                //builder.child("overlay", EnderIO.rl("block/io_overlay"));
            }).build()
            .create(EnderIO.rl(model.getPath() + "_combined"), TextureMapping.cube(block), blockModelGenerators.modelOutput);
    }

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}

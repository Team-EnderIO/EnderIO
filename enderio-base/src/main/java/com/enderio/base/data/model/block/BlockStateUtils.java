package com.enderio.base.data.model.block;

import com.enderio.base.common.block.ColdFireBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class BlockStateUtils {

    public static void fireModel(DataGenContext<Block, ? extends ColdFireBlock> ctx, RegistrateBlockstateProvider prov) {

        MultiPartBlockStateBuilder multiPartBuilder = prov.getMultipartBuilder(ctx.get());
        ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> firstConfiguredPartBuilder = prov.getMultipartBuilder(ctx.get()).part();
        BlockModelProvider blockModelProvider = prov.models();
        List<ModelFile.ExistingModelFile> bottom = createFloorFireModels(Blocks.FIRE).stream().map(blockModelProvider::getExistingFile).toList();
        List<ModelFile.ExistingModelFile> side = createSideFireModels(Blocks.FIRE).stream().map(blockModelProvider::getExistingFile).toList();
        List<ModelFile.ExistingModelFile> top = createTopFireModels(Blocks.FIRE).stream().map(blockModelProvider::getExistingFile).toList();

        ConfiguredModel.Builder<MultiPartBlockStateBuilder.PartBuilder> configuredPartBuilder = null;
        for (ModelFile.ExistingModelFile file: bottom) {
            firstConfiguredPartBuilder.modelFile(file);
            configuredPartBuilder = firstConfiguredPartBuilder.nextModel();
        }
        MultiPartBlockStateBuilder.PartBuilder partBuilder = firstConfiguredPartBuilder.addModel();
        for (Direction direction: Direction.values()) {
            if (direction == Direction.DOWN)
                continue;
            partBuilder.condition(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), false);
        }
        for (Direction direction: Direction.values()) {
            if (direction == Direction.DOWN || direction == Direction.UP)
                continue;
            configuredPartBuilder = multiPartBuilder.part();
            int rotY = switch (direction) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            for (ModelFile.ExistingModelFile file: side) {
                if (rotY != 0)
                    configuredPartBuilder.modelFile(file).rotationY(rotY);
                configuredPartBuilder = configuredPartBuilder.nextModel();
            }
            partBuilder = configuredPartBuilder.addModel();
            partBuilder.condition(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
        }
        for (ModelFile.ExistingModelFile file: top) {
            configuredPartBuilder.modelFile(file);
            configuredPartBuilder = configuredPartBuilder.nextModel();
        }
        partBuilder = configuredPartBuilder.addModel();
        partBuilder.condition(PipeBlock.PROPERTY_BY_DIRECTION.get(Direction.UP), true);
        partBuilder.end();
    }


    private static List<ResourceLocation> createFloorFireModels(Block block) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block, "_floor0");
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block, "_floor1");
        return List.of(resourcelocation, resourcelocation1);
    }

    private static List<ResourceLocation> createSideFireModels(Block block) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block, "_side0");
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block, "_side1");
        ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(block, "_side_alt0");
        ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(block, "_side_alt1");
        return List.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }

    private static List<ResourceLocation> createTopFireModels(Block block) {
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block, "_up0");
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(block, "_up1");
        ResourceLocation resourcelocation2 = ModelLocationUtils.getModelLocation(block, "_up_alt0");
        ResourceLocation resourcelocation3 = ModelLocationUtils.getModelLocation(block, "_up_alt1");
        return List.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }

    public static void paneBlock(DataGenContext<Block, ? extends IronBarsBlock> ctx, RegistrateBlockstateProvider cons) {
        cons.paneBlock(ctx.get(), cons.models().panePost(ctx.getName().concat("_post"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneSide(ctx.getName().concat("_side"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneSideAlt(ctx.getName().concat("_side_alt"), cons.blockTexture(ctx.get()), cons.blockTexture(ctx.get())),
            cons.models().paneNoSide(ctx.getName().concat("_no_side"), cons.blockTexture(ctx.get())),
            cons.models().paneNoSideAlt(ctx.getName().concat("_no_side_alt"), cons.blockTexture(ctx.get())));
    }
}

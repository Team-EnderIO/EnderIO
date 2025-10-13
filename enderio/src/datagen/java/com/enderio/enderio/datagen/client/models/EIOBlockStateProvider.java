package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.misc_blocks.ResettingLeverBlock;
import com.enderio.enderio.data.model.block.ConduitModelBuilder;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EIOBlockStateProvider extends BlockStateProvider {
    public EIOBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, EnderIO.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Alloys
        simpleBlock(EIOBlocks.COPPER_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.ENERGETIC_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.VIBRANT_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.REDSTONE_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.CONDUCTIVE_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.PULSATING_ALLOY_BLOCK.get());
        simpleBlock(EIOBlocks.DARK_STEEL_BLOCK.get());
        simpleBlock(EIOBlocks.SOULARIUM_BLOCK.get());
        simpleBlock(EIOBlocks.END_STEEL_BLOCK.get());

        // Chassis
        simpleTranslucentBlock(EIOBlocks.VOID_CHASSIS.get());
        simpleTranslucentBlock(EIOBlocks.ENSOULED_CHASSIS.get());

        // Dark Steel Building Blocks
        ladderBlock(EIOBlocks.DARK_STEEL_LADDER.get());
        paneBlockWithRenderType(EIOBlocks.DARK_STEEL_BARS.get(), blockTexture(EIOBlocks.DARK_STEEL_BARS.get()), blockTexture(EIOBlocks.DARK_STEEL_BARS.get()),
            "cutout_mipped");
        doorBlockWithRenderType(EIOBlocks.DARK_STEEL_DOOR.get(), modLoc("block/dark_steel_door_bottom"), modLoc("block/dark_steel_door_top"),
            "cutout");
        trapdoorBlockWithRenderType(EIOBlocks.DARK_STEEL_TRAPDOOR.get(), blockTexture(EIOBlocks.DARK_STEEL_TRAPDOOR.get()), true, "cutout");
        paneBlockWithRenderType(EIOBlocks.END_STEEL_BARS.get(), blockTexture(EIOBlocks.END_STEEL_BARS.get()), blockTexture(EIOBlocks.END_STEEL_BARS.get()),
            "cutout_mipped");
        simpleBlock(EIOBlocks.REINFORCED_OBSIDIAN.get());

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
    }

    private void simpleTranslucentBlock(Block block) {
        simpleBlock(block, models()
            .cubeAll(name(block), blockTexture(block))
            .renderType(mcLoc("translucent")));
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

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}

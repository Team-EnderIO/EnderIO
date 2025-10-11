package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.data.model.block.ConduitModelBuilder;
import com.enderio.enderio.init.ConduitBlocks;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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

        // Conduit
        simpleBlock(ConduitBlocks.CONDUIT_BUNDLE.get(), models().getBuilder(ConduitBlocks.CONDUIT_BUNDLE.getId().toString()).customLoader(ConduitModelBuilder::begin).end());
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

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}

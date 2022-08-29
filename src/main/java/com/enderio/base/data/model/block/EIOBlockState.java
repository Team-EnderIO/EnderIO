package com.enderio.base.data.model.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;

public class EIOBlockState {

    public static void paneBlock(DataGenContext<Block, ? extends IronBarsBlock> ctx, RegistrateBlockstateProvider prov) {
        prov.paneBlock(ctx.get(),
            prov.models()
                .panePost(ctx.getName().concat("_post"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSide(ctx.getName().concat("_side"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneSideAlt(ctx.getName().concat("_side_alt"), prov.blockTexture(ctx.get()), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSide(ctx.getName().concat("_no_side"), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")),
            prov.models()
                .paneNoSideAlt(ctx.getName().concat("_no_side_alt"), prov.blockTexture(ctx.get()))
                .renderType(prov.mcLoc("cutout_mipped")));
    }
}

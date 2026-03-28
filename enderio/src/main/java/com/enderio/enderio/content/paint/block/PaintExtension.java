package com.enderio.enderio.content.paint.block;

import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class PaintExtension implements IClientBlockExtensions {

    public static PaintExtension INSTANCE = new PaintExtension();

    @Override
    public void collectDynamicTintValues(BlockState state, BlockAndTintGetter level, BlockPos pos, IntList tintValues) {
        if (level.getBlockEntity(pos) instanceof PaintedBlockEntity) {
            var data = level.getModelData(pos).get(SinglePaintedBlockEntity.PAINT);
            if (data != null) {
                var colors = Minecraft.getInstance().getBlockColors().getTintSources(data.defaultBlockState());
                for (var color : colors) {
                    tintValues.add(color.colorInWorld(data.defaultBlockState(), level, pos));
                }
            }
        }
    }
}

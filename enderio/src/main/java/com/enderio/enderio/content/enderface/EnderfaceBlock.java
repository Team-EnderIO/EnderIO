package com.enderio.enderio.content.enderface;

import com.enderio.enderio.api.poi.EnderPOIApi;
import com.enderio.enderio.foundation.block.EIOEntityBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class EnderfaceBlock extends EIOEntityBlock<EnderfaceBlockEntity> {
    private static final MapCodec<EnderfaceBlock> CODEC = simpleCodec(EnderfaceBlock::new);

    public EnderfaceBlock(Properties properties) {
        super(EIOBlockEntities.ENDERFACE::get, properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof EnderfaceBlockEntity) {
            EnderPOIApi.INSTANCE.removeAt(level, pos);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}

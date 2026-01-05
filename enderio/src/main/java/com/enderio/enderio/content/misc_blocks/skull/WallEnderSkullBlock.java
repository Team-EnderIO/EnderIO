package com.enderio.enderio.content.misc_blocks.skull;

import com.enderio.enderio.foundation.block.entity.EnderSkullBlockEntity;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class WallEnderSkullBlock extends WallSkullBlock {
    public WallEnderSkullBlock(Properties properties) {
        super(EnderSkullBlock.EIOSkulls.ENDERMAN, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderSkullBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? createTickerHelper(blockEntityType, EIOBlockEntities.ENDER_SKULL.get(), EnderSkullBlockEntity::animation) : null;
    }
}

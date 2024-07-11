package com.enderio.base.common.block.skull;

import com.enderio.base.common.blockentity.EnderSkullBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WallEnderSkullBlock extends WallSkullBlock {
    public WallEnderSkullBlock(Properties properties) {
        super(EnderSkullBlock.EIOSkulls.ENDERMAN, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.ENDER_SKULL.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? createTickerHelper(blockEntityType, EIOBlockEntities.ENDER_SKULL.get(), EnderSkullBlockEntity::animation) : null;
    }
}

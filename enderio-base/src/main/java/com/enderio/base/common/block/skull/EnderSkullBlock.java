package com.enderio.base.common.block.skull;

import com.enderio.base.common.blockentity.EnderSkullBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class EnderSkullBlock extends SkullBlock {

    public EnderSkullBlock(Properties properties) {
        super(EIOSkulls.ENDERMAN, properties);
    }

    public enum EIOSkulls implements Type {
        ENDERMAN;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
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

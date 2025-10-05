package com.enderio.enderio.content.paint.block;

import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class PaintedWoodenPressurePlateBlock extends PressurePlateBlock implements EntityBlock, PaintedBlock {

    public PaintedWoodenPressurePlateBlock(Properties properties) {
        super(BlockSetType.OAK, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SinglePaintedBlockEntity(pos, state);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return getPaintedStack(level, pos, this);
    }
}

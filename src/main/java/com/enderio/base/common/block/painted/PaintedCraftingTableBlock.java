package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class PaintedCraftingTableBlock extends CraftingTableBlock implements EntityBlock, IPaintedBlock {

    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    public PaintedCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.SINGLE_PAINTED.create(pos, state);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new CraftingMenu(containerId, inventory, ContainerLevelAccess.create(pLevel, pPos)) {
            @Override
            public boolean stillValid(Player pPlayer) {
                try {
                    return stillValid(access, pPlayer, EIOBlocks.PAINTED_CRAFTING_TABLE.get());
                } catch (Exception e) {
                    return false;
                }
            }
        }, CONTAINER_TITLE);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof SinglePaintedBlockEntity painted && painted.getPaint() != null) {
            return painted.getPaint().defaultBlockState();
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}

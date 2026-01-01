package com.enderio.enderio.content.paint.block;

import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.init.EIOBlocks;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PaintedCraftingTableBlock extends CraftingTableBlock implements EntityBlock, PaintedBlock {

    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    public PaintedCraftingTableBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SinglePaintedBlockEntity(pos, state);
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
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        // We ignore includeData because without this data the item won't work :P
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof PaintedBlockEntity painted) {
            Optional<Block> paint = painted.getPrimaryPaint();

            if (paint.isPresent()) {
                return paint.get().defaultBlockState();
            }
        }

        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}

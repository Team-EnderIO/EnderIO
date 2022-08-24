package com.enderio.conduits.common.items;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.action.RightClickAction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ConduitBlockItem extends BlockItem {

    private final Supplier<IConduitType> type;
    public ConduitBlockItem(Supplier<IConduitType> type, Block block, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    //MC: See original minified to only use stuff we actually need
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        @Nullable
        Player player = context.getPlayer();
        if (level.getBlockEntity(context.getHitResult().getBlockPos()) instanceof ConduitBlockEntity conduit) {
            Optional<InteractionResult> interactionResult = handleRightClickAction(conduit, context);
            if (interactionResult.isPresent())
                return interactionResult.get();
        }
        if (level.getBlockEntity(context.getHitResult().getBlockPos().relative(context.getHitResult().getDirection().getOpposite())) instanceof ConduitBlockEntity conduit) {
            Optional<InteractionResult> interactionResult = handleRightClickAction(conduit, context);
            if (interactionResult.isPresent())
                return interactionResult.get();
        }
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockState blockstate = this.getPlacementState(context);
            if (blockstate == null) {
                return InteractionResult.FAIL;
            } else if (!this.placeBlock(context, blockstate)) {
                return InteractionResult.FAIL;
            } else {
                BlockPos blockpos = context.getClickedPos();
                ItemStack itemstack = context.getItemInHand();
                BlockState blockstate1 = level.getBlockState(blockpos);

                level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                if (level.getBlockEntity(blockpos) instanceof ConduitBlockEntity conduit) {
                    conduit.addType(type.get());
                    if (level.isClientSide()) {
                        conduit.updateClient();
                    }
                }
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

        }
    }

    private Optional<InteractionResult> handleRightClickAction(ConduitBlockEntity conduit, BlockPlaceContext context) {
        RightClickAction action = conduit.addType(type.get());
        @Nullable
        Player player = context.getPlayer();
        if (action instanceof RightClickAction.Upgrade upgradeAction) {
            if (player != null && !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
                player.getInventory().placeItemBackInInventory(upgradeAction.getNotInConduit().getConduitItem().getDefaultInstance());
            }
            return Optional.of(InteractionResult.SUCCESS);
        } else if (action instanceof RightClickAction.Insert && player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
            return Optional.of(InteractionResult.SUCCESS);
        }
        return Optional.empty();
    }
}

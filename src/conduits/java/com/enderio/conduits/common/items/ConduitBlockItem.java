package com.enderio.conduits.common.items;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
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

import java.util.Optional;

public class ConduitBlockItem extends BlockItem {

    private final IConduitType type;
    public ConduitBlockItem(IConduitType type, Block block, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    //MC: See original minified to only use stuff we actually need
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (level.getBlockEntity(context.getHitResult().getBlockPos()) instanceof ConduitBlockEntity conduit) {
            Optional<IConduitType> iConduitType = conduit.addType(type);
            if (ConduitBlockEntity.isDifferent(iConduitType, type)) {
                iConduitType.ifPresent(conduitType -> player.getInventory().placeItemBackInInventory(conduitType.getConduitItem().getDefaultInstance()));
                return InteractionResult.SUCCESS;
            }
        }
        if (level.getBlockEntity(context.getHitResult().getBlockPos().relative(context.getHitResult().getDirection().getOpposite())) instanceof ConduitBlockEntity conduit) {
            Optional<IConduitType> iConduitType = conduit.addType(type);
            if (ConduitBlockEntity.isDifferent(iConduitType, type)) {
                iConduitType.ifPresent(conduitType -> player.getInventory().placeItemBackInInventory(conduitType.getConduitItem().getDefaultInstance()));
                return InteractionResult.SUCCESS;
            }
        }
        if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(context);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else if (!this.placeBlock(blockplacecontext, blockstate)) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    BlockState blockstate1 = level.getBlockState(blockpos);

                    level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                    SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
                    level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    if (level.getBlockEntity(blockpos) instanceof ConduitBlockEntity conduit) {
                        conduit.addType(type);
                        if (level.isClientSide())
                            conduit.requestModelDataUpdate();
                    }
                    if (player == null || !player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
    }
}

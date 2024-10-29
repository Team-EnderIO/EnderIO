package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitType;
import com.enderio.base.client.tooltip.TooltipHandler;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ConduitBlockItem extends BlockItem {

    private final Supplier<? extends ConduitType<?>> type;

    public ConduitBlockItem(Supplier<? extends ConduitType<?>> type, Block block, Properties properties) {
        super(block, properties);
        this.type = type;
    }

    public ConduitType<?> getType() {
        return type.get();
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        @Nullable
        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();
        ItemStack itemstack = context.getItemInHand();

        // Handle placing into an existing block
        if (level.getBlockEntity(blockpos) instanceof ConduitBlockEntity conduit) {
            if (conduit.hasType(type.get())) {
                // Pass through to block
                return level.getBlockState(blockpos).use(level, player, context.getHand(), context.getHitResult());
            }

            conduit.addType(type.get(), player);
            if (level.isClientSide()) {
                conduit.updateClient();
            }

            BlockState blockState = level.getBlockState(blockpos);
            SoundType soundtype = blockState.getSoundType(level, blockpos, context.getPlayer());
            level.playSound(player, blockpos, this.getPlaceSound(blockState, level, blockpos, context.getPlayer()), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockState));

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
        type.get().addToTooltip(level, tooltip::add, tooltipFlag);
        super.appendHoverText(stack, level, tooltip, tooltipFlag);
    }
}

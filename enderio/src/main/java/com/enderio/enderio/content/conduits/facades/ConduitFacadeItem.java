package com.enderio.enderio.content.conduits.facades;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ConduitFacadeItem extends BlockItem {
    public ConduitFacadeItem(Properties properties) {
        super(EIOBlocks.CONDUIT_BUNDLE.get(), properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        @Nullable
        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();

        // Allow placing from the edge of an adjacent block
        BlockState blockState = level.getBlockState(blockpos);
        if (!blockState.canBeReplaced()) {
            // noinspection DataFlowIssue
            return blockState
                    .useItemOn(context.getItemInHand(), level, player, context.getHand(),
                            context.getHitResult().withPosition(blockpos));
        }

        return super.place(context);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        // Must have a valid facade
        var facade = context.getItemInHand().getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
        if (facade == null || !facade.isValid()) {
            return false;
        }

        return super.canPlace(context, state);
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        // Do not register
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        var facade = stack.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
        boolean hasFacadeTooltip = facade != null
                && (facade.type().isBlastResistant() || facade.type().doesHideConduits());

        if (hasFacadeTooltip) {
            if (tooltipFlag.hasShiftDown()) {
                if (facade.type().doesHideConduits()) {
                    tooltipComponents.add(ConduitLang.TRANSPARENT_FACADE_TOOLTIP);
                }

                if (facade.type().isBlastResistant()) {
                    tooltipComponents.add(ConduitLang.BLAST_RESIST_FACADE_TOOLTIP);
                }
            } else {
                tooltipComponents.add(EIOCommonLang.SHOW_DETAIL_TOOLTIP);
            }
        }
    }
}

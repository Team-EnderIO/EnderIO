package com.enderio.enderio.content.conduits.facades;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        var facade = stack.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
        boolean hasFacadeTooltip = facade != null
                && (facade.type().isBlastResistant() || facade.type().doesHideConduits());

        if (hasFacadeTooltip) {
            if (flag.hasShiftDown()) {
                if (facade.type().doesHideConduits()) {
                    tooltipAdder.accept(ConduitLang.TRANSPARENT_FACADE_TOOLTIP);
                }

                if (facade.type().isBlastResistant()) {
                    tooltipAdder.accept(ConduitLang.BLAST_RESIST_FACADE_TOOLTIP);
                }
            } else {
                tooltipAdder.accept(EIOCommonLang.SHOW_DETAIL_TOOLTIP);
            }
        }
    }

    //TODO Move?
    @SubscribeEvent
    public static void itemOverrides(RegisterConditionalItemModelPropertyEvent event) {
        event.register(EnderIO.id("facade"), Painted.CODEC);
    }

    public record Painted() implements ConditionalItemModelProperty {
        public static final MapCodec<Painted> CODEC = MapCodec.unit(new Painted());

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return CODEC;
        }

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
            int seed, ItemDisplayContext displayContext) {
            var paint = stack.get(EIODataComponents.BLOCK_PAINT);
            if (paint != null) {
                return !paint.paint().defaultBlockState().isEmpty();
            }
            return false;
        }
    }
}

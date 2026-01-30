package com.enderio.enderio.content.conduits;

import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.EnderIODataComponents;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class ConduitBlockItem extends BlockItem implements ICustomCreativeTabEntries {

    public ConduitBlockItem(Properties properties) {
        super(EIOBlocks.CONDUIT_BUNDLE.get(), properties);
    }

    public static ItemStack getStackFor(Holder<Conduit<?, ?>> conduit, int count) {
        var stack = new ItemStack(EIOItems.CONDUIT.get(), count);
        stack.set(EnderIODataComponents.CONDUIT, conduit);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        Holder<Conduit<?, ?>> conduit = stack.get(EnderIODataComponents.CONDUIT);
        if (conduit == null) {
            return super.getName(stack);
        }

        return conduit.value().description();
    }

    // Do not use block description, as that is Conduit Bundle.
    // This will ensure missing conduits use the missing conduit string.
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

        // Allow placing from the edge of an adjacent block
        BlockState blockState = level.getBlockState(blockpos);
        if (!blockState.canBeReplaced()) {
            // noinspection DataFlowIssue
            return blockState
                    .useItemOn(context.getItemInHand(), level, player, context.getHand(),
                            context.getHitResult().withPosition(blockpos))
                    .result();
        }

        return super.place(context);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // Ensure the originalStack being used is valid for placement.
        var stack = context.getItemInHand();
        if (!stack.has(EnderIODataComponents.CONDUIT)) {
            var facadeProvider = stack.getCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER);
            if (facadeProvider == null || !facadeProvider.isValid()) {
                return false;
            }
        }

        boolean result = super.placeBlock(context, state);

        // Save the clicked face for connection logic later.
        if (result) {
            var level = context.getLevel();
            if (level.getBlockEntity(context.getClickedPos()) instanceof ConduitBundleBlockEntity conduitBundle) {
                // Try to work out what the player wants to click.
                // If they click directly onto the conduit they want to extend, prioritise that
                // face.
                // Otherwise, try to determine based on their horizontal direction
                var clickedFace = context.getClickedFace();
                var horizontalDirection = context.getHorizontalDirection();

                if (level.getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()))
                        .is(EIOBlocks.CONDUIT_BUNDLE.get())) {
                    conduitBundle.primaryConnectionSide = clickedFace.getOpposite();
                } else if (level.getBlockState(context.getClickedPos().relative(horizontalDirection.getOpposite()))
                        .is(EIOBlocks.CONDUIT_BUNDLE.get())) {
                    conduitBundle.primaryConnectionSide = horizontalDirection.getOpposite();
                }
            }
        }

        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        Holder<Conduit<?, ?>> conduit = stack.get(EnderIODataComponents.CONDUIT);
        if (conduit != null) {
            conduit.value().addToTooltip(context, tooltipComponents::add, tooltipFlag);

            boolean showDetailTooltip = !tooltipFlag.hasShiftDown()
                    && (conduit.value().hasAdvancedTooltip() || conduit.value().showDebugTooltip());

            if (conduit.value().showDebugTooltip() && tooltipFlag.hasShiftDown()) {
                tooltipComponents.add(TooltipUtil.styledWithArgs(ConduitLang.GRAPH_TICK_RATE_TOOLTIP,
                        20 / conduit.value().type().ticker().tickRate()));
            }

            if (showDetailTooltip) {
                tooltipComponents.add(EIOCommonLang.SHOW_DETAIL_TOOLTIP);
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean shouldAddDefaultItem() {
        return false;
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        var registry = parameters.holders().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
        var conduitTypes = registry.listElements().toList();

        var conduitClassTypes = conduitTypes.stream()
            .map(e -> e.value().getClass())
            .sorted(Comparator.comparing(Class::getName))
            .distinct()
            .toList();

        for (var conduitClass : conduitClassTypes) {
            var matchingConduitTypes = conduitTypes.stream()
                .filter(e -> e.value().getClass() == conduitClass)
                // GRIM...
                .sorted((o1, o2) -> compareConduitTo(o1.value(), o2.value()))
                .toList();

            for (var conduitType : matchingConduitTypes) {
                output.accept(ConduitBlockItem.getStackFor(conduitType, 1), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    private static <T extends Conduit<T, ?>> int compareConduitTo(Conduit<T, ?> o1, Conduit<?, ?> o2) {
        return o1.compareTo((T) o2);
    }
}

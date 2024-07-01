package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.Conduit;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.components.RepresentedConduitType;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitComponents;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "enderio", bus = EventBusSubscriber.Bus.MOD)
public class ConduitBlockItem extends BlockItem {

    private static HashMap<Holder<Conduit<?, ?, ?>>, String> TYPE_DESCRIPTION_IDS = new HashMap<>();

    public ConduitBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack getStackFor(Holder<Conduit<?, ?, ?>> conduitType, int count) {
        var stack = new ItemStack(ConduitBlocks.CONDUIT.asItem(), count);
        stack.set(ConduitComponents.REPRESENTED_CONDUIT_TYPE, new RepresentedConduitType(conduitType));
        return stack;
    }

    public static Optional<Holder<Conduit<?, ?, ?>>> getType(ItemStack stack) {
        var representedConduitType = stack.get(ConduitComponents.REPRESENTED_CONDUIT_TYPE);
        return representedConduitType != null
            ? Optional.of(representedConduitType.conduitType())
            : Optional.empty();
    }

    @Override
    public Component getName(ItemStack pStack) {
        return getType(pStack).map(typeHolder -> typeHolder.value().description())
            .orElseGet(() -> super.getName(pStack));
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        @Nullable
        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();
        ItemStack itemstack = context.getItemInHand();

        // Ensure we have a type
        if (getType(itemstack).isEmpty()) {
            return InteractionResult.FAIL;
        }

        // Pass through to existing block.
        BlockState blockState = level.getBlockState(blockpos);
        if (!blockState.isAir()) {
            return level.getBlockState(blockpos).useItemOn(context.getItemInHand(), level, player, context.getHand(), context.getHitResult()).result();
        }

        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        getType(pStack).ifPresent(typeHolder -> {
            List<Component> conduitTooltips = typeHolder.value().getHoverText(pContext, pTooltipFlag);
            if (!conduitTooltips.isEmpty()) {
                pTooltipComponents.addAll(conduitTooltips);
            }
        });

        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

    // High priority so conduits appear at the top of the conduits tab.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == EIOCreativeTabs.CONDUITS_TAB.get()) {
            var registry = event.getParameters().holders().lookupOrThrow(EnderIORegistries.Keys.CONDUIT);
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
                    event.accept(getStackFor(conduitType, 1), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }

    private static <T extends Conduit<T, ?, ?>> int compareConduitTo(Conduit<T, ?, ?> o1, Conduit<?, ?, ?> o2) {
        return o1.compareTo((T)o2);
    }
}

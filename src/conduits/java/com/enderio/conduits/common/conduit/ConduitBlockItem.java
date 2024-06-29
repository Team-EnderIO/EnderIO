package com.enderio.conduits.common.conduit;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.components.RepresentedConduitType;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = "enderio", bus = EventBusSubscriber.Bus.MOD)
public class ConduitBlockItem extends BlockItem {

    private static HashMap<ConduitType<?, ?, ?>, String> TYPE_DESCRIPTION_IDS = new HashMap<>();

    public ConduitBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static <T extends ConduitType<?, ?, ?>> ItemStack getStackFor(Supplier<T> conduitType, int count) {
        return getStackFor(conduitType.get(), count);
    }

    public static ItemStack getStackFor(ConduitType<?, ?, ?> conduitType, int count) {
        var stack = new ItemStack(ConduitBlocks.CONDUIT.asItem(), count);
        stack.set(ConduitComponents.REPRESENTED_CONDUIT_TYPE, new RepresentedConduitType(conduitType));
        return stack;
    }

    @Nullable
    public static ConduitType<?, ?, ?> getType(ItemStack stack) {
        var representedConduitType = stack.get(ConduitComponents.REPRESENTED_CONDUIT_TYPE);
        return representedConduitType == null ? null : representedConduitType.conduitType();
    }

    @Override
    public Component getName(ItemStack pStack) {
        var conduitType = getType(pStack);
        if (conduitType == null) {
            return super.getName(pStack);
        }

        return Component.translatable(TYPE_DESCRIPTION_IDS.computeIfAbsent(conduitType, this::createDescriptionId));
    }

    private String createDescriptionId(ConduitType<?, ?, ?> conduitType) {
        ResourceLocation conduitTypeKey = EnderIORegistries.CONDUIT_TYPES.getKey(conduitType);
        return String.format("item.%s.conduit.%s", conduitTypeKey.getNamespace(), conduitTypeKey.getPath());
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

        ConduitType<?, ?, ?> type = getType(itemstack);
        if (type == null) {
            return InteractionResult.FAIL;
        }

        // Handle placing into an existing block
        if (level.getBlockEntity(blockpos) instanceof ConduitBlockEntity conduit) {
            if (conduit.hasType(type)) {
                // Pass through to block
                return level.getBlockState(blockpos).useItemOn(context.getItemInHand(), level, player, context.getHand(), context.getHitResult()).result();
            }

            conduit.addType(type, player);
            if (level.isClientSide()) {
                conduit.updateClient();
            }

            BlockState blockState = level.getBlockState(blockpos);
            SoundType soundtype = blockState.getSoundType(level, blockpos, player);
            level.playSound(player, blockpos, this.getPlaceSound(blockState, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockState));

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return super.place(context);
    }

    // High priority so conduits appear at the top of the conduits tab.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == EIOCreativeTabs.CONDUITS_TAB.get()) {
            // TODO: When we switch to datapacks:
            //event.getParameters().holders()

            // Get all conduit types, grouped by conduit type.
            var conduitTypes = EnderIORegistries.CONDUIT_TYPES.entrySet().stream()
                .map(Map.Entry::getValue)
                .toList();

            var conduitNetworkTypes = EnderIORegistries.CONDUIT_NETWORK_TYPES.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

            for (var conduitNetworkType : conduitNetworkTypes) {
                var matchingConduitTypes = conduitTypes.stream()
                    .filter(e -> e.networkType() == conduitNetworkType)
                    .sorted()
                    .toList();

                for (var conduitType : matchingConduitTypes) {
                    event.accept(getStackFor(conduitType, 1), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }
        }
    }
}

package com.enderio.enderio.foundation.network;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilter;
import com.enderio.enderio.foundation.network.packets.ServerboundBreakConduitPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundCountFilterPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundDestroyEntireConduitBundlePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundDoubleChannelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundOpenConduitFilterMenu;
import com.enderio.enderio.foundation.network.packets.ServerboundRemoveConduitFacadePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSyncProbeStatePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundTimerFilterPacket;
import com.enderio.enderio.init.EIOConduitTypes;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitServerPayloadHandler {
    private static final ConduitServerPayloadHandler INSTANCE = new ConduitServerPayloadHandler();

    public static ConduitServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleDoubleChannelFilter(ServerboundDoubleChannelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            Object channels = mainHandItem.getCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER);
            if (channels == null) {
                channels = mainHandItem.getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER);
            }

            if (channels instanceof DoubleRedstoneChannel doubleRedstoneChannel) {
                doubleRedstoneChannel.setChannels(packet.channel1(), packet.channel2());
            }
        });
    }

    public void handleTimerFilter(ServerboundTimerFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            var channels = mainHandItem.getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER);
            if (channels instanceof RedstoneTimerFilter timer) {
                timer.setTimer(packet.ticks(), packet.maxTicks());
            }
        });
    }

    public void handleCountFilter(ServerboundCountFilterPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack mainHandItem = context.player().getMainHandItem();
            var channels = mainHandItem.getCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER);
            if (channels instanceof RedstoneCountFilter count) {
                count.setState(packet);
            }
        });
    }

    public void handle(ServerboundOpenConduitFilterMenu packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.containerId() == context.player().containerMenu.containerId) {
                // TODO: Spectator viewing filter menus is broken lol
                if (!context.player().isSpectator() && context.player().containerMenu instanceof ConduitMenu menu) {
                    menu.tryOpenFilterMenu(packet.slot());
                }
            }
        });
    }

    public void handle(ServerboundSyncProbeStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ItemStack heldStack = context.player().getMainHandItem();

            // Sanity check before updating item
            if (heldStack.is(EIOItems.CONDUIT_PROBE)) {
                ConduitProbeItem.setState(context.player(), heldStack, packet.state());
            }
        });
    }

    public void handle(ServerboundBreakConduitPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = (ServerPlayer)context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockstate = level.getBlockState(pos);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Check for safety.
                if (!conduitBundle.hasConduitStrict(packet.conduit())) {
                    return;
                }

                // Fire block break event.
                BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, blockstate);
                if (event.isCanceled()) {
                    // Send block entity data back
                    level.sendBlockUpdated(pos, blockstate, blockstate, Block.UPDATE_ALL);
                    return;
                }

                // Remove the conduit from the bundle
                conduitBundle.removeConduit(packet.conduit(), droppedItem -> {
                    if (!player.getAbilities().instabuild) {
                        var center = pos.getCenter();
                        level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, droppedItem.copy()));
                    }
                });

                // If the bundle is empty, destroy it.
                if (conduitBundle.isEmpty()) {
                    level.removeBlock(pos, false);
                }
            }
        });
    }

    public void handle(ServerboundRemoveConduitFacadePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = (ServerPlayer)context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockState = level.getBlockState(pos);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Fire block break event.
                BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, blockState);
                if (event.isCanceled()) {
                    // Send block entity data back
                    level.sendBlockUpdated(pos, blockState, blockState, Block.UPDATE_ALL);
                    return;
                }

                if (!player.getAbilities().instabuild) {
                    conduitBundle.dropFacadeItem();
                }

                int lightLevelBefore = level.getLightEmission(pos);
                conduitBundle.setFacadeProvider(ItemStack.EMPTY);

                // Handle light update
                if (lightLevelBefore != level.getLightEmission(pos)) {
                    level.getLightEngine().checkBlock(pos);
                }

                // If the bundle is empty, destroy it.
                if (conduitBundle.isEmpty()) {
                    level.removeBlock(pos, false);
                }
            }
        });
    }

    public void handle(ServerboundDestroyEntireConduitBundlePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = (ServerPlayer)context.player();
            var level = player.level();
            var pos = packet.pos();

            // Ensure player can break this block
            if (!player.canInteractWithBlock(pos, 1.0)) {
                return;
            }

            var blockState = level.getBlockState(pos);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBundleBlockEntity conduitBundle) {
                // Fire block break event.
                BlockEvent.BreakEvent event = CommonHooks.fireBlockBreak(level, player.gameMode.getGameModeForPlayer(), player, pos, blockState);
                if (event.isCanceled()) {
                    // Send block entity data back
                    level.sendBlockUpdated(pos, blockState, blockState, Block.UPDATE_ALL);
                    return;
                }

                // Duplicate list to avoid concurrent modification
                var conduits = conduitBundle.getConduits().stream().toList();
                for (var conduit : conduits) {
                    conduitBundle.removeConduit(conduit, droppedItem -> {
                        if (!player.getAbilities().instabuild) {
                            var center = pos.getCenter();
                            level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, droppedItem.copy()));
                        }
                    });
                }

                if (!conduitBundle.getFacadeProvider().isEmpty() &&
                    !player.getAbilities().instabuild) {
                    conduitBundle.dropFacadeItem();
                }

                level.removeBlock(pos, false);
            }
        });
    }
}

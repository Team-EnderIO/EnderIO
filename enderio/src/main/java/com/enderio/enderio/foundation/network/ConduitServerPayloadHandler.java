package com.enderio.enderio.foundation.network;

import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.foundation.network.packets.ServerboundOpenConduitFilterMenu;
import com.enderio.enderio.foundation.network.packets.ServerboundSyncProbeStatePacket;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitServerPayloadHandler {
    private static final ConduitServerPayloadHandler INSTANCE = new ConduitServerPayloadHandler();

    public static ConduitServerPayloadHandler getInstance() {
        return INSTANCE;
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
}

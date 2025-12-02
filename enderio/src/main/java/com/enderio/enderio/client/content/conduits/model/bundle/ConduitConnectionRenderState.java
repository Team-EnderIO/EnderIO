package com.enderio.enderio.client.content.conduits.model.bundle;

import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.IOConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.RedstoneSensitiveConnectionConfig;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.world.item.DyeColor;

public record ConduitConnectionRenderState(boolean canInput, DyeColor inputChannel, boolean canOutput,
        DyeColor outputChannel, boolean isRedstoneSensitive, DyeColor redstoneChannel) {

    public static ConduitConnectionRenderState fake() {
        return new ConduitConnectionRenderState(false, DyeColor.GREEN, false, DyeColor.GREEN, false, DyeColor.RED);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static ConduitConnectionRenderState of(ConnectionConfig connectionConfig) {
        boolean canInsert = false;
        boolean canExtract = false;
        DyeColor insertChannel = DyeColor.GREEN;
        DyeColor extractChannel = DyeColor.GREEN;
        if (connectionConfig instanceof IOConnectionConfig ioConnectionConfig) {
            canInsert = ioConnectionConfig.isInsert();
            canExtract = ioConnectionConfig.isExtract();
            insertChannel = ioConnectionConfig.insertChannel();
            extractChannel = ioConnectionConfig.extractChannel();
        }

        boolean isRedstoneSensitive = false;
        DyeColor redstoneChannel = DyeColor.RED;

        if (connectionConfig instanceof RedstoneSensitiveConnectionConfig redstoneSensitiveConfig) {
            // TODO: Support for multiple colours
            var channelColors = redstoneSensitiveConfig.getRedstoneSignalColors();
            if (!channelColors.isEmpty()) {
                isRedstoneSensitive = true;
                redstoneChannel = channelColors.getFirst();
            }
        }

        return new ConduitConnectionRenderState(canInsert, insertChannel, canExtract, extractChannel, isRedstoneSensitive,
                redstoneChannel);
    }
}

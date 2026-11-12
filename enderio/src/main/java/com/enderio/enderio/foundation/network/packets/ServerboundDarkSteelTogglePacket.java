package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Sent when a dark steel toggle keybind is pressed.
 * @param key the keybind channel, see {@link com.enderio.enderio.content.armory.toggles.DarkSteelToggleKeys}.
 */
public record ServerboundDarkSteelTogglePacket(ResourceLocation key) implements CustomPacketPayload {

    public static final Type<ServerboundDarkSteelTogglePacket> TYPE = new Type<>(EnderIO.rl("dark_steel_toggle"));

    public static final StreamCodec<ByteBuf, ServerboundDarkSteelTogglePacket> STREAM_CODEC = ResourceLocation.STREAM_CODEC
        .map(ServerboundDarkSteelTogglePacket::new, ServerboundDarkSteelTogglePacket::key);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

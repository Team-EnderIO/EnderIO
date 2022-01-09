package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.network.ClientToServerMenuPacket;
import com.enderio.base.common.network.EnderNetwork;
import com.enderio.base.common.network.packet.EmitParticlePacket;
import com.enderio.base.common.network.packet.EmitParticlesPacket;
import com.enderio.base.common.network.packet.UpdateCoordinateSelectionNameMenuPacket;
import net.minecraft.resources.ResourceLocation;

public class EIOPackets extends EnderNetwork {

    private EIOPackets() {}

    public static final EIOPackets NETWORK = new EIOPackets();

    private static final String PROTOCOL_VERSION = "1.0";

    @Override
    public ResourceLocation channelName() {
        return EnderIO.loc("network");
    }

    @Override
    public String getProtocolVersion() {
        return PROTOCOL_VERSION;
    }

    public static EIOPackets getNetwork() {
        return NETWORK;
    }

    @Override
    protected void registerMessages() {
        registerMessage(new ClientToServerMenuPacket.Handler<>(UpdateCoordinateSelectionNameMenuPacket::new), UpdateCoordinateSelectionNameMenuPacket.class);
        registerMessage(new EmitParticlePacket.Handler(), EmitParticlePacket.class);
        registerMessage(new EmitParticlesPacket.Handler(), EmitParticlesPacket.class);
    }
}

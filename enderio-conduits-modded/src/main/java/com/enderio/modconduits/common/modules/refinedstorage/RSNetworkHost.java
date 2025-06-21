package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.base.api.network.DumbStreamCodec;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.legacy.ConduitData;
import com.enderio.conduits.api.network.node.legacy.ConduitDataType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeContainerProviderImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public class RSNetworkHost extends NetworkNodeContainerProviderImpl implements ConduitData<RSNetworkHost> {
    public static final MapCodec<RSNetworkHost> CODEC = RecordCodecBuilder
            .mapCodec(rsNetworkHostInstance -> rsNetworkHostInstance.group(Codec.INT.fieldOf("int").forGetter(i -> i.i))
                    .apply(rsNetworkHostInstance, RSNetworkHost::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RSNetworkHost> STREAM_CODEC = DumbStreamCodec
            .of(RSNetworkHost::new)
            .cast();

    private final int i = 0;

    public RSNetworkHost() {
        super();
    }

    public RSNetworkHost(int i) {
        super();
    }

    @Override
    public RSNetworkHost deepCopy() {
        return new RSNetworkHost();
    }

    @Override
    public ConduitDataType<RSNetworkHost> type() {
        return RefinedStorageCommonModule.LEGACY_DATA_TYPE.get();
    }

    @Override
    public @Nullable NodeData toNodeData() {
        return new RSConduitNodeData();
    }

}

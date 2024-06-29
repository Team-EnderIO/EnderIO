package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public final class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, ConduitData<AE2InWorldConduitNodeHost> {
    private final ConduitType<AE2ConduitOptions, ?, AE2InWorldConduitNodeHost> type;

    @Nullable
    private IManagedGridNode mainNode = null;

    private AE2InWorldConduitNodeHost(ConduitType<AE2ConduitOptions, ?, AE2InWorldConduitNodeHost> type) {
        this.type = type;
    }

    public static AE2InWorldConduitNodeHost create(ConduitType<AE2ConduitOptions, ?, AE2InWorldConduitNodeHost> type) {
        var data = new AE2InWorldConduitNodeHost(type);
        data.initMainNode();
        return data;
    }

    private static AE2InWorldConduitNodeHost dummy(ConduitType<?, ?, ?> type) {
        //noinspection unchecked
        return new AE2InWorldConduitNodeHost((ConduitType<AE2ConduitOptions, ?, AE2InWorldConduitNodeHost>) type);
    }

    private static AE2InWorldConduitNodeHost of(ConduitType<?, ?, ?> type, CompoundTag mainNodeTag) {
        //noinspection unchecked
        var data = new AE2InWorldConduitNodeHost((ConduitType<AE2ConduitOptions, ?, AE2InWorldConduitNodeHost>) type);
        data.initMainNode();
        data.mainNode.loadFromNBT(mainNodeTag);
        return data;
    }

    @Override
    public ConduitDataSerializer<AE2InWorldConduitNodeHost> serializer() {
        return Serializer.INSTANCE;
    }

    @Nullable
    public IManagedGridNode getMainNode() {
        return mainNode;
    }

    public void clearMainNode() {
        mainNode = null;
    }

    public void initMainNode() {
        if (mainNode != null) {
            throw new UnsupportedOperationException("mainNode is already initialized");
        }

        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
            .setVisualRepresentation(ConduitBlockItem.getStackFor(type, 1))
            .setInWorldNode(true)
            .setTagName("conduit");

        mainNode.setIdlePowerUsage(type.options().isDense() ? 0.4d : 0.1d);

        if (type.options().isDense()) {
            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
        }
    }

    private CompoundTag saveMainNode() {
        var tag = new CompoundTag();
        if (mainNode != null) {
            mainNode.saveToNBT(tag);
        }
        return tag;
    }

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        if (mainNode == null) {
            initMainNode();
        }

        return mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        if (type.options().isDense()) {
            return AECableType.DENSE_SMART;
        }

        return AECableType.SMART;
    }

    @Override
    public void applyClientChanges(AE2InWorldConduitNodeHost guiData) {
    }

    public static class Serializer implements ConduitDataSerializer<AE2InWorldConduitNodeHost> {

        public static Serializer INSTANCE = new Serializer();

        public static final MapCodec<AE2InWorldConduitNodeHost> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                EnderIORegistries.CONDUIT_TYPES.byNameCodec().fieldOf("conduit_type").forGetter(e -> e.type),
                CompoundTag.CODEC.fieldOf("main_node").forGetter(AE2InWorldConduitNodeHost::saveMainNode)
            ).apply(instance, AE2InWorldConduitNodeHost::of)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPES)
            .map(AE2InWorldConduitNodeHost::dummy, data -> data.type);

        @Override
        public MapCodec<AE2InWorldConduitNodeHost> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

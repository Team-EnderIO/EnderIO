package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.networking.IManagedGridNode;
import appeng.api.util.AECableType;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitData;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public abstract class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, ConduitData<AE2InWorldConduitNodeHost> {

    public static class Normal extends AE2InWorldConduitNodeHost {

        public Normal() {
            super(AE2Integration.NORMAL.get());
        }

        public Normal(CompoundTag mainNodeTag) {
            super(AE2Integration.NORMAL.get(), mainNodeTag);
        }

        @Override
        public ConduitDataSerializer<AE2InWorldConduitNodeHost> serializer() {
            return AE2Integration.NORMAL_DATA_SERIALIZER.get();
        }

        public static class Serializer implements ConduitDataSerializer<AE2InWorldConduitNodeHost> {

            public static final MapCodec<AE2InWorldConduitNodeHost> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                    CompoundTag.CODEC.fieldOf("main_node").forGetter(AE2InWorldConduitNodeHost::saveMainNode)
                ).apply(instance, Normal::new)
            );

            // TODO: Opt-out of client sync somehow.
            public static final Supplier<StreamCodec<ByteBuf, AE2InWorldConduitNodeHost>> STREAM_CODEC = Suppliers.memoize(
                () -> new StreamCodec<>() {
                    @Override
                    public AE2InWorldConduitNodeHost decode(ByteBuf p_320376_) {
                        return new Normal();
                    }

                    @Override
                    public void encode(ByteBuf p_320158_, AE2InWorldConduitNodeHost p_320396_) {
                    }
                });

            @Override
            public MapCodec<AE2InWorldConduitNodeHost> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> streamCodec() {
                return STREAM_CODEC.get().cast();
            }
        }
    }

    public static class Dense extends AE2InWorldConduitNodeHost {

        public Dense() {
            super(AE2Integration.DENSE.get());
        }

        public Dense(CompoundTag mainNodeTag) {
            super(AE2Integration.DENSE.get(), mainNodeTag);
        }

        @Override
        public ConduitDataSerializer<AE2InWorldConduitNodeHost> serializer() {
            return AE2Integration.DENSE_DATA_SERIALIZER.get();
        }

        public static class Serializer implements ConduitDataSerializer<AE2InWorldConduitNodeHost> {

            public static final MapCodec<AE2InWorldConduitNodeHost> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                    CompoundTag.CODEC.fieldOf("main_node").forGetter(AE2InWorldConduitNodeHost::saveMainNode)
                ).apply(instance, Dense::new)
            );

            // TODO: Opt-out of client sync somehow.
            public static final Supplier<StreamCodec<ByteBuf, AE2InWorldConduitNodeHost>> STREAM_CODEC = Suppliers.memoize(
                () -> new StreamCodec<>() {
                    @Override
                    public AE2InWorldConduitNodeHost decode(ByteBuf p_320376_) {
                        return new Dense();
                    }

                    @Override
                    public void encode(ByteBuf p_320158_, AE2InWorldConduitNodeHost p_320396_) {
                    }
                });

            @Override
            public MapCodec<AE2InWorldConduitNodeHost> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> streamCodec() {
                return STREAM_CODEC.get().cast();
            }
        }
    }

    private final AE2ConduitType type;

    @Nullable
    protected IManagedGridNode mainNode = null;

    public AE2InWorldConduitNodeHost(AE2ConduitType type) {
        this.type = type;
        initMainNode();
    }

    private AE2InWorldConduitNodeHost(AE2ConduitType type, CompoundTag mainNodeTag) {
        this.type = type;
        initMainNode();
        mainNode.loadFromNBT(mainNodeTag);
    }

    private void initMainNode() {
        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
            .setVisualRepresentation(type.getConduitItem())
            .setInWorldNode(true)
            .setTagName("conduit");

        mainNode.setIdlePowerUsage(type.isDense() ? 0.4d : 0.1d);

        if (type.isDense()) {
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
        if (type.isDense()) {
            return AECableType.DENSE_SMART;
        }

        return AECableType.SMART;
    }

    @Override
    public void onCreated(ConduitType<AE2InWorldConduitNodeHost> type, Level level, BlockPos pos, @Nullable Player player) {
        if (mainNode == null) {
            // required because onCreated() can be called after onRemoved()
            initMainNode();
        }

        if (mainNode.isReady()) {
            return;
        }

        if (player != null) {
            mainNode.setOwningPlayer(player);
        }

        GridHelper.onFirstTick(level.getBlockEntity(pos), blockEntity -> mainNode.create(level, pos));
    }

    @Override
    public void updateConnection(Set<Direction> connectedSides) {
        if (mainNode == null) {
            return;
        }

        mainNode.setExposedOnSides(connectedSides);
    }

    @Override
    public void applyClientChanges(AE2InWorldConduitNodeHost guiData) {
    }

    @Override
    public void onRemoved(ConduitType<AE2InWorldConduitNodeHost> type, Level level, BlockPos pos) {
        if (mainNode != null) {
            mainNode.destroy();

            // required because onCreated() can be called after onRemoved()
            mainNode = null;
        }
        level.invalidateCapabilities(pos);
    }
}

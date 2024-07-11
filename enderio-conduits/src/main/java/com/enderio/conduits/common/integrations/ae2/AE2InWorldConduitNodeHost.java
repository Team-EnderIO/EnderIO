//package com.enderio.conduits.common.integrations.ae2;
//
//import appeng.api.networking.GridFlags;
//import appeng.api.networking.GridHelper;
//import appeng.api.networking.IGridNode;
//import appeng.api.networking.IInWorldGridNodeHost;
//import appeng.api.networking.IManagedGridNode;
//import appeng.api.util.AECableType;
//import com.enderio.api.conduit.Conduit;
//import com.enderio.api.conduit.ConduitDataSerializer;
//import com.enderio.api.conduit.ConduitData;
//import com.enderio.conduits.common.conduit.ConduitBlockItem;
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import net.minecraft.core.Direction;
//import net.minecraft.core.Holder;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.codec.StreamCodec;
//import org.jetbrains.annotations.Nullable;
//
//// TODO: I don't like how this depends on the Conduit it belongs to...
//public final class AE2InWorldConduitNodeHost implements IInWorldGridNodeHost, ConduitData<AE2InWorldConduitNodeHost> {
//    private final Holder<Conduit<?>> conduitOwner;
//
//    @Nullable
//    private IManagedGridNode mainNode = null;
//
//    private AE2InWorldConduitNodeHost(Holder<Conduit<?>> conduitOwner) {
//        this.conduitOwner = conduitOwner;
//    }
//
//    public static AE2InWorldConduitNodeHost create(Holder<Conduit<?>> type) {
//        var data = new AE2InWorldConduitNodeHost(type);
//        data.initMainNode();
//        return data;
//    }
//
//    private static AE2InWorldConduitNodeHost dummy(Holder<Conduit<?>> type) {
//        //noinspection unchecked
//        return new AE2InWorldConduitNodeHost(type);
//    }
//
//    private static AE2InWorldConduitNodeHost of(Holder<Conduit<?>> type, CompoundTag mainNodeTag) {
//        //noinspection unchecked
//        var data = new AE2InWorldConduitNodeHost(type);
//        data.initMainNode();
//        data.mainNode.loadFromNBT(mainNodeTag);
//        return data;
//    }
//
//    private boolean isDense() {
//        return conduitOwner.value() instanceof AE2Conduit ae2Conduit && ae2Conduit.isDense();
//    }
//
//    @Override
//    public ConduitDataSerializer<AE2InWorldConduitNodeHost> serializer() {
//        return Serializer.INSTANCE;
//    }
//
//    @Nullable
//    public IManagedGridNode getMainNode() {
//        return mainNode;
//    }
//
//    public void clearMainNode() {
//        mainNode = null;
//    }
//
//    public void initMainNode() {
//        if (mainNode != null) {
//            throw new UnsupportedOperationException("mainNode is already initialized");
//        }
//
//        mainNode = GridHelper.createManagedNode(this, new GridNodeListener())
//            .setVisualRepresentation(ConduitBlockItem.getStackFor(conduitOwner, 1))
//            .setInWorldNode(true)
//            .setTagName("conduit");
//
//        mainNode.setIdlePowerUsage(isDense() ? 0.4d : 0.1d);
//
//        if (isDense()) {
//            mainNode.setFlags(GridFlags.DENSE_CAPACITY);
//        }
//    }
//
//    private CompoundTag saveMainNode() {
//        var tag = new CompoundTag();
//        if (mainNode != null) {
//            mainNode.saveToNBT(tag);
//        }
//        return tag;
//    }
//
//    @Nullable
//    @Override
//    public IGridNode getGridNode(Direction dir) {
//        if (mainNode == null) {
//            initMainNode();
//        }
//
//        return mainNode.getNode();
//    }
//
//    @Override
//    public AECableType getCableConnectionType(Direction dir) {
//        if (isDense()) {
//            return AECableType.DENSE_SMART;
//        }
//
//        return AECableType.SMART;
//    }
//
//    @Override
//    public void applyClientChanges(AE2InWorldConduitNodeHost guiData) {
//    }
//
//    public static class Serializer implements ConduitDataSerializer<AE2InWorldConduitNodeHost> {
//
//        public static Serializer INSTANCE = new Serializer();
//
//        public static final MapCodec<AE2InWorldConduitNodeHost> CODEC = RecordCodecBuilder.mapCodec(
//            instance -> instance.group(
//                Conduit.CODEC.fieldOf("conduit_type").forGetter(e -> e.conduitOwner),
//                CompoundTag.CODEC.fieldOf("main_node").forGetter(AE2InWorldConduitNodeHost::saveMainNode)
//            ).apply(instance, AE2InWorldConduitNodeHost::of)
//        );
//
//        public static final StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> STREAM_CODEC = Conduit.STREAM_CODEC
//            .map(AE2InWorldConduitNodeHost::dummy, data -> data.conduitOwner);
//
//        @Override
//        public MapCodec<AE2InWorldConduitNodeHost> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public StreamCodec<RegistryFriendlyByteBuf, AE2InWorldConduitNodeHost> streamCodec() {
//            return STREAM_CODEC;
//        }
//    }
//}

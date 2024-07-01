package com.enderio.api.conduit;

import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * used for special single use things like RoundRobin for ItemConduits or proxying Caps.
 *
 * @apiNote Must implement hashCode() correctly to properly sync over the network - provided there is any data you send to the client.
 */
public interface ConduitData<T extends ConduitData<T>> {

    Codec<ConduitData<?>> CODEC = EnderIORegistries.CONDUIT_DATA_SERIALIZER.byNameCodec()
        .dispatch(ConduitData::serializer, ConduitDataSerializer::codec);

    StreamCodec<RegistryFriendlyByteBuf, ConduitData<?>> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_DATA_SERIALIZER)
        .dispatch(ConduitData::serializer, ConduitDataSerializer::streamCodec);

    EmptyConduitData EMPTY = new EmptyConduitData();

    // region Events

    /**
     * @return true if a node with this extradata can connect to a node with the otherData as extradata. this.canConnectTo(other) and other.canConnectTo(this) should be the same
     */
    default boolean canConnectTo(T otherData) {
        return true;
    }

    /**
     * This method is called after otherData is integrated into this network. change this or otherData accordingly if you want shared state for all nodes in a graph (like fluids in fluid conduits)
     *
     * @param otherData
     */
    default void onConnectTo(T otherData) {
    }

    // endregion

    // region Client Sync

    /**
     * @return true if this needs to be synced to the client. if this returns true, deepCopy has to be overriden, to create a copy
     */
    default boolean syncDataToClient() {
        return false;
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    default T deepCopy() {
        return cast();
    }

    /**
     * Allows ignoring some fields from the client (for example internal backing fields).
     */
    void applyClientChanges(T guiData);

    // endregion

    // region Serialization

    ConduitDataSerializer<T> serializer();

    default Tag save(HolderLookup.Provider lookupProvider) {
        return ConduitData.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getPartialOrThrow();
    }

    @SuppressWarnings("unchecked")
    static <T extends ConduitData<T>> T parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return (T) ConduitData.CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow();
    }

    // endregion

    default <Z extends ConduitData<?>> Z cast() {
        return (Z) this;
    }

    /**
     * default impl for stuff that don't need an impl
     */
    class EmptyConduitData implements ConduitData<EmptyConduitData> {
        private EmptyConduitData() {
        }

        @Override
        public void applyClientChanges(EmptyConduitData guiData) {
        }

        @Override
        public ConduitDataSerializer<EmptyConduitData> serializer() {
            return Serializer.INSTANCE;
        }

        public static class Serializer implements ConduitDataSerializer<EmptyConduitData> {
            public static MapCodec<EmptyConduitData> CODEC = MapCodec.unit(EMPTY);
            public static StreamCodec<ByteBuf, EmptyConduitData> STREAM_CODEC = StreamCodec.unit(EMPTY);

            public static Serializer INSTANCE = new Serializer();

            @Override
            public MapCodec<EmptyConduitData> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, EmptyConduitData> streamCodec() {
                return STREAM_CODEC.cast();
            }
        }
    }
}

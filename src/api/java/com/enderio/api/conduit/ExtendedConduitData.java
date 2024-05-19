package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.api.travel.TravelTarget;
import com.enderio.api.travel.TravelTargetSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

/**
 * used for special single use things like RoundRobin for ItemConduits or proxying Caps.
 *
 * @apiNote Must implement hashCode() correctly to properly sync over the network - provided there is any data you send to the client.
 */
public interface ExtendedConduitData<T extends ExtendedConduitData<T>> {

    Codec<ExtendedConduitData<?>> CODEC = EnderIORegistries.CONDUIT_DATA_SERIALIZERS.byNameCodec()
        .dispatch(ExtendedConduitData::serializer, ConduitDataSerializer::codec);

    StreamCodec<RegistryFriendlyByteBuf, ExtendedConduitData<?>> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_DATA_SERIALIZERS)
        .dispatch(ExtendedConduitData::serializer, ConduitDataSerializer::streamCodec);

    EmptyExtendedConduitData EMPTY = new EmptyExtendedConduitData();

    /**
     * default impl for stuff that don't need an impl
     */
    class EmptyExtendedConduitData implements ExtendedConduitData<EmptyExtendedConduitData> {
        private EmptyExtendedConduitData() {
        }

        @Override
        public void applyGuiChanges(EmptyExtendedConduitData guiData) {
        }

        @Override
        public ConduitDataSerializer<EmptyExtendedConduitData> serializer() {
            return Serializer.INSTANCE;
        }

        public static class Serializer implements ConduitDataSerializer<EmptyExtendedConduitData> {
            public static MapCodec<EmptyExtendedConduitData> CODEC = MapCodec.unit(EmptyExtendedConduitData::new);
            public static StreamCodec<ByteBuf, EmptyExtendedConduitData> STREAM_CODEC = StreamCodec.unit(EMPTY);

            public static Serializer INSTANCE = new Serializer();

            @Override
            public MapCodec<EmptyExtendedConduitData> codec() {
                return CODEC;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, EmptyExtendedConduitData> streamCodec() {
                return STREAM_CODEC.cast();
            }
        }
    }

    default void onCreated(ConduitType<T> type, Level level, BlockPos pos, @Nullable Player player) {}

    default void onRemoved(ConduitType<T> type, Level level, BlockPos pos) {}

    default void updateConnection(Set<Direction> connectedSides) {}

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

    /**
     * @return true if this needs to be synced to the client. if this returns true, deepCopy has to be overriden, to create a copy
     */
    default boolean syncDataToClient() {
        return false;
    }

    @UseOnly(LogicalSide.CLIENT)
    default T deepCopy() {
        return cast();
    }

    /**
     * Allows ignoring some fields from the client (for example internal backing fields).
     */
    void applyGuiChanges(T guiData);

    // region Serialization

    ConduitDataSerializer<T> serializer();

    default Tag save(HolderLookup.Provider lookupProvider) {
        return ExtendedConduitData.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getPartialOrThrow();
    }

    @SuppressWarnings("unchecked")
    static <T extends ExtendedConduitData<T>> T parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return (T) ExtendedConduitData.CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getPartialOrThrow();
    }

    // endregion

    default <Z extends ExtendedConduitData<Z>> Z cast() {
        return (Z) this;
    }

    default <Z extends ExtendedConduitData<Z>> Z castTo(Class<Z> clazz) {
        return cast();
    }
}

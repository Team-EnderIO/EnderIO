package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

// TODO: Rename to ConduitType.
public interface ConduitType<TType extends ConduitType<TType, TContext, TData>, TContext extends ConduitNetworkContext<TContext>, TData extends ConduitData<TData>> extends
    Comparable<TType> {

    Codec<ConduitType<?, ?, ?>> DIRECT_CODEC = EnderIORegistries.CONDUIT_TYPE_SERIALIZERS.byNameCodec()
        .dispatch(ConduitType::serializer, NewConduitTypeSerializer::codec);

    Codec<Holder<ConduitType<?, ?, ?>>> CODEC = RegistryFixedCodec.create(EnderIORegistries.Keys.CONDUIT_TYPES);
    StreamCodec<RegistryFriendlyByteBuf, Holder<ConduitType<?, ?, ?>>> STREAM_CODEC = ByteBufCodecs.holderRegistry(EnderIORegistries.Keys.CONDUIT_TYPES);

    ResourceLocation texture();
    Component description();

    NewConduitTypeSerializer<TType> serializer();

    /**
     * Get the ticker for this conduit graph type.
     * @apiNote The ticker should never change, it can use the options to determine behaviour in its implementation.
     */
    ConduitTicker<TType, TContext, TData> getTicker();
    ConduitMenuData getMenuData();

    /**
     * Create an instance of this network type's context.
     * @param network The conduit network that this context is for.
     * @apiNote Do not store the network in the context, it is passed in for reference only.
     */
    @Nullable
    TContext createNetworkContext(ConduitNetwork<TContext, TData> network);
    TData createConduitData(Level level, BlockPos pos);

    default boolean canBeInSameBundle(Holder<ConduitType<?, ?, ?>> otherType) {
        return true;
    }

    default boolean canBeReplacedBy(Holder<ConduitType<?, ?, ?>> otherType) {
        return false;
    }

    default boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return false;
    }

    default boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return false;
    }

    // region Events

    default void onCreated(TData data, Level level, BlockPos pos, @Nullable Player player) {
    }

    default void onRemoved(TData data, Level level, BlockPos pos) {
    }

    default void onConnectionsUpdated(TData data, Level level, BlockPos pos, Set<Direction> connectedSides) {
    }

    // endregion

    @Nullable
    default <K> K proxyCapability(BlockCapability<K, Direction> capability, ConduitNode<TContext, TData> node,
        Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        return null;
    }

    default Set<BlockCapability<?, Direction>> getExposedCapabilities() {
        return Set.of();
    }

    default ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}

    default List<Component> getHoverText(Item.TooltipContext context, TooltipFlag tooltipFlag) {
        return List.of();
    }

    default Holder<ConduitType<?, ?, ?>> wrapAsHolder(RegistryAccess registryAccess) {
        var registry = registryAccess.registryOrThrow(EnderIORegistries.Keys.CONDUIT_TYPES);
        return registry.wrapAsHolder(this);
    }

    default ResourceKey<ConduitType<?, ?, ?>> getKey(RegistryAccess registryAccess) {
        return wrapAsHolder(registryAccess).unwrapKey().orElseThrow();
    }
}

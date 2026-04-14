package com.enderio.enderio.api.conduits;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathPropertyConsumer;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.api.conduits.screen.ConduitScreenType;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.joml.Vector2i;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface Conduit<TConduit extends Conduit<TConduit, TConnectionConfig>, TConnectionConfig extends ConnectionConfig>
        extends Comparable<TConduit>, TooltipProvider {

    Codec<Conduit<?, ?>> DIRECT_CODEC = EnderIORegistries.CONDUIT_TYPE.byNameCodec()
            .dispatch(Conduit::type, ConduitType::codec);

    Codec<Holder<Conduit<?, ?>>> CODEC = RegistryFixedCodec.create(EnderIORegistries.Keys.CONDUIT);

    StreamCodec<RegistryFriendlyByteBuf, Holder<Conduit<?, ?>>> STREAM_CODEC = ByteBufCodecs
            .holderRegistry(EnderIORegistries.Keys.CONDUIT);

    /**
     * Gets the default conduit texture.
     */
    Identifier texture();

    /**
     * Gets the conduit description, used for the conduit item.
     */
    Component description();

    /**
     * Gets the conduit type.
     * This is used to define serialization and exposing proxied capabilities.
     */
    ConduitType<TConduit, TConnectionConfig> type();

    /**
     * @implNote if a conduit has a menu, you must also register a {@link ConduitScreenType} for it.
     * @return whether this conduit has a menu.
     */
    boolean hasMenu();

    /**
     * Proxy a capability to the conduit bundle block.
     * @param level the level.
     * @param node the node that is being queried for proxying. Will be null on the client.
     * @param capability the capability being requested.
     * @param context the context for the capability.
     * @return the capability or null if it is not exposed.
     */
    @Nullable
    default <TCapability, TContext> TCapability proxyCapability(Level level, @Nullable ConduitNode node,
            BlockCapability<TCapability, TContext> capability, @Nullable TContext context) {
        return null;
    }

    @ApiStatus.AvailableSince("8.1.0")
    default void collectNodePathProperties(ConduitNode node, ConnectionPathPropertyConsumer consumer) {
    }

    // region Conduit Checks

    /**
     * @param otherConduit the conduit to be replaced.
     * @return whether this conduit can replace the other.
     */
    default boolean canReplaceConduit(TConduit otherConduit) {
        return false;
    }

    /**
     * @implNote This method should be symmetrical, i.e.: `a.canConnectToConduit(b) == b.canConnectToConduit(a)`
     * @return true if both conduits are compatible and thus can connect.
     */
    default boolean canConnectToConduit(TConduit other) {
        // By default only allow a conduit to connect to an exact match.
        return this.equals(other);
    }

    /**
     * If this conduit overrides {@link #canConnectConduits(ConduitNode, ConduitNode)}, return true.
     * This will avoid showing connections between conduits on the client until the server evaluates whether they can connect.
     * @apiNote Failing to override this properly could result in connection desyncs.
     * @return whether this conduit has additional server-side connection checks.
     */
    default boolean hasServerConnectionChecks() {
        return false;
    }

    /**
     * This can be used to prevent connection between nodes with incompatible data.
     * @apiNote Not called by the server if {@link #hasServerConnectionChecks()} does not return true.
     * @implNote This method must be symmetrical, it will only be checked once for two pairs of nodes and conduits.
     * @return true if both nodes are compatible.
     */
    default boolean canConnectConduits(ConduitNode selfNode, ConduitNode otherNode) {
        return true;
    }

    // endregion

    // region Connections

    /**
     * @return if this is not always able to determine connectivity to its neighbours at time of placement, but the tick later
     */
    default boolean hasConnectionDelay() {
        return false;
    }

    default boolean canConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        return canConnectToBlock(level, conduitPos, direction);
    }

    /**
     * @deprecated Use {@link #canConnectToBlock(Level, ConduitCapabilityAccessor, BlockPos, Direction)} instead as it provides capability caching and invalidation handling.
     */
    @Deprecated(since = "8.2.6")
    default boolean canConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return false;
    }

    default boolean canForceConnectToBlock(Level level, ConduitCapabilityAccessor capabilityAccessor, BlockPos conduitPos, Direction direction) {
        return canConnectToBlock(level, capabilityAccessor, conduitPos, direction);
    }

    /**
     * @deprecated Use {@link #canConnectToBlock(Level, ConduitCapabilityAccessor, BlockPos, Direction)} instead as it provides capability caching and invalidation handling.
     */
    @Deprecated(since = "8.2.6")
    default boolean canForceConnectToBlock(Level level, BlockPos conduitPos, Direction direction) {
        return canConnectToBlock(level, conduitPos, direction);
    }

    // endregion

    // region Events

    default void onCreated(ConduitNode node, Level level, BlockPos pos, @Nullable Player player) {
    }

    default void onRemoved(ConduitNode node, Level level, BlockPos pos) {
    }

    default void onConnectionsUpdated(ConduitNode node, Level level, BlockPos pos, Set<Direction> connectedSides) {
    }

    default void onConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
    }

    // endregion

    // region Connection Inventory

    // TODO: Document that item loss will occur if a conduit is upgraded to another
    // type with smaller inventory...

    default int getInventorySize() {
        return 0;
    }

    default boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    default Vector2i getInventorySlotPosition(int slot) {
        if (getInventorySize() > 0) {
            throw new NotImplementedException(
                    "This conduit has an inventory, but getSlotPosition has not been implemented!");
        }

        throw new UnsupportedOperationException("This conduit does not have an inventory.");
    }

    // endregion

    // region Custom Data Sync

    /**
     * Create a custom tag for syncing data from node data or network context to the client for extra GUI behaviour.
     * @return custom sync data.
     */
    @Nullable
    default CompoundTag getExtraGuiData(ConduitBundle conduitBundle, ConduitNode node, Direction side) {
        return null;
    }

    /**
     * Create a custom tag for syncing data from node data or network context to the client for extra behaviours.
     * @return custom sync data.
     */
    @Nullable
    default CompoundTag getExtraWorldData(ConduitBundle conduitBundle, ConduitNode node) {
        return null;
    }

    // endregion

    // region Tooltips

    @Override
    default void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter) {
    }

    /**
     * @return true if this conduit has more tooltips when shift is held.
     */
    default boolean hasAdvancedTooltip() {
        return false;
    }

    /**
     * @return true if this conduit should show graph debug tooltips (when shift is held).
     */
    default boolean showDebugTooltip() {
        return false;
    }

    // endregion
}

package com.enderio.conduits.api;

import com.enderio.base.api.filter.ResourceFilter;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.conduits.api.upgrade.ConduitUpgrade;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public interface Conduit<TConduit extends Conduit<TConduit>> extends
    Comparable<TConduit>, TooltipProvider {

    Codec<Conduit<?>> DIRECT_CODEC = EnderIOConduitsRegistries.CONDUIT_TYPE.byNameCodec()
        .dispatch(Conduit::type, ConduitType::codec);

    Codec<Holder<Conduit<?>>> CODEC = RegistryFixedCodec.create(EnderIOConduitsRegistries.Keys.CONDUIT);

    StreamCodec<RegistryFriendlyByteBuf, Holder<Conduit<?>>> STREAM_CODEC = ByteBufCodecs.holderRegistry(EnderIOConduitsRegistries.Keys.CONDUIT);

    /**
     * Gets the default conduit texture.
     */
    ResourceLocation texture();

    /**
     * Gets the conduit description, used for the conduit item.
     */
    Component description();

    /**
     * @implNote Must be >= 1 and <= 20
     * @return the number of ticks that should pass before the conduit graph ticks.
     */
    default int graphTickRate() {
        return 5;
    }

    /**
     * Gets the conduit type.
     * This is used to define serialization and exposing proxied capabilities.
     */
    ConduitType<TConduit> type();

    /**
     * Get the ticker for this conduit graph type.
     * @apiNote The ticker should never change, it can use the options to determine behaviour in its implementation.
     */
    ConduitTicker<TConduit> getTicker();
    ConduitMenuData getMenuData();

    default boolean canBeInSameBundle(Holder<Conduit<?>> otherConduit) {
        return true;
    }

    default boolean canBeReplacedBy(Holder<Conduit<?>> otherConduit) {
        return false;
    }

    /**
     * @return true if both types are compatible
     */
    default boolean canConnectTo(Holder<Conduit<?>> other) {
        return this.equals(other.value());
    }

    /**
     * This can be used to prevent connection between nodes with incompatible data.
     * @return true if both nodes are compatible.
     */
    default boolean canConnectTo(ConduitNode selfNode, ConduitNode otherNode) {
        return true;
    }

    default boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        return getTicker().canConnectTo(level, conduitPos, direction);
    }

    /**
     * @return if this is not always able to determine connectivity to its neighbours at time of placement, but the tick later
     */
    default boolean hasConnectionDelay() {
        return false;
    }

    default boolean canApplyUpgrade(SlotType slotType, ConduitUpgrade conduitUpgrade) {
        return false;
    }

    default boolean canApplyFilter(SlotType slotType, ResourceFilter resourceFilter) {
        return false;
    }

    /**
     * Gets the conduit texture to display, given the data.
     */
    default ResourceLocation getTexture(ConduitNode node) {
        return texture();
    }

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

    @Nullable
    default <TCapability, TContext> TCapability proxyCapability(BlockCapability<TCapability, TContext> capability, ConduitNode node, Level level, BlockPos pos,
        @Nullable TContext context) {
        return null;
    }

    default ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}

    @Override
    default void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
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
}

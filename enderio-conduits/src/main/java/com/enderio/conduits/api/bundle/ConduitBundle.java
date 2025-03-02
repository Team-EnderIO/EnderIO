package com.enderio.conduits.api.bundle;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.ConnectionStatus;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.facade.FacadeType;
import java.util.List;

import com.enderio.conduits.api.network.node.ConduitNode;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable access to a conduit bundle.
 */
@ApiStatus.Experimental
public interface ConduitBundle {

    // region High-level Bundle Access

    /**
     * @implNote Must be sorted according to {@link com.enderio.conduits.api.ConduitApi#getConduitSortIndex(Holder)}
     * @return a list of all conduits in the bundle.
     */
    List<Holder<Conduit<?, ?>>> getConduits();

    boolean canAddConduit(Holder<Conduit<?, ?>> conduit);

    /**
     * Attempt to add a conduit to the bundle.
     * @param conduit the conduit to add
     * @param player the player adding the conduit, or null if performed from another source.
     * @return the result of the add operation.
     */
    AddConduitResult addConduit(Holder<Conduit<?, ?>> conduit, @Nullable Direction primaryConnectionSide,
        @Nullable Player player);

    /**
     * Remove a conduit from the bundle.
     * @throws IllegalArgumentException if this conduit is not present (in dev only).
     */
    void removeConduit(Holder<Conduit<?, ?>> conduit, @Nullable Player player);

    /**
     * @throws IllegalArgumentException if the conduit is not present.
     * @param conduit the conduit to get a node for.
     * @return the conduit node.
     */
    ConduitNode getConduitNode(Holder<Conduit<?, ?>> conduit);

    // endregion

    /**
     * @param conduit the conduit to get data for.
     * @return the client data tag, or null if there is none or the conduit doesn't sync extra data.
     */
    @Nullable
    CompoundTag getConduitExtraWorldData(Holder<Conduit<?, ?>> conduit);

    // TODO: Docs
    @Nullable
    CompoundTag getConduitExtraGuiData(Direction side, Holder<Conduit<?, ?>> conduit);

    /**
     * @implNote compare conduits using {@link Conduit#canConnectToConduit(Holder)}
     * @param conduit the conduit to check for
     * @return whether the bundle has this conduit, or another which is compatible.
     */
    boolean hasConduitByType(Holder<Conduit<?, ?>> conduit);

    // TODO: Docs
    boolean hasConduitByType(ConduitType<?> conduitType);

    // TODO: Docs
    Holder<Conduit<?, ?>> getConduitByType(ConduitType<?> conduitType);

    /**
     * @param conduit the conduit to check for
     * @return whether the bundle has this specific conduit.
     */
    boolean hasConduitStrict(Holder<Conduit<?, ?>> conduit);

    /**
     * @return whether the bundle has no conduits and no facade.
     */
    boolean isEmpty();

    /**
     * @return whether the bundle has the maximum number of conduits.
     */
    boolean isFull();

    // region Connections

    /**
     * Attempt to connect this conduit something in the given direction.
     * @param side the direction to be connected to.
     * @param conduit the conduit type that is being connected.
     * @param isForcedConnection whether this is a forced connection or automated connection. (Wrench)
     * @return whether a new connection was made.
     */
    boolean tryConnectTo(Direction side, Holder<Conduit<?, ?>> conduit, boolean isForcedConnection);

    /**
     * @implNote Must be sorted according to {@link com.enderio.conduits.api.ConduitApi#getConduitSortIndex(Holder)}
     * @param side the side to check for.
     * @return a list of all conduits connected on this side.
     */
    List<Holder<Conduit<?, ?>>> getConnectedConduits(Direction side);

    /**
     * TODO
     * @param side
     * @param conduit
     * @return
     */
    ConnectionStatus getConnectionStatus(Direction side, Holder<Conduit<?, ?>> conduit);

    /**
     * TODO
     * @param side
     * @param conduit
     * @return
     */
    ConnectionConfig getConnectionConfig(Direction side, Holder<Conduit<?, ?>> conduit);

    /**
     * @throws IllegalStateException if {@link #getConnectionStatus} is not {@link ConnectionStatus#CONNECTED_BLOCK}.
     * @throws IllegalArgumentException if the connection config is not the right type for this conduit.
     * @param side
     * @param config
     */
    void setConnectionConfig(Direction side, Holder<Conduit<?, ?>> conduit, ConnectionConfig config);

    /**
     * TODO
     * @param side
     * @param conduit
     * @return
     */
    <T extends ConnectionConfig> T getConnectionConfig(Direction side, Holder<Conduit<?, ?>> conduit,
            ConnectionConfigType<T> type);

    /**
     * TODO
     * @param side
     * @param conduit
     * @return
     */
    @Nullable IItemHandlerModifiable getConnectionInventory(Direction side, Holder<Conduit<?, ?>> conduit);

    // endregion

    // region Facades

    /**
     * @return the item providing this bundle's facade.
     */
    ItemStack getFacadeProvider();

    /**
     * Set the facade provider for this bundle.
     * Pass {@link ItemStack#EMPTY} to clear the facade.
     * @apiNote The item must have an exposed {@link com.enderio.conduits.api.facade.ConduitFacadeProvider} capability.
     * @param providerStack the stack providing the facade.
     */
    void setFacadeProvider(ItemStack providerStack);

    /**
     * @return whether the bundle has a facade.
     */
    boolean hasFacade();

    /**
     * @throws IllegalStateException if {@link #hasFacade} is false.
     * @return the block this bundle is mimicing.
     */
    Block getFacadeBlock();

    /**
     * @throws IllegalStateException if {@link #hasFacade} is false.
     * @return the type of facade this bundle has.
     */
    FacadeType getFacadeType();

    // endregion

}

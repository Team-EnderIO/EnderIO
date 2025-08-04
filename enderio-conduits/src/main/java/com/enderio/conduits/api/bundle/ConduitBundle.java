package com.enderio.conduits.api.bundle;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.api.connection.ConnectionStatus;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.facade.FacadeType;
import com.enderio.conduits.api.network.node.IConduitNode;
import java.util.List;
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
 * Access into a conduit bundle.
 */
@ApiStatus.Experimental
@ApiStatus.AvailableSince("8.0.0")
public interface ConduitBundle {

    // region High-level Bundle Access

    /**
     * @implNote Must be sorted according to {@link com.enderio.conduits.api.ConduitApi#getConduitSortIndex(Holder)}
     * @return a list of all conduits in the bundle.
     */
    List<Holder<Conduit<?, ?>>> getConduits();

    /**
     * @param conduit The conduit to check for.
     * @return whether this conduit can be added into the bundle.
     */
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
    // TODO GH-1115: Add a drop item nullable consumer? Could be a nice way to handle customising drops?
    void removeConduit(Holder<Conduit<?, ?>> conduit, @Nullable Player player);

    /**
     * @throws IllegalArgumentException if the conduit is not present.
     * @param conduit the conduit to get a node for.
     * @return the conduit node.
     */
    IConduitNode getConduitNode(Holder<Conduit<?, ?>> conduit);

    // endregion

    /**
     * @param conduit the conduit to get data for.
     * @return the client data tag, or null if there is none or the conduit doesn't sync extra data.
     */
    @Nullable
    CompoundTag getConduitExtraWorldData(Holder<Conduit<?, ?>> conduit);

    /**
     * @param conduit the conduit to get data for.
     * @param side    the side to get data for.
     * @return the gui data tag, or null if the conduit does not have custom data.
     */
    @Nullable
    CompoundTag getConduitExtraGuiData(Holder<Conduit<?, ?>> conduit, Direction side);

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
     *
     * @param conduit            the conduit type that is being connected.
     * @param side               the direction to be connected to.
     * @param isForcedConnection whether this is a forced connection or automated connection. (Wrench)
     * @return whether a new connection was made.
     */
    boolean tryConnectTo(Holder<Conduit<?, ?>> conduit, Direction side, boolean isForcedConnection);

    /**
     * @implNote Must be sorted according to {@link com.enderio.conduits.api.ConduitApi#getConduitSortIndex(Holder)}
     * @param side the side to check for.
     * @return a list of all conduits connected on this side.
     */
    List<Holder<Conduit<?, ?>>> getConnectedConduits(Direction side);

    // TODO
    ConnectionStatus getConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side);

    // TODO
    ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side);

    /**
     * @param side
     * @param config
     * @throws IllegalStateException    if {@link #getConnectionStatus} is not {@link ConnectionStatus#CONNECTED_BLOCK}.
     * @throws IllegalArgumentException if the connection config is not the right type for this conduit.
     */
    void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config);

    // TODO
    <T extends ConnectionConfig> T getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side,
            ConnectionConfigType<T> type);

    // TODO
    @Nullable
    IItemHandlerModifiable getConnectionInventory(Holder<Conduit<?, ?>> conduit, Direction side);

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
     * @param providerStack the originalStack providing the facade.
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

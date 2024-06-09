package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO: I might argue that this should actually be an abstract class?
public interface ConduitType<T extends ConduitData<T>> {

    ResourceLocation getTexture(T extendedData);
    ResourceLocation getItemTexture();

    /**
     * Override this method if your conduit type and your conduit item registry name don't match
     * @return the conduit item that holds this type
     */
    default Item getConduitItem() {
        // TODO: To be honest I'd rather this always be explicitly defined
        return BuiltInRegistries.ITEM.get(EnderIORegistries.CONDUIT_TYPES.getKey(this));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canBeInSameBlock(ConduitType<?> other) {
        return true;
    }

    default boolean canBeReplacedBy(ConduitType<?> other) {
        return false;
    }

    ConduitTicker<T> getTicker();

    @UseOnly(LogicalSide.CLIENT)
    ClientConduitData<T> getClientData();

    ConduitMenuData getMenuData();

    T createExtendedConduitData(Level level, BlockPos pos);

    default <K> Optional<K> proxyCapability(
        BlockCapability<K, Direction> cap,
        T extendedConduitData,
        Level level,
        BlockPos pos,
        @Nullable Direction direction,
        @Nullable ConduitNode.IOState state) {
        return Optional.empty();
    }

    /**
     * @param level the level
     * @param pos conduit position
     * @param direction direction the conduit connects to
     * @return the connectiondata that should be set on connection based on context
     */
    default ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}
}

package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IConduitType<T extends IExtendedConduitData<T>> {

    ResourceLocation getTexture(T extendedData);
    ResourceLocation getItemTexture();

    /**
     * Override this method if your conduit type and your conduit item registry name don't match
     * @return the conduit item that holds this type
     */
    default Item getConduitItem() {
        return BuiltInRegistries.ITEM.get(ConduitTypes.getRegistry().getKey(this));
    }

    default boolean canBeInSameBlock(IConduitType<?> other) {
        return true;
    }

    default boolean canBeReplacedBy(IConduitType<?> other) {
        return false;
    }

    ConduitTicker getTicker();

    @UseOnly(LogicalSide.CLIENT)
    ClientConduitData<T> getClientData();
    IConduitMenuData getMenuData();

    T createExtendedConduitData(Level level, BlockPos pos);

    default <K> Optional<K> proxyCapability(BlockCapability<K, Direction> cap, T extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction,
        Optional<NodeIdentifier.IOState> state) {
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

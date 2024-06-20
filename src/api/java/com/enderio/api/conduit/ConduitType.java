package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class ConduitType<T extends ConduitData<T>> {

    public abstract ConduitTicker<T> getTicker();

    public abstract ConduitMenuData getMenuData();

    public abstract T createConduitData(Level level, BlockPos pos);

    /**
     * Override this method if your conduit type and your conduit item registry name don't match
     * @return the conduit item that holds this type
     */
    public Item getConduitItem() {
        return ForgeRegistries.ITEMS.getValue(ConduitTypes.getRegistry().getKey(this));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canBeInSameBlock(ConduitType<?> other) {
        return true;
    }

    public boolean canBeReplacedBy(ConduitType<?> other) {
        return false;
    }

    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, T extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction,
        Optional<NodeIdentifier.IOState> state) {
        return Optional.empty();
    }

    /**
     * @param level the level
     * @param pos conduit position
     * @param direction direction the conduit connects to
     * @return the connectiondata that should be set on connection based on context
     */
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        return new ConduitConnectionData(false, true, RedstoneControl.NEVER_ACTIVE);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract, RedstoneControl control) {}
}

package com.enderio.api.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.ColorControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;
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
        return ForgeRegistries.ITEMS.getValue(ConduitTypes.getRegistry().getKey(this));
    }

    default boolean canBeInSameBlock(IConduitType<?> other) {
        return true;
    }

    default boolean canBeReplacedBy(IConduitType<?> other) {
        return false;
    }

    IConduitTicker getTicker();

    @UseOnly(LogicalSide.CLIENT)
    IClientConduitData<T> getClientData();
    IConduitMenuData getMenuData();

    T createExtendedConduitData(Level level, BlockPos pos);

    default <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, T extendedConduitData, @Nullable Direction direction) {
        return Optional.empty();
    }

    default ConduitConnectionData getDefaultConnection() {
        return new ConduitConnectionData(false, true);
    }

    record ConduitConnectionData(boolean isInsert, boolean isExtract) {}
}

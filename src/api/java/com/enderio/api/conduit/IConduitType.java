package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.IIcon;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IConduitType<T extends IExtendedConduitData<T>> extends IIcon {

    ResourceLocation getTexture(T extendedData);

    ResourceLocation[] getTextures();

    Item getConduitItem();

    default boolean canBeInSameBlock(IConduitType<?> other) {
        return true;
    }

    default boolean canBeReplacedBy(IConduitType<?> other) {
        return false;
    }

    default int getLightLevel(boolean isActive) {
        return 0;
    }

    @Override
    default Vector2i getIconSize() {
        return new Vector2i(24, 24);
    }

    @Override
    default Vector2i getRenderSize() {
        return new Vector2i(12, 12);
    }

    IConduitTicker getTicker();

    IConduitScreenData getData();

    T createExtendedConduitData(Level level, BlockPos pos);

    default <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, T extendedConduitData, @Nullable Direction direction) {
        return Optional.empty();
    }
}

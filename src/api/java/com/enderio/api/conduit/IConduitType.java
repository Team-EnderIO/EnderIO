package com.enderio.api.conduit;

import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.misc.IIcon;
import com.enderio.api.misc.Vector2i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.Optional;

public interface IConduitType extends IIcon {

    ResourceLocation getTexture();

    Item getConduitItem();

    default boolean canBeInSameBlock(IConduitType other) {
        return true;
    }

    default boolean canBeReplacedBy(IConduitType other) {
        return false;
    }

    default int getLightLevel(boolean isActive) {
        return 0;
    }

    //TODO: Remove this and turn to conduitlogicimpl
    default boolean canConnectTo(Level level, BlockPos pos, Direction direction) {
        return Optional.ofNullable(level.getBlockEntity(pos)).map(be -> be.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite())).isPresent();
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
}

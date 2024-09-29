package com.enderio.base.common.paint.blockentity;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.apache.commons.lang3.NotImplementedException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface PaintedBlockEntity {

    // TODO: Not a fan of this part.
    Set<ModelProperty<Block>> PAINT_DATA_PROPERTIES = new HashSet<>();

    Optional<Block> getPrimaryPaint();

    void setPrimaryPaint(Block paint);

    default boolean hasSecondaryPaint() {
        return false;
    }

    default Optional<Block> getSecondaryPaint() {
        return Optional.empty();
    }

    default void setSecondaryPaint(Block paint) {
        throw new NotImplementedException("This block does not support secondary paint.");
    }

    static ModelProperty<Block> createAndRegisterModelProperty() {
        ModelProperty<Block> property = new ModelProperty<>();
        PAINT_DATA_PROPERTIES.add(property);
        return property;
    }
}

package com.enderio.conduits.common.blockentity;

import com.enderio.api.conduit.SlotType;
import net.minecraft.core.Direction;

public record SlotData(Direction direction, int conduitIndex, SlotType slotType) {

    public static SlotData of(int slot) {
        return new SlotData(Direction.values()[slot / 3 / 9], slot / 3 % 9, SlotType.values()[slot % 3]);
    }

    public int slotIndex() {
        return direction.ordinal()*3*9 + conduitIndex * 3 + slotType.ordinal();
    }
}

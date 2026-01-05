package com.enderio.core.common.menu;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;

public class EnderSlot extends Slot implements SlotWithOverlay {
    @Nullable
    private Identifier foregroundSprite;

    public EnderSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Nullable
    public Identifier getForegroundSprite() {
        return foregroundSprite;
    }

    public Slot setForeground(Identifier sprite) {
        foregroundSprite = sprite;
        return this;
    }
}

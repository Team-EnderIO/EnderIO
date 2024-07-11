package com.enderio.core.common.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class EnderSlot extends Slot implements SlotWithOverlay {
    @Nullable
    private ResourceLocation foregroundSprite;

    public EnderSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Nullable
    public ResourceLocation getForegroundSprite() {
        return foregroundSprite;
    }

    public Slot setForeground(ResourceLocation sprite) {
        foregroundSprite = sprite;
        return this;
    }
}

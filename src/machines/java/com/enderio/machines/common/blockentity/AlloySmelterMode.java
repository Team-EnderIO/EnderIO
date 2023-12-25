package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.misc.IIcon;
import com.enderio.api.misc.Vector2i;
import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Alloy smelter mode.
 * Also provides icon behaviours for GUI.
 */
public enum AlloySmelterMode implements IIcon {
    /**
     * Furnace mode, only performs smelting recipes.
     */
    FURNACE(false, true, MachineLang.ALLOY_SMELTER_MODE_FURNACE),

    /**
     * All mode, performs smelting and alloying.
     */
    ALL(true, true, MachineLang.ALLOY_SMELTER_MODE_ALL),

    /**
     * Alloy mode, only performs alloying.
     */
    ALLOYS(true, false, MachineLang.ALLOY_SMELTER_MODE_ALLOY);

    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/icons/alloy_modes.png"); // TODO: Redo widgets
    private static final Vector2i SIZE = new Vector2i(16, 16);

    private final boolean canAlloy;
    private final boolean canSmelt;
    private final Vector2i pos;
    private final Component tooltip;

    AlloySmelterMode(boolean canAlloy, boolean canSmelt, Component tooltip) {
        this.canAlloy = canAlloy;
        this.canSmelt = canSmelt;
        pos = new Vector2i( 48 + 16 * ordinal(), 0);
        this.tooltip = tooltip;
    }

    public boolean canAlloy() {
        return canAlloy;
    }

    public boolean canSmelt() {
        return canSmelt;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Vector2i getIconSize() {
        return SIZE;
    }

    @Override
    public Vector2i getTexturePosition() {
        return pos;
    }

    @Override
    public Component getTooltip() {
        return tooltip;
    }

    @Override
    public Vector2i getTextureSize() {
        return new Vector2i(48, 16);
    }

}


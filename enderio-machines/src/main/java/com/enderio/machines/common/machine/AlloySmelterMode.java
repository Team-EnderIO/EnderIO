package com.enderio.machines.common.machine;

import com.enderio.base.EnderIO;
import com.enderio.base.client.screen.IIcon;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public enum AlloySmelterMode implements IIcon {
    Furnace(false, true, MachineLang.ALLOY_SMELTER_MODE_FURNACE),
    All(true, true, MachineLang.ALLOY_SMELTER_MODE_ALL),
    Alloys(true, false, MachineLang.ALLOY_SMELTER_MODE_ALLOY);

    public final boolean canAlloy;
    public final boolean canSmelt;

    private static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/40/widgetsv2.png"); // TODO: Redo widgets
    private static final Vector2i SIZE = new Vector2i(32, 32);
    private final Vector2i pos;
    private final Component tooltip;

    AlloySmelterMode(boolean canAlloy, boolean canSmelt, Component tooltip) {
        this.canAlloy = canAlloy;
        this.canSmelt = canSmelt;
        pos = new Vector2i( 32 * ordinal(), 288);
        this.tooltip = tooltip;
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
    public Vector2i getRenderSize() {
        return new Vector2i(12, 12);
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
        return new Vector2i(512, 512);
    }
}


package com.enderio.base.client.gui.widget;

import com.enderio.base.client.icon.EIOEnumIcons;
import com.enderio.core.client.gui.widgets.BaseEnumIconWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DyeColorIconWidget extends BaseEnumIconWidget<DyeColor> {

    // Reproduces the old ColorControl order for player familiarity.
    private static final DyeColor[] ORDERED_VALUES = new DyeColor[] {
        DyeColor.GREEN,
        DyeColor.BROWN,
        DyeColor.BLUE,
        DyeColor.PURPLE,
        DyeColor.CYAN,
        DyeColor.LIGHT_GRAY,
        DyeColor.GRAY,
        DyeColor.PINK,
        DyeColor.LIME,
        DyeColor.YELLOW,
        DyeColor.LIGHT_BLUE,
        DyeColor.MAGENTA,
        DyeColor.ORANGE,
        DyeColor.WHITE,
        DyeColor.BLACK,
        DyeColor.RED,
    };

    public DyeColorIconWidget(int pX, int pY, Supplier<DyeColor> getter, Consumer<DyeColor> setter, Component optionName) {
        super(pX, pY, 16, 16, DyeColor.class, getter, setter, optionName);
    }

    @Override
    @Nullable
    public Component getValueTooltip(DyeColor value) {
        return null;
    }

    @Override
    public ResourceLocation getValueIcon(DyeColor value) {
        return EIOEnumIcons.getIcon(value);
    }

    @Override
    public DyeColor[] getValues() {
        return ORDERED_VALUES;
    }
}

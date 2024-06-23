package com.enderio.base.client.gui.widget;

import com.enderio.EnderIO;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EIOCommonWidgets {
    private static final ResourceLocation ICON_RANGE_ENABLE = EnderIO.loc("icon/range_enable");
    private static final ResourceLocation ICON_RANGE_DISABLE = EnderIO.loc("icon/range_disable");

    public static ToggleIconButton createRange(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? ICON_RANGE_ENABLE : ICON_RANGE_DISABLE, null, getter, setter);
    }

    public static ToggleIconButton createRange(int x, int y, Component checkedTooltip, Component uncheckedTooltip, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? ICON_RANGE_DISABLE : ICON_RANGE_ENABLE,
            isChecked -> isChecked ? checkedTooltip : uncheckedTooltip, getter, setter);
    }
}

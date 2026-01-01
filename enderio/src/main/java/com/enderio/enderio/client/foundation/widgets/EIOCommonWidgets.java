package com.enderio.enderio.client.foundation.widgets;

import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EIOCommonWidgets {
    private static final Identifier ICON_RANGE_ENABLE = EnderIO.id("icon/range_enable");
    private static final Identifier ICON_RANGE_DISABLE = EnderIO.id("icon/range_disable");

    private static final Identifier PLUS = EnderIO.id("buttons/plus_small");
    private static final Identifier MINUS = EnderIO.id("buttons/minus_small");
    private static final WidgetSprites RANGE_PLUS_SPRITES = new WidgetSprites(PLUS, PLUS);
    private static final WidgetSprites RANGE_MINUS_SPRITES = new WidgetSprites(MINUS, MINUS);

    public static ToggleIconButton createRange(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? ICON_RANGE_ENABLE : ICON_RANGE_DISABLE, null,
                getter, setter);
    }

    public static ToggleIconButton createRange(int x, int y, Component checkedTooltip, Component uncheckedTooltip,
            Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new ToggleIconButton(x, y, 16, 16, isChecked -> isChecked ? ICON_RANGE_DISABLE : ICON_RANGE_ENABLE,
                isChecked -> isChecked ? checkedTooltip : uncheckedTooltip, getter, setter);
    }

    public static ImageButton createRangeIncrease(int x, int y, Button.OnPress onPress) {
        return new ImageButton(x, y, 8, 8, RANGE_PLUS_SPRITES, onPress);
    }

    public static ImageButton createRangeDecrease(int x, int y, Button.OnPress onPress) {
        return new ImageButton(x, y, 8, 8, RANGE_MINUS_SPRITES, onPress);
    }
}

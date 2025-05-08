package com.enderio.base.client.gui.widget;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.base.client.gui.icon.EIOEnumIcons;
import com.enderio.base.common.lang.EIOEnumLang;
import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RedstoneControlPickerWidget extends BaseEnumPickerWidget<RedstoneControl> {

    public RedstoneControlPickerWidget(int pX, int pY, Supplier<RedstoneControl> getter, Consumer<RedstoneControl> setter, Component optionName) {
        super(pX, pY, 16, 16, RedstoneControl.class, getter, setter, true, optionName);
    }

    @Override
    @Nullable
    public Component getValueTooltip(RedstoneControl value) {
        return EIOEnumLang.REDSTONE_CONTROL.get(value);
    }

    @Override
    public ResourceLocation getValueIcon(RedstoneControl value) {
        return EIOEnumIcons.REDSTONE_CONTROL.get(value);
    }
}

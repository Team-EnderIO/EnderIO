package com.enderio.base.client.gui.widget;

import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.client.icon.EIOEnumIcons;
import com.enderio.base.common.lang.EIOEnumLang;
import com.enderio.core.client.gui.widgets.BaseEnumIconWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RedstoneControlIconWidget extends BaseEnumIconWidget<RedstoneControl> {

    public RedstoneControlIconWidget(int pX, int pY, Supplier<RedstoneControl> getter, Consumer<RedstoneControl> setter, Component optionName) {
        super(pX, pY, 16, 16, RedstoneControl.class, getter, setter, optionName);
    }

    @Override
    @Nullable
    public Component getValueTooltip(RedstoneControl value) {
        return EIOEnumLang.getDescription(value);
    }

    @Override
    public ResourceLocation getValueIcon(RedstoneControl value) {
        return EIOEnumIcons.getIcon(value);
    }
}

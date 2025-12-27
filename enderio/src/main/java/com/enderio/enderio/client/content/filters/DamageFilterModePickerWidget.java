package com.enderio.enderio.client.content.filters;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.enderio.client.foundation.icon.EIOEnumIcons;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DamageFilterModePickerWidget extends BaseEnumPickerWidget<DamageFilterMode> {

    public DamageFilterModePickerWidget(int pX, int pY, Supplier<DamageFilterMode> getter,
            Consumer<DamageFilterMode> setter, Component optionName) {
        super(pX, pY, 16, 16, DamageFilterMode.class, getter, setter, false, optionName);
    }

    @Override
    public Component getValueTooltip(DamageFilterMode value) {
        return value.getComponent();
    }

    @Override
    public Identifier getValueIcon(DamageFilterMode value) {
        return EIOEnumIcons.DAMAGE_FILTER_MODE.get(value);
    }
}

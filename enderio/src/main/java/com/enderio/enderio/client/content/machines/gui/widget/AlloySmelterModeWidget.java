package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.enderio.client.foundation.icon.MachineEnumIcons;
import com.enderio.enderio.content.machines.alloy.AlloySmelterMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AlloySmelterModeWidget extends BaseEnumPickerWidget<AlloySmelterMode> {
    public AlloySmelterModeWidget(int pX, int pY, Supplier<AlloySmelterMode> getter, Consumer<AlloySmelterMode> setter,
            Component optionName) {
        super(pX, pY, 16, 16, AlloySmelterMode.class, getter, setter, true, optionName);
    }

    @Override
    public Component getValueTooltip(AlloySmelterMode value) {
        return value.getComponent();
    }

    @Override
    public ResourceLocation getValueIcon(AlloySmelterMode value) {
        return Objects.requireNonNull(MachineEnumIcons.ALLOY_SMELTER_MODE.get(value));
    }
}

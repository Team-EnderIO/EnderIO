package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.machines.client.gui.icon.MachineEnumIcons;
import com.enderio.machines.common.blocks.alloy.AlloySmelterMode;
import com.enderio.machines.common.lang.MachineEnumLang;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterModeWidget extends BaseEnumPickerWidget<AlloySmelterMode> {
    public AlloySmelterModeWidget(int pX, int pY, Supplier<AlloySmelterMode> getter, Consumer<AlloySmelterMode> setter,
            Component optionName) {
        super(pX, pY, 16, 16, AlloySmelterMode.class, getter, setter, optionName);
    }

    @Nullable
    @Override
    public Component getValueTooltip(AlloySmelterMode value) {
        return MachineEnumLang.ALLOY_SMELTER_MODE.get(value);
    }

    @Override
    public ResourceLocation getValueIcon(AlloySmelterMode value) {
        return Objects.requireNonNull(MachineEnumIcons.ALLOY_SMELTER_MODE.get(value));
    }
}

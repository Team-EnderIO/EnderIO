package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.enderio.client.foundation.icon.MachineEnumIcons;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PoweredSpawnerModeWidget extends BaseEnumPickerWidget<PoweredSpawnerMode> {
    public PoweredSpawnerModeWidget(int x, int y, Supplier<PoweredSpawnerMode> getter,
            Consumer<PoweredSpawnerMode> setter, Component optionName) {
        super(x, y, 16, 16, PoweredSpawnerMode.class, getter, setter, true, optionName);
    }

    @Override
    public Component getValueTooltip(PoweredSpawnerMode value) {
        return value.getComponent();
    }

    @Override
    public Identifier getValueIcon(PoweredSpawnerMode value) {
        return Objects.requireNonNull(MachineEnumIcons.POWERED_SPAWNER_MODE.get(value));
    }
}

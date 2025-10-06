package com.enderio.enderio.client.content.machines.gui.widget;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.enderio.client.foundation.icon.MachineEnumIcons;
import com.enderio.enderio.content.machines.powered_spawner.PoweredSpawnerMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PoweredSpawnerModeWidget extends BaseEnumPickerWidget<PoweredSpawnerMode> {
    public PoweredSpawnerModeWidget(int pX, int pY, Supplier<PoweredSpawnerMode> getter,
            Consumer<PoweredSpawnerMode> setter, Component optionName) {
        super(pX, pY, 16, 16, PoweredSpawnerMode.class, getter, setter, true, optionName);
    }

    @Override
    public Component getValueTooltip(PoweredSpawnerMode value) {
        return value.getComponent();
    }

    @Override
    public ResourceLocation getValueIcon(PoweredSpawnerMode value) {
        return Objects.requireNonNull(MachineEnumIcons.POWERED_SPAWNER_MODE.get(value));
    }
}

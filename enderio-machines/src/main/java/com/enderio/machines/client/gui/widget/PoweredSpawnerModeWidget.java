package com.enderio.machines.client.gui.widget;

import com.enderio.core.client.gui.widgets.BaseEnumPickerWidget;
import com.enderio.machines.client.gui.icon.MachineEnumIcons;
import com.enderio.machines.common.blocks.powered_spawner.PoweredSpawnerMode;
import com.enderio.machines.common.lang.MachineEnumLang;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PoweredSpawnerModeWidget extends BaseEnumPickerWidget<PoweredSpawnerMode> {
    public PoweredSpawnerModeWidget(int pX, int pY, Supplier<PoweredSpawnerMode> getter,
            Consumer<PoweredSpawnerMode> setter, Component optionName) {
        super(pX, pY, 16, 16, PoweredSpawnerMode.class, getter, setter, true, optionName);
    }

    @Nullable
    @Override
    public Component getValueTooltip(PoweredSpawnerMode value) {
        return MachineEnumLang.POWERED_SPAWNER_MODE.get(value);
    }

    @Override
    public ResourceLocation getValueIcon(PoweredSpawnerMode value) {
        return Objects.requireNonNull(MachineEnumIcons.POWERED_SPAWNER_MODE.get(value));
    }
}

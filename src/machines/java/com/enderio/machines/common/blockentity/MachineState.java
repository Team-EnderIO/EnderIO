package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.lang.MachineLang;
import net.minecraft.network.chat.MutableComponent;

public enum MachineState {
    ACTIVE(MachineLang.TOOLTIP_ACTIVE, 0),

    NO_POWER(MachineLang.TOOLTIP_NO_POWER, 1),
    FULL_POWER(MachineLang.TOOLTIP_FULL_POWER, 1),
    NO_SOURCE(MachineLang.TOOLTIP_NO_SOURCE, 1),
    EMPTY_TANK(MachineLang.TOOLTIP_EMPTY_TANK, 1),
    FULL_TANK(MachineLang.TOOLTIP_FULL_TANK, 1),
    NO_SOUL(MachineLang.TOOLTIP_NO_SOULBOUND, 1),
    NO_INPUT(MachineLang.TOOLTIP_NO_SOULBOUND, 1),
    FULL_OUTPUT(MachineLang.TOOLTIP_NO_SOULBOUND, 1),

    NO_CAP(MachineLang.TOOLTIP_NO_CAPACITOR, 2),
    REDSTONE(MachineLang.TOOLTIP_BLOCKED_RESTONE, 2),
    OUTPUT_FULL(MachineLang.TOOLTIP_OUTPUT_FULL, 2),
    INPUT_EMPTY(MachineLang.TOOLTIP_INPUT_EMPTY, 2);


    private final MutableComponent tooltip;
    private final int index;

    MachineState(MutableComponent tooltip, int index) {
        this.tooltip = tooltip;
        this.index = index;
    }

    public MutableComponent getTooltip() {
        return tooltip;
    }

    public int getIndex() {
        return index;
    }
}

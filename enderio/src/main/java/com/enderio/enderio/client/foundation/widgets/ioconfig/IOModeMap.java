package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.content.machines.MachinesLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Comparator;

public enum IOModeMap {

    NONE(IOMode.NONE, EIOCommonLang.NONE, new Rect2i(0, 0, 0, 0)),

    PUSH(IOMode.PUSH, EIOCommonLang.PUSH, new Rect2i(16, 0, 16, 8)),

    PULL(IOMode.PULL, EIOCommonLang.PULL, new Rect2i(0, 0, 16, 8)),

    BOTH(IOMode.BOTH, EIOCommonLang.BOTH, new Rect2i(0, 0, 32, 8)),

    DISABLED(IOMode.DISABLED, EIOCommonLang.DISABLED, new Rect2i(32, 0, 16, 16));

    private static final IOModeMap[] BY_MODE = Arrays.stream(values()).sorted(Comparator.comparingInt(m -> m.mode.ordinal())).toArray(IOModeMap[]::new);

    public static IOModeMap getMapFromMode(IOMode mode) {
        return BY_MODE[mode.ordinal()];
    }

    private final IOMode mode;
    private final Component component;
    private final Rect2i rect;

    IOModeMap(IOMode mode, Component name, Rect2i rect) {
        this.mode = mode;
        this.component = name;
        this.rect = rect;
    }

    public IOMode getMode() {
        return mode;
    }

    public Component getComponent() {
        return component;
    }

    public Rect2i getRect() {
        return rect;
    }

}

package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.base.api.io.IOMode;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Comparator;

public enum IOModeMap {

    NONE(IOMode.NONE, EIOLang.NONE, new Rect2i(0, 0, 0, 0)),

    PUSH(IOMode.PUSH, EIOLang.PUSH, new Rect2i(16, 0, 16, 8)),

    PULL(IOMode.PULL, EIOLang.PULL, new Rect2i(0, 0, 16, 8)),

    BOTH(IOMode.BOTH, EIOLang.BOTH, new Rect2i(0, 0, 32, 8)),

    DISABLED(IOMode.DISABLED, EIOLang.DISABLED, new Rect2i(32, 0, 16, 16));

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

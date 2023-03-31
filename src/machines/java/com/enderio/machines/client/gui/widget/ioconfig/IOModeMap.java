package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.api.io.IOMode;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public enum IOModeMap {

    PUSH(IOMode.PUSH, EIOLang.PUSH, new Rect2i(0, 0, 16, 16)),

    PULL(IOMode.PULL, EIOLang.PULL, new Rect2i(16, 0, 16, 16)),

    PUSHPULL(IOMode.BOTH, EIOLang.PUSHPULL, new Rect2i(0, 0, 32, 16)),

    DISABLED(IOMode.DISABLED, EIOLang.DISABLED, new Rect2i(32, 0, 16, 16)),

    NONE(IOMode.NONE, EIOLang.NONE, new Rect2i(0, 0, 0, 0));

    private final IOMode mode;
    private final Component name;
    private final Rect2i rect;

    IOModeMap(IOMode mode, Component name, Rect2i rect) {
        this.mode = mode;
        this.name = name;
        this.rect = rect;
    }

    public IOMode getMode() {
        return mode;
    }

    public Component getName() {
        return name;
    }

    public Rect2i getRect() {
        return rect;
    }
}

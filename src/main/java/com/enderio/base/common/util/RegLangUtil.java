package com.enderio.base.common.util;

import com.enderio.EnderIO;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.MutableComponent;

/**
 *
 */
public abstract class RegLangUtil {
    protected static final Registrate REGISTRATE = EnderIO.registrate();

    // region Utils

    public static MutableComponent guideBook(String category, String localisedValue) {
        return lang("guidebook", category, localisedValue);
    }

    public static MutableComponent guideBook(String category, String suffix, String localisedValue) {
        return lang("guidebook", category, suffix, localisedValue);
    }

    public static MutableComponent lang(String type, String name, String suffix, String localisedValue) {
        return REGISTRATE.addLang(type, EnderIO.loc(name), suffix, localisedValue);
    }

    public static MutableComponent lang(String type, String name, String localisedValue) {
        return REGISTRATE.addLang(type, EnderIO.loc(name), localisedValue);
    }

    // endregion
}

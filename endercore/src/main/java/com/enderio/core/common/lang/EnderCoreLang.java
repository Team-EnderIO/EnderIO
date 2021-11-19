package com.enderio.core.common.lang;

import com.enderio.core.EnderCore;
import com.tterrag.registrate.Registrate;
import net.minecraft.network.chat.TranslatableComponent;

public class EnderCoreLang {

    private static Registrate REGISTRATE = EnderCore.registrate();

    public static final TranslatableComponent SHOW_DETAIL_TOOLTIP = REGISTRATE.addLang("info", EnderCore.loc("gui.show_advanced_tooltip"), "<Hold Shift>");
    public static final TranslatableComponent DURABILITY = REGISTRATE.addLang("info", EnderCore.loc("gui.durability"), "Durability");
    public static final TranslatableComponent ENERGY_ABBREVIATION = REGISTRATE.addLang("info", EnderCore.loc("energy.abbreviation"), "μI");

    public static void register() {}
}

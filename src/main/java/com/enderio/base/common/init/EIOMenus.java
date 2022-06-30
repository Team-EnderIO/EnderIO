package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class EIOMenus {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<CoordinateMenu> COORDINATE = REGISTRATE.menu("coordinate",
        CoordinateMenu::factory, () -> CoordinateMenuScreen::new).register();

    public static void register() {}
}

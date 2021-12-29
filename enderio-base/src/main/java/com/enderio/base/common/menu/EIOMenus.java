package com.enderio.base.common.menu;

import com.enderio.base.EnderIO;
import com.enderio.base.client.screen.CoordinateMenuScreen;
import com.enderio.registrate.Registrate;
import com.enderio.registrate.util.entry.MenuEntry;

public class EIOMenus {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<CoordinateMenu> COORDINATE = REGISTRATE.menu("coordinate",
        CoordinateMenu::factory, () -> CoordinateMenuScreen::new).register();

    public static void register() {}
}

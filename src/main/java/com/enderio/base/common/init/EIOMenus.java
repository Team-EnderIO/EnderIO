package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;

public class EIOMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIO.getRegilite().menuRegistry();

    public static final RegiliteMenu<CoordinateMenu> COORDINATE = MENU_REGISTRY
        .registerMenu("coordinate", CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static void register() {}
}

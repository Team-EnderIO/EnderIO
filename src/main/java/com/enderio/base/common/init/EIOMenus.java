package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.core.common.registry.EnderDeferredMenu;
import com.enderio.core.common.registry.EnderMenuRegistry;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

public class EIOMenus {
    private static final EnderMenuRegistry MENUS = EnderMenuRegistry.create(EnderIO.MODID);

    public static final EnderDeferredMenu<CoordinateMenu> COORDINATE = MENUS.registerMenu("coordinate",
        CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static void register() {
        MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}

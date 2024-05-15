package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.client.gui.screen.FluidFilterScreen;
import com.enderio.base.client.gui.screen.ItemFilterScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.menu.FilterMenu;
import com.enderio.base.common.menu.FluidFilterMenu;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class EIOMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIO.getRegilite().menuRegistry();

    public static final RegiliteMenu<CoordinateMenu> COORDINATE = MENU_REGISTRY
        .registerMenu("coordinate", CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static final RegiliteMenu<ItemFilterMenu> ITEM_FILTER = MENU_REGISTRY
        .registerMenu("item_filter", ItemFilterMenu::factory, () -> ItemFilterScreen::new);

    public static final RegiliteMenu<FilterMenu> FLUID_FILTER = MENU_REGISTRY
        .registerMenu("fluid_filter", FluidFilterMenu::factory, () -> FluidFilterScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}

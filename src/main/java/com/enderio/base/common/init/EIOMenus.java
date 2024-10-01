package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.client.gui.screen.EntityFilterScreen;
import com.enderio.base.client.gui.screen.FluidFilterScreen;
import com.enderio.base.client.gui.screen.ItemFilterScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.base.common.menu.FluidFilterMenu;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class EIOMenus {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<CoordinateMenu> COORDINATE = REGISTRATE.menu("coordinate",
        CoordinateMenu::factory, () -> CoordinateMenuScreen::new).register();

    public static final MenuEntry<ItemFilterMenu> ITEM_FILTER = REGISTRATE.menu("item_filter",
        ItemFilterMenu::factory, () -> ItemFilterScreen::new).register();

    public static final MenuEntry<FluidFilterMenu> FLUID_FILTER = REGISTRATE.menu("fluid_filter",
        FluidFilterMenu::factory, () -> FluidFilterScreen::new).register();

    public static final MenuEntry<EntityFilterMenu> ENTITY_FILTER = REGISTRATE.menu("entity_filter",
        EntityFilterMenu::factory, () -> EntityFilterScreen::new).register();

    public static void register() {}
}

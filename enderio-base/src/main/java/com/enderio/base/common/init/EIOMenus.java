package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.client.gui.screen.EntityFilterScreen;
import com.enderio.base.client.gui.screen.FluidFilterScreen;
import com.enderio.base.client.gui.screen.ItemFilterScreen;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.base.common.menu.FluidFilterMenu;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.regilite.menus.RegiliteMenus;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class EIOMenus {
    private static final RegiliteMenus MENUS = EnderIOBase.REGILITE.menus();

    public static final Supplier<MenuType<CoordinateMenu>> COORDINATE = MENUS
        .create("coordinate", CoordinateMenu::factory, () -> CoordinateMenuScreen::new)
        .finish();

    public static final Supplier<MenuType<ItemFilterMenu>> ITEM_FILTER = MENUS
        .create("item_filter", ItemFilterMenu::factory, () -> ItemFilterScreen::new)
        .finish();

    public static final Supplier<MenuType<FluidFilterMenu>> FLUID_FILTER = MENUS
        .create("fluid_filter", FluidFilterMenu::factory, () -> FluidFilterScreen::new)
        .finish();

    public static final Supplier<MenuType<EntityFilterMenu>> ENTITY_FILTER = MENUS
        .create("entity_filter", EntityFilterMenu::factory, () -> EntityFilterScreen::new)
        .finish();

    public static void register() {
    }
}

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
import com.enderio.regilite.menus.RegiliteMenuTypes;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class EIOMenus {
    private static final RegiliteMenuTypes MENU_TYPES = EnderIOBase.REGILITE.menuTypes();

    public static final Supplier<MenuType<CoordinateMenu>> COORDINATE = MENU_TYPES
        .createOnly("coordinate", CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static final Supplier<MenuType<ItemFilterMenu>> ITEM_FILTER = MENU_TYPES
        .createOnly("item_filter", ItemFilterMenu::factory, () -> ItemFilterScreen::new);

    public static final Supplier<MenuType<FluidFilterMenu>> FLUID_FILTER = MENU_TYPES
        .createOnly("fluid_filter", FluidFilterMenu::factory, () -> FluidFilterScreen::new);

    public static final Supplier<MenuType<EntityFilterMenu>> ENTITY_FILTER = MENU_TYPES
        .createOnly("entity_filter", EntityFilterMenu::factory, () -> EntityFilterScreen::new);

    public static void register() {
    }
}

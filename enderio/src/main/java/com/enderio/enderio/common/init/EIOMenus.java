package com.enderio.enderio.common.init;

import com.enderio.EnderIOBase;
import com.enderio.enderio.client.gui.screen.CoordinateMenuScreen;
import com.enderio.enderio.client.gui.screen.EnderSoulFilterScreen;
import com.enderio.enderio.client.gui.screen.EnderFluidFilterScreen;
import com.enderio.enderio.client.gui.screen.EnderItemFilterScreen;
import com.enderio.enderio.common.filter.soul.EnderSoulFilterItem;
import com.enderio.enderio.common.filter.soul.EnderSoulFilterMenu;
import com.enderio.enderio.common.filter.fluid.EnderFluidFilterItem;
import com.enderio.enderio.common.filter.fluid.EnderFluidFilterMenu;
import com.enderio.enderio.common.filter.item.general.EnderItemFilterItem;
import com.enderio.enderio.common.filter.item.general.EnderItemFilterMenu;
import com.enderio.enderio.common.menu.CoordinateMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class EIOMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIOBase.REGILITE.menuRegistry();

    public static final RegiliteMenu<CoordinateMenu> COORDINATE = MENU_REGISTRY.registerMenu("coordinate",
            CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BASIC_ITEM_FILTER = MENU_REGISTRY.registerMenu(
            "basic_item_filter", EnderItemFilterItem.Type.BASIC::openMenu, () -> EnderItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> ADVANCED_ITEM_FILTER = MENU_REGISTRY.registerMenu(
            "advanced_item_filter", EnderItemFilterItem.Type.ADVANCED::openMenu, () -> EnderItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BIG_ITEM_FILTER = MENU_REGISTRY
            .registerMenu("big_item_filter", EnderItemFilterItem.Type.BIG::openMenu, () -> EnderItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BIG_ADVANCED_ITEM_FILTER = MENU_REGISTRY.registerMenu(
            "big_advanced_item_filter", EnderItemFilterItem.Type.BIG_ADVANCED::openMenu,
            () -> EnderItemFilterScreen::new);

    public static final RegiliteMenu<EnderFluidFilterMenu> BASIC_FLUID_FILTER = MENU_REGISTRY.registerMenu("basic_fluid_filter",
            EnderFluidFilterItem.Type.BASIC::openMenu, () -> EnderFluidFilterScreen::new);

    public static final RegiliteMenu<EnderSoulFilterMenu> BASIC_SOUL_FILTER = MENU_REGISTRY.registerMenu("basic_soul_filter",
            EnderSoulFilterItem.Type.BASIC::openMenu, () -> EnderSoulFilterScreen::new);

    public static void register(IEventBus eventBus) {
        MENU_REGISTRY.register(eventBus);
    }
}

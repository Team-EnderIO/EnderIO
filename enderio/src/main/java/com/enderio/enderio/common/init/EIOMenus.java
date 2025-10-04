package com.enderio.enderio.common.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.tools.CoordinateMenuScreen;
import com.enderio.enderio.client.content.filters.EnderFluidFilterScreen;
import com.enderio.enderio.client.content.filters.EnderItemFilterScreen;
import com.enderio.enderio.client.content.filters.EnderSoulFilterScreen;
import com.enderio.enderio.common.content.filters.fluid.EnderFluidFilterItem;
import com.enderio.enderio.common.content.filters.fluid.EnderFluidFilterMenu;
import com.enderio.enderio.common.content.filters.item.general.EnderItemFilterItem;
import com.enderio.enderio.common.content.filters.item.general.EnderItemFilterMenu;
import com.enderio.enderio.common.content.filters.soul.EnderSoulFilterItem;
import com.enderio.enderio.common.content.filters.soul.EnderSoulFilterMenu;
import com.enderio.enderio.common.content.tools.coordinate_selector.CoordinateMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class EIOMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIO.REGILITE.menuRegistry();

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

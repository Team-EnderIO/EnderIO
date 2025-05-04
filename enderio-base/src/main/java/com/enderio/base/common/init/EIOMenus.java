package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.client.gui.screen.CoordinateMenuScreen;
import com.enderio.base.client.gui.screen.EntityFilterScreen;
import com.enderio.base.client.gui.screen.FluidFilterScreen;
import com.enderio.base.client.gui.screen.ItemFilterScreen;
import com.enderio.base.client.gui.screen.NewItemFilterScreen;
import com.enderio.base.common.item.filter.EnderItemFilterItem;
import com.enderio.base.common.menu.CoordinateMenu;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.base.common.menu.FluidFilterMenu;
import com.enderio.base.common.menu.ItemFilterMenu;
import com.enderio.base.common.menu.EnderItemFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class EIOMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIOBase.REGILITE.menuRegistry();

    public static final RegiliteMenu<CoordinateMenu> COORDINATE = MENU_REGISTRY
        .registerMenu("coordinate", CoordinateMenu::factory, () -> CoordinateMenuScreen::new);

    public static final RegiliteMenu<ItemFilterMenu> ITEM_FILTER = MENU_REGISTRY
        .registerMenu("item_filter", ItemFilterMenu::factory, () -> ItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BASIC_ITEM_FILTER = MENU_REGISTRY
        .registerMenu("basic_item_filter", EnderItemFilterItem.Type.BASIC::openMenu, () -> NewItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> ADVANCED_ITEM_FILTER = MENU_REGISTRY
        .registerMenu("advanced_item_filter", EnderItemFilterItem.Type.ADVANCED::openMenu, () -> NewItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BIG_ITEM_FILTER = MENU_REGISTRY
        .registerMenu("big_item_filter", EnderItemFilterItem.Type.BIG::openMenu, () -> NewItemFilterScreen::new);

    public static final RegiliteMenu<EnderItemFilterMenu> BIG_ADVANCED_ITEM_FILTER = MENU_REGISTRY
        .registerMenu("big_advanced_item_filter", EnderItemFilterItem.Type.BIG_ADVANCED::openMenu, () -> NewItemFilterScreen::new);

    public static final RegiliteMenu<FluidFilterMenu> FLUID_FILTER = MENU_REGISTRY
        .registerMenu("fluid_filter", FluidFilterMenu::factory, () -> FluidFilterScreen::new);

    public static final RegiliteMenu<EntityFilterMenu> ENTITY_FILTER = MENU_REGISTRY
        .registerMenu("entity_filter", EntityFilterMenu::factory, () -> EntityFilterScreen::new);


    public static void register(IEventBus eventBus) {
        MENU_REGISTRY.register(eventBus);
    }
}

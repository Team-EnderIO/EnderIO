package com.enderio.enderio.conduits.common.init;

import com.enderio.enderio.conduits.EnderIOConduits;
import com.enderio.enderio.conduits.client.gui.screen.ConduitScreen;
import com.enderio.enderio.conduits.client.gui.screen.filter.RedstoneCountFilterScreen;
import com.enderio.enderio.conduits.client.gui.screen.filter.RedstoneDoubleChannelFilterScreen;
import com.enderio.enderio.conduits.client.gui.screen.filter.RedstoneTimerFilterScreen;
import com.enderio.enderio.common.conduits.menu.ConduitMenu;
import com.enderio.enderio.common.filter.redstone.RedstoneCountFilterMenu;
import com.enderio.enderio.common.filter.redstone.RedstoneDoubleChannelFilterMenu;
import com.enderio.enderio.common.filter.redstone.RedstoneTimerFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class ConduitMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIOConduits.REGILITE.menuRegistry();

    public static final RegiliteMenu<ConduitMenu> CONDUIT_MENU = MENU_REGISTRY.registerMenu("conduit", ConduitMenu::new,
            () -> ConduitScreen::new);

    public static final RegiliteMenu<RedstoneDoubleChannelFilterMenu> REDSTONE_DOUBLE_CHANNEL_FILTER = MENU_REGISTRY
            .registerMenu("redstone_and_filter", RedstoneDoubleChannelFilterMenu::factory,
                    () -> RedstoneDoubleChannelFilterScreen::new);

    public static final RegiliteMenu<RedstoneTimerFilterMenu> REDSTONE_TIMER_FILTER = MENU_REGISTRY.registerMenu(
            "redstone_timer_filter", RedstoneTimerFilterMenu::factory, () -> RedstoneTimerFilterScreen::new);

    public static final RegiliteMenu<RedstoneCountFilterMenu> REDSTONE_COUNT_FILTER = MENU_REGISTRY.registerMenu(
            "redstone_count_filter", RedstoneCountFilterMenu::factory, () -> RedstoneCountFilterScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}

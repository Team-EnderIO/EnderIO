package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.client.gui.ConduitScreen;
import com.enderio.conduits.client.gui.RedstoneDoubleChannelFilterScreen;
import com.enderio.conduits.client.gui.RedstoneTimerFilterScreen;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.menu.RedstoneDoubleChannelFilterMenu;
import com.enderio.conduits.common.menu.RedstoneTimerFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class ConduitMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIO.getRegilite().menuRegistry();

    public static final RegiliteMenu<ConduitMenu> CONDUIT_MENU = MENU_REGISTRY
        .registerMenu("conduit", ConduitMenu::factory, () -> ConduitScreen::new);

    public static final RegiliteMenu<RedstoneDoubleChannelFilterMenu> REDSTONE_DOUBLE_CHANNEL_FILTER = MENU_REGISTRY
        .registerMenu("redstone_and_filter", RedstoneDoubleChannelFilterMenu::factory, () -> RedstoneDoubleChannelFilterScreen::new);

    public static final RegiliteMenu<RedstoneTimerFilterMenu> REDSTONE_TIMER_FILTER = MENU_REGISTRY
        .registerMenu("redstone_timer_filter", RedstoneTimerFilterMenu::factory, () -> RedstoneTimerFilterScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}

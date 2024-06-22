package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.client.gui.ConduitScreen;
import com.enderio.conduits.client.gui.RedstoneCountFilterScreen;
import com.enderio.conduits.client.gui.RedstoneDoubleChannelFilterScreen;
import com.enderio.conduits.client.gui.RedstoneTimerFilterScreen;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.menu.RedstoneCountFilterMenu;
import com.enderio.conduits.common.menu.RedstoneDoubleChannelFilterMenu;
import com.enderio.conduits.common.menu.RedstoneTimerFilterMenu;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class ConduitMenus {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<ConduitMenu> CONDUIT_MENU = REGISTRATE.menu("conduit", ConduitMenu::factory, () -> ConduitScreen::new).register();

    public static final MenuEntry<RedstoneDoubleChannelFilterMenu> REDSTONE_DOUBLE_CHANNEL_FILTER = REGISTRATE
        .menu("redstone_and_filter", RedstoneDoubleChannelFilterMenu::factory, () -> RedstoneDoubleChannelFilterScreen::new).register();

    public static final MenuEntry<RedstoneTimerFilterMenu> REDSTONE_TIMER_FILTER = REGISTRATE
        .menu("redstone_timer_filter", RedstoneTimerFilterMenu::factory, () -> RedstoneTimerFilterScreen::new).register();

    public static final MenuEntry<RedstoneCountFilterMenu> REDSTONE_COUNT_FILTER = REGISTRATE
        .menu("redstone_count_filter", RedstoneCountFilterMenu::factory, () -> RedstoneCountFilterScreen::new).register();

    public static void register() {}
}

package com.enderio.conduits.common.init;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.client.gui.ConduitScreen;
import com.enderio.conduits.client.gui.RedstoneCountFilterScreen;
import com.enderio.conduits.client.gui.RedstoneDoubleChannelFilterScreen;
import com.enderio.conduits.client.gui.RedstoneTimerFilterScreen;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.menu.RedstoneCountFilterMenu;
import com.enderio.conduits.common.menu.RedstoneDoubleChannelFilterMenu;
import com.enderio.conduits.common.menu.RedstoneTimerFilterMenu;
import com.enderio.regilite.menus.RegiliteMenuTypes;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ConduitMenus {
    private static final RegiliteMenuTypes MENU_TYPES = EnderIOConduits.REGILITE.menuTypes();

    public static final Supplier<MenuType<ConduitMenu>> CONDUIT_MENU = MENU_TYPES
        .createOnly("conduit", ConduitMenu::factory, () -> ConduitScreen::new);

    public static final Supplier<MenuType<RedstoneDoubleChannelFilterMenu>> REDSTONE_DOUBLE_CHANNEL_FILTER = MENU_TYPES
        .createOnly("redstone_and_filter", RedstoneDoubleChannelFilterMenu::factory, () -> RedstoneDoubleChannelFilterScreen::new);

    public static final Supplier<MenuType<RedstoneTimerFilterMenu>> REDSTONE_TIMER_FILTER = MENU_TYPES
        .createOnly("redstone_timer_filter", RedstoneTimerFilterMenu::factory, () -> RedstoneTimerFilterScreen::new);

    public static final Supplier<MenuType<RedstoneCountFilterMenu>> REDSTONE_COUNT_FILTER = MENU_TYPES
        .createOnly("redstone_count_filter", RedstoneCountFilterMenu::factory, () -> RedstoneCountFilterScreen::new);

    public static void register() {
    }
}

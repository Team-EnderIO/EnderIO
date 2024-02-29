package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.client.gui.ConduitScreen;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import com.enderio.regilite.registry.MenuRegistry;
import net.neoforged.bus.api.IEventBus;

public class ConduitMenus {
    private static final MenuRegistry MENU_REGISTRY = EnderIO.getRegilite().menuRegistry();

    public static final RegiliteMenu<ConduitMenu> CONDUIT_MENU = MENU_REGISTRY
        .registerMenu("conduit", ConduitMenu::factory, () -> ConduitScreen::new);

    public static void register(IEventBus bus) {
        MENU_REGISTRY.register(bus);
    }
}

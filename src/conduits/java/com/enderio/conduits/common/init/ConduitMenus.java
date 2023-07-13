package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.client.gui.ConduitScreen;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.MenuEntry;

public class ConduitMenus {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final MenuEntry<ConduitMenu> CONDUIT_MENU = REGISTRATE.menu("conduit", ConduitMenu::factory, () -> ConduitScreen::new).register();

    public static void register() {}
}

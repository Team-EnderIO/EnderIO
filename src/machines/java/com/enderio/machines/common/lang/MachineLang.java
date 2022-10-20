package com.enderio.machines.common.lang;

import com.enderio.base.common.util.RegLangUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MachineLang extends RegLangUtil {
    public static final MutableComponent PROGRESS_TOOLTIP = lang("gui", "progress", "Progress %s%%");

    public static final Component ALLOY_SMELTER_MODE = lang("gui", "alloy_smelter.mode", "Smelting Mode");
    public static final Component ALLOY_SMELTER_MODE_ALL = lang("gui", "alloy_smelter.mode_all", "Alloying and Smelting");
    public static final Component ALLOY_SMELTER_MODE_ALLOY = lang("gui", "alloy_smelter.mode_alloy", "Alloys Only");
    public static final Component ALLOY_SMELTER_MODE_FURNACE = lang("gui", "alloy_smelter.mode_furnace", "Furnace Only");
    public static final MutableComponent SAG_MILL_GRINDINGBALL_TOOLTIP = lang("gui", "grinding_ball", "Remaining: %s%%\\nSAG Mill Grinding Ball\\nMain Output %s%%\\nBonus Output %s%%\\nPower Use %s%%");

    public static final Component TOOLTIP_ENERGY_EQUIVALENCE = lang("gui", "energy_equivalence", "A unit of energy, equivalent to FE.");

    static {
        // region Machine Category

        guideBook("machines", "title", "Machines");
        guideBook("machines", "desc", "TODO: DESCRIPTION");

        guideBook("machines.alloy_smelter", "title", "Alloy Smelter");
        guideBook("machines.alloy_smelter", "landing", "TODO: LANDING");
        guideBook("machines.alloy_smelter", "crafting", "TODO: CRAFTING");

        guideBook("machines.sag_mill", "title", "Sag Mill");
        guideBook("machines.sag_mill", "landing", "TODO: LANDING");
        guideBook("machines.sag_mill", "crafting", "TODO: CRAFTING");

        guideBook("machines.slice_and_splice", "title", "Slice n Splice");
        guideBook("machines.slice_and_splice", "landing", "TODO: LANDING");
        guideBook("machines.slice_and_splice", "crafting", "TODO: CRAFTING");

        // endregion
    }

    public static void register() {}
}

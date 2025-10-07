package com.enderio.enderio.foundation.lang;

import com.enderio.enderio.EnderIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class MachineLang {
    public static final Component TOOLTIP_ENERGY_EQUIVALENCE = addTranslation("gui", EnderIO.rl("energy_equivalence"),
            "A unit of energy, equivalent to FE.");

    public static final Component PHOTOVOLTAIC_CELL = addTranslation("tooltip", EnderIO.rl("photovoltaic_cell/main"),
            "Solar Power!");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED = addTranslation("tooltip",
            EnderIO.rl("photovoltaic_cell/advanced"), "Produces Power during daylight hours");
    public static final Component PHOTOVOLTAIC_CELL_ADVANCED2 = addTranslation("tooltip",
            EnderIO.rl("photovoltaic_cell/advanced2"), "Must have a clear line of sight to the sky");
    public static final MutableComponent PHOTOVOLTAIC_CELL_ADVANCED3 = addTranslation("tooltip",
            EnderIO.rl("photovoltaic_cell/advanced3"), "Max Output: ");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_TITLE = addTranslation("advancements",
            EnderIO.rl("place_capacitor_bank.title"), "Modular Power Storage");
    public static final Component PLACE_CAPACITOR_BANK_ADVANCEMENT_DESCRIPTION = addTranslation("advancements",
            EnderIO.rl("place_capacitor_bank.description"), "Build a Capacitor Bank");
    public static final Component MULTIBLOCK_CONNECTED_TEXTURES = addTranslation("hint",
            EnderIO.rl("connected_textures.text"),
            "If you are looking for connected textures on the capacitor bank, you might want to install Athena on your client");

    public static final Component FARMING_STATION_EXPERIMENT = addTranslation("pack",
            EnderIO.rl("experiment.farming_station"), "EnderIO: Farming Station");
    public static final Component ENDERFACE_EXPERIMENT = addTranslation("pack", EnderIO.rl("experiment.ender_io"),
            "EnderIO: The Ender IO");
    public static final Component NIARD_EXPERIMENT = addTranslation("pack", EnderIO.rl("experiment.niard"),
            "EnderIO: Niard");

    // TODO: NEO-PORT: Common lang base class?
    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.REGILITE.addTranslation(prefix, id, translation);
    }

    public static void register() {
    }
}

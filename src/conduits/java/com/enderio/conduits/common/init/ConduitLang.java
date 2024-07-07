package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConduitLang {

    // region Conduit Types

    public static final Component ENERGY_CONDUIT = addTranslation("item", EnderIO.loc("conduit.energy"), "Energy Conduit");
    public static final Component ENHANCED_ENERGY_CONDUIT = addTranslation("item", EnderIO.loc("conduit.enhanced_energy"), "Enhanced Energy Conduit");
    public static final Component ENDER_ENERGY_CONDUIT = addTranslation("item", EnderIO.loc("conduit.ender_energy"), "Ender Energy Conduit");
    public static final Component REDSTONE_CONDUIT = addTranslation("item", EnderIO.loc("conduit.redstone"), "Redstone Conduit");
    public static final Component FLUID_CONDUIT = addTranslation("item", EnderIO.loc("conduit.fluid"), "Fluid Conduit");
    public static final Component PRESSURIZED_FLUID_CONDUIT = addTranslation("item", EnderIO.loc("conduit.pressurized_fluid"), "Pressurized Fluid Conduit");
    public static final Component ENDER_FLUID_CONDUIT = addTranslation("item", EnderIO.loc("conduit.ender_fluid"), "Ender Fluid Conduit");
    public static final Component ITEM_CONDUIT = addTranslation("item", EnderIO.loc("conduit.item"), "Item Conduit");

    // endregion

    public static final MutableComponent ENERGY_RATE_TOOLTIP = addTranslation("tooltip", EnderIO.loc("conduit.energy.rate"), "Max Output %s \u00B5I/t");
    public static final MutableComponent FLUID_RATE_TOOLTIP = addTranslation("tooltip", EnderIO.loc("conduit.fluid.rate"), "Transfer Rate %s mB/t");
    public static final Component MULTI_FLUID_TOOLTIP = addTranslation("tooltip", EnderIO.loc("conduit.fluid.multi"),
        "Allows multiple fluids to be transported on the same line");

    public static final Component CONDUIT_INSERT = addTranslation("gui", EnderIO.loc("conduit.insert"), "Insert");
    public static final Component CONDUIT_EXTRACT = addTranslation("gui", EnderIO.loc("conduit.extract"), "Extract");

    private static MutableComponent addTranslation(String prefix, ResourceLocation id, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, id, translation);
    }

    private static MutableComponent addTranslation(String prefix, ResourceLocation path, String name, String translation) {
        return EnderIO.getRegilite().addTranslation(prefix, ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + "/" + name), translation);
    }

    public static void register() {
    }
}

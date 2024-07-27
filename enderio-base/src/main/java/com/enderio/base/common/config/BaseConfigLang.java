package com.enderio.base.common.config;

import com.enderio.EnderIOBase;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class BaseConfigLang {
    public static void register() {
        // Blocks
        addTranslation("blocks", "Blocks");
        addTranslation("brokenSpawner", "Broken Spawner");
        addTranslation("dropChance", "Drop Chance");
        addTranslation("explosionResistance", "Explosion Resistance");
        addTranslation("darkSteelLadderBoost", "Dark Steel Ladder Boost");

        addTranslation("enchantments", "Enchantments");
        addTranslation("repellent", "Repellent");
        addTranslation("chanceBase", "Base Chance");
        addTranslation("chancePerLevel", "Chance per Level");
        addTranslation("rangeBase", "Range Base");
        addTranslation("nonPlayerChance", "Non-Player Chance");

        addTranslation("items", "Items");
        addTranslation("food", "Food");
        addTranslation("enderioChance", "Teleport Chance"); // TODO: This config key needs changing...
        addTranslation("enderioRange", "Teleport Range");
        addTranslation("electromagnet", "Electromagnet");
        addTranslation("energyUse", "Energy Use");
        addTranslation("maxEnergy", "Max Energy");
        addTranslation("range", "Range");
        addTranslation("maxItems", "Max Items");
        addTranslation("levitationstaff", "Staff of Levity");
        addTranslation("travelling", "Staff of Travelling");
        addTranslation("blinkRange", "Blink Range");
        addTranslation("disabledTime", "Disabled Time");
        addTranslation("itemToBlockRange", "Item to Block Range");
        addTranslation("blockToBlockRange", "Block to Block Range");

        addTranslation("grainsOfInfinity", "Grains of Infinity");
        addTranslation("makesSound", "Makes Sound?");
        addTranslation("fireMinAge", "Fire Min Age");

        addTranslation("visual", "Visuals");
        addTranslation("machineParticles", "Machine Particles Enabled?");
    }

    private static void addTranslation(String key, String translation) {
        // TODO: More translation options in Regilite
        EnderIOBase.REGILITE.addTranslation(() -> EnderIOBase.MODULE_MOD_ID + "." + "configuration" + "." + key, translation);
    }
}

package com.enderio.armory.common.config;

import com.enderio.armory.EnderIOArmory;

public class ArmoryConfigLang {

    public static void register() {
        addTranslation("darksteel", "Dark Steel");

        addTranslation("darksteelaxe", "Dark Axe");
        addTranslation("energyUsePerFelledLog", "Energy use per log felled");

        addTranslation("darksteelpickaxe", "Dark Pickaxe");
        addTranslation("obsidianBreakPowerUse", "Obsidian break energy use");
        addTranslation("speedBoostWhenObsidian", "Obsidian break speed boost");
        addTranslation("useObsidianBreakSpeedAtHardness", "Use obsidian break speed at hardness");

        addTranslation("darksteelsword", "The Ender");
        addTranslation("modHeadChance_l1", "Level I beheading chance ");
        addTranslation("modHeadChance_l2", "Level II beheading chance ");
        addTranslation("modHeadChance_l3", "Level III beheading chance ");
        addTranslation("modHeadChance_l4", "Level IV beheading chance ");
        addTranslation("attackDamageIncrease_l1", "Level I attack damage increase ");
        addTranslation("attackDamageIncrease_l2", "Level II attack damage increase ");
        addTranslation("attackDamageIncrease_l3", "Level III attack damage increase ");
        addTranslation("attackDamageIncrease_l4", "Level IV attack damage increase ");
        addTranslation("attackSpeedIncrease_l1", "Level I attack speed increase ");
        addTranslation("attackSpeedIncrease_l2", "Level II attack speed increase ");
        addTranslation("attackSpeedIncrease_l3", "Level III attack speed increase ");
        addTranslation("attackSpeedIncrease_l4", "Level IV attack speed increase ");

        addTranslation("upgrades", "Upgrades");

        addTranslation("empowered", "Empowered");
        addTranslation("efficiencyBoost", "Efficiency Boost");
        addTranslation("energyUsePerDamagePoint", "Energy use uer damage point");
        addTranslation("activationCost_l1", "Level I XP cost ");
        addTranslation("activationCost_l2", "Level II XP cost ");
        addTranslation("activationCost_l3", "Level III XP cost ");
        addTranslation("activationCost_l4", "Level IV XP cost ");
        addTranslation("damageAbsorptionChance_l1", "Level I damage absorbtion %");
        addTranslation("damageAbsorptionChance_l2", "Level II damage absorbtion % ");
        addTranslation("damageAbsorptionChance_l3", "Level III damage absorbtion %");
        addTranslation("damageAbsorptionChance_l4", "Level IV damage absorbtion %");
        addTranslation("maxEnergy_l1", "Level I max energy ");
        addTranslation("maxEnergy_l2", "Level II max energy ");
        addTranslation("maxEnergy_l3", "Level III max energy ");
        addTranslation("maxEnergy_l4", "Level IV max energy ");

        addTranslation("explosive", "Explosive");
        addTranslation("explosiveEnergyPerBlock", "Energy use per block");
        addTranslation("explosiveActivationCost_l1", "Level I XP Cost");
        addTranslation("explosiveActivationCost_l2", "Level II XP Cost");
        addTranslation("explosive_range_l1", "Level I range");
        addTranslation("explosive_range_l2", "Level II range");

        addTranslation("explosivePenetration", "Explosive Penetration");
        addTranslation("explosivePenetrationActivationCost_l1", "Level I XP Cost");
        addTranslation("explosivePenetrationActivationCost_l2", "Level II XP Cost");
        addTranslation("explosivePenetration_l1", "Level I depth");
        addTranslation("explosivePenetration_l2", "Level II depth");

        addTranslation("spoonActivationCost", "Spoon XP Cost");
        addTranslation("forkActivationCost", "Fork XP Cost");
        addTranslation("directActivationCost", "Direct XP Cost");
        addTranslation("travelActivationCost", "Travel XP Cost");
        addTranslation("stepAssistActivationCost", "Step Assist XP Cost");
        addTranslation("gliderActivationCost", "Glider Assist XP Cost");
        addTranslation("elytraActivationCost", "Elytra Assist XP Cost");

        addTranslation("nightVision", "Night Vision");
        addTranslation("nightVisionActivationCost", "Night Vision XP Cost");
        addTranslation("nightVisionEnergyUse", "Night Vision Energy Per Tick");

        addTranslation("solar", "Solar");
        addTranslation("solarActivationCost_l1", "Level I XP Cost");
        addTranslation("solarActivationCost_l2", "Level II XP Cost");
        addTranslation("solarActivationCost_l3", "Level III XP Cost");

        addTranslation("speed", "Speed");
        addTranslation("speedEnergyUsePerUnitMoved", "Energy use per block");
        addTranslation("speedActivationCost_l1", "Level I XP Cost");
        addTranslation("speedActivationCost_l2", "Level II XP Cost");
        addTranslation("speedActivationCost_l3", "Level III XP Cost");
        addTranslation("speedBoost_l1", "Level I Speed boost");
        addTranslation("speedBoost_l2", "Level II Speed boost");
        addTranslation("speedBoost_l3", "Level III Speed boost");

        addTranslation("jump", "Jump");
        addTranslation("jumpEnergyUsePerUnitMoved", "Energy use per jump");
        addTranslation("jumpActivationCost_l1", "Level I XP Cost");
        addTranslation("jumpActivationCost_l2", "Level II XP Cost");
        addTranslation("jumpCount_l1", "Level I jump count");
        addTranslation("jumpCount_l2", "Level II jump count");

    }

    private static void addTranslation(String key, String translation) {
        EnderIOArmory.REGILITE.addTranslation(() -> EnderIOArmory.MODULE_MOD_ID + "." + "configuration" + "." + key,
                translation);
    }

}

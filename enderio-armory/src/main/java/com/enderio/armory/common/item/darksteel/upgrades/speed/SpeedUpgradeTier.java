package com.enderio.armory.common.item.darksteel.upgrades.speed;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.base.api.EnderIO;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum SpeedUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.SPEED_I_BOOST, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_I, ArmoryLang.DS_UPGRADE_SPEED_I),
    TWO(ArmoryConfig.COMMON.SPEED_II_BOOST, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_II,
            ArmoryLang.DS_UPGRADE_SPEED_II),
    THREE(ArmoryConfig.COMMON.SPEED_III_BOOST, ArmoryConfig.COMMON.SPEED_ACTIVATION_COST_III,
            ArmoryLang.DS_UPGRADE_SPEED_III);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Double> magnitude;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;
    private AttributeModifier attributeModifier;

    SpeedUpgradeTier(ModConfigSpec.ConfigValue<Double> magnitude, ModConfigSpec.ConfigValue<Integer> activationCost,
            Component displayName) {
        this.magnitude = magnitude;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new SpeedUpgrade(this);
    }

    @Override
    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    @Override
    public int getLevel() {
        return ordinal();
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public ModConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }

    public AttributeModifier getAttributeModifier() {
        if (attributeModifier == null) {
            attributeModifier = new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE, "speed_upgrade_" + ordinal()),
                    magnitude.get(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
        return attributeModifier;
    }
}

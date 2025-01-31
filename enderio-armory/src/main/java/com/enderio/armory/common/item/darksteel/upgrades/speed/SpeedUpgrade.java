package com.enderio.armory.common.item.darksteel.upgrades.speed;

import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class SpeedUpgrade extends TieredUpgrade<SpeedUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive";

    public SpeedUpgrade() {
        this(SpeedUpgradeTier.ONE);
    }

    public SpeedUpgrade(SpeedUpgradeTier tier) {
        super(tier, NAME);
    }

    public double getMagnitude() {
        return tier.getMagnitude().get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_SPEED_DESCRIPTION);
    }

    @Override
    protected SpeedUpgradeTier getBaseTier() {
        return SpeedUpgradeTier.ONE;
    }

    @Override
    protected Optional<SpeedUpgradeTier> getTier(int tier) {
        if (tier >= SpeedUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(SpeedUpgradeTier.values()[tier]);
    }

    public static void applySpeed(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        if (!stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS)
                || !DarkSteelCapability.hasUpgrade(stack, SpeedUpgrade.NAME)) {
            return;
        }
        Optional<SpeedUpgrade> upgrade = DarkSteelCapability.getUpgradeAs(stack, SpeedUpgrade.NAME, SpeedUpgrade.class);
        upgrade.ifPresent(speedUpgrade -> e.addModifier(Attributes.MOVEMENT_SPEED,
                speedUpgrade.tier.getAttributeModifier(), EquipmentSlotGroup.LEGS));
    }

}

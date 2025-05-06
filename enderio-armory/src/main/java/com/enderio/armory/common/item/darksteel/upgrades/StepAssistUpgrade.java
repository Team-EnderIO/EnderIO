package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.api.EnderIO;
import com.enderio.core.common.energy.ItemStackEnergy;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public class StepAssistUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "step_assist";

    private static final AttributeModifier ATTRIBUTE_MODIFIER = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE, "step_assist_upgrade"), 1,
            AttributeModifier.Operation.ADD_VALUE);

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_STEP_ASSIST;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_STEP_ASSIST_DESCRIPTION);
    }

    public static void applyStepHeightModifiers(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        if (!stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_BOOTS)
                || !DarkSteelHelper.hasUpgrade(stack, StepAssistUpgrade.NAME)) {
            return;
        }
        if (ItemStackEnergy.getEnergyStored(stack) > 0) {
            e.addModifier(Attributes.STEP_HEIGHT, ATTRIBUTE_MODIFIER, EquipmentSlotGroup.FEET);
        }
    }

}

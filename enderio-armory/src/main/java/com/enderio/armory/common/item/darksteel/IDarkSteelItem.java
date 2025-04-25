package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryFeatureFlags;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.item.CreativeTabVariants;
import com.enderio.core.common.util.TooltipUtil;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IDarkSteelItem extends AdvancedTooltipProvider, CreativeTabVariants {

    default Optional<EmpoweredUpgrade> getEmpoweredUpgrade(ItemStack stack) {
        return DarkSteelHelper.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
    }

    default ItemStack createFullyUpgradedStack(Item item) {
        ItemStack is = new ItemStack(item);
        Collection<? extends IDarkSteelUpgrade> ups = DarkSteelUpgradeRegistry.instance().createAllUpgradesForItem(is);
        for (IDarkSteelUpgrade upgrade : ups) {
            IDarkSteelUpgrade maxTier = upgrade;
            Optional<? extends IDarkSteelUpgrade> nextTier = maxTier.getNextTier();
            while (nextTier.isPresent()) {
                maxTier = nextTier.get();
                nextTier = maxTier.getNextTier();
            }
            DarkSteelHelper.addUpgrade(is, maxTier);
        }
        ItemStackEnergy.setFull(is);
        return is;
    }

    @Override
    default void addBasicTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        if (player != null) {
            if (player.level().enabledFeatures().contains(ArmoryFeatureFlags.ARMORY_REWRITE)) {
                addCurrentUpgradeTooltips(itemStack, tooltips, false);
            }
        }
    }

    @Override
    default void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        addDurabilityTooltips(itemStack, tooltips);
        addCurrentUpgradeTooltips(itemStack, tooltips, true);
        addAvailableUpgradesTooltips(itemStack, tooltips);
    }

    default void addDurabilityTooltips(ItemStack itemStack, List<Component> tooltips) {
        if (itemStack.isDamageableItem()) {
            String durability = (itemStack.getMaxDamage() - itemStack.getDamageValue()) + "/"
                    + itemStack.getMaxDamage();
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DURABILITY_AMOUNT, durability).withStyle(ChatFormatting.GRAY));
        }
        if (DarkSteelHelper.hasUpgrade(itemStack, EmpoweredUpgrade.NAME)) {
            String energy = String.format("%,d", ItemStackEnergy.getEnergyStored(itemStack)) + "/"
                    + String.format("%,d", ItemStackEnergy.getMaxEnergyStored(itemStack));
            tooltips.add(TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, energy).withStyle(ChatFormatting.GRAY));
        }
    }

    default void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        var upgrades = DarkSteelHelper.getUpgrades(itemStack);
        upgrades.stream()
                .sorted(Comparator.comparing(IDarkSteelUpgrade::getName))
                .forEach(upgrade -> tooltips.add(1,
                        upgrade.getDisplayName().copy().withStyle(ChatFormatting.DARK_AQUA)));
    }

    default void addAvailableUpgradesTooltips(ItemStack itemStack, List<Component> tooltips) {
        var availUpgrades = DarkSteelHelper.getUpgradesApplicable(itemStack);
        if (!availUpgrades.isEmpty()) {
            tooltips.add(ArmoryLang.DS_UPGRADE_AVAILABLE.copy().withStyle(ChatFormatting.YELLOW));
            availUpgrades.stream()
                    .sorted(Comparator.comparing(IDarkSteelUpgrade::getName))
                    .forEach(upgrade -> tooltips.add(Component.literal(" " + upgrade.getDisplayName().getString())
                            .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC)));
        }
    }

    default boolean isDurabilityBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0 || ItemStackEnergy.getMaxEnergyStored(stack) > 0;
    }

}

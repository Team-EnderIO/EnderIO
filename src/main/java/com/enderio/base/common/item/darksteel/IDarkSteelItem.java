package com.enderio.base.common.item.darksteel;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.nbt.INamedNBTSerializable;
import com.enderio.base.common.capability.DarkSteelUpgradeable;
import com.enderio.base.common.capability.EnergyDelegator;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.EnergyUtil;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.*;

public interface IDarkSteelItem extends IMultiCapabilityItem, IAdvancedTooltipProvider {

    default Optional<EmpoweredUpgrade> getEmpoweredUpgrade(ItemStack stack) {
        return DarkSteelUpgradeable.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class);
    }

    default MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        return initDarkSteelCapabilities(provider, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())));
    }

    default MultiCapabilityProvider initDarkSteelCapabilities(MultiCapabilityProvider provider, ResourceLocation forItem) {
        provider.addSerialized(EIOCapabilities.DARK_STEEL_UPGRADABLE, LazyOptional.of(() -> new DarkSteelUpgradeable(forItem)));
        provider.addSimple(ForgeCapabilities.ENERGY, LazyOptional.of(() -> new EnergyDelegator(provider)));
        return provider;
    }

    default List<ItemStack> getCreativeItems(Item item) {
        List<ItemStack> items = new ArrayList<>();
        ItemStack is = new ItemStack(item);
        items.add(is.copy());
        items.add(createFullyUpgradedStack(item));
        return items;
    }

    default ItemStack createFullyUpgradedStack(Item item) {
        ItemStack is = new ItemStack(item);
        Collection<? extends IDarkSteelUpgrade> ups = DarkSteelUpgradeable.getAllPossibleUpgrades(is);
        for(IDarkSteelUpgrade upgrade : ups) {
            IDarkSteelUpgrade maxTier = upgrade;
            Optional<? extends IDarkSteelUpgrade> nextTier = maxTier.getNextTier();
            while(nextTier.isPresent()) {
                maxTier = nextTier.get();
                nextTier = maxTier.getNextTier();
            }
            DarkSteelUpgradeable.addUpgrade(is,maxTier);
        }
        EnergyUtil.setFull(is);
        return is;
    }

    @Override
    default void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
    }

    @Override
    default void addBasicTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        addCurrentUpgradeTooltips(itemStack, tooltips, false);
    }

    @Override
    default void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        addDurabilityTooltips(itemStack, tooltips);
        addCurrentUpgradeTooltips(itemStack, tooltips, true);
        addAvailableUpgradesTooltips(itemStack, tooltips);
    }

    default void addDurabilityTooltips(ItemStack itemStack,  List<Component> tooltips) {
        String durability = (itemStack.getMaxDamage() - itemStack.getDamageValue()) + "/" + itemStack.getMaxDamage();
        tooltips.add(TooltipUtil.withArgs(EIOLang.DURABILITY_AMOUNT, durability).withStyle(ChatFormatting.GRAY));
        if (DarkSteelUpgradeable.hasUpgrade(itemStack, EmpoweredUpgrade.NAME)) {
            String energy =  String.format("%,d",EnergyUtil.getEnergyStored(itemStack)) + "/" +  String.format("%,d",EnergyUtil.getMaxEnergyStored(itemStack));
            tooltips.add(TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, energy).withStyle(ChatFormatting.GRAY));
        }
    }

    default  void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        var upgrades = DarkSteelUpgradeable.getUpgrades(itemStack);
        upgrades
            .stream()
            .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
            .forEach(upgrade -> tooltips.add(1, upgrade.getDisplayName().copy().withStyle(ChatFormatting.DARK_AQUA)));
    }

    default void addAvailableUpgradesTooltips(ItemStack itemStack, List<Component> tooltips) {
        var availUpgrades = DarkSteelUpgradeable.getUpgradesApplicable(itemStack);
        if(!availUpgrades.isEmpty()) {
            tooltips.add(EIOLang.DS_UPGRADE_AVAILABLE.copy().withStyle(ChatFormatting.YELLOW));
            availUpgrades
                .stream()
                .sorted(Comparator.comparing(INamedNBTSerializable::getSerializedName))
                .forEach(upgrade -> tooltips.add(
                    Component.literal(" " + upgrade.getDisplayName().getString()).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC)));
        }
    }

    default boolean isDurabilityBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0 || EnergyUtil.getMaxEnergyStored(stack) > 0;
    }

}
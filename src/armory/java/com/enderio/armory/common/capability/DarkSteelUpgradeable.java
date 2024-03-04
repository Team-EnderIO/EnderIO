package com.enderio.armory.common.capability;

import com.enderio.EnderIO;
import com.enderio.api.capability.IDarkSteelUpgradable;
import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgradeTier;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public class DarkSteelUpgradeable implements IDarkSteelUpgradable, INBTSerializable<Tag> {

    // region Utils

    public static ItemStack addUpgrade(ItemStack itemStack, IDarkSteelUpgrade upgrade) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.addUpgrade(upgrade);
        }
        return itemStack;
    }

    public static void removeUpgrade(ItemStack itemStack, String upgrade) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.removeUpgrade(upgrade);
        }
    }

    public static Collection<IDarkSteelUpgrade> getUpgrades(ItemStack itemStack) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.getUpgrades();
        }
        return Collections.emptyList();
    }

    public static boolean hasUpgrade(ItemStack itemStack, String name) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.hasUpgrade(name);
        }
        return false;
    }

    public static <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(ItemStack itemStack, String upgrade, Class<T> as) {
        IDarkSteelUpgradable cap = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        return cap.getUpgradeAs(upgrade, as);
    }

    public static Collection<IDarkSteelUpgrade> getUpgradesApplicable(ItemStack itemStack) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.getUpgradesApplicable();
        }
        return Collections.emptyList();
    }

    public static Collection<IDarkSteelUpgrade> getAllPossibleUpgrades(ItemStack itemStack) {
        IDarkSteelUpgradable capability = itemStack.getCapability(EIOCapabilities.DarkSteelUpgradable.ITEM);
        if (capability != null) {
            capability.getAllPossibleUpgrades();
        }
        return Collections.emptyList();
    }

    // endregion

    // region Class Impl

    // TODO: I would move this to EIONBTKeys but unsure what it does
    //       its fine for now, upgrades will be seeing breaking changes anyway
    private static final String ON_ITEM_KEY = "onItem";

    private final Map<String, IDarkSteelUpgrade> upgrades = new HashMap<>();

    /**
     * The type of item that is upgradable, used to determine valid upgrades.
     */
    private ResourceLocation onItem;

    public DarkSteelUpgradeable() {
        this(EnderIO.loc("empty"));
    }

    public DarkSteelUpgradeable(ResourceLocation onItem) {
        this.onItem = onItem;
    }

    @Override
    public void addUpgrade(IDarkSteelUpgrade upgrade) {
        removeUpgradeInSlot(upgrade.getSlot());
        upgrades.put(upgrade.getName(), upgrade);
    }

    @Override
    public void removeUpgrade(String name) {
        upgrades.remove(name);
    }

    private void removeUpgradeInSlot(String slot) {
        for (var entry : upgrades.entrySet()) {
            if (entry.getValue().getSlot().equals(slot)) {
                upgrades.remove(entry.getKey());
                break;
            }
        }
    }

    @Override
    public boolean canApplyUpgrade(IDarkSteelUpgrade upgrade) {
        if (upgrades.isEmpty()) {
            return EmpoweredUpgrade.NAME.equals(upgrade.getName()) && upgrade.isBaseTier();
        }

        Optional<IDarkSteelUpgrade> existing = getUpgrade(upgrade.getName());
        if (existing.isPresent()) {
            return existing.get().isValidUpgrade(upgrade);
        }
        if (!upgrade.isBaseTier()) {
            return false;
        }
        return DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onItem).contains(upgrade.getName());
    }

    @Override
    public Optional<IDarkSteelUpgrade> getUpgrade(String upgrade) {
        return Optional.ofNullable(upgrades.get(upgrade));
    }

    @Override
    public Collection<IDarkSteelUpgrade> getUpgrades() {
        return upgrades.values();
    }

    @Override
    public boolean hasUpgrade(String upgrade) {
        return upgrades.containsKey(upgrade);
    }

    @Override
    public Collection<IDarkSteelUpgrade> getUpgradesApplicable() {
        if (upgrades.isEmpty()) {
            return List.of(EmpoweredUpgradeTier.ONE.getFactory().get());
        }
        final List<IDarkSteelUpgrade> result = new ArrayList<>();
        upgrades.values().forEach(upgrade -> upgrade.getNextTier().ifPresent(result::add));

        getAllPossibleUpgrades().forEach(upgrade -> {
            if (!hasUpgrade(upgrade.getName())) {
                result.add(upgrade);
            }
        });
        return result;
    }

    @Override
    public Collection<IDarkSteelUpgrade> getAllPossibleUpgrades() {
        Set<String> upgradeNames = DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onItem);
        final List<IDarkSteelUpgrade> result = new ArrayList<>();
        upgradeNames.forEach(s -> DarkSteelUpgradeRegistry.instance().createUpgrade(s).ifPresent(result::add));
        return result;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (var entry : upgrades.entrySet()) {
            tag.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        tag.putString(ON_ITEM_KEY, onItem.toString());
        return tag;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        upgrades.clear();
        if (tag instanceof CompoundTag nbt) {
            for (String key : nbt.getAllKeys()) {
                DarkSteelUpgradeRegistry.instance().createUpgrade(key).ifPresent(upgrade -> {
                    upgrade.deserializeNBT(Objects.requireNonNull(nbt.get(key)));
                    addUpgrade(upgrade);
                });
            }
            onItem = new ResourceLocation(nbt.getString(ON_ITEM_KEY));
        }
    }

    // endregion
}

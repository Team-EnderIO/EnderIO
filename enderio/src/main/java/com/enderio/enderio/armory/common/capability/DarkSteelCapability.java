package com.enderio.enderio.armory.common.capability;

import com.enderio.enderio.api.armory.capability.DarkSteelUpgrade;
import com.enderio.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgradeTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DarkSteelCapability implements com.enderio.enderio.api.armory.capability.DarkSteelCapability {

    private final ItemStack onStack;

    private final Map<String, DarkSteelUpgrade> upgrades = new HashMap<>();

    public DarkSteelCapability(ItemStack onStack) {
        this.onStack = onStack;
        @Nullable
        DarkSteelItemUpgrades tmp = onStack.get(ArmoryDataComponents.DARK_STEEL_ITEM_UPGRADES);
        if (tmp != null) {
            for (UpgradeData data : tmp.upgradesData) {
                DarkSteelUpgrade up = createUpgrade(data.upgradeName, data.data.copyTag());
                if (up != null) {
                    upgrades.put(up.getName(), up);
                }
            }
        }
    }

    @Override
    public void addUpgrade(DarkSteelUpgrade upgrade) {
        removeUpgradeInSlot(upgrade.getSlot());
        upgrades.put(upgrade.getName(), upgrade);
        upgrade.onAddedToItem(onStack);
        updateComponent();
    }

    @Override
    public void removeUpgrade(String name) {
        if (!upgrades.containsKey(name)) {
            return;
        }

        DarkSteelUpgrade upgrade = upgrades.remove(name);
        upgrade.onRemovedFromItem(onStack);
        updateComponent();
    }

    @Override
    public boolean canApplyUpgrade(DarkSteelUpgrade upgrade) {
        if (upgrades.isEmpty()) {
            return EmpoweredUpgrade.NAME.equals(upgrade.getName()) && upgrade.isBaseTier();
        }

        Optional<DarkSteelUpgrade> existing = getUpgrade(upgrade.getName());
        if (existing.isPresent()) {
            return existing.get().isValidUpgrade(upgrade);
        }
        if (!upgrade.isBaseTier()) {
            return false;
        }
        return DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onStack).contains(upgrade.getName());
    }

    @Override
    public <T extends DarkSteelUpgrade> Optional<T> getUpgradeAs(String upgradeName, Class<T> as) {
        return getUpgrade(upgradeName).filter(as::isInstance).map(as::cast);
    }

    @Override
    public Optional<DarkSteelUpgrade> getUpgrade(String upgrade) {
        return Optional.ofNullable(upgrades.get(upgrade));
    }

    @Override
    public Collection<DarkSteelUpgrade> getUpgrades() {
        return upgrades.values();
    }

    @Override
    public boolean hasUpgrade(String upgrade) {
        return upgrades.containsKey(upgrade);
    }

    @Override
    public Collection<DarkSteelUpgrade> getUpgradesApplicable() {
        if (upgrades.isEmpty()) {
            return List.of(EmpoweredUpgradeTier.ONE.getFactory().get());
        }
        final List<DarkSteelUpgrade> result = new ArrayList<>();
        upgrades.values().forEach(upgrade -> upgrade.getNextTier().ifPresent(result::add));
        DarkSteelUpgradeRegistry.instance().createAllUpgradesForItem(onStack).forEach(upgrade -> {
            if (!hasUpgrade(upgrade.getName())) {
                result.add(upgrade);
            }
        });
        return result;
    }

    private void updateComponent() {
        List<UpgradeData> newData = new ArrayList<>();
        for (DarkSteelUpgrade up : upgrades.values()) {
            UpgradeData d = new UpgradeData(up.getName(), CustomData.of(up.serializeNBT()));
            newData.add(d);
        }
        onStack.set(ArmoryDataComponents.DARK_STEEL_ITEM_UPGRADES, new DarkSteelItemUpgrades(newData));
    }

    @javax.annotation.Nullable
    private DarkSteelUpgrade createUpgrade(String name, CompoundTag data) {
        Optional<DarkSteelUpgrade> upgrade = DarkSteelUpgradeRegistry.instance().createUpgrade(name);
        if (upgrade.isPresent()) {
            upgrade.get().deserializeNBT(data);
            return upgrade.get();
        }
        return null;
    }

    private void removeUpgradeInSlot(String slot) {
        for (var entry : upgrades.entrySet()) {
            if (entry.getValue().getSlot().equals(slot)) {
                upgrades.remove(entry.getKey());
                break;
            }
        }
    }

    public record UpgradeData(String upgradeName, CustomData data) {
    }

    public record DarkSteelItemUpgrades(List<UpgradeData> upgradesData) {

        public static final Codec<UpgradeData> UPGRADE_DATA_CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Codec.STRING.fieldOf("upgradeName").forGetter(UpgradeData::upgradeName),
                        CustomData.CODEC.fieldOf("data").forGetter(UpgradeData::data))
                .apply(instance, UpgradeData::new));

        public static final Codec<List<UpgradeData>> UPGRADE_LIST_CODEC = Codec.list(UPGRADE_DATA_CODEC);

        public static final Codec<DarkSteelItemUpgrades> ITEM_UPGRADES_CODEC = RecordCodecBuilder
                .create(instance -> instance
                        .group(UPGRADE_LIST_CODEC.fieldOf("upgradesData")
                                .forGetter(DarkSteelItemUpgrades::upgradesData))
                        .apply(instance, DarkSteelItemUpgrades::new));

    }

}

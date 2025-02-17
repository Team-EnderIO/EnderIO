package com.enderio.armory.common.capability;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgradeTier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class DarkSteelCapability implements IDarkSteelCapability {

    private final ItemStack onStack;

    private final Supplier<DataComponentType<DarkSteelItemUpgrades>> componentType;

    private final Map<String, IDarkSteelUpgrade> upgrades = new HashMap<>();

    public DarkSteelCapability(Supplier<DataComponentType<DarkSteelCapability.DarkSteelItemUpgrades>> componentType,
            ItemStack onStack) {
        this.componentType = componentType;
        this.onStack = onStack;
        @Nullable
        DarkSteelItemUpgrades tmp = onStack.get(ArmoryDataComponents.DARK_STEEL_ITEM_UPGRADES);
        if (tmp != null) {
            for (UpgradeData data : tmp.upgradesData) {
                IDarkSteelUpgrade up = DarkSteelUpgradeRegistry.instance()
                        .loadUpgrade(data.upgradeName, data.data.copyTag());
                if (up != null) {
                    upgrades.put(up.getName(), up);
                }
            }
        }
    }

    @Override
    public void addUpgrade(IDarkSteelUpgrade upgrade) {
        removeUpgradeInSlot(upgrade.getSlot());
        upgrades.put(upgrade.getName(), upgrade);
        upgrade.onAddedToItem(onStack);
        updateData();
    }

    @Override
    public void removeUpgrade(String name) {
        IDarkSteelUpgrade upgrade = upgrades.remove(name);
        upgrade.onRemovedFromItem(onStack);
        updateData();
    }

    private void updateData() {
        List<UpgradeData> newData = new ArrayList<>();
        for (IDarkSteelUpgrade up : upgrades.values()) {
            UpgradeData d = new UpgradeData(up.getName(), CustomData.of(up.serializeNBT()));
            newData.add(d);
        }
        onStack.set(componentType, new DarkSteelItemUpgrades(newData));
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
        return DarkSteelUpgradeRegistry.instance().getUpgradesForItem(onStack).contains(upgrade.getName());
    }

    @Override
    public <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(String upgradeName, Class<T> as) {
        return getUpgrade(upgradeName).filter(as::isInstance).map(as::cast);
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
        DarkSteelUpgradeRegistry.instance().createAllUpgradesForItem(onStack).forEach(upgrade -> {
            if (!hasUpgrade(upgrade.getName())) {
                result.add(upgrade);
            }
        });
        return result;
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

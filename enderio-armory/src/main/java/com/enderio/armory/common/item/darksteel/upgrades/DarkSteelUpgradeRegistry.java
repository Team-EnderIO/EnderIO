package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.GliderUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.base.api.EnderIO;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class DarkSteelUpgradeRegistry {

    public static final String UPGRADE_PREFIX = EnderIO.NAMESPACE + ".darksteel.upgrade.";

    private static final DarkSteelUpgradeRegistry INST = new DarkSteelUpgradeRegistry();

    static {
        INST.registerUpgrade(EmpoweredUpgrade::new);
        INST.registerUpgrade(SpoonUpgrade::new);
        INST.registerUpgrade(ForkUpgrade::new);
        INST.registerUpgrade(DirectUpgrade::new);
        INST.registerUpgrade(ExplosiveUpgrade::new);
        INST.registerUpgrade(ExplosivePenetrationUpgrade::new);
        INST.registerUpgrade(TravelUpgrade::new);
        INST.registerUpgrade(StepAssistUpgrade::new);
        INST.registerUpgrade(SpeedUpgrade::new);
        INST.registerUpgrade(JumpUpgrade::new);
        INST.registerUpgrade(GliderUpgrade::new);
        INST.registerUpgrade(ElytraUpgrade::new);
    }

    public static DarkSteelUpgradeRegistry instance() {
        return INST;
    }

    private final Map<String, Supplier<IDarkSteelUpgrade>> registeredUpgrades = new HashMap<>();

    private final Map<TagKey<Item>, Set<String>> possibleUpgrades = new HashMap<>();

    private DarkSteelUpgradeRegistry() {
    }

    public void registerUpgrade(Supplier<IDarkSteelUpgrade> upgrade) {
        registeredUpgrades.put(upgrade.get().getName(), upgrade);
    }

    public Optional<IDarkSteelUpgrade> createUpgrade(String name) {
        Supplier<IDarkSteelUpgrade> val = registeredUpgrades.get(name);
        if (val == null) {
            return Optional.empty();
        }
        return Optional.of(val.get());
    }

    @javax.annotation.Nullable
    public IDarkSteelUpgrade loadUpgrade(String name, CompoundTag data) {
        Optional<IDarkSteelUpgrade> upgrade = createUpgrade(name);
        if (upgrade.isPresent()) {
            upgrade.get().deserializeNBT(data);
            return upgrade.get();
        }
        return null;
    }

    public boolean hasUpgrade(ItemStack stack) {
        return !stack.isEmpty() && stack.has(ArmoryDataComponents.DARK_STEEL_UPGRADE);
    }

    public void registerUpgradesForItem(TagKey<Item> forItem, String... upgrades) {
        Set<String> currentValues = possibleUpgrades.getOrDefault(forItem, new HashSet<>());
        Collections.addAll(currentValues, upgrades);
        possibleUpgrades.put(forItem, currentValues);
    }

    public Set<String> getUpgradesForItem(ItemStack stack) {
        Set<String> result = new HashSet<>();
        possibleUpgrades.forEach((tag, value) -> {
            if (stack.is(tag)) {
                result.addAll(value);
            }
        });
        return Collections.unmodifiableSet(result);
    }

}

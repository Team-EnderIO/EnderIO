package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosivePenetrationUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.explosive.ExplosiveUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.GliderUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.jump.JumpUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisisionUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.base.api.EnderIO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class DarkSteelUpgradeRegistry {

    public static final String UPGRADE_PREFIX = EnderIO.NAMESPACE + ".darksteel.upgrade.";

    private static final DarkSteelUpgradeRegistry INST = new DarkSteelUpgradeRegistry();

    public static DarkSteelUpgradeRegistry instance() {
        return INST;
    }

    private final Map<String, Supplier<IDarkSteelUpgrade>> registeredUpgrades = new HashMap<>();

    private final Map<TagKey<Item>, Set<String>> upgradeSupport = new HashMap<>();

    private DarkSteelUpgradeRegistry() {
        registerUpgrade(EmpoweredUpgrade::new);
        registerUpgrade(SpoonUpgrade::new);
        registerUpgrade(ForkUpgrade::new);
        registerUpgrade(DirectUpgrade::new);
        registerUpgrade(ExplosiveUpgrade::new);
        registerUpgrade(ExplosivePenetrationUpgrade::new);
        registerUpgrade(TravelUpgrade::new);
        registerUpgrade(StepAssistUpgrade::new);
        registerUpgrade(SpeedUpgrade::new);
        registerUpgrade(JumpUpgrade::new);
        registerUpgrade(GliderUpgrade::new);
        registerUpgrade(ElytraUpgrade::new);
        registerUpgrade(NightVisisionUpgrade::new);
        registerUpgrade(SolarUpgrade::new);
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

    public void registerUpgradesForItem(TagKey<Item> forItem, String... upgrades) {
        Set<String> currentValues = upgradeSupport.getOrDefault(forItem, new HashSet<>());
        Collections.addAll(currentValues, upgrades);
        upgradeSupport.put(forItem, currentValues);
    }

    public Set<String> getUpgradesForItem(ItemStack stack) {
        Set<String> result = new HashSet<>();
        upgradeSupport.forEach((tag, value) -> {
            if (stack.is(tag)) {
                result.addAll(value);
            }
        });
        return Collections.unmodifiableSet(result);
    }

    public Collection<IDarkSteelUpgrade> createAllUpgradesForItem(ItemStack stack) {
        Set<String> upgradeNames = getUpgradesForItem(stack);
        final List<IDarkSteelUpgrade> result = new ArrayList<>();
        upgradeNames.forEach(s -> createUpgrade(s).ifPresent(result::add));
        return result;
    }

}

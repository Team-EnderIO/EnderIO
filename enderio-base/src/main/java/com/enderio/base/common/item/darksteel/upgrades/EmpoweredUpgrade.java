package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.config.base.BaseConfig;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.energy.EnergyStorage;

import java.util.*;
import java.util.function.Supplier;

public class EmpoweredUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "empowered";
    public static final String TIER_KEY = "tier";
    public static final String STORAGE_KEY = "storage";

    public enum Tier {

        ONE(BaseConfig.COMMON.ITEMS.EMPOWERED_MAX_ENERGY_I,
            BaseConfig.COMMON.ITEMS.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I,
            BaseConfig.COMMON.ITEMS.EMPOWERED_ACTIVATION_COST_I,
            EIOLang.DS_UPGRADE_EMPOWERED_I),
        TWO(BaseConfig.COMMON.ITEMS.EMPOWERED_MAX_ENERGY_II,
            BaseConfig.COMMON.ITEMS.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II,
            BaseConfig.COMMON.ITEMS.EMPOWERED_ACTIVATION_COST_II,
            EIOLang.DS_UPGRADE_EMPOWERED_II),
        THREE(BaseConfig.COMMON.ITEMS.EMPOWERED_MAX_ENERGY_III,
            BaseConfig.COMMON.ITEMS.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III,
            BaseConfig.COMMON.ITEMS.EMPOWERED_ACTIVATION_COST_III,
            EIOLang.DS_UPGRADE_EMPOWERED_III),
        FOUR(BaseConfig.COMMON.ITEMS.EMPOWERED_MAX_ENERGY_IV,
            BaseConfig.COMMON.ITEMS.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV,
            BaseConfig.COMMON.ITEMS.EMPOWERED_ACTIVATION_COST_IV,
            EIOLang.DS_UPGRADE_EMPOWERED_IV);

        private final Supplier<EmpoweredUpgrade> factory;
        private final ForgeConfigSpec.ConfigValue<Integer> maxStorage;
        private final ForgeConfigSpec.ConfigValue<Float> damageAbsorptionChance;
        private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
        private final Component displayName;

        Tier(ForgeConfigSpec.ConfigValue<Integer> maxStorage, ForgeConfigSpec.ConfigValue<Float> damageAbsorptionChance,
            ForgeConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
            this.maxStorage = maxStorage;
            this.damageAbsorptionChance = damageAbsorptionChance;
            this.activationCost = activationCost;
            this.displayName = displayName;
            factory = () -> new EmpoweredUpgrade(this);
        }

        public int getMaxStorage() {
            return maxStorage.get();
        }

        public float getDamageAbsorptionChance() {
            return damageAbsorptionChance.get();
        }

        public Supplier<EmpoweredUpgrade> getFactory() {
            return factory;
        }

        public ForgeConfigSpec.ConfigValue<Integer> getActivationCost() { return activationCost; }

        public Component getDisplayName() { return displayName; }
    }

    private static Optional<EmpoweredUpgrade> getUpgradeForTier(int tier) {
        return getConfigForTier(tier).map(config -> config.getFactory().get());
    }

    private static Optional<Tier> getConfigForTier(int tier) {
        if (tier >= Tier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(Tier.values()[tier]);
    }

    private static final Random RANDOM = new Random();

    private final ForgeConfigSpec.ConfigValue<Integer> speedBoostWhenPowered = BaseConfig.COMMON.ITEMS.EMPOWERED_EFFICIENCY_BOOST;

    private final ForgeConfigSpec.ConfigValue<Integer> powerUsePerDamagePoint = BaseConfig.COMMON.ITEMS.EMPOWERED_ENERGY_PER_DAMAGE;

    private Tier tier;

    private EnergyStorage storage;

    public EmpoweredUpgrade() {
        this(Tier.ONE);
    }

    public EmpoweredUpgrade(Tier tier) {
        this.tier = tier;
        storage = new EnergyStorage(tier.getMaxStorage());
    }

    public float adjustDestroySpeed(float speed) {
        if (storage.getEnergyStored() > 0) {
            speed += speedBoostWhenPowered.get();
        }
        return speed;
    }

    public int adjustDamage(int oldDamage, int newDamage) {
        int damageTaken = newDamage - oldDamage;
        if (damageTaken > 0 && storage.getEnergyStored() > 0 && RANDOM.nextDouble() < tier.getDamageAbsorptionChance()) {
            storage.extractEnergy(damageTaken * powerUsePerDamagePoint.get(), false);
            return oldDamage;
        }
        return newDamage;
    }

    @Override
    public boolean isBaseTier() {
        return tier.ordinal() == 0;
    }

    @Override
    public Optional<? extends IDarkSteelUpgrade> getNextTier() {
        return getUpgradeForTier(tier.ordinal() + 1);
    }

    @Override
    public boolean isValidUpgrade(IDarkSteelUpgrade upgrade) {
        if (upgrade instanceof EmpoweredUpgrade eu) {
            return eu.tier.ordinal() == tier.ordinal() + 1;
        }
        return false;
    }

    public EnergyStorage getStorage() {
        return storage;
    }

    @Override
    public String getSerializedName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return tier.getDisplayName();
    }

    @Override
    public Collection<Component> getDescription() {
        List<Component> result = new ArrayList<>();
        result.add(EIOLang.DS_UPGRADE_EMPOWERED_DESCRIPTION);
        result.add(TooltipUtil.withArgs(EIOLang.DS_UPGRADE_EMPOWERED_STORAGE, String.format("%,d", tier.getMaxStorage())));
        result.add(TooltipUtil.withArgs(EIOLang.DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION, (int)(tier.getDamageAbsorptionChance() * 100)));
        return result;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TIER_KEY, tier.ordinal());
        nbt.put(STORAGE_KEY, storage.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            int level = nbt.getInt(TIER_KEY);
            tier = getConfigForTier(level).orElse(Tier.ONE);
            storage = new EnergyStorage(tier.getMaxStorage());
            storage.deserializeNBT(nbt.get(STORAGE_KEY));
        }
    }
}

package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.base.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.base.config.base.BaseConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.enderio.base.common.lang.EIOLang.*;

public class ExplosiveRadiusUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive_radius";

    public static final String TIER_KEY = "tier";

    public enum Tier {
        ONE(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_RADIUS_I,
            BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_RADIUS_ACTIVATION_COST_I,
            DS_UPGRADE_EXPLOSIVE_I),
        TWO(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_RADIUS_II,
            BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_RADIUS_ACTIVATION_COST_II,
            DS_UPGRADE_EXPLOSIVE_II)
        ;

        private final Supplier<ExplosiveRadiusUpgrade> factory;
        private final ForgeConfigSpec.ConfigValue<Integer> magnitude;
        private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
        private final Component displayName;

        Tier(ForgeConfigSpec.ConfigValue<Integer> magnitude, ForgeConfigSpec.ConfigValue<Integer> activationCost,
            Component displayName) {
            this.magnitude = magnitude;
            this.activationCost = activationCost;
            this.displayName = displayName;
            factory = () -> new ExplosiveRadiusUpgrade(this);
        }

        public ForgeConfigSpec.ConfigValue<Integer> getMagnitude() { return magnitude;}

        public Supplier<ExplosiveRadiusUpgrade> getFactory() {
            return factory;
        }

        public ForgeConfigSpec.ConfigValue<Integer> getActivationCost() { return activationCost; }

        public Component getDisplayName() { return displayName; }
    }

    private Tier tier;

    public ExplosiveRadiusUpgrade() {
        this(Tier.ONE);
    }

    public ExplosiveRadiusUpgrade(Tier tier) {
        this.tier = tier;
    }

    public int getMagnitude() {
        return tier.getMagnitude().get();
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
        if (upgrade instanceof ExplosiveRadiusUpgrade up) {
            return up.tier.ordinal() == tier.ordinal() + 1;
        }
        return false;
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
        return List.of(DS_UPGRADE_EXPLOSIVE_DESCRIPTION);
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TIER_KEY, tier.ordinal());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            int level = nbt.getInt(TIER_KEY);
            tier = getConfigForTier(level).orElse(Tier.ONE);
        }
    }

    private static Optional<Tier> getConfigForTier(int tier) {
        if (tier >= Tier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(Tier.values()[tier]);
    }
    private static Optional<ExplosiveRadiusUpgrade> getUpgradeForTier(int tier) {
        return getConfigForTier(tier).map(config -> config.getFactory().get());
    }
}

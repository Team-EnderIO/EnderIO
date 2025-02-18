package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public abstract class TieredUpgrade<T extends IUpgradeTier> implements IDarkSteelUpgrade {

    public static final String TIER_KEY = "tier";

    protected T tier;
    private final String serializedName;

    protected TieredUpgrade(T tier, String serializedName) {
        this.tier = tier;
        this.serializedName = serializedName;
    }

    @Override
    public boolean isBaseTier() {
        return tier.getLevel() == 0;
    }

    @Override
    public Optional<? extends IDarkSteelUpgrade> getNextTier() {
        return getUpgradeForTier(tier.getLevel() + 1);
    }

    @Override
    public Optional<? extends IUpgradeTier> getTier() {
        return Optional.of(tier);
    }

    @Override
    public boolean isValidUpgrade(IDarkSteelUpgrade upgrade) {
        if (getName().equals(upgrade.getName()) && upgrade instanceof TieredUpgrade<?> up) {
            return up.tier.getLevel() == tier.getLevel() + 1;
        }
        return false;
    }

    @Override
    public String getName() {
        return serializedName;
    }

    @Override
    public Component getDisplayName() {
        return tier.getDisplayName();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(TIER_KEY, tier.getLevel());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt) {
            int level = nbt.getInt(TIER_KEY);
            tier = getTier(level).orElse(getBaseTier());
        }
    }

    protected Optional<? extends IDarkSteelUpgrade> getUpgradeForTier(int level) {
        return getTier(level).map(upgradeTier -> upgradeTier.getFactory().get());
    }

    protected abstract T getBaseTier();

    protected abstract Optional<T> getTier(int tier);

}

package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.enderio.base.common.lang.EIOLang.DS_UPGRADE_EXPLOSIVE_DESCRIPTION;

public abstract class AbstractTieredUpgrade<T extends IUpgradeTier> implements IDarkSteelUpgrade {

    public static final String TIER_KEY = "tier";

    protected T tier;
    private final String serializedName;

    protected AbstractTieredUpgrade(T tier, String serializedName) {
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
    public boolean isValidUpgrade(IDarkSteelUpgrade upgrade) {
        if (getSerializedName().equals(upgrade.getSerializedName()) && upgrade instanceof AbstractTieredUpgrade up) {
            return up.tier.getLevel() == tier.getLevel() + 1;
        }
        return false;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
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

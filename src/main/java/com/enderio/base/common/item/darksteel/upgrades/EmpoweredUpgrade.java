package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.energy.EnergyStorage;

import java.util.*;

public class EmpoweredUpgrade extends TieredUpgrade<EmpoweredUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "empowered";
    public static final String STORAGE_KEY = "storage";

    private static final Random RANDOM = new Random();

    private final ForgeConfigSpec.ConfigValue<Integer> speedBoostWhenPowered = BaseConfig.COMMON.DARK_STEEL.EMPOWERED_EFFICIENCY_BOOST;

    private final ForgeConfigSpec.ConfigValue<Integer> powerUsePerDamagePoint = BaseConfig.COMMON.DARK_STEEL.EMPOWERED_ENERGY_PER_DAMAGE;

    private EnergyStorage storage;

    public EmpoweredUpgrade() {
        this(EmpoweredUpgradeTier.ONE);
    }

    public EmpoweredUpgrade(EmpoweredUpgradeTier tier) {
        super(tier, NAME);
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

    public EnergyStorage getStorage() {
        return storage;
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
    protected EmpoweredUpgradeTier getBaseTier() {
        return EmpoweredUpgradeTier.ONE;
    }

    @Override
    protected Optional<EmpoweredUpgradeTier> getTier(int tier) {
        if (tier >= EmpoweredUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(EmpoweredUpgradeTier.values()[tier]);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = super.serializeNBT();
        nbt.put(STORAGE_KEY, storage.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        super.deserializeNBT(tag);
        if (tag instanceof CompoundTag nbt) {
            storage = new EnergyStorage(tier.getMaxStorage());
            storage.deserializeNBT(nbt.get(STORAGE_KEY));
        }
    }
}

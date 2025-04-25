package com.enderio.armory.common.item.darksteel.upgrades.empowered;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EmpoweredUpgrade extends TieredUpgrade<EmpoweredUpgradeTier> {

    public static int getAdjustedDamage(ItemStack stack, int newDamage) {
        return DarkSteelHelper.getUpgradeAs(stack, EmpoweredUpgrade.NAME, EmpoweredUpgrade.class)
                .map(empoweredUpgrade -> empoweredUpgrade.adjustDamage(stack.getItem().getDamage(stack), newDamage,
                        stack))
                .orElse(newDamage);
    }

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "empowered";

    private static final Random RANDOM = new Random();

    private final ModConfigSpec.ConfigValue<Integer> speedBoostWhenPowered = ArmoryConfig.COMMON.EMPOWERED_EFFICIENCY_BOOST;

    private final ModConfigSpec.ConfigValue<Integer> powerUsePerDamagePoint = ArmoryConfig.COMMON.EMPOWERED_ENERGY_PER_DAMAGE;

    public EmpoweredUpgrade() {
        this(EmpoweredUpgradeTier.ONE);
    }

    public EmpoweredUpgrade(EmpoweredUpgradeTier tier) {
        super(tier, NAME);
    }

    public float adjustDestroySpeed(float speed, ItemStack pStack) {
        if (ItemStackEnergy.getEnergyStored(pStack) > 0) {
            speed += speedBoostWhenPowered.get();
        }
        return speed;
    }

    public int adjustDamage(int oldDamage, int newDamage, ItemStack pStack) {
        int damageTaken = newDamage - oldDamage;
        if (damageTaken > 0 && ItemStackEnergy.getEnergyStored(pStack) > 0
                && RANDOM.nextDouble() < tier.getDamageAbsorptionChance()) {
            ItemStackEnergy.extractEnergy(pStack, damageTaken * powerUsePerDamagePoint.get(), false);
            return oldDamage;
        }
        return newDamage;
    }

    public int getMaxEnergyStored() {
        return tier.getMaxStorage();
    }

    public int getMaxEnergyTransfer() {
        return tier.getMaxStorage();
    }

    public int getAttackDamageIncrease() {
        return tier.getAttackDamageIncrease();
    }

    public double getAttackSpeedIncrease() {
        return tier.getAttackSpeedIncrease();
    }

    public int getLevel() {
        return tier.getLevel();
    }

    public double getMobHeadChance() {
        return tier.getMobHeadChance();
    }

    @Override
    public Collection<Component> getDescription() {
        List<Component> result = new ArrayList<>();
        result.add(ArmoryLang.DS_UPGRADE_EMPOWERED_DESCRIPTION);
        result.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_STORAGE,
                String.format("%,d", tier.getMaxStorage())));
        result.add(TooltipUtil.withArgs(ArmoryLang.DS_UPGRADE_EMPOWERED_DAMAGE_ABSORPTION,
                (int) (tier.getDamageAbsorptionChance() * 100)));
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

}

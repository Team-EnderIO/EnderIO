package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.common.config.ArmoryConfig;
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

//    @Override
//    public void onAddedToItem(ItemStack stack) {
//
//        if(true) {
//            return;
//        }
//
//        //This works but doesn't take into account having power
//        if(stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)) {
//            ItemAttributeModifiers curMOds = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
//            ItemAttributeModifiers adjusted = curMOds.withModifierAdded(Attributes.ATTACK_DAMAGE,
//                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 420, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
//            adjusted = adjusted.withModifierAdded(Attributes.ATTACK_SPEED,
//                new AttributeModifier(BASE_ATTACK_SPEED_ID, 99, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
//            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, adjusted);
//        }
//    }

//    @Override
//    public void onRemovedFromItem(ItemStack stack) {
//
//        if(true) {
//            return;
//        }
//
//        //This works but doesn't take into account having power
//        if(stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)) {
//            ItemStack defaultStack = new ItemStack(stack.getItem());
//            ItemAttributeModifiers defaultMods = defaultStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
////            defaultMods.forEach(MAINHAND, (att, mod) -> {
////
////                AttributeModifier toRemove = new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 420, AttributeModifier.Operation.ADD_VALUE);
////                if(toRemove.equals(mod)) {
////                    System.out.println("Found mod to remove");
////                }
////            });
//            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, defaultMods);
//        }
//    }

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

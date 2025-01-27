package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgradeTier;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import org.jetbrains.annotations.Nullable;

public class DarkSteelSwordItem extends SwordItem implements AdvancedTooltipProvider, IDarkSteelItem {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD, EmpoweredUpgrade.NAME);
    }

    public DarkSteelSwordItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER,
                pProperties.attributes(createAttributes(ArmoryItems.DARK_STEEL_TIER, 3, -2.4F)));
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {

        Optional<EmpoweredUpgrade> empUp = DarkSteelCapability.getEmpoweredUpgrade(pStack);
        if (empUp.isEmpty()) {
            return super.hurtEnemy(pStack, pTarget, pAttacker);
        }
        EmpoweredUpgradeTier tier = empUp.get().getEmpoweredTier();
        if (pTarget.isDeadOrDying() && Math.random() < tier.getMobHeadChance()) {
            Optional<ItemStack> skull = getSkull(pTarget);
            skull.ifPresent(itemStack -> Containers.dropItemStack(pAttacker.level(), pAttacker.position().x,
                    pAttacker.position().y, pAttacker.position().z, itemStack));
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    // Could use this instead and only add damage if we have power
//    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
//        ItemStack stack = damageSource.getWeaponItem();
//        if(stack == null) {
//            return 0;
//        }
//        return getEmpoweredUpgrade(stack)
//            .map(empoweredUpgrade -> empoweredUpgrade.getEmpoweredTier().getAttackDamageIncrease())
//            .orElse(0);
//    }

    public static void addUpgradeModifiers(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        Optional<EmpoweredUpgrade> empUp = DarkSteelCapability.getEmpoweredUpgrade(stack);
        if (empUp.isEmpty()) {
            return;
        }
        EmpoweredUpgradeTier tier = empUp.get().getEmpoweredTier();
        e.addModifier(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE,
                                "the_ender_attack_boost_" + tier.getLevel()),
                        tier.getAttackDamageIncrease(), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
        e.addModifier(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE,
                                "the_ender_attack_speed_boost_" + tier.getLevel()),
                        tier.getAttackSpeedIncrease(), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        int finalDamage = getEmpoweredUpgrade(stack)
                .map(empoweredUpgrade -> empoweredUpgrade.adjustDamage(getDamage(stack), newDamage, stack))
                .orElse(newDamage);
        super.setDamage(stack, finalDamage);
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        if (DarkSteelCapability.getEmpoweredUpgrade(itemStack).isEmpty()) {
//            tooltips.add(Component.literal("Cuts off mob heads once Empowered"));
            tooltips.add(TooltipUtil.style(ArmoryLang.HEAD_DROP_INFO));
        }
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        Optional<EmpoweredUpgrade> empUp = DarkSteelCapability.getEmpoweredUpgrade(itemStack);
        if (empUp.isPresent()) {
            EmpoweredUpgradeTier tier = empUp.get().getEmpoweredTier();
            tooltips.add(
                    TooltipUtil.withArgs(ArmoryLang.HEAD_DROP_CHANCE, (int) Math.round(tier.getMobHeadChance() * 100)));
        }
        addDurabilityTooltips(itemStack, tooltips);
        addCurrentUpgradeTooltips(itemStack, tooltips, true);
        addAvailableUpgradesTooltips(itemStack, tooltips);
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);

        ItemStack fullyUpgraded = createFullyUpgradedStack(this);
        ItemStackEnergy.setFull(fullyUpgraded);
        modifier.accept(fullyUpgraded);
    }

    private static Optional<ItemStack> getSkull(LivingEntity pTarget) {
        if (pTarget.getType() == EntityType.SKELETON || pTarget.getType() == EntityType.STRAY) {
            return Optional.of(new ItemStack(Items.SKELETON_SKULL));
        }
        if (pTarget.getType() == EntityType.ZOMBIE || pTarget.getType() == EntityType.DROWNED
                || pTarget.getType() == EntityType.HUSK || pTarget.getType() == EntityType.ZOMBIE_VILLAGER) {
            return Optional.of(new ItemStack(Items.ZOMBIE_HEAD));
        }
        if (pTarget.getType() == EntityType.WITHER_SKELETON) {
            return Optional.of(new ItemStack(Items.WITHER_SKELETON_SKULL));
        }
        if (pTarget.getType() == EntityType.CREEPER) {
            return Optional.of(new ItemStack(Items.CREEPER_HEAD));
        }
        if (pTarget.getType() == EntityType.ENDER_DRAGON) {
            return Optional.of(new ItemStack(Items.DRAGON_HEAD));
        }
        if (pTarget.getType() == EntityType.ENDERMAN) {
            return Optional.of(new ItemStack(EIOBlocks.ENDERMAN_HEAD));
        }
        if (pTarget.getType() == EntityType.PIGLIN || pTarget.getType() == EntityType.PIGLIN_BRUTE
                || pTarget.getType() == EntityType.ZOMBIFIED_PIGLIN) {
            return Optional.of(new ItemStack(Items.PIGLIN_HEAD));
        }
        if (pTarget instanceof Player player) {
            ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
            stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
            return Optional.of(stack);
        }
        return Optional.empty();
    }

}

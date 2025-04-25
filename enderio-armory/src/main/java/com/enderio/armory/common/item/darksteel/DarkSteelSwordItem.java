package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryFeatureFlags;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.travel.TravelUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class DarkSteelSwordItem extends SwordItem implements AdvancedTooltipProvider, IDarkSteelItem {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD, EmpoweredUpgrade.NAME,
                        DirectUpgrade.NAME, TravelUpgrade.NAME);
    }

    /**
     * Stops an Enderman from teleporting is its last damage is from a DarkSteelSword
     * @param event may be canceled
     */
    public static void onEntityTeleport(EntityTeleportEvent event) {
        if (event.getEntity() instanceof EnderMan em) {
            if (em.getLastDamageSource() == null || em.getLastDamageSource().getWeaponItem() == null) {
                return;
            }
            if (em.getLastDamageSource().getWeaponItem().is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)) {
                event.setCanceled(true);
            }
        }
    }

    /**
     * Applies the damage and speed bonuses from Empowered Upgrades for a DarkSteelSword
     * @param e event
     */
    public static void applyAttackModifiers(ItemAttributeModifierEvent e) {
        ItemStack stack = e.getItemStack();
        if (!stack.is(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_SWORD)) {
            return;
        }
        Optional<EmpoweredUpgrade> empUpOpt = DarkSteelHelper.getEmpoweredUpgrade(stack);
        if (empUpOpt.isEmpty()) {
            return;
        }
        if (ItemStackEnergy.getEnergyStored(stack) > 0) {
            EmpoweredUpgrade empUp = empUpOpt.get();
            e.addModifier(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE,
                                    "the_ender_attack_boost_" + empUp.getLevel()),
                            empUp.getAttackDamageIncrease(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
            e.addModifier(Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE,
                                    "the_ender_attack_speed_boost_" + empUp.getLevel()),
                            empUp.getAttackSpeedIncrease(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND);
        }
    }

    public DarkSteelSwordItem(Properties pProperties) {
        super(ArmoryItems.DARK_STEEL_TIER,
                pProperties.attributes(createAttributes(ArmoryItems.DARK_STEEL_TIER, 3, -2.4F))
                        .component(EIODataComponents.TRAVEL_ITEM, false));
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (pTarget.level().enabledFeatures().contains(ArmoryFeatureFlags.ARMORY_REWRITE)) {
            Optional<EmpoweredUpgrade> empUp = DarkSteelHelper.getEmpoweredUpgrade(pStack);
            if (empUp.isEmpty() || ItemStackEnergy.getEnergyStored(pStack) <= 0) {
                return super.hurtEnemy(pStack, pTarget, pAttacker);
            }

            if (pTarget.isDeadOrDying() && Math.random() < empUp.get().getMobHeadChance()) {
                Optional<ItemStack> skull = getSkull(pTarget);
                skull.ifPresent(itemStack -> Containers.dropItemStack(pAttacker.level(), pAttacker.position().x,
                        pAttacker.position().y, pAttacker.position().z, itemStack));
            }
        } else {
            // Temporary head drop logic
            if (pTarget.isDeadOrDying() && pTarget.level().random.nextFloat() < 0.07) {
                Optional<ItemStack> skull = getSkull(pTarget);
                skull.ifPresent(itemStack -> Containers.dropItemStack(pAttacker.level(), pAttacker.position().x,
                        pAttacker.position().y, pAttacker.position().z, itemStack));
            }
        }

        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        super.setDamage(stack, EmpoweredUpgrade.getAdjustedDamage(stack, newDamage));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        @Nullable
        InteractionResultHolder<ItemStack> res = TravelUpgrade.onUse(level, player, usedHand, this);
        if (res != null) {
            return res;
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        @Nullable
        InteractionResult res = TravelUpgrade.onUse(context, this);
        if (res != null) {
            return res;
        }
        return super.useOn(context);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
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

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        // TODO: Remove null checks when feature checks are removed
        if (player != null) {
            if (player.level().enabledFeatures().contains(ArmoryFeatureFlags.ARMORY_REWRITE)) {
                if (DarkSteelHelper.getEmpoweredUpgrade(itemStack).isEmpty()) {
                    tooltips.add(TooltipUtil.style(ArmoryLang.ENDER_HEAD_DROP_INFO));
                }
            } else {
                tooltips.add(Component.literal("This item is currently only used to get mob heads"));
            }
        }
    }

    @Override
    public void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        // TODO: Remove null checks when feature checks are removed
        if (player != null) {
            if (player.level().enabledFeatures().contains(ArmoryFeatureFlags.ARMORY_REWRITE)) {
                Optional<EmpoweredUpgrade> empUp = DarkSteelHelper.getEmpoweredUpgrade(itemStack);
                empUp.ifPresent(empoweredUpgrade -> tooltips.add(TooltipUtil.withArgs(ArmoryLang.ENDER_HEAD_DROP_CHANCE,
                        (int) Math.round(empoweredUpgrade.getMobHeadChance() * 100))));
                empUp.ifPresent(empoweredUpgrade -> tooltips.add(TooltipUtil.style(ArmoryLang.ENDER_BLOCK_TELEPORT)));
                addDurabilityTooltips(itemStack, tooltips);
                addCurrentUpgradeTooltips(itemStack, tooltips, true);
                addAvailableUpgradesTooltips(itemStack, tooltips);
            } else {
                tooltips.add(TooltipUtil.withArgs(ArmoryLang.ENDER_HEAD_DROP_CHANCE, 7));
            }
        }
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);

        // TODO: Temporarily disabled to avoid confusion while locked behind an
        // experiment.
//        ItemStack fullyUpgraded = createFullyUpgradedStack(this);
//        ItemStackEnergy.setFull(fullyUpgraded);
//        modifier.accept(fullyUpgraded);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return DarkSteelHelper.hasUpgrade(pStack, EmpoweredUpgrade.NAME) || super.isFoil(pStack);
    }

}

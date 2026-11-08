package com.enderio.enderio.content.armory.items;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.ElytraFlyHook;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.WalkOnPowderedSnowHook;
import com.enderio.enderio.content.armory.DarkSteelHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Set;

public class DarkSteelArmorItem extends ArmorItem implements DarkSteelUpgradableItem {
    private static final Set<DarkSteelUpgradeHookType<?>> ARMOR_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.CAN_FREEZE
    );

    private static final Set<DarkSteelUpgradeHookType<?>> CHESTPLATE_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.ELYTRA_FLY,
        DarkSteelUpgradeHookTypes.CAN_FREEZE
    );

    private static final Set<DarkSteelUpgradeHookType<?>> BOOTS_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.CAN_FREEZE,
        DarkSteelUpgradeHookTypes.WALK_ON_POWDERED_SNOW
    );

    public DarkSteelArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public Set<DarkSteelUpgradeHookType<?>> availableHookTypes() {
        // Vanilla only checks the chest slot for elytra flight, and the feet slot for walking on powdered snow.
        return switch (getType()) {
            case CHESTPLATE -> CHESTPLATE_HOOKS;
            case BOOTS -> BOOTS_HOOKS;
            default -> ARMOR_HOOKS;
        };
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return WalkOnPowderedSnowHook.canWalkOnPowderedSnow(DarkSteelHelper.getUpgradeContainer(stack),
            DarkSteelHelper.getHookContext(stack), wearer);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return ElytraFlyHook.canElytraFly(DarkSteelHelper.getUpgradeContainer(stack), DarkSteelHelper.getHookContext(stack), entity);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        boolean continueFlying = ElytraFlyHook.elytraFlightTick(DarkSteelHelper.getUpgradeContainer(stack),
            DarkSteelHelper.getHookContext(stack), entity, flightTicks);

        // Mirror ElytraItem's vibration emission.
        if (continueFlying && !entity.level().isClientSide && (flightTicks + 1) % 10 == 0) {
            entity.gameEvent(GameEvent.ELYTRA_GLIDE);
        }

        return continueFlying;
    }
}

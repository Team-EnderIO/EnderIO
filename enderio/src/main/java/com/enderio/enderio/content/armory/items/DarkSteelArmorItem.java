package com.enderio.enderio.content.armory.items;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.ElytraFlyHook;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.WalkOnPowderedSnowHook;
import com.enderio.enderio.content.armory.DarkSteelHelper;
import com.enderio.enderio.content.armory.IDarkSteelItem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class DarkSteelArmorItem extends ArmorItem implements IDarkSteelItem, DarkSteelUpgradableItem {
    private static final Set<DarkSteelUpgradeHookType<?>> ARMOR_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.EQUIPMENT_TICK,
        DarkSteelUpgradeHookTypes.CAN_FREEZE
    );

    private static final Set<DarkSteelUpgradeHookType<?>> CHESTPLATE_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.EQUIPMENT_TICK,
        DarkSteelUpgradeHookTypes.ELYTRA_FLY,
        DarkSteelUpgradeHookTypes.CAN_FREEZE
    );

    private static final Set<DarkSteelUpgradeHookType<?>> BOOTS_HOOKS = Set.of(
        DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES,
        DarkSteelUpgradeHookTypes.EQUIPMENT_TICK,
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return DarkSteelCommonUtility.damageItem(stack, amount, entity, onBroken);
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

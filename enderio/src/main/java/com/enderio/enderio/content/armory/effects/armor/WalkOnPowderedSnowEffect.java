package com.enderio.enderio.content.armory.effects.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.WalkOnPowderedSnowHook;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Allows the wearer to walk on powdered snow, the same as leather boots. Only has an effect on boots.
 */
public class WalkOnPowderedSnowEffect extends DarkSteelUpgradeEffect implements WalkOnPowderedSnowHook {

    public static final WalkOnPowderedSnowEffect INSTANCE = new WalkOnPowderedSnowEffect();
    public static final MapCodec<WalkOnPowderedSnowEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.WALK_ON_POWDERED_SNOW);

    private WalkOnPowderedSnowEffect() {
    }

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return HOOKS;
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean canWalkOnPowderedSnow(DarkSteelHookContext context, LivingEntity wearer) {
        return true;
    }
}

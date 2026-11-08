package com.enderio.enderio.content.armory.effects.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.CanFreezeHook;
import com.mojang.serialization.MapCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Prevents the wearer from freezing in powdered snow, the same as items in {@link ItemTags#FREEZE_IMMUNE_WEARABLES}.
 */
public class PreventsFreezingEffect extends DarkSteelUpgradeEffect implements CanFreezeHook {

    public static final PreventsFreezingEffect INSTANCE = new PreventsFreezingEffect();
    public static final MapCodec<PreventsFreezingEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.CAN_FREEZE);

    private PreventsFreezingEffect() {
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
    public boolean canFreeze(DarkSteelHookContext context, LivingEntity entity) {
        return false;
    }
}

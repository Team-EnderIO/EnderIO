package com.enderio.enderio.api.armory.upgrades;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.List;
import java.util.function.Function;

// Based on Tinkers modules and hooks.
public abstract class DarkSteelUpgradeEffect {
    public static final Codec<DarkSteelUpgradeEffect> CODEC = EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT.byNameCodec()
        .dispatchStable(DarkSteelUpgradeEffect::codec, Function.identity());

    /**
     * This can return dynamic results to turn on and off hook behaviors.
     * @implNote returning a hook when the interface is not implemented by the effect will cause an exception.
     * @return a list of upgrade hook types that are exposed by this effect.
     */
    public abstract List<DarkSteelUpgradeHookType<?>> getActiveHooks();

    protected abstract MapCodec<? extends DarkSteelUpgradeEffect> codec();
}

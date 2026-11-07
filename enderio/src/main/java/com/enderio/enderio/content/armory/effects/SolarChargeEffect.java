package com.enderio.enderio.content.armory.effects;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.DarkSteelInventoryTickHook;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.List;

public class SolarChargeEffect extends DarkSteelUpgradeEffect implements DarkSteelInventoryTickHook {

    public static final MapCodec<SolarChargeEffect> CODEC = MapCodec.unit(new SolarChargeEffect());

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return List.of(DarkSteelUpgradeHookTypes.INVENTORY_TICK);
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    @Override
    public void onInventoryTick(DarkSteelHookContext context, Level level, Entity entity) {
        // Do solar charging things lol
        level.random.nextBoolean();
    }
}

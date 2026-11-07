package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.content.armory.effects.SolarChargeEffect;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODarkSteelUpgradeEffects {
    private static final DeferredRegister<MapCodec<? extends DarkSteelUpgradeEffect>> DARK_STEEL_UPGRADE_EFFECTS = DeferredRegister.create(EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT,
        EnderIO.MOD_ID);

    public static void register(IEventBus eventBus) {
        DARK_STEEL_UPGRADE_EFFECTS.register("solar_charge_effect", () -> SolarChargeEffect.CODEC);

        DARK_STEEL_UPGRADE_EFFECTS.register(eventBus);
    }
}

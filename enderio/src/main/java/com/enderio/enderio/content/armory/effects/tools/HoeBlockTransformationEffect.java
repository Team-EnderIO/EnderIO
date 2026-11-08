package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class HoeBlockTransformationEffect extends DarkSteelUpgradeEffect implements CanPerformActionHook, UseOnHook {

    public static final HoeBlockTransformationEffect INSTANCE = new HoeBlockTransformationEffect();

    public static final MapCodec<HoeBlockTransformationEffect> CODEC = MapCodec.unit(INSTANCE);

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION,
        DarkSteelUpgradeHookTypes.USE_ON);

    private HoeBlockTransformationEffect() {
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
    public boolean canPerformAction(DarkSteelHookContext context, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.HOE_TILL;
    }

    @Override
    public InteractionResult useOn(DarkSteelHookContext context, UseOnContext useOnContext, InteractionResult currentResult) {
        if (currentResult != InteractionResult.PASS) {
            return currentResult;
        }

        // TODO: Reimplement directly for nice-ness
        return Items.DIAMOND_HOE.useOn(useOnContext);
    }
}

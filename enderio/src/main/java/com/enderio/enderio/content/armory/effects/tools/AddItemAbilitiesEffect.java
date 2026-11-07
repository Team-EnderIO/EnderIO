package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Arrays;
import java.util.List;

public final class AddItemAbilitiesEffect extends DarkSteelUpgradeEffect implements CanPerformActionHook {

    public static final MapCodec<AddItemAbilitiesEffect> CODEC = RecordCodecBuilder.mapCodec(inst ->
        inst.group(
            Codec.list(Codec.string(1, 255), 1, 255).fieldOf("item_abilities").forGetter(AddItemAbilitiesEffect::abilityNames)
        ).apply(inst, AddItemAbilitiesEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION);

    private final List<ItemAbility> abilities;

    public AddItemAbilitiesEffect(ItemAbility... abilities) {
        this.abilities = Arrays.asList(abilities);
    }

    private AddItemAbilitiesEffect(List<String> abilityNames) {
        abilities = abilityNames.stream().map(ItemAbility::get).toList();
    }

    public List<ItemAbility> abilities() {
        return abilities;
    }

    private List<String> abilityNames() {
        return abilities.stream().map(ItemAbility::name).toList();
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
        return abilities.contains(itemAbility);
    }
}

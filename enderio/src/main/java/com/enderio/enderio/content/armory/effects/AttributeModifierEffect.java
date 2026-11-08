package com.enderio.enderio.content.armory.effects;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.ModifyAttributesHook;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.List;

/**
 * @apiNote No condition is available for this effect as it is not possible for changes in the condition to be detected and used by the hook.
 */
public class AttributeModifierEffect extends DarkSteelUpgradeEffect implements ModifyAttributesHook {

    public static final MapCodec<AttributeModifierEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierEffect::attribute),
            AttributeModifier.MAP_CODEC.forGetter(AttributeModifierEffect::modifier),
            Codec.BOOL.optionalFieldOf("hide_from_tooltip", true).forGetter(AttributeModifierEffect::hideFromTooltip)
        ).apply(inst, AttributeModifierEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES);

    private final Holder<Attribute> attribute;
    private final AttributeModifier modifier;
    private final boolean hideFromTooltip;

    public AttributeModifierEffect(Holder<Attribute> attribute, AttributeModifier modifier, boolean hideFromTooltip) {
        this.attribute = attribute;
        this.modifier = modifier;
        this.hideFromTooltip = hideFromTooltip;
    }

    public AttributeModifierEffect(Holder<Attribute> attribute, AttributeModifier modifier) {
        this(attribute, modifier, true);
    }

    public Holder<Attribute> attribute() {
        return attribute;
    }

    public AttributeModifier modifier() {
        return modifier;
    }

    public boolean hideFromTooltip() {
        return hideFromTooltip;
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
    public void modifyAttributes(DarkSteelHookContext context, ItemAttributeModifierEvent event) {
        event.addModifier(attribute, modifier, getUpgradeSlotGroup(context.itemStack()));
    }

    // TOOD: Not 100% happy with this but need to think more.
    private static EquipmentSlotGroup getUpgradeSlotGroup(ItemStack stack) {
        EquipmentSlot slot = stack.getEquipmentSlot();
        if (slot == null) {
            Equipable equipable = Equipable.get(stack);
            slot = equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
        }

        return EquipmentSlotGroup.bySlot(slot);
    }
}

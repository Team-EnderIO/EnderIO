package com.enderio.enderio.content.armory.effects;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
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
 * Adds an attribute modifier to the item while {@link #condition()} is active.
 * @apiNote Attribute modifiers are only recalculated when the equipped item changes, and the condition is checked without a level or entity.
 * Only use conditions that depend on the item's own data, such as {@code enderio:toggled}.
 */
public class AttributeModifierEffect extends DarkSteelUpgradeEffect implements ModifyAttributesHook {

    public static final MapCodec<AttributeModifierEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierEffect::attribute),
            AttributeModifier.MAP_CODEC.forGetter(AttributeModifierEffect::modifier),
            Codec.BOOL.optionalFieldOf("hide_from_tooltip", true).forGetter(AttributeModifierEffect::hideFromTooltip),
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(AttributeModifierEffect::condition)
        ).apply(inst, AttributeModifierEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES);

    private final Holder<Attribute> attribute;
    private final AttributeModifier modifier;
    private final boolean hideFromTooltip;
    private final EffectCondition condition;

    public AttributeModifierEffect(Holder<Attribute> attribute, AttributeModifier modifier, boolean hideFromTooltip, EffectCondition condition) {
        this.attribute = attribute;
        this.modifier = modifier;
        this.hideFromTooltip = hideFromTooltip;
        this.condition = condition;
    }

    public AttributeModifierEffect(Holder<Attribute> attribute, AttributeModifier modifier, EffectCondition condition) {
        this(attribute, modifier, true, condition);
    }

    public AttributeModifierEffect(Holder<Attribute> attribute, AttributeModifier modifier, boolean hideFromTooltip) {
        this(attribute, modifier, hideFromTooltip, AlwaysActiveCondition.INSTANCE);
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

    public EffectCondition condition() {
        return condition;
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
        if (!condition.isActive(context.itemStack(), null, null)) {
            return;
        }

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

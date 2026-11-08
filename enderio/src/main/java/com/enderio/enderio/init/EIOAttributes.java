package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, EnderIO.MOD_ID);

    /**
     * How many blocks area mining extends out from the mined block, across the face that was hit.
     * 1 gives a 3x3 face, 2 gives a 5x5 face and so on.
     */
    public static final DeferredHolder<Attribute, Attribute> MINING_AREA_RADIUS = register("mining_area_radius", 0, 0, 16);

    /**
     * How many blocks area mining extends behind the mined block, away from the face that was hit.
     */
    public static final DeferredHolder<Attribute, Attribute> MINING_AREA_DEPTH = register("mining_area_depth", 0, 0, 16);

    private static DeferredHolder<Attribute, Attribute> register(String name, double defaultValue, double min, double max) {
        // Synced so the client can draw the area highlight.
        return ATTRIBUTES.register(name,
            () -> new RangedAttribute(Util.makeDescriptionId("attribute", EnderIO.rl(name)), defaultValue, min, max).setSyncable(true));
    }

    /**
     * @return whether modifiers for this attribute should be left out of item tooltips.
     */
    public static boolean isHiddenFromTooltips(Holder<Attribute> attribute) {
        return attribute.is(MINING_AREA_RADIUS.getKey()) || attribute.is(MINING_AREA_DEPTH.getKey());
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
        eventBus.addListener(EIOAttributes::addPlayerAttributes);
    }

    private static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, MINING_AREA_RADIUS);
        event.add(EntityType.PLAYER, MINING_AREA_DEPTH);
    }
}

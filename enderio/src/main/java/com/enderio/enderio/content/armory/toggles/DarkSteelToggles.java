package com.enderio.enderio.content.armory.toggles;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks dark steel upgrade toggle states.
 */
public record DarkSteelToggles(Map<ResourceLocation, Boolean> states) {

    public static final DarkSteelToggles EMPTY = new DarkSteelToggles(Map.of());

    public static final Codec<DarkSteelToggles> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.BOOL)
        .xmap(DarkSteelToggles::new, DarkSteelToggles::states);

    public static final StreamCodec<ByteBuf, DarkSteelToggles> STREAM_CODEC = ByteBufCodecs
        .<ByteBuf, ResourceLocation, Boolean, Map<ResourceLocation, Boolean>>map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.BOOL)
        .map(DarkSteelToggles::new, DarkSteelToggles::states);

    public DarkSteelToggles {
        states = Map.copyOf(states);
    }

    public DarkSteelToggles with(ResourceLocation toggle, boolean state, boolean defaultState) {
        var newStates = new HashMap<>(states);
        if (state == defaultState) {
            newStates.remove(toggle);
        } else {
            newStates.put(toggle, state);
        }

        return new DarkSteelToggles(newStates);
    }
}

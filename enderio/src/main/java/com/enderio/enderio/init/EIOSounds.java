package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, EnderIO.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STIRLING = SOUNDS.register("stirling",
        () -> SoundEvent.createVariableRangeEvent(EnderIO.id("block.stirling_generator.burn")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SLICER = SOUNDS.register("slice_and_splice",
        () -> SoundEvent.createVariableRangeEvent(EnderIO.id("block.slice_and_splice.slice")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SAG_MILL = SOUNDS.register("sag_mill",
        () -> SoundEvent.createVariableRangeEvent(EnderIO.id("block.sag_mill.tumble")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SOUL_BINDER = SOUNDS.register("soul_binder",
        () -> SoundEvent.createVariableRangeEvent(EnderIO.id("block.soul_binder.grind")));
    public static final DeferredHolder<SoundEvent, SoundEvent> VAT = SOUNDS.register("vat",
        () -> SoundEvent.createVariableRangeEvent(EnderIO.id("block.vat.gurgle")));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

}

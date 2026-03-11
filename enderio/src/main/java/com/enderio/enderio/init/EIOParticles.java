package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.particle.RangeParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class EIOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, EnderIO.MOD_ID);

    public static final RegistryObject<RangeParticleType> RANGE_PARTICLE = PARTICLE_TYPES
            .register("range_particle", RangeParticleType::new);

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }

}

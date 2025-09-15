package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.particle.RangeParticleType;
import com.enderio.base.common.particle.SoulParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, EnderIO.NAMESPACE);

    public static final DeferredHolder<ParticleType<?>, RangeParticleType> RANGE_PARTICLE = PARTICLE_TYPES
            .register("range_particle", RangeParticleType::new);

    public static final DeferredHolder<ParticleType<?>, SoulParticleType> SOUL_PARTICLE = PARTICLE_TYPES
            .register("soul_particle", SoulParticleType::new);

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }

}

package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.common.particle.RangeParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, EnderIOBase.REGISTRY_NAMESPACE);

    public static final DeferredHolder<ParticleType<?>, RangeParticleType> RANGE_PARTICLE = PARTICLE_TYPES.register("range_particle", RangeParticleType::new);

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }

}

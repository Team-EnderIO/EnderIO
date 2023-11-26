package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.particle.RangeParticleType;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class EIOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EnderIO.MODID);

    public static final RegistryObject<RangeParticleType> RANGE_PARTICLE = PARTICLE_TYPES.register("range_particle", RangeParticleType::new);

    public static void register() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PARTICLE_TYPES.register(eventBus);
    }

}

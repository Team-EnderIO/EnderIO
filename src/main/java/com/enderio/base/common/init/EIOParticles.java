package com.enderio.base.common.init;

import com.enderio.EnderIO;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EIOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EnderIO.MODID);

    public static final RegistryObject<SimpleParticleType> RANGE_PARTICLE = PARTICLE_TYPES.register("range_particle", () -> new SimpleParticleType(true));

    public static void register() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        PARTICLE_TYPES.register(eventBus);
    }

}

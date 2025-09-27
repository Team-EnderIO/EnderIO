package com.enderio.base.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.api.registry.EnderIORegistries;
import com.enderio.machines.common.soulpot.BiomeOrigin;
import com.enderio.machines.common.soulpot.BlockOrigin;
import com.enderio.machines.common.soulpot.HeightOrigin;
import com.enderio.machines.common.soulpot.LightOrigin;
import com.enderio.machines.common.soulpot.LogicOrigin;
import com.enderio.machines.common.soulpot.NotOrigin;
import com.enderio.machines.common.soulpot.OriginType;
import com.enderio.machines.common.soulpot.StructureOrigin;
import com.enderio.machines.common.soulpot.SurfaceOrigin;
import com.google.common.base.Suppliers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOOrigins {


    public static final DeferredRegister<OriginType<?>> REGISTER = DeferredRegister
        .create(EnderIORegistries.ORIGIN_TYPES, EnderIO.NAMESPACE);

    public static final Supplier<OriginType<LogicOrigin>> LOGIC_ORIGIN = REGISTER.register("logic", Suppliers.ofInstance(OriginType.LOGIC));
    public static final Supplier<OriginType<BiomeOrigin>> BIOME_ORIGIN = REGISTER.register("biome", Suppliers.ofInstance(OriginType.BIOME));
    public static final Supplier<OriginType<StructureOrigin>> STRUCTURE_ORIGIN = REGISTER.register("structure", Suppliers.ofInstance(OriginType.STRUCTURE));
    public static final Supplier<OriginType<LightOrigin>> LIGHT_ORIGIN = REGISTER.register("light", Suppliers.ofInstance(OriginType.LIGHT));
    public static final Supplier<OriginType<BlockOrigin>> BLOCK_ORIGIN = REGISTER.register("block", Suppliers.ofInstance(OriginType.BLOCK));
    public static final Supplier<OriginType<SurfaceOrigin>> SURFACE_ORIGIN = REGISTER.register("surface", Suppliers.ofInstance(OriginType.SURFACE));
    public static final Supplier<OriginType<HeightOrigin>> HEIGHT_ORIGIN = REGISTER.register("height", Suppliers.ofInstance(OriginType.HEIGHT));
    public static final Supplier<OriginType<NotOrigin>> NOT_ORIGIN = REGISTER.register("not", Suppliers.ofInstance(OriginType.NOT));

    public static void init(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}

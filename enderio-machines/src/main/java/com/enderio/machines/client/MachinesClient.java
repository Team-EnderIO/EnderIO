package com.enderio.machines.client;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.client.rendering.model.IOOverlayBakedModel;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachinesClient {
    @SubscribeEvent
    public static void customModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(EIOMachines.loc("io_overlay"), new IOOverlayBakedModel.Loader());
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre event) {
    }

    @SubscribeEvent
    public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MachineBlockEntities.FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(MachineBlockEntities.PRESSURIZED_FLUID_TANK.get(), FluidTankBER::new);
    }
}

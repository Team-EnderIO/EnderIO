package com.enderio.base.client;

import com.enderio.EnderIO;
import com.enderio.base.client.particle.RangeParticle;
import com.enderio.base.client.renderer.item.GlassIconDecorator;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIOParticles;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.client.item.FluidBarDecorator;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterAdditional event) {
        event.register(EnderIO.loc("item/wood_gear_helper"));
        event.register(EnderIO.loc("item/stone_gear_helper"));
        event.register(EnderIO.loc("item/iron_gear_helper"));
        event.register(EnderIO.loc("item/energized_gear_helper"));
        event.register(EnderIO.loc("item/vibrant_gear_helper"));
        event.register(EnderIO.loc("item/dark_bimetal_gear_helper"));
    }

    @SubscribeEvent
    public static void itemDecorators(RegisterItemDecorationsEvent event) {
        // Register tools
        event.register(EIOItems.LEVITATION_STAFF.get(), FluidBarDecorator.INSTANCE);
        event.register(EIOItems.TRAVEL_STAFF.get(), EnergyBarDecorator.INSTANCE);
        event.register(EIOItems.DARK_STEEL_AXE.get(), EnergyBarDecorator.INSTANCE);
        event.register(EIOItems.DARK_STEEL_PICKAXE.get(), EnergyBarDecorator.INSTANCE);

        // Register all glass blocks
        EIOBlocks.GLASS_BLOCKS.values().forEach(blocks -> blocks.getAllBlocks().forEach(block -> event.register(block.get(), GlassIconDecorator.INSTANCE)));
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(EIOParticles.RANGE_PARTICLE.get(), RangeParticle.Provider::new);
    }
}

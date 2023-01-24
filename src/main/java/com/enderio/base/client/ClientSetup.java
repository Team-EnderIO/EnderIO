package com.enderio.base.client;

import com.enderio.EnderIO;
import com.enderio.base.client.renderer.glider.ActiveGliderRenderLayer;
import com.enderio.core.client.item.EnergyBarDecorator;
import com.enderio.core.client.item.FluidBarDecorator;
import com.enderio.base.client.renderer.item.GlassIconDecorator;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
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
        event.register(EnderIO.loc("glider/glider_test_1"));
    }

    @SubscribeEvent
    public static void itemDecorators(RegisterItemDecorationsEvent event) {
        // Register tools
        event.register(EIOItems.LEVITATION_STAFF.get(), FluidBarDecorator.INSTANCE);
        event.register(EIOItems.DARK_STEEL_AXE.get(), EnergyBarDecorator.INSTANCE);
        event.register(EIOItems.DARK_STEEL_PICKAXE.get(), EnergyBarDecorator.INSTANCE);

        // Register all glass blocks
        EIOBlocks.GLASS_BLOCKS.values().forEach(blocks -> blocks.getAllBlocks().forEach(block -> event.register(block.get(), GlassIconDecorator.INSTANCE)));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {

                playerRenderer.addLayer(new ActiveGliderRenderLayer(playerRenderer));
            }
        }
    }

    public static BakedModel glider;

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
        glider = event.getModels().get(EnderIO.loc("glider/glider_test_1"));
    }
}

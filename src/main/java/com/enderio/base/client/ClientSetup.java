package com.enderio.base.client;

import com.enderio.EnderIO;
import com.enderio.base.client.model.PaintedBlockGeometry;
import com.enderio.base.client.particle.RangeParticle;
import com.enderio.base.client.renderer.block.EnderSkullRenderer;
import com.enderio.base.client.renderer.glider.ActiveGliderRenderLayer;
import com.enderio.base.client.renderer.item.GlassIconDecorator;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIOBlocks;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.init.EIOParticles;
import com.enderio.core.client.item.FluidBarDecorator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    private static final Map<Item, ResourceLocation> HANG_GLIDER_MODEL_LOCATION = new HashMap<>();
    public static final Map<Item, BakedModel> GLIDER_MODELS = new HashMap<>();
    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterAdditional event) {
        event.register(EnderIO.loc("item/wood_gear_helper"));
        event.register(EnderIO.loc("item/stone_gear_helper"));
        event.register(EnderIO.loc("item/iron_gear_helper"));
        event.register(EnderIO.loc("item/energized_gear_helper"));
        event.register(EnderIO.loc("item/vibrant_gear_helper"));
        event.register(EnderIO.loc("item/dark_bimetal_gear_helper"));
        Set<ResourceLocation> gliderModels = Minecraft
            .getInstance()
            .getResourceManager()
            .listResources("models/enderio_glider", rl -> rl.getPath().endsWith(".json"))
            .keySet();
        for (ResourceLocation gliderModelPath : gliderModels) {
            Optional<Item> gliderItem = findGliderForModelRL(gliderModelPath);
            if (gliderItem.isPresent()) {
                ResourceLocation modelLookupLocation = new ResourceLocation(gliderModelPath.getNamespace(),
                    gliderModelPath.getPath().substring("models/".length(), gliderModelPath.getPath().length() - 5));
                event.register(modelLookupLocation);
                HANG_GLIDER_MODEL_LOCATION.put(gliderItem.get(), modelLookupLocation);
            }
        }
    }

    @SubscribeEvent
    public static void itemDecorators(RegisterItemDecorationsEvent event) {
        // Register tools
        event.register(EIOItems.LEVITATION_STAFF.get(), FluidBarDecorator.INSTANCE);
//        event.register(EIOItems.DARK_STEEL_AXE.get(), EnergyBarDecorator.INSTANCE);
//        event.register(EIOItems.DARK_STEEL_PICKAXE.get(), EnergyBarDecorator.INSTANCE);

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


    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
        GLIDER_MODELS.clear();
        HANG_GLIDER_MODEL_LOCATION.forEach((item, modelRL) -> {
            BakedModel bakedModel = event.getModels().get(modelRL);
            if (bakedModel != null) {
                GLIDER_MODELS.put(item, bakedModel);
            }
        });
        HANG_GLIDER_MODEL_LOCATION.clear();
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(EIOParticles.RANGE_PARTICLE.get(), RangeParticle.Provider::new);
    }

    private static Optional<Item> findGliderForModelRL(ResourceLocation rl) {
        String namespace = rl.getNamespace();
        String path = rl.getPath().substring("models/enderio_glider/".length(), rl.getPath().length() - 5);
        return Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(namespace, path)));
    }
    @SubscribeEvent
    public static void modelInit(ModelEvent.RegisterGeometryLoaders event) {
        event.register("painted_block", new PaintedBlockGeometry.Loader());
    }

    @SubscribeEvent
    public static void modelRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EIOBlockEntities.ENDER_SKULL.get(), EnderSkullRenderer::new);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnderSkullRenderer.ENDER_SKULL, EnderSkullRenderer.EnderSkullModel::createMobHeadLayer);
    }
}

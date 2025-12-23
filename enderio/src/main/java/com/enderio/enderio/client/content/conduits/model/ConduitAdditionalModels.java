package com.enderio.enderio.client.content.conduits.model;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.conduits.model.modifier.ConduitModelModifiers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT)
public class ConduitAdditionalModels {
    private static final Map<ResourceLocation, BakedModel> MODELS = new HashMap<>();

    public static final ResourceLocation CONDUIT_CONNECTOR = EnderIO.rl("block/conduit_connector");
    public static final ResourceLocation CONDUIT_FACADE_OVERLAY = EnderIO.rl("block/conduit_facade_overlay");
    public static final ResourceLocation CONDUIT_CONNECTION = EnderIO.rl("block/conduit_connection");
    public static final ResourceLocation CONDUIT_CORE = EnderIO.rl("block/conduit_core");
    public static final ResourceLocation BOX = EnderIO.rl("block/box/1x1x1");
    public static final ResourceLocation CONDUIT_CONNECTION_BOX = EnderIO.rl("block/conduit_connection_box");
    public static final ResourceLocation CONDUIT_IO_IN = EnderIO.rl("block/io/input");
    public static final ResourceLocation CONDUIT_IO_IN_OUT = EnderIO.rl("block/io/in_out");
    public static final ResourceLocation CONDUIT_IO_OUT = EnderIO.rl("block/io/output");
    public static final ResourceLocation CONDUIT_IO_REDSTONE = EnderIO.rl("block/io/redstone");

    public static BakedModel modelOf(ResourceLocation location) {
        return MODELS.get(location);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        // Register all models
        event.register(CONDUIT_CONNECTION);
        event.register(CONDUIT_FACADE_OVERLAY);
        event.register(CONDUIT_CONNECTOR);
        event.register(CONDUIT_CORE);
        event.register(BOX);
        event.register(CONDUIT_CONNECTION_BOX);
        event.register(CONDUIT_IO_IN);
        event.register(CONDUIT_IO_IN_OUT);
        event.register(CONDUIT_IO_OUT);
        event.register(CONDUIT_IO_REDSTONE);

        // Ensure conduit model modifiers are ready, then load all model dependencies.
        ConduitModelModifiers.init();
        ConduitModelModifiers.getAllModelDependencies().forEach(event::register);
    }

    @SubscribeEvent
    public static void bakingModelsFinished(ModelEvent.BakingCompleted event) {
        MODELS.put(CONDUIT_CONNECTION, event.getModelManager().getStandaloneModel(CONDUIT_CONNECTION));
        MODELS.put(CONDUIT_FACADE_OVERLAY, event.getModelManager().getStandaloneModel(CONDUIT_FACADE_OVERLAY));
        MODELS.put(CONDUIT_CONNECTOR, event.getModelManager().getStandaloneModel(CONDUIT_CONNECTOR));
        MODELS.put(CONDUIT_CORE, event.getModelManager().getStandaloneModel(CONDUIT_CORE));
        MODELS.put(BOX, event.getModelManager().getStandaloneModel(BOX));
        MODELS.put(CONDUIT_CONNECTION_BOX, event.getModelManager().getStandaloneModel(CONDUIT_CONNECTION_BOX));
        MODELS.put(CONDUIT_IO_IN, event.getModelManager().getStandaloneModel(CONDUIT_IO_IN));
        MODELS.put(CONDUIT_IO_IN_OUT, event.getModelManager().getStandaloneModel(CONDUIT_IO_IN_OUT));
        MODELS.put(CONDUIT_IO_OUT, event.getModelManager().getStandaloneModel(CONDUIT_IO_OUT));
        MODELS.put(CONDUIT_IO_REDSTONE, event.getModelManager().getStandaloneModel(CONDUIT_IO_REDSTONE));
    }
}

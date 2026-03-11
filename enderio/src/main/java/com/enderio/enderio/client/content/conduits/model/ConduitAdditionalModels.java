package com.enderio.enderio.client.content.conduits.model;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.conduits.model.modifier.ConduitModelModifiers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ConduitAdditionalModels {
    private static final List<ModelResourceLocation> MODEL_LOCATIONS = new ArrayList<>();
    private static final Map<ModelResourceLocation, BakedModel> MODELS = new HashMap<>();

    public static final ModelResourceLocation CONDUIT_CONNECTOR = loc("block/conduit_connector");
    public static final ModelResourceLocation CONDUIT_FACADE_OVERLAY = loc("block/conduit_facade_overlay");
    public static final ModelResourceLocation CONDUIT_CONNECTION = loc("block/conduit_connection");
    public static final ModelResourceLocation CONDUIT_CORE = loc("block/conduit_core");
    public static final ModelResourceLocation BOX = loc("block/box/1x1x1");
    public static final ModelResourceLocation CONDUIT_CONNECTION_BOX = loc("block/conduit_connection_box");
    public static final ModelResourceLocation CONDUIT_IO_IN = loc("block/io/input");
    public static final ModelResourceLocation CONDUIT_IO_IN_OUT = loc("block/io/in_out");
    public static final ModelResourceLocation CONDUIT_IO_OUT = loc("block/io/output");
    public static final ModelResourceLocation CONDUIT_IO_REDSTONE = loc("block/io/redstone");

    private static ModelResourceLocation loc(String modelName) {
        ModelResourceLocation loc = new ModelResourceLocation(EnderIO.rl(modelName), "");
        MODEL_LOCATIONS.add(loc);
        return loc;
    }

    public static BakedModel modelOf(ModelResourceLocation location) {
        return MODELS.get(location);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (ModelResourceLocation model : MODEL_LOCATIONS) {
            event.register(model);
        }

        // Ensure conduit model modifiers are ready, then load all model dependencies.
        ConduitModelModifiers.init();
        ConduitModelModifiers.getAllModelDependencies().forEach(event::register);
    }

    @SubscribeEvent
    public static void bakingModelsFinished(ModelEvent.BakingCompleted event) {
        for (ModelResourceLocation modelLocation : MODEL_LOCATIONS) {
            MODELS.put(modelLocation, event.getModels().get(modelLocation));
        }
    }
}

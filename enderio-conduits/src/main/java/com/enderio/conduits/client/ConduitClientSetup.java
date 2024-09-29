package com.enderio.conduits.client;

import com.enderio.EnderIOBase;
import com.enderio.conduits.api.model.RegisterConduitCoreModelModifiersEvent;
import com.enderio.conduits.api.screen.RegisterConduitScreenExtensionsEvent;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.client.gui.conduit.ConduitScreenExtensions;
import com.enderio.conduits.client.gui.conduit.FluidConduitScreenExtension;
import com.enderio.conduits.client.gui.conduit.ItemConduitScreenExtension;
import com.enderio.conduits.client.model.ConduitGeometry;
import com.enderio.conduits.client.model.ConduitItemModelLoader;
import com.enderio.conduits.client.model.conduit.modifier.ConduitCoreModelModifiers;
import com.enderio.conduits.client.model.conduit.modifier.FluidConduitCoreModelModifier;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ConduitClientSetup {

    private static final List<ModelResourceLocation> MODEL_LOCATIONS = new ArrayList<>();
    private static final Map<ModelResourceLocation, BakedModel> MODELS = new HashMap<>();

    public static final ModelResourceLocation CONDUIT_CONNECTOR = loc("block/conduit_connector");
    public static final ModelResourceLocation CONDUIT_FACADE = loc("block/conduit_facade");
    public static final ModelResourceLocation CONDUIT_CONNECTION = loc("block/conduit_connection");
    public static final ModelResourceLocation CONDUIT_CORE = loc("block/conduit_core");
    public static final ModelResourceLocation BOX = loc("block/box/1x1x1");
    public static final ModelResourceLocation CONDUIT_CONNECTION_BOX = loc("block/conduit_connection_box");
    public static final ModelResourceLocation CONDUIT_IO_IN = loc("block/io/input");
    public static final ModelResourceLocation CONDUIT_IO_IN_OUT = loc("block/io/in_out");
    public static final ModelResourceLocation CONDUIT_IO_OUT = loc("block/io/output");
    public static final ModelResourceLocation CONDUIT_IO_REDSTONE = loc("block/io/redstone");

    private ConduitClientSetup() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ConduitScreenExtensions.init();
    }

    @SubscribeEvent
    public static void registerConduitCoreModelModifiers(RegisterConduitCoreModelModifiersEvent event) {
        event.register(ConduitTypes.FLUID.get(), FluidConduitCoreModelModifier::new);
    }

    @SubscribeEvent
    public static void registerConduitScreenExtensions(RegisterConduitScreenExtensionsEvent event) {
        event.register(ConduitTypes.FLUID.get(), FluidConduitScreenExtension::new);
        event.register(ConduitTypes.ITEM.get(), ItemConduitScreenExtension::new);
    }

    @SubscribeEvent
    public static void modelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register(EnderIOBase.loc("conduit"), new ConduitGeometry.Loader());
        event.register(EnderIOBase.loc("conduit_item"), new ConduitItemModelLoader());
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (ModelResourceLocation model : MODEL_LOCATIONS) {
            event.register(model);
        }

        // Ensure conduit model modifiers are ready, then load all model dependencies.
        ConduitCoreModelModifiers.init();
        ConduitCoreModelModifiers.getAllModelDependencies().forEach(event::register);
    }

    @SubscribeEvent
    public static void bakingModelsFinished(ModelEvent.BakingCompleted event) {
        for (ModelResourceLocation modelLocation : MODEL_LOCATIONS) {
            MODELS.put(modelLocation, event.getModels().get(modelLocation));
        }
    }

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block block) {
        block.register(new ConduitBlockColor(), ConduitBlocks.CONDUIT.get());
    }

    public static class ConduitBlockColor implements BlockColor {

        @Override
        public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
            return DyeColor.values()[tintIndex].getTextureDiffuseColor();
        }
    }

    private static ModelResourceLocation loc(String modelName) {
        ModelResourceLocation loc = ModelResourceLocation.standalone(EnderIOBase.loc(modelName));
        MODEL_LOCATIONS.add(loc);
        return loc;
    }

    public static BakedModel modelOf(ModelResourceLocation location) {
        return MODELS.get(location);
    }

    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }
}

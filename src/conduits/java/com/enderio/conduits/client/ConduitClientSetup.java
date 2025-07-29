package com.enderio.conduits.client;

import com.enderio.EnderIO;
import com.enderio.api.conduit.model.RegisterConduitCoreModelModifiersEvent;
import com.enderio.api.conduit.screen.RegisterConduitScreenExtensionsEvent;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.client.gui.ConduitIconTextureManager;
import com.enderio.conduits.client.gui.conduit.ConduitScreenExtensions;
import com.enderio.conduits.client.gui.conduit.FluidConduitScreenExtension;
import com.enderio.conduits.client.gui.conduit.ItemConduitScreenExtension;
import com.enderio.conduits.client.model.ConduitGeometry;
import com.enderio.conduits.client.model.conduit.modifier.ConduitCoreModelModifiers;
import com.enderio.conduits.client.model.conduit.modifier.FluidConduitCoreModelModifier;
import com.enderio.conduits.client.model.conduit.modifier.RedstoneConduitCoreModelModifier;
import com.enderio.conduits.common.init.ConduitBlocks;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConduitClientSetup {

    private static final List<ResourceLocation> MODEL_LOCATIONS = new ArrayList<>();
    private static final Map<ResourceLocation, BakedModel> MODELS = new HashMap<>();

    public static final ResourceLocation CONDUIT_CONNECTOR = loc("block/conduit_connector");
    public static final ResourceLocation CONDUIT_FACADE = loc("block/conduit_facade");
    public static final ResourceLocation CONDUIT_CONNECTION = loc("block/conduit_connection");
    public static final ResourceLocation CONDUIT_CORE = loc("block/conduit_core");
    public static final ResourceLocation BOX = loc("block/box/1x1x1");
    public static final ResourceLocation CONDUIT_CONNECTION_BOX = loc("block/conduit_connection_box");
    public static final ResourceLocation CONDUIT_IO_IN = loc("block/io/input");
    public static final ResourceLocation CONDUIT_IO_IN_OUT = loc("block/io/in_out");
    public static final ResourceLocation CONDUIT_IO_OUT = loc("block/io/output");
    public static final ResourceLocation CONDUIT_IO_REDSTONE = loc("block/io/redstone");

    private ConduitClientSetup() {}

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ConduitScreenExtensions.init();
        
        event.enqueueWork(() -> 
            ItemProperties.register(ConduitItems.CONDUIT_PROBE.get(), EnderIO.loc("conduit_probe_state"), 
                ((itemStack, clientLevel, livingEntity, i) -> {
                    if (itemStack.getItem() instanceof ConduitProbeItem) {
                        return ConduitProbeItem.getState(itemStack).ordinal();
                    }
                    return 0;
                }))
        );
    }

    @SubscribeEvent
    public static void registerConduitCoreModelModifiers(RegisterConduitCoreModelModifiersEvent event) {
        event.register(EIOConduitTypes.FLUID.get(), () -> FluidConduitCoreModelModifier.INSTANCE);
        event.register(EIOConduitTypes.FLUID2.get(), () -> FluidConduitCoreModelModifier.INSTANCE);
        event.register(EIOConduitTypes.FLUID3.get(), () -> FluidConduitCoreModelModifier.INSTANCE);
        event.register(EIOConduitTypes.REDSTONE.get(), RedstoneConduitCoreModelModifier::new);
    }

    @SubscribeEvent
    public static void registerConduitScreenExtensions(RegisterConduitScreenExtensionsEvent event) {
        event.register(EIOConduitTypes.FLUID.get(), () -> FluidConduitScreenExtension.INSTANCE);
        event.register(EIOConduitTypes.FLUID2.get(), () -> FluidConduitScreenExtension.INSTANCE);
        event.register(EIOConduitTypes.FLUID3.get(), () -> FluidConduitScreenExtension.INSTANCE);
        event.register(EIOConduitTypes.ITEM.get(), ItemConduitScreenExtension::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ConduitIconTextureManager(Minecraft.getInstance().getTextureManager()));
    }

    @SubscribeEvent
    public static void modelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register("conduit", new ConduitGeometry.Loader());
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (ResourceLocation model : MODEL_LOCATIONS) {
            event.register(model);
        }

        // Ensure conduit model modifiers are ready, then load all model dependencies.
        ConduitCoreModelModifiers.init();
        ConduitCoreModelModifiers.getAllModelDependencies().forEach(event::register);
    }

    @SubscribeEvent
    public static void bakingModelsFinished(ModelEvent.BakingCompleted event) {
        for (ResourceLocation modelLocation : MODEL_LOCATIONS) {
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
            return ColorControl.values()[tintIndex].getColor();
        }
    }

    private static ResourceLocation loc(String modelName) {
        ResourceLocation loc = EnderIO.loc(modelName);
        MODEL_LOCATIONS.add(loc);
        return loc;
    }

    public static BakedModel modelOf(ResourceLocation location) {
        return MODELS.get(location);
    }

    public static Level getClientLevel() {
        return Minecraft.getInstance().level;
    }
}

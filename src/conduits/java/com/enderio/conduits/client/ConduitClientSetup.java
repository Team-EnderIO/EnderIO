package com.enderio.conduits.client;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConduitClientSetup {

    public static final ResourceLocation CONDUIT_CONNECTOR = EnderIO.loc("block/conduit_connector");
    public static final ResourceLocation CONDUIT_CONNECTION = EnderIO.loc("block/conduit_connection");
    public static final ResourceLocation CONDUIT_CORE = EnderIO.loc("block/conduit_core");

    private ConduitClientSetup() {}

    @SubscribeEvent
    public static void modelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register("conduit", new ConduitGeometry.Loader());
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(CONDUIT_CONNECTOR);
        event.register(CONDUIT_CONNECTION);
        event.register(CONDUIT_CORE);
    }

    @SubscribeEvent
    public static void textureStich(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location() == InventoryMenu.BLOCK_ATLAS) {
            for (IConduitType type : ConduitTypes.getRegistry().getValues()) {
                event.addSprite(type.getTexture());
            }
        }
    }
}
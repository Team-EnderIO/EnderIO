package com.enderio.enderio.client.content.conduits.model;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.conduits.model.modifier.ConduitModelModifiers;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class ConduitAdditionalModels {

    public static final Identifier CONDUIT_CONNECTOR = EnderIO.id("block/conduit_connector");
    public static final Identifier CONDUIT_FACADE_OVERLAY = EnderIO.id("block/conduit_facade_overlay");
    public static final Identifier CONDUIT_CONNECTION = EnderIO.id("block/conduit_connection");
    public static final Identifier CONDUIT_CORE = EnderIO.id("block/conduit_core");
    public static final Identifier CONDUIT_ITEM = EnderIO.id("block/conduit_item");
    public static final Identifier BOX = EnderIO.id("block/box/1x1x1");
    public static final Identifier CONDUIT_CONNECTION_BOX = EnderIO.id("block/conduit_connection_box");
    public static final Identifier CONDUIT_IO_IN = EnderIO.id("block/io/input");
    public static final Identifier CONDUIT_IO_IN_OUT = EnderIO.id("block/io/in_out");
    public static final Identifier CONDUIT_IO_OUT = EnderIO.id("block/io/output");
    public static final Identifier CONDUIT_IO_REDSTONE = EnderIO.id("block/io/redstone");

    @SubscribeEvent
    public static void registerModels(ModelEvent.BakingCompleted event) {

        ConduitModelModifiers.init();
        //ConduitModelModifiers.getAllModelDependencies().forEach(event::register); //TODO this needs to be different
    }
}

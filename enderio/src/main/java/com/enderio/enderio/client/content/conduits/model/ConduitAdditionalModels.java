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

    public static final Identifier CONDUIT_CONNECTOR = EnderIO.rl("block/conduit_connector");
    public static final Identifier CONDUIT_FACADE_OVERLAY = EnderIO.rl("block/conduit_facade_overlay");
    public static final Identifier CONDUIT_CONNECTION = EnderIO.rl("block/conduit_connection");
    public static final Identifier CONDUIT_CORE = EnderIO.rl("block/conduit_core");
    public static final Identifier BOX = EnderIO.rl("block/box/1x1x1");
    public static final Identifier CONDUIT_CONNECTION_BOX = EnderIO.rl("block/conduit_connection_box");
    public static final Identifier CONDUIT_IO_IN = EnderIO.rl("block/io/input");
    public static final Identifier CONDUIT_IO_IN_OUT = EnderIO.rl("block/io/in_out");
    public static final Identifier CONDUIT_IO_OUT = EnderIO.rl("block/io/output");
    public static final Identifier CONDUIT_IO_REDSTONE = EnderIO.rl("block/io/redstone");

    @SubscribeEvent
    public static void registerModels(ModelEvent.BakingCompleted event) {

        ConduitModelModifiers.init();
        //ConduitModelModifiers.getAllModelDependencies().forEach(event::register); //TODO this needs to be different
    }
}

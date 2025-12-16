package com.enderio.enderio.api.poi;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterEnderPOIRenderersEvent extends Event implements IModBusEvent {

    public interface TravelRendererFactory<T extends EnderPOI> {
        POIRenderer<T> createRenderer();
    }

    private final Map<EnderPOIType<?>, TravelRendererFactory<?>> renderers = new ConcurrentHashMap<>();

    public <T extends EnderPOI> void register(EnderPOIType<T> type, TravelRendererFactory<T> rendererFactory) {
        renderers.put(type, rendererFactory);
    }

    public Map<EnderPOIType<?>, TravelRendererFactory<?>> getRenderers() {
        return Map.copyOf(renderers);
    }
}

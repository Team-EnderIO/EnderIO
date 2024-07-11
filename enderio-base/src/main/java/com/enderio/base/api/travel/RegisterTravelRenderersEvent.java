package com.enderio.base.api.travel;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterTravelRenderersEvent extends Event implements IModBusEvent {

    public interface TravelRendererFactory<T extends TravelTarget> {
        TravelRenderer<T> createRenderer();
    }

    private final Map<TravelTargetType<?>, TravelRendererFactory<?>> renderers = new ConcurrentHashMap<>();

    public <T extends TravelTarget> void register(TravelTargetType<T> type, TravelRendererFactory<T> rendererFactory) {
        renderers.put(type, rendererFactory);
    }

    public Map<TravelTargetType<?>, TravelRendererFactory<?>> getRenderers() {
        return Map.copyOf(renderers);
    }
}

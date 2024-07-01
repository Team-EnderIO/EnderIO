package com.enderio.conduits.client.gui.conduit;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.screen.ConduitScreenExtension;
import com.enderio.api.conduit.screen.RegisterConduitScreenExtensionsEvent;
import me.liliandev.ensure.ensures.EnsureSide;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// TODO: This needs to be reworked now.
public class ConduitScreenExtensions {
    private static Map<Conduit<?, ?, ?>, ConduitScreenExtension<?>> EXTENSIONS;

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static void init() {
        var event = new RegisterConduitScreenExtensionsEvent();
        ModLoader.postEvent(event);
        var factories = event.getExtensions();

        EXTENSIONS = new HashMap<>();
        factories.forEach((t, f) -> EXTENSIONS.put(t, f.createExtension()));
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    @Nullable
    public static <T extends ConduitData<T>> ConduitScreenExtension<T> get(Conduit<?, ?, T> type) {
        //noinspection unchecked
        //return (ConduitScreenExtension<T>) EXTENSIONS.get(type);

        return null;
    }
}

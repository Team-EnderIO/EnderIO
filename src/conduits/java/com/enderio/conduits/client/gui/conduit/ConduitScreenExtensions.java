package com.enderio.conduits.client.gui.conduit;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.screen.ConduitScreenExtension;
import com.enderio.api.conduit.screen.RegisterConduitScreenExtensionsEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConduitScreenExtensions {
    private static Map<ConduitType<?>, ConduitScreenExtension<?>> EXTENSIONS;

    @UseOnly(LogicalSide.CLIENT)
    public static void init() {
        var event = new RegisterConduitScreenExtensionsEvent();
        ModLoader.get().postEvent(event);
        var factories = event.getExtensions();

        EXTENSIONS = new HashMap<>();
        factories.forEach((t, f) -> EXTENSIONS.put(t, f.createExtension()));
    }

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    public static <T extends ConduitData<T>> ConduitScreenExtension<T> get(ConduitType<T> type) {
        //noinspection unchecked
        return (ConduitScreenExtension<T>) EXTENSIONS.get(type);
    }
}

package com.enderio.enderio.client.content.conduits.gui.screen_type;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.screen.ConduitScreenType;
import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
import me.liliandev.ensure.ensures.EnsureSide;
import net.neoforged.fml.ModLoader;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class ConduitScreenTypes {
    private static Map<ConduitType<?>, ConduitScreenType<?>> SCREEN_TYPES;

    @EnsureSide(EnsureSide.Side.CLIENT)
    public static void init() {
        var event = new RegisterConduitScreenTypesEvent();
        ModLoader.postEvent(event);
        SCREEN_TYPES = Map.copyOf(event.getScreenTypes());
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    @Nullable
    public static <T extends Conduit<T, U>, U extends ConnectionConfig> ConduitScreenType<U> get(
            ConduitType<T> conduitType) {
        // noinspection unchecked
        return (ConduitScreenType<U>) SCREEN_TYPES.get(conduitType);
    }
}

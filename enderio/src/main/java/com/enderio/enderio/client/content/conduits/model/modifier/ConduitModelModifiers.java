package com.enderio.enderio.client.content.conduits.model.modifier;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConduitModelModifiers {
    private static Map<ConduitType<?, ?>, ConduitModelModifier> MODIFIERS;

    @UseOnly(LogicalSide.CLIENT)
    public static void init() {
        var event = new RegisterConduitModelModifiersEvent();
        ModLoader.postEvent(event);
        var factories = event.getModifiers();

        MODIFIERS = new HashMap<>();
        factories.forEach((t, f) -> MODIFIERS.put(t, f.createModifier()));
    }

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    public static ConduitModelModifier getModifier(ConduitType<?, ?> type) {
        return MODIFIERS.get(type);
    }

    @UseOnly(LogicalSide.CLIENT)
    public static Set<ModelResourceLocation> getAllModelDependencies() {
        return MODIFIERS.values()
                .stream()
                .flatMap(modifier -> modifier.getModelDependencies().stream())
                .collect(Collectors.toSet());
    }
}

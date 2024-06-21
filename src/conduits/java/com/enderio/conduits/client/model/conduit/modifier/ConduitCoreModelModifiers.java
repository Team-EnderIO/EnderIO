package com.enderio.conduits.client.model.conduit.modifier;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.model.ConduitCoreModelModifier;
import com.enderio.api.conduit.model.RegisterConduitCoreModelModifiersEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConduitCoreModelModifiers {
    private static Map<ConduitType<?>, ConduitCoreModelModifier<?>> MODIFIERS;

    @UseOnly(LogicalSide.CLIENT)
    public static void init() {
        var event = new RegisterConduitCoreModelModifiersEvent();
        ModLoader.get().postEvent(event);
        var factories = event.getModifiers();

        MODIFIERS = new HashMap<>();
        factories.forEach((t, f) -> MODIFIERS.put(t, f.createModifier()));
    }

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    public static <T extends ConduitData<T>> ConduitCoreModelModifier<T> getModifier(ConduitType<T> type) {
        //noinspection unchecked
        return (ConduitCoreModelModifier<T>) MODIFIERS.get(type);
    }

    @UseOnly(LogicalSide.CLIENT)
    public static Set<ResourceLocation> getAllModelDependencies() {
        return MODIFIERS.values().stream().flatMap(modifier -> modifier.getModelDependencies().stream()).collect(Collectors.toSet());
    }
}

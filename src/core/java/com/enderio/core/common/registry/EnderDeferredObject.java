package com.enderio.core.common.registry;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.StringUtils;

public class EnderDeferredObject<R, T extends R> extends DeferredHolder<R, T> implements ITranslatable {
    protected String translation = StringUtils.capitalize(getId().getPath().replace('_', ' '));

    protected EnderDeferredObject(ResourceKey<R> key) {
        super(key);
    }

    @Override
    public String getTranslation() {
        return translation;
    }

    public EnderDeferredObject<R, T> setTranslation(String translation) {
        this.translation = translation;
        return this;
    }
}

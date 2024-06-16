package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ConduitIconTextureManager extends TextureAtlasHolder {

    @Nullable
    public static ConduitIconTextureManager INSTANCE;

    public ConduitIconTextureManager(TextureManager pTextureManager) {
        super(pTextureManager, EnderIO.loc("textures/atlas/conduit_icons.png"), EnderIO.loc("conduit_icons"));
        INSTANCE = this;
    }

    public TextureAtlasSprite get(ConduitType<?> conduitType) {
        return this.getSprite(Objects.requireNonNull(EnderIORegistries.CONDUIT_TYPES.getKey(conduitType)));
    }
}

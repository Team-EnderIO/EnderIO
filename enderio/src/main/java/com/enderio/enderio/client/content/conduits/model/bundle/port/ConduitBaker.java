package com.enderio.enderio.client.content.conduits.model.bundle.port;

import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class ConduitBaker implements ModelBaker {

    private final ModelBaker baker;
    private final ConduitSpriteGetter sprites;

    public ConduitBaker(ModelBaker baker, TextureAtlasSprite sprite) {
        this.baker = baker;
        this.sprites = new ConduitSpriteGetter(this.baker.sprites(), sprite);
    }

    @Override
    public ResolvedModel getModel(Identifier modelLocation) {
        return baker.getModel(modelLocation);
    }

    @Override
    public SpriteGetter sprites() {
        return this.sprites;
    }

    @Override
    public <T> T compute(SharedOperationKey<T> key) {
        return baker.compute(key);
    }

    static class ConduitSpriteGetter implements SpriteGetter {

        private final SpriteGetter spriteGetter;
        private final TextureAtlasSprite sprite;

        public ConduitSpriteGetter(SpriteGetter spriteGetter, @Nullable TextureAtlasSprite sprite) {
            this.spriteGetter = spriteGetter;
            this.sprite = sprite;
        }

        @Override
        public TextureAtlasSprite get(Material material, ModelDebugName debugName) {
            return spriteGetter.get(material, debugName);
        }

        @Override
        public TextureAtlasSprite reportMissingReference(String name, ModelDebugName debugName) {
            return spriteGetter.reportMissingReference(name, debugName);
        }

        @Override
        public TextureAtlasSprite resolveSlot(TextureSlots textureSlots, String name, ModelDebugName modelDebugName) {
            if (sprite != null) {
                return sprite;
            }
            return spriteGetter.resolveSlot(textureSlots, name, modelDebugName);
        }

    }
}

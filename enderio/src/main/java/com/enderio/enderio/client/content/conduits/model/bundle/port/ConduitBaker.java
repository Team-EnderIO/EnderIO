package com.enderio.enderio.client.content.conduits.model.bundle.port;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;

public class ConduitBaker implements ModelBaker {

    private final ModelBaker baker;
    private final ConduitMaterialBaker materials;

    public ConduitBaker(ModelBaker baker, Material material) {
        this.baker = baker;
        this.materials = new ConduitMaterialBaker(this.baker.materials(), material);
    }

    @Override
    public ResolvedModel getModel(Identifier modelLocation) {
        return baker.getModel(modelLocation);
    }

    @Override
    public BlockStateModelPart missingBlockModelPart() {
        return baker.missingBlockModelPart();
    }

    @Override
    public MaterialBaker materials() {
        return this.materials;
    }

    @Override
    public Interner interner() {
        return baker.interner();
    }

    @Override
    public <T> T compute(SharedOperationKey<T> key) {
        return baker.compute(key);
    }

    static class ConduitMaterialBaker extends MaterialBaker {

        // 26.2-port: MaterialBaker is now an abstract class; the parent needs the
        //   missing-sprite TextureAtlasSprite passed in. We pass null here (matches the
        //   default behaviour used by the parent MaterialBaker class), but a proper
        //   port should plumb in the real missing-sprite.
        ConduitMaterialBaker(MaterialBaker materialBaker, Material sprite) {
            super(/* missingSprite= */ null);
            this.materialBaker = materialBaker;
            this.sprite = sprite;
        }

        private final MaterialBaker materialBaker;
        private final Material sprite;

        @Override
        public Material.Baked get(Material material, ModelDebugName modelDebugName) {
            return materialBaker.get(material, modelDebugName);
        }

        // 26.2-port: MaterialBaker added a new abstract bake(Material) method
        @Override
        protected Material.Baked bake(Material material) {
            return materialBaker.get(material, () -> "conduit_baker");
        }

        @Override
        public Material.Baked reportMissingReference(String name, ModelDebugName debugName) {
            return materialBaker.reportMissingReference(name, debugName);
        }

        @Override
        public Material.Baked resolveSlot(TextureSlots textureSlots, String name, ModelDebugName modelDebugName) {
            if (sprite != null) {
                return materialBaker.get(sprite, modelDebugName);
            }

            return materialBaker.resolveSlot(textureSlots, name, modelDebugName);
        }

    }
}

package com.enderio.enderio.client.content.conduits.model.bundle.port;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ConduitBaker implements ModelBaker {
    private final ModelBaker baker;
    private final ConduitMaterialBaker materials;

    private static SpriteLoader.@Nullable Preparations blockAtlas;
    private static Map<Material, ConduitMaterialBaker> materialBakers = new HashMap<>();

    public ConduitBaker(ModelBaker baker, Material material) {
        this.baker = baker;
        this.materials = materialBakers.computeIfAbsent(material, ignored -> new ConduitMaterialBaker(this.baker.materials(), material));
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

    // Approach based on XFact's FramedBlocks RuntimeMaterialBaker.
    public static CompletableFuture<Void> reload(
        PreparableReloadListener.SharedState currentReload,
        @SuppressWarnings("unused") Executor taskExecutor,
        PreparableReloadListener.PreparationBarrier preparationBarrier,
        Executor reloadExecutor) {
        return currentReload.get(AtlasManager.PENDING_STITCH)
            .get(AtlasIds.BLOCKS)
            .thenCompose(preparationBarrier::wait)
            .thenAcceptAsync(ConduitBaker::reload, reloadExecutor);
    }

    private static void reload(SpriteLoader.Preparations blockAtlas) {
        ConduitBaker.blockAtlas = blockAtlas;
        materialBakers.clear();
    }

    static class ConduitMaterialBaker extends MaterialBaker {
        ConduitMaterialBaker(MaterialBaker materialBaker, Material sprite) {
            super(Objects.requireNonNull(blockAtlas, "Not ready to bake materials").missing());
            this.materialBaker = materialBaker;
            this.sprite = sprite;
        }

        private final MaterialBaker materialBaker;
        private final Material sprite;

        @Override
        public Material.Baked get(Material material, ModelDebugName modelDebugName) {
            return materialBaker.get(material, modelDebugName);
        }

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

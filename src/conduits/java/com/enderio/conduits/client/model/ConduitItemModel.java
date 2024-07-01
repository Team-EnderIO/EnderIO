package com.enderio.conduits.client.model;

import com.enderio.api.conduit.Conduit;
import com.enderio.conduits.common.components.RepresentedConduitType;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.core.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConduitItemModel extends BakedModelWrapper<BakedModel> {

    private final ConduitItemOverrides itemOverrides = new ConduitItemOverrides();

    public ConduitItemModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public ItemOverrides getOverrides() {
        return itemOverrides;
    }

    public static class ConduitItemOverrides extends ItemOverrides {

        private final Map<RepresentedConduitType, BakedModel> CACHE = new HashMap<>();

        @Nullable
        @Override
        public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
            RepresentedConduitType representedConduitType = pStack.get(ConduitComponents.REPRESENTED_CONDUIT_TYPE);
            return CACHE.computeIfAbsent(representedConduitType, t -> createBakedModel(t, pModel, pLevel));
        }

        private BakedModel createBakedModel(@Nullable RepresentedConduitType representedConduitType, BakedModel model, @Nullable ClientLevel level) {
            ResourceLocation conduitTexture = MissingTextureAtlasSprite.getLocation();
            if (representedConduitType != null) {
                Optional<ResourceKey<Conduit<?, ?, ?>>> key = representedConduitType.conduitType().unwrapKey();

                if (key.isPresent()) {
                    ResourceLocation location = key.get().location();
                    conduitTexture = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "block/conduit/" + location.getPath());
                }
            }

            // Get the replacement texture.
            TextureAtlasSprite newTexture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(conduitTexture);

            // Construct a new baked model, replacing the texture of each quad with the new texture.
            IModelBuilder<?> builder = IModelBuilder.of(model.useAmbientOcclusion(), model.usesBlockLight(), model.isGui3d(),
                model.getTransforms(), model.getOverrides(), newTexture, RenderTypeGroup.EMPTY);

            var unculledQuads = model.getQuads(null, null, level.random, ModelData.EMPTY, null);
            for (BakedQuad quad : unculledQuads) {
                builder.addUnculledFace(paintQuad(quad, newTexture));
            }

            for (Direction side : Direction.values()) {
                var culledQuads = model.getQuads(null, side, level.random, ModelData.EMPTY, null);
                for (BakedQuad quad : culledQuads) {
                    builder.addCulledFace(side, paintQuad(quad, newTexture));
                }
            }

            return builder.build();
        }

        // TODO: This is stolen from painted blocks.
        //       This should be moved into a common utility.
        protected BakedQuad paintQuad(BakedQuad toCopy, TextureAtlasSprite sprite) {
            BakedQuad copied = new BakedQuad(Arrays.copyOf(toCopy.getVertices(), 32), -1, toCopy.getDirection(), sprite, toCopy.isShade());

            for (int i = 0; i < 4; i++) {
                float[] uv0 = RenderUtil.unpackVertices(copied.getVertices(), i, IQuadTransformer.UV0, 2);
                uv0[0] = (uv0[0] - toCopy.getSprite().getU0()) * sprite.contents().width() / toCopy.getSprite().contents().width() + sprite.getU0();
                uv0[1] = (uv0[1] - toCopy.getSprite().getV0()) * sprite.contents().height() / toCopy.getSprite().contents().height() + sprite.getV0();
                int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
                copied.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
                copied.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
            }
            return copied;
        }
    }
}

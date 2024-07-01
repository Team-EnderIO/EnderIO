package com.enderio.conduits.client.model.conduit.modifier;

import com.enderio.EnderIO;
import com.enderio.api.conduit.model.ConduitCoreModelModifier;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.core.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidConduitCoreModelModifier implements ConduitCoreModelModifier<FluidConduitData> {

    private static final ModelResourceLocation FLUID_MODEL = ModelResourceLocation.standalone(EnderIO.loc("block/extra/fluids"));

    @Override
    public List<BakedQuad> createConnectionQuads(FluidConduitData data, @Nullable Direction facing, Direction connectionDirection, RandomSource rand,
        @Nullable RenderType type) {
        if (!data.isMultiFluid && data.lockedFluid() != null) {
            return new FluidPaintQuadTransformer(data.lockedFluid())
                .process(Minecraft.getInstance().getModelManager().getModel(FLUID_MODEL)
                    .getQuads(Blocks.COBBLESTONE.defaultBlockState(), facing, rand, ModelData.EMPTY, type));
        }

        return List.of();
    }

    @Override
    public List<ModelResourceLocation> getModelDependencies() {
        return List.of(FLUID_MODEL);
    }

    private record FluidPaintQuadTransformer(Fluid fluid) implements IQuadTransformer {
        @Override
        public void processInPlace(BakedQuad quad) {
            IClientFluidTypeExtensions clientExtension = IClientFluidTypeExtensions.of(fluid);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(clientExtension.getStillTexture());
            for (int i = 0; i < 4; i++) {
                float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
                uv0[0] = (uv0[0] - quad.getSprite().getU0()) * sprite.contents().width() / quad.getSprite().contents().height() + sprite.getU0();
                uv0[1] = (uv0[1] - quad.getSprite().getV0()) * sprite.contents().width() / quad.getSprite().contents().height() + sprite.getV0();
                int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
                quad.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
                quad.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
                RenderUtil.putColorARGB(quad.getVertices(), i, clientExtension.getTintColor());
            }
            quad.sprite = sprite;
        }
    }
}

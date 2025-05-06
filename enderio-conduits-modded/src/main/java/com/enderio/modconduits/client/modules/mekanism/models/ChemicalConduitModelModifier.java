package com.enderio.modconduits.client.modules.mekanism.models;

import com.enderio.base.api.EnderIO;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.model.ConduitModelModifier;
import com.enderio.core.client.RenderUtil;
import com.enderio.modconduits.common.modules.mekanism.chemical.ChemicalConduit;
import java.util.List;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class ChemicalConduitModelModifier implements ConduitModelModifier {

    private static final ModelResourceLocation FLUID_MODEL = ModelResourceLocation
            .standalone(EnderIO.loc("block/extra/fluids"));

    @Override
    public List<BakedQuad> createConnectionQuads(Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData,
            @Nullable Direction facing, Direction connectionDirection, RandomSource rand, @Nullable RenderType type) {
        if (!(conduit.value() instanceof ChemicalConduit chemicalConduit)) {
            return List.of();
        }

        if (chemicalConduit.isMultiChemical()) {
            return List.of();
        }

        if (extraWorldData == null || !extraWorldData.contains("LockedChemical")) {
            return List.of();
        }

        ResourceLocation lockedFluidId = ResourceLocation.parse(extraWorldData.getString("LockedChemical"));
        Chemical lockedChemical = MekanismAPI.CHEMICAL_REGISTRY.get(lockedFluidId);

        if (!lockedChemical.isEmptyType()) {
            return new ChemicalPaintQuadTransformer(lockedChemical).process(Minecraft.getInstance()
                    .getModelManager()
                    .getModel(FLUID_MODEL)
                    .getQuads(Blocks.COBBLESTONE.defaultBlockState(), facing, rand, ModelData.EMPTY, type));
        }

        return List.of();
    }

    @Override
    public List<ModelResourceLocation> getModelDependencies() {
        return List.of(FLUID_MODEL);
    }

    private record ChemicalPaintQuadTransformer(Chemical chemical) implements IQuadTransformer {
        @Override
        public void processInPlace(BakedQuad quad) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(chemical.getIcon());
            for (int i = 0; i < 4; i++) {
                float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
                uv0[0] = (uv0[0] - quad.getSprite().getU0()) * sprite.contents().width()
                        / quad.getSprite().contents().height() + sprite.getU0();
                uv0[1] = (uv0[1] - quad.getSprite().getV0()) * sprite.contents().width()
                        / quad.getSprite().contents().height() + sprite.getV0();
                int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
                quad.getVertices()[IQuadTransformer.UV0 + i * IQuadTransformer.STRIDE] = packedTextureData[0];
                quad.getVertices()[IQuadTransformer.UV0 + 1 + i * IQuadTransformer.STRIDE] = packedTextureData[1];
                RenderUtil.putColorARGB(quad.getVertices(), i, chemical.getTint());
            }
            quad.sprite = sprite;
        }
    }
}

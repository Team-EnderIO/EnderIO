package com.enderio.modded_conduits.client.modules.mekanism.models;

import com.enderio.core.client.RenderUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.modded_conduits.common.modules.mekanism.chemical.ChemicalConduit;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelIdentifier;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ChemicalConduitModelModifier implements ConduitModelModifier {

    private static final ModelIdentifier FLUID_MODEL = ModelIdentifier
            .standalone(EnderIO.rl("block/extra/fluids"));

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

        Identifier lockedFluidId = Identifier.parse(extraWorldData.getString("LockedChemical"));
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
    public List<ModelIdentifier> getModelDependencies() {
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

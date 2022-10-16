package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.RenderUtil;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class FluidClientData extends IClientConduitData.Simple<FluidExtendedData> {

    private static final ResourceLocation MODEL = EnderIO.loc("block/extra/fluids");
    private static final ResourceLocation WIDGET_TEXTURE = EnderIO.loc("textures/gui/fluidbackground.png");
    public FluidClientData(ResourceLocation getTextureLocation, Vector2i getTexturePosition) {
        super(getTextureLocation, getTexturePosition);
    }

    @Override
    public List<BakedQuad> createConnectionQuads(FluidExtendedData extendedConduitData, @Nullable Direction facing, Direction connectionDirection,
        RandomSource rand, @Nullable RenderType type) {
        if (!extendedConduitData.isMultiFluid && extendedConduitData.lockedFluid != null)
            return new FluidPaintQuadTransformer(extendedConduitData.lockedFluid).process(getModel(MODEL).getQuads(Blocks.COBBLESTONE.defaultBlockState(), facing, rand, ModelData.EMPTY, type));
        return List.of();
    }

    @Override
    public List<AbstractWidget> createWidgets(Screen screen, FluidExtendedData extendedConduitData, Supplier<Direction> direction, Vector2i widgetsStart) {
        return List.of(new FluidWidget(screen, widgetsStart.add(0, 20), () -> extendedConduitData.lockedFluid, () -> extendedConduitData.shouldReset = true));
    }

    @Override
    public List<ResourceLocation> modelsToLoad() {
        return List.of(MODEL);
    }

    private record FluidPaintQuadTransformer(Fluid fluid) implements IQuadTransformer {
        @Override
        public void processInPlace(BakedQuad quad) {
            IClientFluidTypeExtensions clientExtension = IClientFluidTypeExtensions.of(fluid);
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(clientExtension.getStillTexture());
            for (int i = 0; i < 4; i++) {
                float[] uv0 = RenderUtil.unpackVertices(quad.getVertices(), i, IQuadTransformer.UV0, 2);
                uv0[0] = (uv0[0] - quad.getSprite().getU0()) * sprite.getWidth() / quad.getSprite().getWidth() + sprite.getU0();
                uv0[1] = (uv0[1] - quad.getSprite().getV0()) * sprite.getHeight() / quad.getSprite().getHeight() + sprite.getV0();
                int[] packedTextureData = RenderUtil.packUV(uv0[0], uv0[1]);
                quad.getVertices()[4 + i * STRIDE] = packedTextureData[0];
                quad.getVertices()[5 + i * STRIDE] = packedTextureData[1];
                RenderUtil.putColorARGB(quad.getVertices(), i, clientExtension.getTintColor());
            }
        }
    }

    private static class FluidWidget extends AbstractWidget {
        private final Runnable onPress;
        private final Supplier<Fluid> currentFluid;
        private final Screen addedOn;
        public FluidWidget(Screen addedOn, Vector2i pos, Supplier<Fluid> fluid, Runnable onPress) {
            super(pos.x(), pos.y(), 14, 14, Component.empty());
            this.onPress = onPress;
            this.currentFluid = fluid;
            this.addedOn = addedOn;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        @Override
        public void renderButton(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {

            renderToolTip(poseStack, pMouseX, pMouseY);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGET_TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            blit(poseStack, x, y, 0, 0, this.width, this.height);
            if (currentFluid.get() == null)
                return;
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(currentFluid.get());
            ResourceLocation still = props.getStillTexture();
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);
                RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

                int color = props.getTintColor();
                RenderSystem.setShaderColor(
                    FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F,
                    FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F);
                RenderSystem.enableBlend();


                int atlasWidth = (int)(sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int)(sprite.getHeight() / (sprite.getV1() - sprite.getV0()));

                blit(poseStack, x + 1, y + 1, getBlitOffset(), sprite.getU0()*atlasWidth, sprite.getV0()*atlasHeight, 12, 12, atlasWidth, atlasHeight);

                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
            if (isHovered && isActive()) {
                List<Component> components = new ArrayList<>();
                components.add(EIOLang.FLUID_CONDUIT_CHANGE_FLUID1);
                components.add(EIOLang.FLUID_CONDUIT_CHANGE_FLUID2);
                if (currentFluid.get() != null) {
                    components.add(TooltipUtil.withArgs(EIOLang.FLUID_CONDUIT_CHANGE_FLUID3, currentFluid.get().getFluidType().getDescription()));
                }
                addedOn.renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
        @Override
        public void onClick(double pMouseX, double pMouseY) {
            onPress.run();
        }
    }
}

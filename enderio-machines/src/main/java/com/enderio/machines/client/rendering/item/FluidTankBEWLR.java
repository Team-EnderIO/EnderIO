package com.enderio.machines.client.rendering.item;

import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistries;

// TODO: No longer lights in the inventory/hand like other machines...
public class FluidTankBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final FluidTankBEWLR INSTANCE = new FluidTankBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public FluidTankBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(EIOMachines.loc("fluid_tank"), "facing=north"));
        poseStack.pushPose();

        Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(RenderType.cutout()));

        // Read the fluid from the NBT, if it has fluid, then we display it.
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains("BlockEntityTag")) {
            CompoundTag blockEntityTag = nbt.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("Fluids")) {
                CompoundTag tank = blockEntityTag.getCompound("Fluids");

                if (tank.contains("FluidName") && tank.contains("Amount")) {
                    Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tank.getString("FluidName")));
                    int amount = tank.getInt("Amount");

                    if (fluid != null && amount > 0) {
                        VertexConsumer fluidBuffer;
                        if (ItemBlockRenderTypes.canRenderInLayer(fluid.defaultFluidState(), RenderType.translucent())) {
                            fluidBuffer = buffer.getBuffer(RenderType.translucent());
                        } else {
                            fluidBuffer = buffer.getBuffer(RenderType.solid());
                        }

                        renderFluid(fluid, amount / (float) FluidTankBlockEntity.CAPACITY, poseStack.last().pose(), poseStack.last().normal(), fluidBuffer);
                    }
                }
            }
        }

        poseStack.popPose();
    }

    private void renderFluid(Fluid fluid, float fillAmount, Matrix4f pose, Matrix3f normal, VertexConsumer consumer) {
        // Get fluid crap
        FluidAttributes attributes = fluid.getAttributes();
        TextureAtlasSprite texture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(attributes.getStillTexture());
        int color = attributes.getColor();

        // Get sizes
        float fluidHeight = (14 * fillAmount) / 16.0f;
        float depth = 0.0625F;

        float minX = depth;
        float maxX = 1.0f - depth;
        float minY = depth;
        float maxY = depth + fluidHeight;
        float minZ = depth;
        float maxZ = 1 - minZ;

        // Up
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625F, 0.0625F + fluidHeight, 0.0625F + fluidHeight, 1.0F - 0.0625F, 1.0F - 0.0625F, 0.0625F, 0.0625F);

        // South
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625f, 0.0625F, 0.0625f + fluidHeight, 1.0F - depth, 1.0F - depth, 1.0F - depth, 1.0F - depth);

        // North
        this.renderFace(pose, normal, consumer, texture, color, 0.0625F, 1.0F - 0.0625F, 0.0625F + fluidHeight, 0.0625F, depth, depth, depth, depth);

        // East
        this.renderFace(pose, normal, consumer, texture, color, depth, depth, 0.0625F, 0.0625F + fluidHeight, 0.0625f, 1.0F - 0.0625f, 1.0F - 0.0625f, 0.0625F);

        // West
        this.renderFace(pose, normal, consumer, texture, color, 1.0F - depth, 1.0F - depth, 0.0625F + fluidHeight, 0.0625F, 0.0625f, 1.0F - 0.0625f, 1.0F - 0.0625f, 0.0625F);
    }

    private void renderFace(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, TextureAtlasSprite texture, int color, float x0, float x1, float y0, float y1, float z0, float z1, float z2, float z3) {
        // TODO: Get U/V's using the height of the face so it doesn't squash the texture.

        // Smarter method that doesn't need to run calculations every frame.
        float height;
        if (y0 > y1)
            height = y0 - y1;
        else height = y1 - y0;

        float minU = 0.0625f * texture.getWidth(); // pos 1 in a 16x16
        float maxU = (1.0f - 0.0625f) * texture.getWidth(); // pos 15 in a 16x16
        float minV = 0.0625f * texture.getHeight(); // pos 1 in a 16x16
        float maxV = height * texture.getHeight(); // pos 15 in a 16x16

        consumer.vertex(pose, x0, y0, z0).color(color).uv(texture.getU(minU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y0, z1).color(color).uv(texture.getU(maxU), texture.getV(minV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x1, y1, z2).color(color).uv(texture.getU(maxU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
        consumer.vertex(pose, x0, y1, z3).color(color).uv(texture.getU(minU), texture.getV(maxV)).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0f, 0.0f, 0.0f).endVertex();
    }
}

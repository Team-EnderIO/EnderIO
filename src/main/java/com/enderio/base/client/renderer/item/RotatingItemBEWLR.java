package com.enderio.base.client.renderer.item;

import com.enderio.core.common.util.PoseStackHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

public class RotatingItemBEWLR extends BlockEntityWithoutLevelRenderer {

    public static final RotatingItemBEWLR INSTANCE = new RotatingItemBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public RotatingItemBEWLR(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pOverlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        if (pStack.getItem() instanceof IRotatingItem rotatingItem) {
            ResourceLocation itemRegName = ForgeRegistries.ITEMS.getKey(pStack.getItem());

            BakedModel model = mc.getModelManager().getModel(new ResourceLocation(itemRegName.getNamespace(), "item/" + itemRegName.getPath() + "_helper"));
            pPoseStack.pushPose();
            if (rotatingItem.getTicksPerRotation() != 0) {
                PoseStackHelper.rotateAroundPivot(pPoseStack, new Vector3f(0.5F, 0.5F, 0F), Axis.ZP,
                    (360.0F / rotatingItem.getTicksPerRotation()) * (mc.player.clientLevel.getGameTime() % rotatingItem.getTicksPerRotation()), true); // rotates the item 360/tpr degrees each tick
            }
            mc.getItemRenderer().renderModelLists(model, pStack, pPackedLight, pOverlay, pPoseStack, pBuffer.getBuffer(RenderType.cutout()));
            pPoseStack.popPose();
        }
    }
}
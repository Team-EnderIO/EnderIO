package com.enderio.base.client.renderer.item;

import com.enderio.base.client.renderer.block.EnderSkullRenderer;
import com.enderio.base.common.block.EnderSkullBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EnderSkullIBEWLR extends BlockEntityWithoutLevelRenderer {
    public static EnderSkullIBEWLR INSTANCE = new EnderSkullIBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    private EnderSkullRenderer.EnderSkullModel enderskullmodel;

    protected EnderSkullIBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
        this.enderskullmodel = new EnderSkullRenderer.EnderSkullModel(entityModelSet.bakeLayer(EnderSkullRenderer.ENDER_SKULL));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
        int packedOverlay) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof EnderSkullBlock skullBlock) {
                EnderSkullRenderer.renderSkull(null, 180f, 0, poseStack, buffer, packedLight, enderskullmodel, EnderSkullRenderer.RENDERTYPE);
            }
        }
    }
}

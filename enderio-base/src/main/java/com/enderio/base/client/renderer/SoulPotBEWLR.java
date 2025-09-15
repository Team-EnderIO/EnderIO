package com.enderio.base.client.renderer;

import com.enderio.base.common.blockentity.SoulPotBlockEntity;
import com.enderio.base.common.init.EIOBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SoulPotBEWLR extends BlockEntityWithoutLevelRenderer {


    private final SoulPotBlockEntity blockEntity;

    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public static final SoulPotBEWLR INSTANCE = new SoulPotBEWLR(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public SoulPotBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
        blockEntity = new SoulPotBlockEntity(BlockPos.ZERO, EIOBlocks.SOUL_POT.get().defaultBlockState());

        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {

        blockEntity.applyComponentsFromItemStack(stack);

        this.blockEntityRenderDispatcher.renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
    }
}

package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class ObeliskBER implements BlockEntityRenderer<BlockEntity, BlockEntityRenderState> {

    private final Supplier<Item> supplier;

    public ObeliskBER(Supplier<Item> itemSupplier) {
        this.supplier = itemSupplier;
    }

    public static <T extends BlockEntity> Function<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T, ? extends BlockEntityRenderState>> factory(
            Supplier<Item> itemSupplier) {
        return context -> new ObeliskBER(itemSupplier);
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BannerRenderState();
    }

    @Override
    public void submit(BlockEntityRenderState blockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
        CameraRenderState cameraRenderState) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0.75, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 position = minecraft.player.position();
        float f1 = (float) (Mth.atan2(position.z - blockEntityRenderState.blockPos.getZ() - 0.5D,
            position.x - blockEntityRenderState.blockPos.getX() - 0.5D) * 180.0f / Math.PI + 90);
        poseStack.mulPose(Axis.YP.rotationDegrees(-f1 + 180));
        ItemStack stack = new ItemStack(supplier.get());
        ItemStackRenderState renderState = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(renderState, stack, ItemDisplayContext.GUI, null, null, 0);
        if (stack.getItem() instanceof BlockItem) { //Blocks are in iso, let's correct that
            poseStack.mulPose(Axis.YP.rotationDegrees(45));
            poseStack.mulPose(Axis.XP.rotationDegrees(-30));
            poseStack.scale(1.2f, 1.2f, 1.2f);
        }

        // TODO: 1.21.11: packed Light stolen from GuiRenderer. Check it.
        renderState.submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}

package com.enderio.machines.client.rendering.item;

import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.common.blocks.fluid_tank.FluidTankBlockEntity;
import com.enderio.machines.common.blocks.fluid_tank.FluidTankBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

// TODO: No longer lights in the inventory/hand like other machines...
public class FluidTankBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final FluidTankBEWLR INSTANCE = new FluidTankBEWLR(
            Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public FluidTankBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // Get the model for the fluid tank block
        BakedModel model = Minecraft.getInstance()
                .getModelManager()
                .getModel(new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(stack.getItem()), "facing=north"));
        poseStack.pushPose();

        // Render the main model
        Minecraft.getInstance()
                .getItemRenderer()
                .renderModelLists(model, stack, packedLight, packedOverlay, poseStack,
                        buffer.getBuffer(RenderType.cutout()));

        // Read the fluid from the NBT, if it has fluid, then we render it.
        IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            FluidStack fluid = fluidHandler.getFluidInTank(0); // Only one tank present
            if (!fluid.isEmpty()) {
                VertexConsumer fluidBuffer = buffer.getBuffer(Sheets.translucentCullBlockSheet());

                int capacity = FluidTankBlockEntity.Standard.CAPACITY;
                if (stack.getItem() instanceof FluidTankBlockItem tank) {
                    capacity = tank.getCapacity();
                }

                PoseStack.Pose pose = poseStack.last();
                IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
                FluidTankBER.renderFluid(pose, fluidBuffer, fluid.getFluid(), fluid.getAmount() / (float) capacity,
                        props.getTintColor(), packedLight);
            }
        }

        poseStack.popPose();
    }
}

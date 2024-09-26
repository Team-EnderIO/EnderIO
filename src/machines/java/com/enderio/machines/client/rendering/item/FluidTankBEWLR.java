package com.enderio.machines.client.rendering.item;

import com.enderio.machines.client.rendering.blockentity.FluidTankBER;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.init.MachineBlocks;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;

// TODO: No longer lights in the inventory/hand like other machines...
public class FluidTankBEWLR extends BlockEntityWithoutLevelRenderer {
    public static final FluidTankBEWLR INSTANCE = new FluidTankBEWLR(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public FluidTankBEWLR(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // Get the model for the fluid tank block
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(ForgeRegistries.ITEMS.getKey(stack.getItem()), "facing=north"));
        poseStack.pushPose();

        // Render the main model
        Minecraft.getInstance().getItemRenderer().renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(RenderType.cutout()));

        LazyOptional<IFluidHandlerItem> fluidHandlerItemOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (fluidHandlerItemOptional.isPresent()) {
            IFluidHandlerItem fluidHandlerItem = fluidHandlerItemOptional.orElseThrow(IllegalStateException::new);

            FluidStack fluidStack = fluidHandlerItem.getFluidInTank(0);

            if (!fluidStack.isEmpty()) {
                // Get the preferred render buffer
                VertexConsumer fluidBuffer = buffer.getBuffer(Sheets.translucentCullBlockSheet());

                // Determine capacity.
                int capacity = FluidTankBlockEntity.Standard.CAPACITY;
                if (stack.is(MachineBlocks.PRESSURIZED_FLUID_TANK.get().asItem())) {
                    capacity = FluidTankBlockEntity.Enhanced.CAPACITY;
                }

                PoseStack.Pose pose = poseStack.last();
                IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
                FluidTankBER.renderFluid(pose.pose(), pose.normal(), fluidBuffer, fluidStack.getFluid(), fluidStack.getAmount() / (float) capacity, props.getTintColor(), packedLight);
            }
        }

        poseStack.popPose();
    }
}

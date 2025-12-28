package com.enderio.enderio.client.content.fluid_tank;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

// TODO: No longer lights in the inventory/hand like other machines...
// TODO: PORT: Hook back up
public class FluidTankRenderer implements SpecialModelRenderer<IFluidHandlerItem> {

    public static final FluidTankRenderer INSTANCE = new FluidTankRenderer();

    public FluidTankRenderer() {
    }

    @Override
    public void render(@Nullable IFluidHandlerItem fluidHandler, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource,
        int packedLight, int packedOverlay, boolean hasFoilType) {
        //TODO render this model statically
        // Get the model for the fluid tank block
//        BakedModel model = Minecraft.getInstance()
//            .getModelManager()
//            .getModel(new ModelIdentifier(BuiltInRegistries.ITEM.getKey(stack.getItem()), "facing=north"));
//        poseStack.pushPose();
//
//        // Render the main model
//        Minecraft.getInstance()
//            .getItemRenderer()
//            .renderModelLists(model, stack, packedLight, packedOverlay, poseStack,
//                buffer.getBuffer(RenderType.cutout()));

        // Read the fluid from the NBT, if it has fluid, then we render it.
        if (fluidHandler != null) {
            FluidStack fluid = fluidHandler.getFluidInTank(0); // Only one tank present
            if (!fluid.isEmpty()) {
                VertexConsumer fluidBuffer = bufferSource.getBuffer(Sheets.translucentItemSheet()); //TODO cullsheet is gone

                int capacity = FluidTankBlockEntity.Standard.CAPACITY;
                capacity = fluidHandler.getTankCapacity(0);
                //TODO why did we use this?
//                if (stack.getItem() instanceof FluidTankBlockItem tank) {
//                    capacity = tank.getCapacity();
//                }

                PoseStack.Pose pose = poseStack.last();
                IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
                FluidRendererUtil.submitFluid(pose, fluidBuffer, fluid.getFluid(), fluid.getAmount() / (float) capacity,
                    props.getTintColor(), packedLight);
            }
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Set<Vector3f> output) {

    }

    @Override
    public @Nullable IFluidHandlerItem extractArgument(ItemStack stack) {
        return stack.getCapability(Capabilities.FluidHandler.ITEM);
    }

    public static final class Unbaked implements SpecialModelRenderer.Unbaked {

        public static final Identifier ID = EnderIO.rl("fluid_tank");
        public static final FluidTankRenderer.Unbaked INSTANCE = new FluidTankRenderer.Unbaked();
        public static final MapCodec<FluidTankRenderer.Unbaked> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return FluidTankRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return CODEC;
        }
    }
}

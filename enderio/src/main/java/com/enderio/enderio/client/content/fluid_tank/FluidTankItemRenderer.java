package com.enderio.enderio.client.content.fluid_tank;

import com.enderio.core.client.FluidRendererUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

// TODO: No longer lights in the inventory/hand like other machines...
// TODO: PORT: Hook back up
public class FluidTankItemRenderer implements SpecialModelRenderer<ResourceHandler<FluidResource>> {

    public static final FluidTankItemRenderer INSTANCE = new FluidTankItemRenderer();

    public FluidTankItemRenderer() {
    }

    @Override
    public void submit(@Nullable ResourceHandler<FluidResource> fluidHandler, ItemDisplayContext displayContext, PoseStack poseStack,
        SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
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
            var fluid = fluidHandler.getResource(0); // Only one tank present
            if (!fluid.isEmpty()) {
                int capacity = FluidTankBlockEntity.Standard.CAPACITY;
                capacity = fluidHandler.getCapacityAsInt(0, fluid);
                //TODO why did we use this?
//                if (stack.getItem() instanceof FluidTankBlockItem tank) {
//                    capacity = tank.getCapacity();
//                }

                IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
                FluidRendererUtil.submitFluid(poseStack, Sheets.translucentItemSheet(), nodeCollector, fluid.getFluid(),
                    fluidHandler.getAmountAsInt(0) / (float) capacity, props.getTintColor(), packedLight);
            }
        }

        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> p_470829_) {

    }

    @Override
    public @Nullable ResourceHandler<FluidResource> extractArgument(ItemStack stack) {
        return stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
    }

    public static final class Unbaked implements SpecialModelRenderer.Unbaked {

        public static final Identifier ID = EnderIO.id("fluid_tank");
        public static final FluidTankItemRenderer.Unbaked INSTANCE = new FluidTankItemRenderer.Unbaked();
        public static final MapCodec<FluidTankItemRenderer.Unbaked> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public @Nullable SpecialModelRenderer<?> bake(BakingContext context) {
            return FluidTankItemRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked> type() {
            return CODEC;
        }
    }
}

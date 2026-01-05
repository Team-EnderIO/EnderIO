package com.enderio.enderio.client.content.fluid_tank;

import com.enderio.core.client.FluidRendererUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

// TODO: No longer lights in the inventory/hand like other machines...
// TODO: PORT: Hook back up
public class FluidTankItemRenderer implements SpecialModelRenderer<FluidTankItemRenderer.FluidTankState> {

    public static final FluidTankItemRenderer INSTANCE = new FluidTankItemRenderer();

    public FluidTankItemRenderer() {
    }

    @Override
    public void submit(@Nullable FluidTankState state, ItemDisplayContext displayContext, PoseStack poseStack,
        SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
        if (state != null) {
            FluidRendererUtil.submitFluid(poseStack, Sheets.translucentBlockItemSheet(), nodeCollector, state.fluid,
                state.amount / (float) state.capacity, state.color, packedLight);
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> p_470829_) {

    }

    @Override
    public @Nullable FluidTankState extractArgument(ItemStack stack) {
        var fluidHandler = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack));
        if (fluidHandler != null) {
            var fluid = fluidHandler.getResource(0); // Only one tank present
            if (fluid.isEmpty()) {
                return null;
            }
            int capacity = fluidHandler.getCapacityAsInt(0, fluid);
            int amount = fluidHandler.getAmountAsInt(0);
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
            int color = props.getTintColor();
            return new FluidTankState(fluid.getFluid(), capacity, amount, color);
        }
        return null;
    }

    public record FluidTankState(Fluid fluid, int capacity, int amount, int color) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<FluidTankItemRenderer.Unbaked> CODEC = MapCodec.unit(new Unbaked());

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

package com.enderio.enderio.client.content.fluid_tank;

import com.enderio.core.client.FluidRendererUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidTankItemRenderer implements SpecialModelRenderer<FluidTankItemRenderer.FluidTankState> {

    public static final FluidTankItemRenderer INSTANCE = new FluidTankItemRenderer();

    public FluidTankItemRenderer() {
    }

    @Override
    public void submit(FluidTankItemRenderer.FluidTankState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords,
        int overlayCoords, boolean hasFoil, int outlineColor) {
        if (state != null) {
            FluidRendererUtil.submitFluid(poseStack, Sheets.translucentBlockItemSheet(), submitNodeCollector, state.fluid,
                state.fluid.amount() / (float) state.capacity, lightCoords);
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
            return new FluidTankState(fluid.toStack(amount), capacity);
        }
        return null;
    }

    public record FluidTankState(FluidStack fluid, int capacity) {}

    public record Unbaked() implements SpecialModelRenderer.Unbaked<FluidTankItemRenderer.FluidTankState> {

        public static final MapCodec<FluidTankItemRenderer.Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public @Nullable SpecialModelRenderer<FluidTankItemRenderer.FluidTankState> bake(BakingContext context) {
            return FluidTankItemRenderer.INSTANCE;
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<FluidTankItemRenderer.FluidTankState>> type() {
            return CODEC;
        }
    }
}

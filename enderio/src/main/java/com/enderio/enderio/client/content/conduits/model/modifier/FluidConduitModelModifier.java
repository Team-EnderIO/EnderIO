package com.enderio.enderio.client.content.conduits.model.modifier;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBaker;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FluidConduitModelModifier implements ConduitModelModifier {

    private static final Identifier FLUID_MODEL = EnderIO.id("block/extra/fluids");

    public List<BlockStateModelPart> createConnectionQuads(ModelBaker baker, ModelState modelState, Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData) {
        if (!(conduit.value() instanceof FluidConduit fluidConduit)) {
            return List.of();
        }

        if (fluidConduit.isMultiFluid() || extraWorldData == null) {
            return List.of();
        }

        var fluid = extraWorldData.getString("LockedFluid");
        if (fluid.isEmpty()) {
            return List.of();
        }

        Fluid lockedFluid = BuiltInRegistries.FLUID.getValue(Identifier.parse(fluid.get())); //TODO use value input?
        if (!lockedFluid.isSame(Fluids.EMPTY)) {
            // Get fluid model
            FluidState fluidState = lockedFluid.defaultFluidState();
            FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
            return List.of(SimpleModelWrapper.bake(new ConduitBaker(baker, new Material(fluidModel.stillMaterial().sprite().contents().name(), fluidModel.stillMaterial().forceTranslucent())), FLUID_MODEL, modelState));
        }

        return List.of();
    }

    @Override
    public List<Identifier> getModelDependencies() {
        return List.of(FLUID_MODEL);
    }
}

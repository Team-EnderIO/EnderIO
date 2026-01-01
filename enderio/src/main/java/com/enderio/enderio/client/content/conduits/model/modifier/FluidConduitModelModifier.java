package com.enderio.enderio.client.content.conduits.model.modifier;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBaker;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidConduitModelModifier implements ConduitModelModifier {

    private static final Identifier FLUID_MODEL = EnderIO.rl("block/extra/fluids");

    public List<BlockModelPart> createConnectionQuads(ModelBaker baker, ModelState modelState, Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData) {
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
            var clientExtension = IClientFluidTypeExtensions.of(lockedFluid);
            TextureAtlasSprite sprite = Minecraft.getInstance()
                .getAtlasManager()
                .getAtlasOrThrow(AtlasIds.BLOCKS)
                .getSprite(clientExtension.getStillTexture());
            return List.of(SimpleModelWrapper.bake(new ConduitBaker(baker, sprite), FLUID_MODEL, modelState));
        }

        return List.of();
    }

    @Override
    public List<Identifier> getModelDependencies() {
        return List.of(FLUID_MODEL);
    }
}

package com.enderio.conduits.client;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.ConduitBundle;
import com.enderio.conduits.common.blockentity.ConduitType;
import com.enderio.core.data.model.EIOModel;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConduitBlockModel implements IDynamicBakedModel {

    private BakedModel connector;
    private BakedModel connection;
    private BakedModel core;

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        findModels();
        ConduitBundle conduitBundle = extraData.get(ConduitBlockEntity.BUNDLE_MODEL_PROPERTY);
        if (conduitBundle != null) {
            for (IConduitType type : conduitBundle.getTypes()) {
                quads.addAll(new ConduitTextureEmissiveQuadTransformer(type, false).process(core.getQuads(state, side, rand, extraData, renderType)));
            }
            for (Direction direction : Direction.values()) {
                Direction preRotation = rotateDirection(direction, side);
                if (conduitBundle.getConnection(direction).isEnd()) {
                    quads.addAll(QuadTransformers.applying(rotateTransformation(direction)).process(connector.getQuads(state, preRotation, rand, extraData, renderType)));
                }
                for (IConduitType connectedType : conduitBundle.getConnection(direction).getConnectedTypes()) {
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(connectedType, false).andThen(QuadTransformers.applying(rotateTransformation(direction))).process(connection.getQuads(state, preRotation, rand, extraData, renderType)));
                }
            }
        }
        return quads;
    }

    public void findModels() {
        connector = Minecraft.getInstance().getModelManager().getModel(ConduitClientSetup.CONDUIT_CONNECTOR);
        connection = Minecraft.getInstance().getModelManager().getModel(ConduitClientSetup.CONDUIT_CONNECTION);
        core = Minecraft.getInstance().getModelManager().getModel(ConduitClientSetup.CONDUIT_CORE);
    }

    /**
     *
     * @param toDirection the Direction you want to transform to from the Bottom as base
     * @param toTransform the Direction to follow the same Transformation as bottom -> toDirection
     * @return the Direction toTransform was transformed to
     */
    @Nullable
    public static Direction rotateDirection(Direction toDirection, @Nullable Direction toTransform) {
        if (toTransform == null)
            return null;
        return switch (toDirection) {
            case DOWN -> toTransform;
            case UP -> toTransform.getClockWise(Direction.Axis.Z).getClockWise(Direction.Axis.Z);
            case NORTH -> toTransform.getCounterClockWise(Direction.Axis.X);
            case SOUTH -> toTransform.getClockWise(Direction.Axis.X);
            case WEST -> toTransform.getCounterClockWise(Direction.Axis.Z);
            case EAST -> toTransform.getClockWise(Direction.Axis.Z);
        };
    }

    public static Transformation rotateTransformation(Direction toDirection) {
        Quaternion quaternion = Quaternion.ONE.copy();
        switch (toDirection) {
            case UP -> quaternion.mul(Vector3f.ZP.rotationDegrees(180));
            case NORTH -> quaternion.mul(Vector3f.XP.rotationDegrees(90));
            case SOUTH -> quaternion.mul(Vector3f.XN.rotationDegrees(90));
            case WEST -> quaternion.mul(Vector3f.ZN.rotationDegrees(90));
            case EAST -> quaternion.mul(Vector3f.ZP.rotationDegrees(90));
        }
        Transformation transformation = new Transformation(null, quaternion, null, null);
        return transformation.applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return EIOModel.getMissingTexture();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    private static boolean isMissingModel(BakedModel model) {
        return model == Minecraft.getInstance().getModelManager().getMissingModel();
    }
}

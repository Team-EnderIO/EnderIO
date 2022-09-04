package com.enderio.conduits.client.model;

import com.enderio.api.conduit.IConduitType;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.conduits.common.Area;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.ConduitBundle;
import com.enderio.conduits.common.blockentity.ConduitConnection;
import com.enderio.conduits.common.blockentity.OffsetHelper;
import com.enderio.conduits.common.blockentity.connection.DynamicConnectionState;
import com.enderio.conduits.common.blockentity.connection.IConnectionState;
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
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.enderio.conduits.client.ConduitClientSetup.*;

public class ConduitBlockModel implements IDynamicBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();
        ConduitBundle conduitBundle = extraData.get(ConduitBlockEntity.BUNDLE_MODEL_PROPERTY);
        if (conduitBundle != null) {
            Direction.Axis axis = OffsetHelper.findMainAxis(conduitBundle);
            Map<IConduitType, List<Vec3i>> offsets = new HashMap<>();
            for (Direction direction : Direction.values()) {
                Direction preRotation = rotateDirection(direction, side);
                ConduitConnection connection = conduitBundle.getConnection(direction);
                IQuadTransformer rotation = QuadTransformers.applying(rotateTransformation(direction));
                if (connection.isEnd()) {
                    quads.addAll(rotation.process(modelOf(CONDUIT_CONNECTOR).getQuads(state, preRotation, rand, extraData, renderType)));
                }
                var connectedTypes = connection.getConnectedTypes(conduitBundle);
                for (int i = 0; i < connectedTypes.size(); i++) {
                    IConduitType type = connectedTypes.get(i);
                    Vec3i offset = OffsetHelper.translationFor(direction.getAxis(), OffsetHelper.offsetConduit(i, connectedTypes.size()));
                    offsets.computeIfAbsent(type, ignored -> new ArrayList<>()).add(offset);
                    IQuadTransformer rotationTranslation = rotation.andThen(QuadTransformers.applying(translateTransformation(offset)));
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(type, false).andThen(rotationTranslation)
                        .process(modelOf(CONDUIT_CONNECTION).getQuads(state, preRotation, rand, extraData, renderType)));
                    if (connection.isEnd()) {
                        quads.addAll(rotationTranslation.process(modelOf(CONDUIT_CONNECTION_BOX).getQuads(state, preRotation, rand, extraData, renderType)));

                        IConnectionState connectionState = connection.getConnectionState(i);
                        if (connectionState instanceof DynamicConnectionState dyn) {
                            IQuadTransformer color = rotationTranslation.andThen(new ColorQuadTransformer(dyn.insert(), dyn.extract()));
                            BakedModel model = null;
                            if (dyn.isExtract() && dyn.isInsert()) {
                                model = modelOf(CONDUIT_IO_IN_OUT);
                            } else if (dyn.isInsert()) {
                                model = modelOf(CONDUIT_IO_IN);
                            } else if (dyn.isExtract()) {
                                model = modelOf(CONDUIT_IO_OUT);
                            }
                            if (model != null)
                                quads.addAll(color.process(model.getQuads(state, preRotation, rand, extraData, renderType)));
                            if (dyn.control() == RedstoneControl.ACTIVE_WITH_SIGNAL
                                || dyn.control() == RedstoneControl.ACTIVE_WITHOUT_SIGNAL) {
                                quads.addAll(rotationTranslation.andThen(new ColorQuadTransformer(null, dyn.redstoneChannel()))
                                    .process(modelOf(CONDUIT_IO_REDSTONE).getQuads(state, preRotation, rand, extraData, renderType)));
                            }
                        }
                    }
                }
            }

            var allTypes = conduitBundle.getTypes();
            @Nullable
            Area box = null;
            Map<IConduitType, Integer> notRendered = new HashMap<>();
            List<IConduitType> rendered = new ArrayList<>();
            for (int i = 0; i < allTypes.size(); i++) {
                var type = allTypes.get(i);
                @Nullable
                List<Vec3i> offsetsForType = offsets.get(type);
                if (offsetsForType != null) {
                    //all are pointing to the same xyz reference meaning that we can draw the core
                    if (offsetsForType.stream().distinct().count() == 1) {
                        rendered.add(type);
                    } else {
                        if (box == null) {
                            box = new Area(offsetsForType.toArray(new Vec3i[0]));
                        } else {
                            offsetsForType.forEach(box::makeContain);
                        }
                    }
                } else {
                    notRendered.put(type, i);
                }
            }

            Set<Vec3i> duplicateFinder = new HashSet<>();
            //rendered have only one distinct pos, so I can safely assume get(0) is valid
            List<Vec3i> duplicatePositions = rendered.stream().map(offsets::get).map(l -> l.get(0)).filter(n -> !duplicateFinder.add(n)).toList();
            for (Vec3i duplicatePosition : duplicatePositions) {
                if (box == null) {
                    box = new Area(duplicatePosition);
                } else {
                    box.makeContain(duplicatePosition);
                }
            }
            for (IConduitType toRender : rendered) {
                List<Vec3i> offsetsForType = offsets.get(toRender);
                if (box == null || !box.contains(offsetsForType.get(0)))
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(toRender, false)
                        .andThen(QuadTransformers.applying(translateTransformation(offsetsForType.get(0))))
                        .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
            }

            if (box != null) {
                for (Map.Entry<IConduitType, Integer> notRenderedEntry : notRendered.entrySet()) {
                    Vec3i offset = OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size()));
                    if (!box.contains(offset))
                        quads.addAll(new ConduitTextureEmissiveQuadTransformer(notRenderedEntry.getKey(), false)
                            .andThen(QuadTransformers.applying(translateTransformation(offset)))
                            .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
                }

                quads.addAll(new BoxTextureQuadTransformer(box.size()).andThen(QuadTransformers.applying(translateTransformation(box.getMin())))
                    .process(modelOf(BOX).getQuads(state, side, rand, extraData, renderType)));
            } else {
                for (Map.Entry<IConduitType, Integer> notRenderedEntry : notRendered.entrySet()) {
                    quads.addAll(new ConduitTextureEmissiveQuadTransformer(notRenderedEntry.getKey(), false)
                        .andThen(QuadTransformers.applying(translateTransformation(OffsetHelper.translationFor(axis, OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size())))))
                        .process(modelOf(CONDUIT_CORE).getQuads(state, side, rand, extraData, renderType)));
                }
            }
        }
        return quads;
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

    private static Transformation translateTransformation(Vec3i offset) {
        return new Transformation(scale(offset, 3/16f), null, null, null);
    }

    private static Vector3f scale(Vec3i vector, float scaler) {
        return new Vector3f(vector.getX()*scaler, vector.getY()*scaler, vector.getZ()*scaler);
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

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }

    private static boolean isMissingModel(BakedModel model) {
        return model == Minecraft.getInstance().getModelManager().getMissingModel();
    }
}

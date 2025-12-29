package com.enderio.enderio.client.content.conduits.model.bundle.port;

import com.enderio.core.common.util.Area;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.client.content.conduits.model.ConduitAdditionalModels;
import com.enderio.enderio.client.content.conduits.model.bundle.ConduitBundleRenderState;
import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.content.conduits.OffsetHelper;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConduitBlockStateModel implements DynamicBlockStateModel {

    private final ModelBaker baker;

    public ConduitBlockStateModel(ModelBaker baker) {
        this.baker = baker;
    }

    @Override
    public TextureAtlasSprite particleIcon() {
        return null;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockModelPart> parts) {
        ConduitBundleRenderState bundleState = level.getModelData(pos).get(ConduitBundleRenderState.PROPERTY);

        if (bundleState != null) {
            // If the facade should hide the conduits, escape early.
            if (ClientFacadeVisibility.areFacadesVisible() && bundleState.hasFacade() && bundleState.doesFacadeHideConduits()) {
                return;
            }

            Direction.Axis axis = bundleState.mainAxis();
            Map<Holder<Conduit<?, ?>>, List<Vec3i>> offsets = new HashMap<>();

            for (Direction direction : Direction.values()) {
                boolean isEnd = bundleState.isConnectionEndpoint(direction);
                ModelState rotation = new ModelState() {
                    @Override
                    public Transformation transformation() {
                        return rotateTransformation(direction);
                    }
                };

                if (isEnd) {
                    parts.add(SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.CONDUIT_CONNECTOR, rotation));
                }

                var connectedTypes = bundleState.getConnectedConduits(direction);
                for (int i = 0; i < connectedTypes.size(); i++) {
                    Holder<Conduit<?, ?>> conduit = connectedTypes.get(i);
                    CompoundTag extraWorldData = bundleState.getExtraWorldData(conduit);

                    Vec3i offset = OffsetHelper.translationFor(direction.getAxis(),
                        OffsetHelper.offsetConduit(i, connectedTypes.size()));
                    offsets.computeIfAbsent(conduit, ignored -> new ArrayList<>()).add(offset);

                    ModelState rotationTranslation = new ModelState() {
                        @Override
                        public Transformation transformation() {
                            return rotateTransformation(direction).compose(translateTransformation(offset));
                        }
                    };

                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, sprite(bundleState.getTexture(conduit))),
                        ConduitAdditionalModels.CONDUIT_CONNECTION, rotationTranslation)); //TODO emission?

//                    ConduitModelModifier conduitModelModifier = ConduitModelModifiers  //TODO for fluids
//                        .getModifier(conduit.value().type());
//                    if (conduitModelModifier != null) {
//                        quads.addAll(rotationTranslation.process(conduitModelModifier.createConnectionQuads(conduit,
//                            extraWorldData, side, direction, rand, renderType)));
//                    }

                    if (isEnd) {
                        parts.add(SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.CONDUIT_CONNECTION_BOX, rotationTranslation));

                        var connectionState = bundleState.getConnectionState(direction, conduit);
                        if (connectionState != null) {
                            ModelState color = rotationTranslation;
//                            IQuadTransformer color = rotationTranslation.andThen(new ColorQuadTransformer( //TODO fix color
//                                connectionState.inputChannel(), connectionState.outputChannel()));

                            Identifier model = null;
                            if (connectionState.canInput() && connectionState.canOutput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_IN_OUT;
                            } else if (connectionState.canInput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_IN;
                            } else if (connectionState.canOutput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_OUT;
                            }

                            if (model != null) {
                                parts.add(SimpleModelWrapper.bake(this.baker, model, color));
                            }

                            // TODO: Need support for dual-color redstone control.
                            if (connectionState.isRedstoneSensitive()) {
                                    //.andThen(new ColorQuadTransformer(null, connectionState.redstoneChannel())) //TODO redstone color
                                parts.add(SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.CONDUIT_IO_REDSTONE, rotationTranslation));

                                // TODO: Use this to render two redstone signal colours?
                                //                                // Shrink the size
                                //                                var scale = new Vector3f(1, 0.5f, 1);
                                //
                                //                                // move into position
                                //                                var transformation = new Transformation(new Vector3f(0, 4 / 32f, 0), null, scale, null);
                                //                                var transformation1 = new Transformation(new Vector3f(0, 5 / 32f, 0), null, scale, null);
                                //
                                //                                quads.addAll(QuadTransformers.applying(transformation)
                                //                                    .andThen(rotationTranslation)
                                //                                    .andThen(new ColorQuadTransformer(null, connectionState.extractRedstoneChannel()))
                                //                                    .process(modelOf(CONDUIT_IO_REDSTONE).getQuads(state, preRotation, rand,
                                //                                        extraData, renderType)));
                                //
                                //                                quads.addAll(QuadTransformers.applying(transformation1)
                                //                                        .andThen(rotationTranslation)
                                ////                                        .andThen(QuadTransformers.applying(translateTransformation(normal.mul(-1 / 16f))))
                                //                                        .andThen(new ColorQuadTransformer(null, DyeColor.GREEN))
                                //                                        .process(modelOf(CONDUIT_IO_REDSTONE).getQuads(state, preRotation, rand,
                                //                                                extraData, renderType)));
                            }
                        }
                    }
                }
            }

            var allTypes = bundleState.conduits();
            @Nullable Area box = null;
            Map<Holder<Conduit<?, ?>>, Integer> notRendered = new HashMap<>();
            List<Holder<Conduit<?, ?>>> rendered = new ArrayList<>();
            for (int i = 0; i < allTypes.size(); i++) {
                var type = allTypes.get(i);
                @Nullable
                List<Vec3i> offsetsForType = offsets.get(type);
                if (offsetsForType != null) {
                    // all are pointing to the same xyz reference meaning that we can draw the core
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
            // rendered have only one distinct pos, so I can safely assume get(0) is valid
            List<Vec3i> duplicatePositions = rendered.stream()
                .map(offsets::get)
                .map(List::getFirst)
                .filter(n -> !duplicateFinder.add(n))
                .toList();
            for (Vec3i duplicatePosition : duplicatePositions) {
                if (box == null) {
                    box = new Area(duplicatePosition);
                } else {
                    box.makeContain(duplicatePosition);
                }
            }
            for (Holder<Conduit<?, ?>> toRender : rendered) {
                List<Vec3i> offsetsForType = offsets.get(toRender);
                if (box == null || !box.contains(offsetsForType.getFirst())) {
                    ModelState offsetType = new ModelState() {
                        @Override
                        public Transformation transformation() {
                            return translateTransformation(offsetsForType.getFirst());
                        }
                    };
                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, sprite(bundleState.getTexture(toRender))),
                        ConduitAdditionalModels.CONDUIT_CORE, offsetType)); //TODO emissive?
                }
            }

            if (box != null) {
                for (Map.Entry<Holder<Conduit<?, ?>>, Integer> notRenderedEntry : notRendered.entrySet()) {
                    Vec3i offset = OffsetHelper.translationFor(axis,
                        OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size()));
                    if (!box.contains(offset)) {
                        ModelState translate = new ModelState() {
                            @Override
                            public Transformation transformation() {
                                return translateTransformation(offset);
                            }
                        };
                        parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, sprite(bundleState.getTexture(notRenderedEntry.getKey()))),
                            ConduitAdditionalModels.CONDUIT_CORE, translate)); //TODO emissive?
                    }
                }
                Vec3i min = box.getMin();
                ModelState boxTranslate = new ModelState() {
                    @Override
                    public Transformation transformation() {
                        return translateTransformation(min);
                    }
                };

                final var model = SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.BOX, boxTranslate);
                var size = box.size();

                //TODO improve
                parts.add(new BlockModelPart() {
                    @Override
                    public List<BakedQuad> getQuads(@Nullable Direction direction) {
                        return new BoxTextureQuadTransformer(size).process(model.getQuads(direction));
                    }

                    @Override
                    public boolean useAmbientOcclusion() {
                        return model.useAmbientOcclusion();
                    }

                    @Override
                    public TextureAtlasSprite particleIcon() {
                        return model.particleIcon();
                    }
                });


            } else {
                for (Map.Entry<Holder<Conduit<?, ?>>, Integer> notRenderedEntry : notRendered.entrySet()) {
                    ModelState translate = new ModelState() {
                        @Override
                        public Transformation transformation() {
                            return translateTransformation(OffsetHelper.translationFor(axis,
                                OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size())));
                        }
                    };

                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, sprite(bundleState.getTexture(notRenderedEntry.getKey()))),
                        ConduitAdditionalModels.CONDUIT_CORE, translate));

                }
            }
        }
    }

    public static Transformation rotateTransformation(Direction toDirection) {
        Quaternionf quaternion = new Quaternionf();
        switch (toDirection) {
        case UP -> quaternion.mul(Axis.ZP.rotationDegrees(180));
        case NORTH -> quaternion.mul(Axis.XP.rotationDegrees(90));
        case SOUTH -> quaternion.mul(Axis.XN.rotationDegrees(90));
        case WEST -> quaternion.mul(Axis.ZN.rotationDegrees(90));
        case EAST -> quaternion.mul(Axis.ZP.rotationDegrees(90));
        default -> {
        }
        }
        Transformation transformation = new Transformation(null, quaternion, null, null);
        return transformation.applyOrigin(new Vector3f(.5f, .5f, .5f));
    }

    private static Transformation translateTransformation(Vec3i offset) {
        return new Transformation(scale(offset, 3 / 16f), null, null, null);
    }

    private static Vector3f scale(Vec3i vector, float scaler) {
        return new Vector3f(vector.getX() * scaler, vector.getY() * scaler, vector.getZ() * scaler);
    }

    private static TextureAtlasSprite sprite(Identifier location) {
        return Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(location);
    }

    public record Unbaked() implements CustomUnbakedBlockStateModel {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return null;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new ConduitBlockStateModel(baker);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CONNECTION);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_FACADE_OVERLAY);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CONNECTOR);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CORE);
            resolver.markDependency(ConduitAdditionalModels.BOX);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_IN);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_IN_OUT);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_OUT);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_REDSTONE);
        }
    }
}

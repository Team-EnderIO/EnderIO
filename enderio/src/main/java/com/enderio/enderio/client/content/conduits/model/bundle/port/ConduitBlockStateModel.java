package com.enderio.enderio.client.content.conduits.model.bundle.port;

import com.enderio.core.common.util.Area;
import com.enderio.core.data.model.ModelHelper;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.enderio.client.content.conduits.model.ConduitAdditionalModels;
import com.enderio.enderio.client.content.conduits.model.bundle.ConduitBundleRenderState;
import com.enderio.enderio.client.content.conduits.model.facades.ClientFacadeVisibility;
import com.enderio.enderio.client.content.conduits.model.modifier.ConduitModelModifiers;
import com.enderio.enderio.content.conduits.OffsetHelper;
import com.mojang.math.OctahedralGroup;
import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.AtlasIds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

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
    public Material.Baked particleMaterial() {
        return new Material.Baked(ModelHelper.getMissingTexture(), false);
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
         //This is only used for facades.
        ConduitBundleRenderState bundleState = level.getModelData(pos).get(ConduitBundleRenderState.PROPERTY);

        if (bundleState == null) {
            return new Material.Baked(ModelHelper.getMissingTexture(), false);
        }

        if (bundleState.hasFacade() && ClientFacadeVisibility.areFacadesVisible()) {
            var model = Minecraft.getInstance()
                .getModelManager()
                .getBlockModelSet()
                .get(bundleState.facade());

            if (model instanceof BlockStateModel blockStateModel) {
                return blockStateModel.particleMaterial(level, pos, state);
            }
        }

        // Shouldn't be called anymore, but sensible fallback to have:
        if (bundleState.conduits().isEmpty()) {
            return new Material.Baked(ModelHelper.getMissingTexture(), false);
        }

        return new Material.Baked(Minecraft.getInstance()
            .getAtlasManager()
            .getAtlasOrThrow(AtlasIds.BLOCKS)
            .getSprite(bundleState.getTexture(bundleState.conduits().getFirst())), false);
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        // TODO: 26.1 can we refine this? for now we'll just return all flags lol
        return 0xFFFFFFFF;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
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
                ModelState rotation = rotate(direction);

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
                            var mat = new Matrix4f();
                            translateTransformation(offset).getMatrix().mul(rotate(direction).transformation().getMatrix(), mat);
                            return new Transformation(mat);
                        }
                    };

                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, new Material(bundleState.getTexture(conduit))),
                        ConduitAdditionalModels.CONDUIT_CONNECTION, rotationTranslation)); //TODO emission?

                    ConduitModelModifier conduitModelModifier = ConduitModelModifiers
                        .getModifier(conduit.value().type());
                    if (conduitModelModifier != null) {
                        parts.addAll(conduitModelModifier.createConnectionQuads(baker, rotationTranslation, conduit, extraWorldData));
                    }

                    if (isEnd) {
                        parts.add(SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.CONDUIT_CONNECTION_BOX, rotationTranslation));

                        var connectionState = bundleState.getConnectionState(direction, conduit);
                        if (connectionState != null) {
                            Identifier model = null;
                            if (connectionState.canInput() && connectionState.canOutput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_IN_OUT;
                            } else if (connectionState.canInput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_IN;
                            } else if (connectionState.canOutput()) {
                                model = ConduitAdditionalModels.CONDUIT_IO_OUT;
                            }

                            if (model != null) {
                                final var io = SimpleModelWrapper.bake(this.baker, model, rotationTranslation);
                                parts.add(new BlockStateModelPart() {
                                    @Override
                                    public List<BakedQuad> getQuads(@Nullable Direction direction) {
                                        return withColor(io.getQuads(direction), connectionState.inputChannel(), connectionState.outputChannel());
                                    }

                                    @Override
                                    public boolean useAmbientOcclusion() {
                                        return io.useAmbientOcclusion();
                                    }

                                    @Override
                                    public Material.Baked particleMaterial() {
                                        return io.particleMaterial();
                                    }

                                    @Override
                                    public @BakedQuad.MaterialFlags int materialFlags() {
                                        return io.materialFlags();
                                    }
                                });
                            }

                            // TODO: Need support for dual-color redstone control.
                            if (connectionState.isRedstoneSensitive()) {
                                final BlockStateModelPart redstone = SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.CONDUIT_IO_REDSTONE, rotationTranslation);
                                parts.add(new BlockStateModelPart() {

                                    @Override
                                    public List<BakedQuad> getQuads(@Nullable Direction direction) {
                                        return withColor(redstone.getQuads(direction), null, connectionState.redstoneChannel());
                                    }

                                    @Override
                                    public boolean useAmbientOcclusion() {
                                        return redstone.useAmbientOcclusion();
                                    }

                                    @Override
                                    public Material.Baked particleMaterial() {
                                        return redstone.particleMaterial();
                                    }

                                    @Override
                                    public @BakedQuad.MaterialFlags int materialFlags() {
                                        return redstone.materialFlags();
                                    }
                                });

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
                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, new Material(bundleState.getTexture(toRender))),
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
                        parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, new Material(bundleState.getTexture(notRenderedEntry.getKey()))),
                            ConduitAdditionalModels.CONDUIT_CORE, translate)); //TODO emissive?
                    }
                }
                Vec3i min = box.getMin();
                var size = box.size();
                ModelState boxTranslate = new ModelState() {
                    @Override
                    public Transformation transformation() {
                        var scaling = new Transformation(null, null, new Vector3f(size.getX(), size.getY(), size.getZ()), null)
                            .applyOrigin(new Vector3f(-0.5f)).getMatrix();
                        var center = new Transformation(new Vector3f(6.5f / 16, 6.5f /16, 6.5f /16), null, null, null).getMatrix();
                        var translate = translateTransformation(min).getMatrix();
                        return new Transformation(translate.mul(center.mul(scaling, new Matrix4f()), new Matrix4f()));
                    }
                };

                final var model = SimpleModelWrapper.bake(this.baker, ConduitAdditionalModels.BOX, boxTranslate);
                parts.add(model);


            } else {
                for (Map.Entry<Holder<Conduit<?, ?>>, Integer> notRenderedEntry : notRendered.entrySet()) {
                    ModelState translate = new ModelState() {
                        @Override
                        public Transformation transformation() {
                            return translateTransformation(OffsetHelper.translationFor(axis,
                                OffsetHelper.offsetConduit(notRenderedEntry.getValue(), allTypes.size())));
                        }
                    };

                    parts.add(SimpleModelWrapper.bake(new ConduitBaker(this.baker, new Material(bundleState.getTexture(notRenderedEntry.getKey()))),
                        ConduitAdditionalModels.CONDUIT_CORE, translate));

                }
            }
        }
    }

    public List<BakedQuad> withColor(List<BakedQuad> quads, @Nullable DyeColor insert, @Nullable DyeColor extract) {
        List<BakedQuad> newQuads = new ArrayList<>(quads);
        for (BakedQuad quad : quads) {
            if (!quad.materialInfo().isTinted()) {
                newQuads.add(quad);
            } else if (insert != null && quad.materialInfo().tintIndex() == 1) {
                var mutableQuad = new MutableQuad();
                mutableQuad.setFrom(quad);
                mutableQuad.setColor(insert.getTextureDiffuseColor());
                newQuads.add(mutableQuad.toBakedQuad());
            } else if (extract != null && quad.materialInfo().tintIndex() == 0) {
                var mutableQuad = new MutableQuad();
                mutableQuad.setFrom(quad);
                mutableQuad.setColor(extract.getTextureDiffuseColor());
                newQuads.add(mutableQuad.toBakedQuad());
            } else {
                newQuads.add(quad);
            }
        }
        return newQuads;
    }

    public static ModelState rotate(Direction toDirection) {
        return switch (toDirection) {
            case UP -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Z_180);
            case NORTH -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_X_270);
            case SOUTH -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_X_90);
            case WEST -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Z_90);
            case EAST -> BlockModelRotation.get(OctahedralGroup.BLOCK_ROT_Z_270);
            default -> BlockModelRotation.get(OctahedralGroup.IDENTITY);
        };
    }

    private static Transformation translateTransformation(Vec3i offset) {
        return new Transformation(scale(offset, 3 / 16f), null, null, null);
    }

    private static Vector3f scale(Vec3i vector, float scaler) {
        return new Vector3f(vector.getX() * scaler, vector.getY() * scaler, vector.getZ() * scaler);
    }

    public record Unbaked() implements CustomUnbakedBlockStateModel {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return CODEC;
        }

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new ConduitBlockStateModel(baker);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CONNECTION);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CONNECTOR);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CORE);
            resolver.markDependency(ConduitAdditionalModels.BOX);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_IN);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_IN_OUT);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_OUT);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_IO_REDSTONE);
            resolver.markDependency(ConduitAdditionalModels.CONDUIT_CONNECTION_BOX);
            for (var id : ConduitModelModifiers.getAllModelDependencies()) {
                resolver.markDependency(id);
            }
        }
    }
}

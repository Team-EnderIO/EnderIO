package com.enderio.enderio.client.content.misc_blocks;

import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlock;
import com.enderio.enderio.foundation.block.entity.EnderSkullBlockEntity;
import com.enderio.enderio.init.EIOBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import static net.minecraft.client.renderer.blockentity.SkullBlockRenderer.TRANSFORMATIONS;

public class EnderSkullRenderer implements BlockEntityRenderer<EnderSkullBlockEntity, EnderSkullRenderer.EnderSkullBlockRenderState> {

    public static final RenderType RENDERTYPE = RenderTypes.entityCutoutZOffset(Identifier.withDefaultNamespace("textures/entity/enderman/enderman.png"));
    public static final ModelLayerLocation ENDER_SKULL = new ModelLayerLocation(Identifier.withDefaultNamespace("enderman_head"), "main");

    private final EnderSkullModel skullmodelbase;

    public EnderSkullRenderer(BlockEntityRendererProvider.Context context) {
        skullmodelbase = new EnderSkullModel(context.entityModelSet().bakeLayer(ENDER_SKULL));
    }

    @Override
    public EnderSkullBlockRenderState createRenderState() {
        return new EnderSkullBlockRenderState();
    }

    @Override
    public void extractRenderState(EnderSkullBlockEntity blockEntity, EnderSkullBlockRenderState renderState, float partialTick, Vec3 cameraPosition,
        ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        renderState.animationProgress = blockEntity.getAnimation(partialTick);
        BlockState blockState = blockEntity.getBlockState();
        if (blockState.getBlock() instanceof WallSkullBlock) {
            Direction facing = blockState.getValue(WallSkullBlock.FACING);
            renderState.transformation = TRANSFORMATIONS.wallTransformation(facing);
        } else {
            renderState.transformation = TRANSFORMATIONS.freeTransformations(blockState.getValue(SkullBlock.ROTATION));
        }

        float f = blockEntity.getAnimation(partialTick);
        BlockState blockstate = blockEntity.getBlockState();
        LocalPlayer player = Minecraft.getInstance().player;
        Vec3 position = player.position();
        HitResult hitResult = player.pick(10D, 0.0f, false); //I would rather not do this every tick, but I don't see how.
        skullmodelbase.active = false;

        if (hitResult instanceof BlockHitResult blockHitResult && player.level().getBlockEntity(blockHitResult.getBlockPos()) == blockEntity) {
            blockEntity.setAnimation(30.0f);
            f = 30.0f;
        }

        if (f > 0) {
            skullmodelbase.active = true;
            float rotationDegrees = (float) (
                Mth.atan2(position.z - blockEntity.getBlockPos().getZ() - 0.5D, position.x - blockEntity.getBlockPos().getX() - 0.5D) * 180.0f / Math.PI + 90);
            int rotation = RotationSegment.convertToSegment(rotationDegrees);

            if (blockstate.is(EIOBlocks.ENDERMAN_HEAD.get())) {
                player.level().setBlock(blockEntity.getBlockPos(), blockstate.setValue(SkullBlock.ROTATION, rotation), 3);
            }
            //TODO fix smooth rendering? It may be more fun to have it be rigid?
            //renderState.transformation = renderState.transformation.compose(new Transformation(new Matrix4f().identity().rotateY((float) Math.toRadians(rotationDegrees))));
        }
        renderState.skullType = EnderSkullBlock.EIOSkulls.ENDERMAN;
        renderState.renderType = RENDERTYPE;
    }

    @Override
    public void submit(EnderSkullBlockRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        Transformation compose = renderState.transformation;

        if (renderState.animationProgress > 0) {
            float rotationDegrees = (float) (Minecraft.getInstance().player.getRandom().nextGaussian() * 2);
            compose = compose.compose(
                new Transformation(new Matrix4f().identity().rotateY((float) Math.toRadians(rotationDegrees))));
        }

        poseStack.mulPose(compose);
        SkullBlockRenderer.submitSkull(renderState.animationProgress, poseStack, nodeCollector, renderState.lightCoords, skullmodelbase, renderState.renderType, 0, renderState.breakProgress);
        poseStack.popPose();
    }

    public static class EnderSkullBlockRenderState extends BlockEntityRenderState {
        public float animationProgress;
        public Transformation transformation = Transformation.IDENTITY;
        public SkullBlock.Type skullType = SkullBlock.Types.ZOMBIE;
        public RenderType renderType;
    }

    public static class EnderSkullModel extends SkullModelBase {
        private final ModelPart head;
        private final ModelPart hat;
        private boolean active = false;

        public EnderSkullModel(ModelPart root) {
            super(root);
            head = root.getChild("head");
            hat = root.getChild("hat");
        }

        public static MeshDefinition createHeadModel() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();
            partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(-0.5F)), PartPose.ZERO);
            partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
            return meshdefinition;
        }

        public static LayerDefinition createMobHeadLayer() {
            MeshDefinition meshdefinition = createHeadModel();
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void setupAnim(State renderState) {
            this.head.yRot = renderState.yRot * (float) (Math.PI / 180.0);
            this.head.xRot = renderState.xRot * (float) (Math.PI / 180.0);
            this.hat.yRot = head.yRot;
            this.hat.xRot = head.xRot;
            this.head.y = 0;
            if (active) {
                this.head.y =- 5f;
            }
        }
    }
}

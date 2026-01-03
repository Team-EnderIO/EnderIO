package com.enderio.enderio.content.glass;

import com.enderio.enderio.config.base.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FusedQuartzBlock extends TransparentBlock {
    // TODO: Connected textures
    private final GlassIdentifier glassIdentifier;

    public FusedQuartzBlock(Properties props, GlassIdentifier glassIdentifier, @Nullable DyeColor color) {
        super(props);
        this.glassIdentifier = glassIdentifier;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return glassIdentifier.lighting() == GlassLighting.EMITTING ? 15 : 0;
    }

    @Override
    protected int getLightBlock(BlockState state) {
        // TODO: 1.21.4: Is there a constant for light level so this isn't a magic number?
        return glassIdentifier.lighting() == GlassLighting.BLOCKING ? 15 : 0;
    }

    @Override
    public float getExplosionResistance() {
        return glassIdentifier.explosionResistance() ? BaseConfig.COMMON.BLOCKS.EXPLOSION_RESISTANCE.get().floatValue() : super.getExplosionResistance();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
            if (glassIdentifier.collisionPredicate().canPass(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    public GlassIdentifier glassIdentifier() {
        return glassIdentifier;
    }

    public GlassLighting getGlassLighting() {
        return glassIdentifier.lighting();
    }

    public GlassCollisionPredicate getCollisionPredicate() {
        return glassIdentifier.collisionPredicate();
    }
}

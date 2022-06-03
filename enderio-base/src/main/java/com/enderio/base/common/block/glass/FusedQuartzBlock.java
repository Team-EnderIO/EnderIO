package com.enderio.base.common.block.glass;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.config.base.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FusedQuartzBlock extends AbstractGlassBlock {
    // TODO: Connected textures

    private final GlassCollisionPredicate collisionPredicate;
    private final GlassLighting glassLighting;
    private final boolean explosionResistant;

    public FusedQuartzBlock(Properties pProps, GlassIdentifier glassIdentifier) {
        super(pProps);
        this.collisionPredicate = glassIdentifier.collisionPredicate();
        this.glassLighting = glassIdentifier.lighting();
        this.explosionResistant = glassIdentifier.explosion_resistance();
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable BlockGetter pLevel, @Nonnull List<Component> pTooltip, @Nonnull TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        if (explosionResistant)
            pTooltip.add(EIOLang.BLOCK_BLAST_RESISTANT);
        if (glassLighting == GlassLighting.EMITTING)
            pTooltip.add(EIOLang.FUSED_QUARTZ_EMITS_LIGHT);
        if (glassLighting == GlassLighting.BLOCKING)
            pTooltip.add(EIOLang.FUSED_QUARTZ_BLOCKS_LIGHT);
        collisionPredicate.getDescription().ifPresent(pTooltip::add);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return glassLighting == GlassLighting.EMITTING ? 15 : 0;
    }

    @Override
    public int getLightBlock(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return glassLighting == GlassLighting.BLOCKING ? pLevel.getMaxLightLevel() : 0;
    }

    @Override
    public float getExplosionResistance() {
        return explosionResistant ? BaseConfig.COMMON.BLOCKS.EXPLOSION_RESISTANCE.get() : super.getExplosionResistance();
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityCollisionContext) {
            if (collisionPredicate.canPass(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    public GlassLighting getGlassLighting() {
        return glassLighting;
    }

    public GlassCollisionPredicate getCollisionPredicate() {
        return collisionPredicate;
    }
}

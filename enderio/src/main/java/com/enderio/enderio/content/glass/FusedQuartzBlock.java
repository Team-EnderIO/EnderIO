package com.enderio.enderio.content.glass;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FusedQuartzBlock extends TransparentBlock {
    // TODO: Connected textures

    private final GlassCollisionPredicate collisionPredicate;
    private final GlassLighting glassLighting;
    private final boolean explosionResistant;
    private final String descriptionId;

    public FusedQuartzBlock(Properties pProps, GlassIdentifier glassIdentifier, @Nullable DyeColor color) {
        super(pProps);
        this.collisionPredicate = glassIdentifier.collisionPredicate();
        this.glassLighting = glassIdentifier.lighting();
        this.explosionResistant = glassIdentifier.explosion_resistance();

        String baseName = explosionResistant ? "fused_quartz" : "clear_glass";
        String lightingName = glassLighting != GlassLighting.NONE ? "_" + glassLighting.shortName() : "";
        String colorName = color != null ? "_" + color.getName() : "";
        descriptionId = "block.enderio." + baseName + lightingName + colorName;
    }

    @Override
    public String getDescriptionId() {
        return descriptionId;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pContext, pTooltip, pFlag);

        if (explosionResistant) {
            pTooltip.add(EIOCommonLang.BLOCK_BLAST_RESISTANT);
        }

        if (glassLighting == GlassLighting.EMITTING) {
            pTooltip.add(GlassLang.EMITS_LIGHT);
        }

        if (glassLighting == GlassLighting.BLOCKING) {
            pTooltip.add(GlassLang.BLOCKS_LIGHT);
        }

        Component collisionTooltip = collisionPredicate.getComponent();
        if (collisionTooltip != null) {
            pTooltip.add(collisionTooltip);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return glassLighting == GlassLighting.EMITTING ? 15 : 0;
    }

    @Override
    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return glassLighting == GlassLighting.BLOCKING ? pLevel.getMaxLightLevel() : 0;
    }

    @Override
    public float getExplosionResistance() {
        return explosionResistant ? BaseConfig.COMMON.BLOCKS.EXPLOSION_RESISTANCE.get().floatValue() : super.getExplosionResistance();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
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

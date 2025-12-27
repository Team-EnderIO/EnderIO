package com.enderio.enderio.content.glass;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
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
    private final GlassIdentifier glassIdentifier;

    public FusedQuartzBlock(Properties pProps, GlassIdentifier glassIdentifier, @Nullable DyeColor color) {
        super(pProps.overrideDescription(getDescriptionId(glassIdentifier, color)));
        this.glassIdentifier = glassIdentifier;
    }

    private static String getDescriptionId(GlassIdentifier glassIdentifier, @Nullable DyeColor color) {
        String baseName = glassIdentifier.explosionResistance() ? "fused_quartz" : "clear_glass";
        String lightingName = glassIdentifier.lighting() != GlassLighting.NONE ? "_" + glassIdentifier.lighting().shortName() : "";
        String colorName = color != null ? "_" + color.getName() : "";
        return "block.enderio." + baseName + lightingName + colorName;
    }

    // TODO: 1.21.8: what replaced appendHoverText?
//    @Override
//    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltip, TooltipFlag pFlag) {
//        super.appendHoverText(pStack, pContext, pTooltip, pFlag);
//
//        if (glassIdentifier.explosionResistance()) {
//            pTooltip.add(EIOCommonLang.BLOCK_BLAST_RESISTANT);
//        }
//
//        if (glassIdentifier.lighting() == GlassLighting.EMITTING) {
//            pTooltip.add(GlassLang.EMITS_LIGHT);
//        }
//
//        if (glassIdentifier.lighting() == GlassLighting.BLOCKING) {
//            pTooltip.add(GlassLang.BLOCKS_LIGHT);
//        }
//
//        Component collisionTooltip = glassIdentifier.collisionPredicate().getComponent();
//        if (collisionTooltip != null) {
//            pTooltip.add(collisionTooltip);
//        }
//    }

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

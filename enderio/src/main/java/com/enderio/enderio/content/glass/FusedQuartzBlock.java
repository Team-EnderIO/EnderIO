package com.enderio.enderio.content.glass;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FusedQuartzBlock extends AbstractGlassBlock {
    // TODO: Connected textures
    private final GlassIdentifier glassIdentifier;
    private final String descriptionId;

    public FusedQuartzBlock(Properties props, GlassIdentifier glassIdentifier, @Nullable DyeColor color) {
        super(props);
        this.glassIdentifier = glassIdentifier;

        String baseName = glassIdentifier.explosionResistance() ? "fused_quartz" : "clear_glass";
        String lightingName = glassIdentifier.lighting() != GlassLighting.NONE ? "_" + glassIdentifier.lighting().shortName() : "";
        String colorName = color != null ? "_" + color.getName() : "";
        descriptionId = "block.enderio." + baseName + lightingName + colorName;
    }

    @Override
    public String getDescriptionId() {
        return descriptionId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (glassIdentifier.explosionResistance()) {
            tooltip.add(EIOCommonLang.BLOCK_BLAST_RESISTANT);
        }

        if (glassIdentifier.lighting() == GlassLighting.EMITTING) {
            tooltip.add(GlassLang.EMITS_LIGHT);
        }

        if (glassIdentifier.lighting() == GlassLighting.BLOCKING) {
            tooltip.add(GlassLang.BLOCKS_LIGHT);
        }

        Component collisionTooltip = glassIdentifier.collisionPredicate().getComponent();
        if (collisionTooltip != null) {
            tooltip.add(collisionTooltip);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return glassIdentifier.lighting() == GlassLighting.EMITTING ? 15 : 0;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return glassIdentifier.lighting() == GlassLighting.BLOCKING ? level.getMaxLightLevel() : 0;
    }

    @Override
    public float getExplosionResistance() {
        return glassIdentifier.explosionResistance() ? BaseConfig.COMMON.BLOCKS.EXPLOSION_RESISTANCE.get().floatValue() : super.getExplosionResistance();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext != CollisionContext.empty()) {
            if (glassIdentifier.collisionPredicate().canPass(entityCollisionContext)) {
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, level, pos, context);
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

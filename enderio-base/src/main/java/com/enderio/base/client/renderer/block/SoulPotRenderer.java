package com.enderio.base.client.renderer.block;

import com.enderio.base.common.blockentity.SoulPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;

public class SoulPotRenderer implements BlockEntityRenderer<SoulPotBlockEntity> {

    private final Decorated decorated;

    public SoulPotRenderer(BlockEntityRendererProvider.Context context) {
        decorated = new Decorated(context);
    }

    @Override
    public void render(SoulPotBlockEntity soulPot, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        decorated.render(decorate(soulPot), partialTicks, poseStack, multiBufferSource, packedLight, packedOverlay);
    }

    private static class Decorated extends DecoratedPotRenderer {

        public Decorated(BlockEntityRendererProvider.Context context) {
            super(context);
        }
    }

    private static DecoratedPotBlockEntity decorate(SoulPotBlockEntity soulPot) {
        DecoratedPotBlockEntity decoratedPotBlockEntity = BlockEntityType.DECORATED_POT.create(soulPot.getBlockPos(), Blocks.DECORATED_POT.defaultBlockState());
        CompoundTag tag = new CompoundTag();
        soulPot.saveDecorations(tag);
        if (soulPot.getCaughtEntity() != null) {
            decoratedPotBlockEntity.lastWobbleStyle = DecoratedPotBlockEntity.WobbleStyle.POSITIVE;
            long gameTime = Minecraft.getInstance().level.getGameTime();
            decoratedPotBlockEntity.wobbleStartedAtTick = gameTime - (gameTime + soulPot.getBlockPos().hashCode()) % DecoratedPotBlockEntity.WobbleStyle.POSITIVE.duration;
        }
        decoratedPotBlockEntity.setLevel(soulPot.getLevel());
        decoratedPotBlockEntity.loadCustomOnly(tag, Minecraft.getInstance().level.registryAccess());

        return decoratedPotBlockEntity;
    }
}

package com.enderio.enderio.client.content.armory;

import com.enderio.enderio.content.armory.effects.tools.AreaMiningEffect;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

/**
 * Outlines each block that an {@link AreaMiningEffect} will mine, alongside the normal block highlight.
 * Uses the same position logic as the server, so only blocks that will actually be broken are outlined.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class AreaMiningHighlight {

    @SubscribeEvent
    public static void onHighlight(RenderHighlightEvent.Block event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        AreaMiningEffect effect = AreaMiningEffect.getActiveEffect(tool);
        if (effect == null || !effect.isEnabledFor(player)) {
            return;
        }

        BlockHitResult target = event.getTarget();
        BlockPos origin = target.getBlockPos();
        BlockState originState = player.level().getBlockState(origin);
        if (originState.isAir()) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        VertexConsumer lines = event.getMultiBufferSource().getBuffer(RenderType.lines());

        var mineablePositions = effect.getMinablePositions(player.level(), player, tool, origin, originState, target.getDirection());
        var mineableRange = BoundingBox.encapsulatingPositions(mineablePositions);
        if (mineableRange.isEmpty()) {
            return;
        }

        AABB box = AABB.of(mineableRange.get()).move(-camera.x, -camera.y, -camera.z);
        LevelRenderer.renderLineBox(event.getPoseStack(), lines, box, 0.25F, 0.0F, 0.0F, 0.4F);
    }
}

package com.enderio.base.common.integrations;

import com.enderio.api.integration.ClientIntegration;
import com.enderio.base.client.ClientSetup;
import com.enderio.base.common.init.EIOItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import cpw.mods.modlauncher.EnumerationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnderIOSelfClientIntegration implements ClientIntegration {

    public static final ClientIntegration INSTANCE = new EnderIOSelfClientIntegration();
    private static final ModelPart FLAG = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");

    private static final List<Pair<Holder<BannerPattern>, DyeColor>> PATTERNS = createRandomPattern();
    @Override
    public void renderHangGlider(PoseStack posestack, MultiBufferSource buffer, int light, int overlay, AbstractClientPlayer player, float pPartialTick) {
        posestack.pushPose();
        posestack.scale(1.5f,1.5f,1.5f);
        posestack.translate(0, -0.6f, 0.7f);
        if (player.isShiftKeyDown())
            posestack.translate(0, 0.05, 0);
        Minecraft.getInstance().getItemRenderer().render(EIOItems.PINK_GLIDER.asStack(), ItemTransforms.TransformType.NONE, false, posestack, buffer, light, overlay, ClientSetup.glider);
        posestack.scale(0.2f, 0.2f, 0.2f);
        posestack.translate(0, -1f, .5-0.06f);
        for(int i = 0; i < 17 && i < PATTERNS.size() && i > 0; ++i) {
            Pair<Holder<BannerPattern>, DyeColor> pair = PATTERNS.get(i);
            float[] afloat = pair.getSecond().getTextureDiffuseColors();
            pair.getFirst().unwrapKey().map(Sheets::getBannerMaterial)
                .ifPresent(material ->
                    FLAG.render(posestack, material.buffer(buffer, RenderType::entityNoOutline), light, overlay, afloat[0], afloat[1], afloat[2], 1.0F)
            );
        }
        posestack.popPose();
    }

    private static List<Pair<Holder<BannerPattern>, DyeColor>> createRandomPattern() {
        List<Pair<Holder<BannerPattern>, DyeColor>> patterns = new ArrayList<>();
        Random random = new Random();
        for (int i = random.nextInt(3); i < 6; i++) {
            patterns.add(new Pair<>(
                Registry.BANNER_PATTERN.getRandom(new LegacyRandomSource(random.nextInt())).orElseThrow(), DyeColor.values()[random.nextInt(DyeColor.values().length)]));
        }
        return patterns;
    }
}

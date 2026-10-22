package com.enderio.enderio.client.foundation.model.item;

import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Adds a marquee of the stored mob when Shift is held.
 * Matches 1.12.2 pace and colour currently.
 */
public class SoulNameItemRenderer implements ItemModel, SpecialModelRenderer<String> {
    private static final int MOB_NAME_COLOR = 0xFF0030B0;
    private static final int WINDOW_LENGTH = 3;
    private static final long SCROLL_INTERVAL_MS = 200;

    private final Matrix4fc transformation;

    private SoulNameItemRenderer(Matrix4fc transformation) {
        this.transformation = new Matrix4f(transformation);
    }

    @Override
    public void update(ItemStackRenderState renderState, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext,
        @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        if (displayContext != ItemDisplayContext.GUI || !BaseConfig.CLIENT.SOUL_NAME_OVERLAY.get()) {
            return;
        }

        String name = extractArgument(stack);
        if (name == null || name.isEmpty()) {
            return;
        }

        // Invalidate the GUI item cache even while Shift is up, so pressing it reveals the overlay immediately.
        renderState.setAnimated();
        if (!InputConstants.isKeyDown(InputConstants.KEY_LSHIFT) && !InputConstants.isKeyDown(InputConstants.KEY_RSHIFT)) {
            return;
        }

        renderState.appendModelIdentityElement(this);
        renderState.appendModelIdentityElement(name);
        var layer = renderState.newLayer();
        layer.setLocalTransform(transformation);
        layer.setUsesBlockLight(false);
        layer.setupSpecialModel(this, name);
    }

    @Override
    public @Nullable String extractArgument(ItemStack stack) {
        var soul = stack.get(EIODataComponents.SOUL);
        return soul == null || soul.entityType() == null ? null : soul.entityType().getDescription().getString();
    }

    @Override
    public void submit(@Nullable String name, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil,
        int outlineColor) {
        if (name == null || name.isEmpty()) {
            return;
        }

        // Shape and order the full name before selecting a window, preserving RTL runs and joined letters.
        var characters = new ArrayList<FormattedCharSequence>();
        Component.literal(name).getVisualOrderText().accept((_, style, codePoint) -> {
            characters.add(FormattedCharSequence.codepoint(codePoint, style));
            return true;
        });

        characters.add(FormattedCharSequence.codepoint(' ', Style.EMPTY));

        // Step through the characters to display up to three at a time.
        var font = Minecraft.getInstance().font;
        int step = (int) ((Util.getMillis() / SCROLL_INTERVAL_MS) % characters.size());
        int start = font.isBidirectional() ? characters.size() - 1 - WINDOW_LENGTH - step : step;
        var window = new ArrayList<FormattedCharSequence>(WINDOW_LENGTH);
        for (int i = 0; i < WINDOW_LENGTH; i++) {
            window.add(characters.get(Math.floorMod(start + i, characters.size())));
        }

        var text = FormattedCharSequence.composite(window);
        poseStack.pushPose();
        // ItemTransform.NO_TRANSFORM still translates by (-0.5, -0.5, -0.5).
        // In the resulting 0..1 model space, this places the text at GUI pixel (8, 5).
        poseStack.translate(0.5f, 11 / 16f, 0.6f);
        poseStack.scale(1 / 16f, -1 / 16f, 1 / 16f);
        collector.submitText(poseStack, -font.width(text) / 2f, 0, text, true, Font.DisplayMode.SEE_THROUGH,
            lightCoords, MOB_NAME_COLOR, 0, 0);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> consumer) {
        // The underlying item model supplies the item bounds.
    }

    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            return new SoulNameItemRenderer(transformation);
        }

        @Override
        public void resolveDependencies(Resolver resolver) {}
    }
}

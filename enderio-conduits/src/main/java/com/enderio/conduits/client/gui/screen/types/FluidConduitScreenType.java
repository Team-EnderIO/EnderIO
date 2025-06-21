package com.enderio.conduits.client.gui.screen.types;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitConnectionConfig;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.network.C2SClearLockedFluidPacket;
import com.enderio.core.common.util.TooltipUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.network.PacketDistributor;

public class FluidConduitScreenType extends IOConduitScreenType<FluidConduitConnectionConfig> {

    private static final ResourceLocation ICON_ROUND_ROBIN_ENABLED = EnderIO.loc("icon/round_robin_enabled");
    private static final ResourceLocation ICON_ROUND_ROBIN_DISABLED = EnderIO.loc("icon/round_robin_disabled");
    private static final ResourceLocation ICON_SELF_FEED_ENABLED = EnderIO.loc("icon/self_feed_enabled");
    private static final ResourceLocation ICON_SELF_FEED_DISABLED = EnderIO.loc("icon/self_feed_disabled");

    private static final ResourceLocation ICON_INCREASE = EnderIO.loc("icon/increase");
    private static final ResourceLocation ICON_DECREASE = EnderIO.loc("icon/decrease");

    @Override
    public void renderLabels(ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess, GuiGraphics guiGraphics, int startX, int startY, Font font,
        int mouseX, int mouseY) {
        super.renderLabels(dataAccess, guiGraphics, startX, startY, font, mouseX, mouseY);

        if (dataAccess.conduit() instanceof FluidConduit fluidConduit && fluidConduit.doesSupportPriority()) {
            String priority = String.valueOf(dataAccess.getConnectionConfig().insertPriority());
            guiGraphics.drawString(font, ConduitLang.CONDUIT_PRIORITY, 22, 7 + 4 + 4 + 8 + 16 + 12, 0x000000, false);
            guiGraphics.drawString(font, priority, 90 - font.width(priority), 7 + 4 + 4 + 8 + 16 + 12, 0x000000, false);
        }
    }

    private int getIncrement() {
        if (Screen.hasControlDown()) {
            return 100;
        }

        if (Screen.hasShiftDown()) {
            return 10;
        }

        return 1;
    }

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Locked fluid widget
        if (dataAccess.conduit() instanceof FluidConduit fluidConduit && !fluidConduit.isMultiFluid()) {
            screen.addRenderableWidget(new FluidWidget(startX, startY + 20, () -> getLockedFluid(dataAccess),
                    () -> PacketDistributor.sendToServer(new C2SClearLockedFluidPacket(dataAccess.getBlockPos()))));
        } else {
            // Channel colors
            screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
                    () -> dataAccess.getConnectionConfig().insertChannel(),
                    value -> dataAccess.updateConnectionConfig(config -> config.withInsertChannel(value)));
        }

        screen.addFilterConfigureButton(startX + 1, startY + 82, FluidConduit.INSERT_FILTER_SLOT);

        // Priority up/down
        if (dataAccess.conduit() instanceof FluidConduit fluidConduit && fluidConduit.doesSupportPriority()) {
            screen.addIconButton(startX + 70, startY + 38, 9, 9, Component.empty(), ICON_INCREASE,
                () -> dataAccess.updateConnectionConfig(config -> config.withPriority(config.insertPriority() + getIncrement())));
            screen.addIconButton(startX + 70, startY + 38 + 9, 9, 9, Component.empty(), ICON_DECREASE,
                () -> dataAccess.updateConnectionConfig(config -> config.withPriority(config.insertPriority() - getIncrement())));
        }
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        if (dataAccess.conduit() instanceof FluidConduit fluidConduit && fluidConduit.isMultiFluid()) {
            // Channel colors
            screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
                    () -> dataAccess.getConnectionConfig().extractChannel(),
                    value -> dataAccess.updateConnectionConfig(config -> config.withExtractChannel(value)));
        }

        // TODO: Could be good fluid conduit features?
        /*
         * // Round robin screen.addToggleButton(90 + 16 + 4, 20, 16, 16,
         * ConduitLang.ROUND_ROBIN_ENABLED, ConduitLang.ROUND_ROBIN_DISABLED,
         * ICON_ROUND_ROBIN_ENABLED, ICON_ROUND_ROBIN_DISABLED, () ->
         * dataAccess.getConnectionConfig().isRoundRobin(), value ->
         * dataAccess.updateConnectionConfig(config -> config.withIsRoundRobin(value)));
         *
         * // Self feed screen.addToggleButton(90 + (16 + 4) * 2, 20, 16, 16,
         * ConduitLang.SELF_FEED_ENABLED, ConduitLang.SELF_FEED_DISABLED,
         * ICON_SELF_FEED_ENABLED, ICON_SELF_FEED_DISABLED, () ->
         * dataAccess.getConnectionConfig().isSelfFeed(), value ->
         * dataAccess.updateConnectionConfig(config -> config.withIsSelfFeed(value)));
         */

        // Redstone control
        var redstoneChannelWidget = screen.addColorPicker(startX + 16 + 4, startY + 40, ConduitLang.REDSTONE_CHANNEL,
                () -> dataAccess.getConnectionConfig().extractRedstoneChannel(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneChannel(value)));

        // Only show the redstone widget when redstone control is sensitive to signals.
        screen.addPreRenderAction(() -> redstoneChannelWidget.visible = dataAccess.getConnectionConfig()
                .extractRedstoneControl()
                .isRedstoneSensitive());

        screen.addRedstoneControlPicker(startX, startY + 40, EIOLang.REDSTONE_MODE,
                () -> dataAccess.getConnectionConfig().extractRedstoneControl(),
                value -> dataAccess.updateConnectionConfig(config -> config.withExtractRedstoneControl(value)));

        screen.addFilterConfigureButton(startX + 1, startY + 82, FluidConduit.EXTRACT_FILTER_SLOT);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected FluidConduitConnectionConfig setLeftEnabled(FluidConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    protected FluidConduitConnectionConfig setRightEnabled(FluidConduitConnectionConfig config, boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }

    private Fluid getLockedFluid(ConduitMenuDataAccess<FluidConduitConnectionConfig> dataAccess) {
        var tag = dataAccess.getExtraGuiData();
        if (tag == null) {
            return Fluids.EMPTY;
        }

        if (!tag.contains("LockedFluid")) {
            return Fluids.EMPTY;
        }

        return BuiltInRegistries.FLUID.get(ResourceLocation.parse(tag.getString("LockedFluid")));
    }

    private static class FluidWidget extends AbstractWidget {
        private static final ResourceLocation WIDGET_TEXTURE = EnderIO.loc("textures/gui/fluidbackground.png");

        private final Runnable onPress;
        private final Supplier<Fluid> currentFluid;

        FluidWidget(int x, int y, Supplier<Fluid> fluid, Runnable onPress) {
            super(x, y, 14, 14, Component.empty());
            this.onPress = onPress;
            this.currentFluid = fluid;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (isHoveredOrFocused()) {
                MutableComponent tooltip = ConduitLang.FLUID_CONDUIT_CHANGE_FLUID1.copy();
                tooltip.append("\n").append(ConduitLang.FLUID_CONDUIT_CHANGE_FLUID2);
                if (!currentFluid.get().isSame(Fluids.EMPTY)) {
                    tooltip.append("\n")
                            .append(TooltipUtil.withArgs(ConduitLang.FLUID_CONDUIT_CHANGE_FLUID3,
                                    currentFluid.get().getFluidType().getDescription()));
                }
                setTooltip(Tooltip.create(TooltipUtil.style(tooltip)));
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            guiGraphics.blit(WIDGET_TEXTURE, getX(), getY(), 0, 0, this.width, this.height);
            if (currentFluid.get().isSame(Fluids.EMPTY)) {
                return;
            }

            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(currentFluid.get());
            ResourceLocation still = props.getStillTexture();
            AbstractTexture texture = Minecraft.getInstance()
                    .getTextureManager()
                    .getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = props.getTintColor();
                RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F,
                        FastColor.ARGB32.green(color) / 255.0F, FastColor.ARGB32.blue(color) / 255.0F,
                        FastColor.ARGB32.alpha(color) / 255.0F);
                RenderSystem.enableBlend();

                int atlasWidth = (int) (sprite.contents().width() / (sprite.getU1() - sprite.getU0()));
                int atlasHeight = (int) (sprite.contents().height() / (sprite.getV1() - sprite.getV0()));

                guiGraphics.blit(TextureAtlas.LOCATION_BLOCKS, getX() + 1, getY() + 1, 0, sprite.getU0() * atlasWidth,
                        sprite.getV0() * atlasHeight, 12, 12, atlasWidth, atlasHeight);

                RenderSystem.setShaderColor(1, 1, 1, 1);
            }

            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            onPress.run();
        }
    }
}

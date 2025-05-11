package com.enderio.modconduits.client.modules.mekanism.screens;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.screen.ConduitMenuDataAccess;
import com.enderio.conduits.api.screen.ConduitScreenHelper;
import com.enderio.conduits.api.screen.IOConduitScreenType;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.modconduits.common.modules.mekanism.chemical.C2SClearLockedChemicalPacket;
import com.enderio.modconduits.common.modules.mekanism.chemical.ChemicalConduit;
import com.enderio.modconduits.common.modules.mekanism.chemical.ChemicalConduitConnectionConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Supplier;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChemicalConduitScreenType extends IOConduitScreenType<ChemicalConduitConnectionConfig> {
    private static final ResourceLocation ICON_ROUND_ROBIN_ENABLED = EnderIO.loc("icon/round_robin_enabled");
    private static final ResourceLocation ICON_ROUND_ROBIN_DISABLED = EnderIO.loc("icon/round_robin_disabled");
    private static final ResourceLocation ICON_SELF_FEED_ENABLED = EnderIO.loc("icon/self_feed_enabled");
    private static final ResourceLocation ICON_SELF_FEED_DISABLED = EnderIO.loc("icon/self_feed_disabled");

    @Override
    public void createLeftWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<ChemicalConduitConnectionConfig> dataAccess) {
        super.createLeftWidgets(screen, startX, startY, dataAccess);

        // Locked fluid widget
        if (dataAccess.conduit() instanceof ChemicalConduit fluidConduit && !fluidConduit.isMultiChemical()) {
            screen.addRenderableWidget(new ChemicalWidget(startX, startY + 20, () -> getLockedChemical(dataAccess),
                    () -> PacketDistributor.sendToServer(new C2SClearLockedChemicalPacket(dataAccess.getBlockPos()))));
        } else {
            // Channel colors
            screen.addColorPicker(startX, startY + 20, ConduitLang.CONDUIT_CHANNEL,
                    () -> dataAccess.getConnectionConfig().insertChannel(),
                    value -> dataAccess.updateConnectionConfig(config -> config.withInsertChannel(value)));
        }

        screen.addFilterConfigureButton(startX + 1, startY + 82, ChemicalConduit.INSERT_FILTER_SLOT);
    }

    @Override
    public void createRightWidgets(ConduitScreenHelper screen, int startX, int startY,
            ConduitMenuDataAccess<ChemicalConduitConnectionConfig> dataAccess) {
        super.createRightWidgets(screen, startX, startY, dataAccess);

        if (dataAccess.conduit() instanceof ChemicalConduit fluidConduit && fluidConduit.isMultiChemical()) {
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

        screen.addFilterConfigureButton(startX + 1, startY + 82, ChemicalConduit.EXTRACT_FILTER_SLOT);

        // TODO: Show redstone signal indicators using the extra NBT payload.
    }

    @Override
    protected ChemicalConduitConnectionConfig setLeftEnabled(ChemicalConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsInsert(isEnabled);
    }

    @Override
    protected ChemicalConduitConnectionConfig setRightEnabled(ChemicalConduitConnectionConfig config,
            boolean isEnabled) {
        return config.withIsExtract(isEnabled);
    }

    private Chemical getLockedChemical(ConduitMenuDataAccess<ChemicalConduitConnectionConfig> dataAccess) {
        var tag = dataAccess.getExtraGuiData();
        if (tag == null) {
            return MekanismAPI.EMPTY_CHEMICAL;
        }

        if (!tag.contains("LockedChemical")) {
            return MekanismAPI.EMPTY_CHEMICAL;
        }

        return MekanismAPI.CHEMICAL_REGISTRY.get(ResourceLocation.parse(tag.getString("LockedChemical")));
    }

    private static class ChemicalWidget extends AbstractWidget {
        private static final ResourceLocation WIDGET_TEXTURE = EnderIO.loc("textures/gui/chemicalbackground.png");

        private final Runnable onPress;
        private final Supplier<Chemical> currentChemical;

        ChemicalWidget(int x, int y, Supplier<Chemical> chemical, Runnable onPress) {
            super(x, y, 14, 14, Component.empty());
            this.onPress = onPress;
            this.currentChemical = chemical;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (isHoveredOrFocused()) {
                MutableComponent tooltip = MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID1.copy();
                tooltip.append("\n").append(MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID2);
                if (!currentChemical.get().isEmptyType()) {
                    tooltip.append("\n")
                            .append(TooltipUtil.withArgs(MekanismModule.CHEMICAL_CONDUIT_CHANGE_FLUID3,
                                    currentChemical.get().getChemical().getTextComponent()));
                }
                setTooltip(Tooltip.create(TooltipUtil.style(tooltip)));
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            guiGraphics.blit(WIDGET_TEXTURE, getX(), getY(), 0, 0, this.width, this.height);
            if (currentChemical.get().isEmptyType()) {
                return;
            }

            ResourceLocation still = currentChemical.get().getIcon();
            AbstractTexture texture = Minecraft.getInstance()
                    .getTextureManager()
                    .getTexture(TextureAtlas.LOCATION_BLOCKS);
            if (texture instanceof TextureAtlas atlas) {
                TextureAtlasSprite sprite = atlas.getSprite(still);

                int color = currentChemical.get().getTint();
                RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                        (color & 0xFF) / 255.0F, 1);
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

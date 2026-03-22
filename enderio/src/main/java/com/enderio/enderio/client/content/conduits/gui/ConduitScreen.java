package com.enderio.enderio.client.content.conduits.gui;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.screen.ConduitMenuDataAccess;
import com.enderio.enderio.api.conduits.screen.ConduitScreenHelper;
import com.enderio.enderio.api.conduits.screen.ConduitScreenType;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.client.content.conduits.gui.screen_type.ConduitScreenTypes;
import com.enderio.enderio.client.foundation.widgets.DyeColorPickerWidget;
import com.enderio.enderio.client.foundation.widgets.RedstoneControlPickerWidget;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.conduits.menu.ConduitMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConduitScreen extends EnderContainerScreen<ConduitMenu> {
    public static final Identifier TEXTURE = EnderIO.id("textures/gui/conduit.png");
    private static final int WIDTH = 206;
    private static final int HEIGHT = 195;

    private final ScreenHelper screenHelper = new ScreenHelper();

    private final ConduitScreenTypeContainer<?> screenTypeContainer;

    private final List<Runnable> preRenderActions = new ArrayList<>();

    public ConduitScreen(ConduitMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT);

//        this.shouldRenderLabels = true;

        // Get the screen type for this conduit, if available.
        this.screenTypeContainer = new ConduitScreenTypeContainer<>(menu.getConduit().value());
    }

    @Override
    protected void init() {
        super.init();
        preRenderActions.clear();

        if (screenTypeContainer.hasScreenType()) {
            screenTypeContainer.addWidgets(screenHelper);
        }

        for (int i = 0; i < 9; i++) {
            addRenderableWidget(new ConduitSelectionButton(getGuiLeft() + 206, getGuiTop() + 4 + 24 * i, i,
                    menu::getConduit, menu::getConnectedConduits,
                    idx -> handleButtonPress(ConduitMenu.BUTTON_CHANGE_CONDUIT_START_ID + idx)));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        preRenderActions.forEach(Runnable::run);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);

        var conduit = menu.getConduit();
        for (int slot = 0; slot < conduit.value().getInventorySize(); slot++) {
            var pos = conduit.value().getInventorySlotPosition(slot);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getGuiLeft() + pos.x() - 1, getGuiTop() + pos.y() - 1, 206, 0, 18, 18, 256, 256);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);

        if (screenTypeContainer.hasScreenType()) {
            screenTypeContainer.renderLabels(graphics, mouseX, mouseY);
        } else {
            graphics.text(this.font, ConduitLang.ERROR_NO_SCREEN_TYPE, 22, 7 + 4, CommonColors.DARK_GRAY, false);
        }
    }

    // Due to the generics, the menu data access and screen type need to be
    // contained here.
    private class ConduitScreenTypeContainer<U extends ConnectionConfig> {
        private final ConduitMenuDataAccess<U> dataAccess;

        @Nullable
        private final ConduitScreenType<U> screenType;

        public ConduitScreenTypeContainer(Conduit<?, U> conduit) {
            this.dataAccess = createDataAccess(menu.getBlockPos(), conduit);
            this.screenType = ConduitScreenTypes.get(conduit.type());
        }

        public boolean hasScreenType() {
            return screenType != null;
        }

        public void addWidgets(ScreenHelper screenHelper) {
            if (screenType != null) {
                screenType.createScreenWidgets(screenHelper, getGuiLeft(), getGuiTop(), dataAccess);
            }
        }

        public void renderLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
            if (screenType != null) {
                screenType.renderScreenLabels(dataAccess, graphics, font, mouseX, mouseY);
            }
        }

        private <T extends Conduit<T, U>, U extends ConnectionConfig> ConduitMenuDataAccess<U> createDataAccess(
                BlockPos pos, Conduit<T, U> conduit) {
            return new ConduitMenuDataAccess<>() {
                @Override
                public Conduit<?, U> conduit() {
                    return conduit;
                }

                @Override
                public BlockPos getBlockPos() {
                    return pos;
                }

                @Override
                public U getConnectionConfig() {
                    return menu.connectionConfig(conduit.type().connectionConfigType());
                }

                @Override
                public void updateConnectionConfig(java.util.function.Function<U, U> configModifier) {
                    var newConfig = configModifier.apply(menu.connectionConfig(conduit.type().connectionConfigType()));

                    // Update on the client so UI is immediately in sync
                    menu.setConnectionConfig(newConfig);
                }

                @Override
                public CompoundTag getExtraGuiData() {
                    return menu.extraGuiData();
                }
            };
        }
    }

    private class ScreenHelper implements ConduitScreenHelper {

        private static final Identifier ICON_CONFIGURE = EnderIO.id("icon/configure");

        @Override
        public AbstractWidget addCheckbox(int x, int y, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            var widget = ToggleIconButton.createCheckbox(x, y, getter, setter);
            addRenderableWidget(widget);
            return widget;
        }

        @Override
        public AbstractWidget addColorPicker(int x, int y, Component title, Supplier<DyeColor> getter,
                Consumer<DyeColor> setter) {
            var widget = new DyeColorPickerWidget(x, y, getter, setter, title);
            addRenderableWidget(widget);
            return widget;
        }

        @Override
        public AbstractWidget addRedstoneControlPicker(int x, int y, Component title, Supplier<RedstoneControl> getter,
                Consumer<RedstoneControl> setter) {
            var widget = new RedstoneControlPickerWidget(x, y, getter, setter, title);
            addRenderableWidget(widget);
            return widget;
        }

        @Override
        public AbstractWidget addIconButton(int x, int y, int width, int height, Component title,
                Identifier sprite, Consumer<InputWithModifiers> onPress) {
            var widget = new IconButton(x, y, width, height, sprite, title, onPress);
            addRenderableWidget(widget);
            return widget;
        }

        @Override
        public AbstractWidget addToggleButton(int x, int y, int width, int height, Component enabledTitle,
                Component disabledTitle, Identifier enabledSprite, Identifier disabledSprite,
                Supplier<Boolean> getter, Consumer<Boolean> setter) {

            var widget = ToggleIconButton.of(x, y, width, height, enabledSprite, disabledSprite, enabledTitle,
                    disabledTitle, getter, setter);
            addRenderableWidget(widget);
            return widget;
        }

        @Override
        public AbstractWidget addFilterConfigureButton(int x, int y, int slot) {
            var widget = addIconButton(x, y, 16, 16, Component.empty(), ICON_CONFIGURE,
                    input -> menu.tryOpenFilterMenu(slot));
            addPreRenderAction(() -> {
                var inventory = menu.getConduitInventory();
                widget.visible = inventory != null
                        && inventory.getStackInSlot(slot).getCapability(EnderIOCapabilities.FILTER_MENU_PROVIDER) != null;
            });
            return widget;
        }

        // Dynamic UI utilities

        @Override
        public void addPreRenderAction(Runnable runnable) {
            preRenderActions.add(runnable);
        }

        // Custom widgets

        @Override
        public <W extends GuiEventListener & NarratableEntry> W addWidget(W listener) {
            return ConduitScreen.this.addWidget(listener);
        }

        @Override
        public <W extends Renderable> W addRenderableOnly(W renderable) {
            return ConduitScreen.this.addRenderableOnly(renderable);
        }

        @Override
        public <W extends GuiEventListener & Renderable & NarratableEntry> W addRenderableWidget(W widget) {
            return ConduitScreen.this.addRenderableWidget(widget);
        }

        @Override
        public void removeWidget(GuiEventListener listener) {
            ConduitScreen.this.removeWidget(listener);
        }
    }
}

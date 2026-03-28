package com.enderio.core.client.gui.screen;

import com.enderio.core.common.menu.LegacyBaseBlockEntityMenu;
import com.enderio.core.common.menu.SlotWithOverlay;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class EnderContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    private static final int ITEM_RENDER_Z = 400;

    private final Multimap<Integer, Renderable> overlayRenderables = HashMultimap.create();
    private final Multimap<Integer, GuiEventListener> overlayWidgets = HashMultimap.create();

    private final Map<String, StateRestoringWidget> stateRestoringWidgets = new HashMap<>();

    // TODO: 1.21: Intention is that all screens will have labels in future.
    protected boolean shouldRenderLabels = false;

    public EnderContainerScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
    }

    /**
     * Utility for server-side button presses
     * @param id The button ID that was pressed.
     */
    protected void handleButtonPress(int id) {
        Objects.requireNonNull(this.getMinecraft().gameMode).handleInventoryButtonClick(getMenu().containerId, id);
    }

    protected void centerAlignTitleLabelX() {
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (menu instanceof LegacyBaseBlockEntityMenu<?> baseBlockEntityMenu
            && baseBlockEntityMenu.getBlockEntity() == null) {
            return;
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractContents(graphics, mouseX, mouseY, a);

        for (var layer : overlayRenderables.keySet()) {
            for (var overlay : overlayRenderables.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    overlay.extractRenderState(graphics, mouseX, mouseY,
                        Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false));
                }
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        if (shouldRenderLabels) {
            super.extractLabels(graphics, xm, ym);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Do not render tooltips if the mouse is over an overlay.
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(mouseX, mouseY)) {
                        return;]
                    }
                }
            }
        }

        if (!renderCustomTooltip(graphics, mouseX, mouseY)) {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }

    // Return true to cancel normal tooltips
    //TODO this is no longer the case
    protected boolean renderCustomTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        return false;
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemstack, Slot slot,
            @Nullable String countString) {
        super.renderSlotContents(graphics, itemstack, slot, countString);

        if (slot instanceof SlotWithOverlay slotWithOverlay) {
            if (slotWithOverlay.getForegroundSprite() != null) {
                // TODO: 1.21.8: Check this?
//                RenderSystem.disableDepthTest();
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, slotWithOverlay.getForegroundSprite(), slot.x, slot.y, 16, 16);
//                RenderSystem.enableDepthTest();
            }
        }
    }

    public <U extends StateRestoringWidget> U addRestorableState(String key, U widget) {
        stateRestoringWidgets.put(key, widget);
        return widget;
    }

    public EditBox addRestorableState(String key, EditBox widget) {
        stateRestoringWidgets.put(key, new RestorableEditBox(widget));
        return widget;
    }

    public <U extends Renderable> U addOverlayRenderableOnly(int layer, U renderable) {
        overlayRenderables.put(layer, renderable);
        return renderable;
    }

    public <U extends Renderable & GuiEventListener> U addOverlayRenderable(int layer, U widget) {
        overlayRenderables.put(layer, widget);
        overlayWidgets.put(layer, widget);
        return widget;
    }

    @Override
    protected void clearWidgets() {
        overlayRenderables.clear();
        overlayWidgets.clear();
        stateRestoringWidgets.clear();
        super.clearWidgets();
    }

    @Override
    public void resize(int width, int height) {
        // Gather state to persist
        Map<String, Object> valuesBeforeResize = stateRestoringWidgets.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValueForRestore()));

        super.resize(width, height);

        // Restore state
        for (String key : valuesBeforeResize.keySet()) {
            stateRestoringWidgets.get(key).restoreValue(valuesBeforeResize.get(key));
        }
    }

    // region Gui event passthrough

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    overlay.mouseMoved(mouseX, mouseY);
                }
            }
        }

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(event.x(), event.y())) {
                        setFocused(overlay);
                        return overlay.mouseClicked(event, isDoubleClick);
                    }
                }
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(event.x(), event.y())) {
                        return overlay.mouseReleased(event);
                    }
                }
            }
        }

        return super.mouseReleased(event);
    }

    // Always pass mouse drag event through widgets first.

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(event.x(), event.y())) {
                        return overlay.mouseDragged(event, dragX, dragY);
                    }
                }
            }
        }

        if (getFocused() instanceof AbstractWidget abstractWidget && abstractWidget.isActive()) {
            return abstractWidget.mouseDragged(event, dragX, dragY);
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(mouseX, mouseY)) {
                        return overlay.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
                    }
                }
            }
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // Final to preserve order, use onKeyPressed for screen-level event handling.
    @Override
    public final boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.player.closeContainer();
            return true;
        }

        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.keyPressed(event)) {
                        return true;
                    }
                }
            }
        }

        if (onKeyPressed(event)) {
            return true;
        }

        return super.keyPressed(event);
    }

    public boolean onKeyPressed(KeyEvent event) {
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.keyReleased(event)) {
                        return true;
                    }
                }
            }
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.charTyped(event)) {
                        return true;
                    }
                }
            }
        }

        return super.charTyped(event);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    var path = overlay.nextFocusPath(event);
                    if (path != null) {
                        return path;
                    }
                }
            }
        }

        return super.nextFocusPath(event);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (var layer : overlayWidgets.keySet()) {
            for (var overlay : overlayWidgets.get(layer)) {
                if (!(overlay instanceof AbstractWidget widget) || widget.isActive()) {
                    if (overlay.isMouseOver(mouseX, mouseY)) {
                        return true;
                    }
                }
            }
        }

        return super.isMouseOver(mouseX, mouseY);
    }

    // endregion
}

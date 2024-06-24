package com.enderio.core.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple way to provide a screen overlay without popping a new screen entirely onto the stack.
 * This should be used in scenarios where you want the overlay to be interactible alongside the rest of the underlying screen.
 * If you intend for the overlay to close, or to be the only interactible element, consider using screen layers instead.
 * <p>
 *  Examples of gui's to use this class for:
 *  <ul>
 *   <li>A selection popup picker</li>
 *   <li>The IO config overlay, or another overlay that will integrate over the screen</li>
 *  </ul>
 * </p>
 * <p>
 *  Examples of reasons to not use this class:
 *  <ul>
 *   <li>A full screen pop-up gui, like conduit filters from the conduit screen</li>
 *  </ul>
 * </p>
 */
public abstract class BaseScreenOverlay implements Renderable, GuiEventListener {

    // TODO: Decide access protection here
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    private boolean isActive;

    private final List<Renderable> renderables = new ArrayList<>();
    private final List<GuiEventListener> widgets = new ArrayList<>();

    public BaseScreenOverlay(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    protected void init() {
    }

    protected <T extends Renderable> T addRenderableOnly(T renderable) {
        renderables.add(renderable);
        return renderable;
    }

    protected <T extends Renderable & GuiEventListener> T addRenderableWidget(T widget) {
        widgets.add(widget);
        renderables.add(widget);
        return widget;
    }

    protected <T extends GuiEventListener> T addWidget(T widget) {
        widgets.add(widget);
        return widget;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        for (var renderable : renderables) {
            renderable.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    protected void renderBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouse, float pPartialTick) {
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        for (var widget : widgets) {
            widget.mouseMoved(pMouseX, pMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (var widget : widgets) {
            if (widget.mouseClicked(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        for (var widget : widgets) {
            if (widget.mouseReleased(pMouseX, pMouseY, pButton)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        for (var widget : widgets) {
            if (widget.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        for (var widget : widgets) {
            if (widget.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        for (var widget : widgets) {
            if (widget.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        for (var widget : widgets) {
            if (widget.keyReleased(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        for (var widget : widgets) {
            if (widget.charTyped(pCodePoint, pModifiers)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX >= (double)this.x
            && pMouseY >= (double)this.y
            && pMouseX < (double)(this.x + this.width)
            && pMouseY < (double)(this.y + this.height);
    }

    // TODO: How does overlay focus work?
    @Override
    public void setFocused(boolean pFocused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}

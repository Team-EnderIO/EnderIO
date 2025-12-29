package com.enderio.enderio.client.foundation.widgets.ioconfig;

import com.enderio.core.client.gui.widgets.EnderButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IOConfigButton extends EnderButton {
    public static final Identifier IO_CONFIG = EnderIO.rl("buttons/io_config");
    private final IOConfigOverlay configRenderer;
    @Nullable private final Consumer<Boolean> callback;

    public IOConfigButton(int x, int y, IOConfigOverlay configRenderer) {
        this(x, y, configRenderer, null);
    }

    public IOConfigButton(int x, int y, IOConfigOverlay configRenderer, @Nullable Consumer<Boolean> callback) {
        super(x, y, 16, 16, EIOCommonLang.IOCONFIG);
        this.configRenderer = configRenderer;
        this.callback = callback;
        setTooltip(Tooltip.create(EIOCommonLang.IOCONFIG.copy().withStyle(ChatFormatting.WHITE)));
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, IO_CONFIG, getX(), getY(), width, height);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        boolean state = !configRenderer.isActive();
        configRenderer.setVisible(state);
        if (callback != null) {
            callback.accept(state);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

}

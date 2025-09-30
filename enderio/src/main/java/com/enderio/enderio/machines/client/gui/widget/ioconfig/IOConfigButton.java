package com.enderio.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.core.client.gui.widgets.EnderButton;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.common.lang.EIOLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IOConfigButton extends EnderButton {
    public static final ResourceLocation IO_CONFIG = EnderIOAPI.loc("buttons/io_config");
    private final IOConfigOverlay configRenderer;
    @Nullable private final Consumer<Boolean> callback;

    public IOConfigButton(int x, int y, IOConfigOverlay configRenderer) {
        this(x, y, configRenderer, null);
    }

    public IOConfigButton(int x, int y, IOConfigOverlay configRenderer, @Nullable Consumer<Boolean> callback) {
        super(x, y, 16, 16, EIOLang.IOCONFIG);
        this.configRenderer = configRenderer;
        this.callback = callback;
        setTooltip(Tooltip.create(EIOLang.IOCONFIG.copy().withStyle(ChatFormatting.WHITE)));
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blitSprite(IO_CONFIG, getX(), getY(), width, height);
    }

    @Override
    public void onPress() {
        boolean state = !configRenderer.isActive();
        configRenderer.setVisible(state);
        if (callback != null) {
            callback.accept(state);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

}

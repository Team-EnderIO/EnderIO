package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.EnderButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

// TODO: Redesign this
public class IOConfigButton extends EnderButton {
    public static final ResourceLocation IO_CONFIG = EnderIO.loc("buttons/io_config");
    public static final ResourceLocation NEIGHBOURS = EnderIO.loc("buttons/neighbour");
    private final IOConfigWidget configRenderer;
    @Nullable private final Consumer<Boolean> callback;

    public IOConfigButton(int x, int y, IOConfigWidget configRenderer) {
        this(x, y, configRenderer, null);
    }

    public IOConfigButton(int x, int y, IOConfigWidget configRenderer, @Nullable Consumer<Boolean> callback) {
        super(x, y, 16, 16, EIOLang.IOCONFIG);
        this.configRenderer = configRenderer;
        this.callback = callback;
        setTooltip(Tooltip.create(EIOLang.IOCONFIG.copy().withStyle(ChatFormatting.WHITE)));

        // TODO: This should be a child of the config widget.
        /*neighbourButton = new ImageButton(screen.getGuiLeft() + screen.getXSize() - 5 - 16, screen.getGuiTop() + screen.getYSize() - 5 - 16, 16, 16,
            new WidgetSprites(NEIGHBOURS, NEIGHBOURS), (b) -> configRenderer.toggleNeighbourVisibility(), EIOLang.TOGGLE_NEIGHBOUR);
        neighbourButton.setTooltip(Tooltip.create(EIOLang.TOGGLE_NEIGHBOUR.copy().withStyle(ChatFormatting.WHITE)));
        neighbourButton.visible = show;
        addRenderableWidget.apply(neighbourButton);*/
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

    public record Inset(int left, int right, int top, int bottom) {}

}

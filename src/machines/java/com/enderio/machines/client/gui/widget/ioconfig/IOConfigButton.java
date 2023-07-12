package com.enderio.machines.client.gui.widget.ioconfig;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.machines.common.blockentity.base.MultiConfigurable;
import com.enderio.machines.common.menu.MachineMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class IOConfigButton<U extends EIOScreen<?>, T extends AbstractWidget> extends AbstractWidget {
    private static final int RENDERER_HEIGHT = 80;
    public static final ResourceLocation IOCONFIG = EnderIO.loc("textures/gui/icons/io_config.png");
    private static final Inset INSET_ZERO = new Inset(0, 0, 0, 0);
    private final IOConfigWidget<U> configRenderer;
    private final ImageButton neighbourButton;
    private final Supplier<Boolean> playerInvVisible;
    private final Function<Boolean, Boolean> setPlayerInvVisible;
    private final U addedOn;

    public IOConfigButton(U addedOn, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font) {
        this(addedOn, x, y, width, height, menu, addRenderableWidget, font, INSET_ZERO);
    }

    public IOConfigButton(U addedOn, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font,
        Inset inset) {
        super(x, y, width, height, EIOLang.IOCONFIG);
        this.addedOn = addedOn;
        this.playerInvVisible = menu::getPlayerInvVisible;
        this.setPlayerInvVisible = menu::setPlayerInvVisible;
        setTooltip(Tooltip.create(EIOLang.IOCONFIG.copy().withStyle(ChatFormatting.WHITE)));

        var show = !playerInvVisible.get();
        List<BlockPos> configurables = menu.getBlockEntity() instanceof MultiConfigurable multiConfigurable ?
            multiConfigurable.getConfigurables() :
            List.of(menu.getBlockEntity().getBlockPos());
        configRenderer = new IOConfigWidget<>(addedOn, addedOn.getGuiLeft() + 5 + inset.left,
            addedOn.getGuiTop() + addedOn.getYSize() - RENDERER_HEIGHT - 5 + inset.top, addedOn.getXSize() - 10 - inset.left - inset.right,
            RENDERER_HEIGHT - inset.top - inset.bottom, configurables, font);
        configRenderer.visible = show;
        addRenderableWidget.apply(configRenderer);

        neighbourButton = new ImageButton(addedOn.getGuiLeft() + addedOn.getXSize() - 5 - 16, addedOn.getGuiTop() + addedOn.getYSize() - 5 - 16, 16, 16, 16, 0,
            0, IOCONFIG, 48, 32, (b) -> configRenderer.toggleNeighbourVisibility(), EIOLang.TOGGLE_NEIGHBOUR);
        neighbourButton.setTooltip(Tooltip.create(EIOLang.TOGGLE_NEIGHBOUR.copy().withStyle(ChatFormatting.WHITE)));
        neighbourButton.visible = show;
        addRenderableWidget.apply(neighbourButton);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        Vector2i pos = new Vector2i(getX(), getY());
        addedOn.renderSimpleArea(guiGraphics, pos, pos.add(new Vector2i(width, height)));

        RenderSystem.enableDepthTest();
        guiGraphics.blit(IOCONFIG, this.getX(), this.getY(), 0, 0, this.width, this.height, 48, 32);
        RenderSystem.disableDepthTest();
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        var state = !setPlayerInvVisible.apply(!playerInvVisible.get()); // toggle the variable and set state to opposite of it
        configRenderer.visible = state;
        neighbourButton.visible = state;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    public record Inset(int left, int right, int top, int bottom) {}

}

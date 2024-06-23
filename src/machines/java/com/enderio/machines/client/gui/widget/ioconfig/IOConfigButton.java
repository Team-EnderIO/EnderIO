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
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IOConfigButton<U extends EIOScreen<?>, T extends AbstractWidget> extends AbstractButton {
    private static final int RENDERER_HEIGHT = 80;
    public static final ResourceLocation IO_CONFIG = EnderIO.loc("buttons/io_config");
    public static final ResourceLocation NEIGHBOURS = EnderIO.loc("buttons/neighbour");
    private static final Inset INSET_ZERO = new Inset(0, 0, 0, 0);
    private final IOConfigWidget<U> configRenderer;
    private final ImageButton neighbourButton;
    private final Supplier<Boolean> playerInvVisible;
    private final Function<Boolean, Boolean> setPlayerInvVisible;
    private final U screen;
    @Nullable private final Consumer<Boolean> callback;

    public IOConfigButton(U screen, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font) {
        this(screen, x, y, width, height, menu, addRenderableWidget, font, INSET_ZERO);
    }

    public IOConfigButton(U screen, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font,
        Inset inset) {
        this(screen, x, y, width, height, menu, addRenderableWidget, font, inset, null);
    }

    public IOConfigButton(U screen, int x, int y, int width, int height, MachineMenu<?> menu, Function<AbstractWidget, T> addRenderableWidget, Font font,
        Inset inset, @Nullable Consumer<Boolean> callback) {
        super(x, y, width, height, EIOLang.IOCONFIG);
        this.screen = screen;
        this.playerInvVisible = menu::getPlayerInvVisible;
        this.setPlayerInvVisible = menu::setPlayerInvVisible;
        this.callback = callback;
        setTooltip(Tooltip.create(EIOLang.IOCONFIG.copy().withStyle(ChatFormatting.WHITE)));

        var show = !playerInvVisible.get();
        List<BlockPos> configurables = menu.getBlockEntity() instanceof MultiConfigurable multiConfigurable ?
            multiConfigurable.getConfigurables() :
            List.of(menu.getBlockEntity().getBlockPos());
        configRenderer = new IOConfigWidget<>(screen, screen.getGuiLeft() + 5 + inset.left,
            screen.getGuiTop() + screen.getYSize() - RENDERER_HEIGHT - 5 + inset.top, screen.getXSize() - 10 - inset.left - inset.right,
            RENDERER_HEIGHT - inset.top - inset.bottom, configurables, font);
        configRenderer.visible = show;
        addRenderableWidget.apply(configRenderer);

        neighbourButton = new ImageButton(screen.getGuiLeft() + screen.getXSize() - 5 - 16, screen.getGuiTop() + screen.getYSize() - 5 - 16, 16, 16,
            new WidgetSprites(NEIGHBOURS, NEIGHBOURS), (b) -> configRenderer.toggleNeighbourVisibility(), EIOLang.TOGGLE_NEIGHBOUR);
        neighbourButton.setTooltip(Tooltip.create(EIOLang.TOGGLE_NEIGHBOUR.copy().withStyle(ChatFormatting.WHITE)));
        neighbourButton.visible = show;
        addRenderableWidget.apply(neighbourButton);
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int color) {
        guiGraphics.blitSprite(IO_CONFIG, this.getX(), this.getY(), this.width, this.height);
    }

    @Override
    public void onPress() {
        var state = !setPlayerInvVisible.apply(!playerInvVisible.get()); // toggle the variable and set state to opposite of it
        configRenderer.visible = state;
        neighbourButton.visible = state;
        if (callback != null) {
            callback.accept(state);
        }
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

    public record Inset(int left, int right, int top, int bottom) {}

}

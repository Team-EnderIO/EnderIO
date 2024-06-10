package com.enderio.conduits.client.gui;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.SlotType;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.menu.ConduitMenu;
import com.enderio.conduits.common.menu.ConduitSlot;
import com.enderio.conduits.common.network.C2SSetConduitConnectionState;
import com.enderio.conduits.common.network.C2SSetConduitExtendedData;
import com.enderio.core.client.gui.screen.EIOScreen;
import com.enderio.core.client.gui.widgets.CheckBox;
import com.enderio.core.client.gui.widgets.EnumIconWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConduitScreen extends EIOScreen<ConduitMenu> {

    public static final ResourceLocation TEXTURE = EnderIO.loc("textures/gui/conduit.png");
    public ConduitScreen(ConduitMenu pMenu, Inventory pPlayerInventory, Component title) {
        super(pMenu, pPlayerInventory, title);
    }

    private final List<ConduitSelectionButton> typeSelectionButtons = new ArrayList<>();
    private final List<GuiEventListener> typedButtons = new ArrayList<>();
    private boolean recalculateTypedButtons = true;

    @Override
    protected void init() {
        super.init();
        updateConnectionWidgets(true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        ConduitMenuData data = menu.getConduitType().getMenuData();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(getGuiLeft(), getGuiTop(), 0);
        if (data.showBarSeparator()) {
            guiGraphics.blit(getBackgroundImage(), 102, 7, 255, 0, 1, 97);
        }
        for (SlotType type: SlotType.values()) {
            if (type.isAvailableFor(data)) {
                guiGraphics.blit(getBackgroundImage(), type.getX()-1, type.getY()-1, 206, 0, 18, 18);
            }
        }
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(guiGraphics, pMouseX, pMouseY);

        ConduitMenuData data = menu.getConduitType().getMenuData();

        guiGraphics.drawString(this.font, ConduitLang.CONDUIT_INSERT,  22 + 16,  7 + 4, 4210752, false);

        if (data.showBothEnable()) {
            guiGraphics.drawString(this.font, ConduitLang.CONDUIT_EXTRACT, 112 + 16, 7 + 4, 4210752, false);
        }
    }

    private void updateConnectionWidgets(boolean forceUpdate) {
        if (forceUpdate || recalculateTypedButtons) {
            recalculateTypedButtons = false;
            typedButtons.forEach(this::removeWidget);
            typedButtons.clear();
            ConduitMenuData data = menu.getConduitType().getMenuData();
            Vector2i pos = new Vector2i(22, 7).add(getGuiLeft(), getGuiTop());

            addTypedButton(
                new CheckBox(pos,
                    () -> getOnDynamic(dyn -> dyn.isInsert(), false),
                    bool -> actOnDynamic(dyn -> dyn.withEnabled(false, bool))));

            if (data.showBothEnable()) {
                addTypedButton(
                    new CheckBox(pos.add(90, 0),
                        () -> getOnDynamic(dyn -> dyn.isExtract(), false),
                        bool -> actOnDynamic(dyn -> dyn.withEnabled(true, bool))));
            }
            if (data.showColorInsert()) {
                addTypedButton(
                    new EnumIconWidget<>(this, pos.x(), pos.y() + 20,
                        () -> getOnDynamic(dyn -> dyn.insertChannel(), ColorControl.GREEN),
                        color -> actOnDynamic(dyn -> dyn.withColor(false, color)),
                    EIOLang.CONDUIT_CHANNEL));
            }

            if (data.showColorExtract()) {
                addTypedButton(
                    new EnumIconWidget<>(this, pos.x() + 90, pos.y() + 20,
                        () -> getOnDynamic(dyn -> dyn.extractChannel(), ColorControl.GREEN),
                        color -> actOnDynamic(dyn -> dyn.withColor(true, color)),
                        EIOLang.CONDUIT_CHANNEL));
            }

            if (data.showRedstoneExtract()) {
                addTypedButton(
                    new EnumIconWidget<>(this, pos.x() + 90, pos.y() + 40,
                        () -> getOnDynamic(dyn -> dyn.control(), RedstoneControl.ACTIVE_WITH_SIGNAL),
                        mode -> actOnDynamic(dyn -> dyn.withRedstoneMode(mode)),
                        EIOLang.REDSTONE_MODE));

                addTypedButton(
                    new EnumIconWidget<>(this, pos.x() + 90 + 20, pos.y() + 40,
                        () -> getOnDynamic(dyn -> dyn.redstoneChannel(), ColorControl.GREEN),
                        color -> actOnDynamic(dyn -> dyn.withRedstoneChannel(color)),
                        EIOLang.REDSTONE_CHANNEL));
            }

            menu.getConduitType()
                .getClientData()
                .createWidgets(this, () -> getBundle().getNodeFor(menu.getConduitType()).getConduitData().cast(),
                    this::sendExtendedConduitUpdate, menu::getDirection,
                    new Vector2i(22, 7).add(getGuiLeft(), getGuiTop()))
                .forEach(this::addTypedButton);
        }

        List<ConduitType<?>> validConnections = new ArrayList<>();
        for (ConduitType<?> type : getBundle().getTypes()) {
            if (getConnectionState(type) instanceof DynamicConnectionState) {
                validConnections.add(type);
            }
        }

        if (forceUpdate || !typeSelectionButtons.stream().map(ConduitSelectionButton::getType).toList().equals(validConnections)) {
            typeSelectionButtons.forEach(this::removeWidget);
            typeSelectionButtons.clear();
            for (int i = 0; i < validConnections.size(); i++) {
                ConduitType<?> connection = validConnections.get(i);
                ConduitSelectionButton button = new ConduitSelectionButton(getGuiLeft() + 206, getGuiTop() + 4 + 24*i, connection, menu::getConduitType, type -> {
                    menu.setConduitType(type);
                    recalculateTypedButtons = true;
                });

                typeSelectionButtons.add(button);
                addRenderableWidget(button);
            }
        }
    }

    private <T extends ConduitData<T>> void sendExtendedConduitUpdate(Function<T, T> map) {
        ConduitType<T> conduitType = (ConduitType<T>)menu.getConduitType();
        T currentData = getBundle().getNodeFor(conduitType).getConduitData().cast();
        var menu = getMenu();

        PacketDistributor.sendToServer(new C2SSetConduitExtendedData(
            menu.getBlockEntity().getBlockPos(),
            menu.getConduitType(),
            map.apply(currentData)));
    }

    private void addTypedButton(AbstractWidget button) {
        typedButtons.add(button);
        addRenderableWidget(button);
    }

    private void actOnDynamic(Function<DynamicConnectionState, DynamicConnectionState> map) {
        if (getConnectionState() instanceof DynamicConnectionState dyn) {
            PacketDistributor.sendToServer(new C2SSetConduitConnectionState(
                getMenu().getBlockEntity().getBlockPos(),
                getMenu().getDirection(),
                getMenu().getConduitType(),
                map.apply(dyn)
            ));
        }
    }
    private <T> T getOnDynamic(Function<DynamicConnectionState, T> map, T defaultValue) {
        return getConnectionState() instanceof DynamicConnectionState dyn ? map.apply(dyn) : defaultValue;
    }

    private ConnectionState getConnectionState() {
        return getConnectionState(menu.getConduitType());
    }

    private ConnectionState getConnectionState(ConduitType<?> type) {
        return getBundle().getConnectionState(menu.getDirection(), type);
    }

    private ConduitBundle getBundle() {
        return menu.getBlockEntity().getBundle();
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //close and don't render if someone removed the conduit we are looking at or similar
        if (!menu.stillValid(minecraft.player)) {
            minecraft.player.closeContainer();
        } else {
            updateConnectionWidgets(false);
            menu.getConduitSlots().forEach(ConduitSlot::updateVisibilityPosition);
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public ResourceLocation getBackgroundImage() {
        return TEXTURE;
    }

    @Override
    protected Vector2i getBackgroundImageSize() {
        return new Vector2i(206,195);
    }
}

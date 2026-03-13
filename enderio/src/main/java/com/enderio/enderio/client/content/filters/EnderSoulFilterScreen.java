package com.enderio.enderio.client.content.filters;

import com.enderio.core.client.gui.screen.EnderContainerScreen;
import com.enderio.core.client.gui.widgets.IconButton;
import com.enderio.core.client.gui.widgets.ToggleIconButton;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.content.filters.soul.EnderSoulFilterMenu;
import com.enderio.enderio.content.filters.soul.SoulFilterSlot;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;

public class EnderSoulFilterScreen extends EnderContainerScreen<EnderSoulFilterMenu> {

    private static final int WIDTH = 183;
    private static final int HEIGHT = 199;

    private static final Identifier BG_2x9 = EnderIO.id("textures/gui/screens/filter_2x9.png");
    private static final Identifier BG_1x9 = EnderIO.id("textures/gui/screens/filter_1x9.png");
    private static final Identifier BG_3x9 = EnderIO.id("textures/gui/screens/filter_3x9.png");
    private static final Identifier BG_4x9 = EnderIO.id("textures/gui/screens/filter_4x9.png");

    private static final Identifier BACK_SPRITE = EnderIO.id("icon/back");

    private static final Identifier ICON_MATCH_COMPONENTS = EnderIO.id("icon/match_components");
    private static final Identifier ICON_IGNORE_COMPONENTS = EnderIO.id("icon/ignore_components");

    private static final Identifier ICON_ALLOW_LIST = EnderIO.id("icon/allow_list");
    private static final Identifier ICON_DENY_LIST = EnderIO.id("icon/deny_list");

    private final Identifier backgroundTexture;

    public EnderSoulFilterScreen(EnderSoulFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, WIDTH, HEIGHT - (4 - menu.type.rowCount()) * 18);

        this.shouldRenderLabels = true;

        this.titleLabelX = 28;
        this.titleLabelY = 14;

        this.inventoryLabelX += 6;
        this.inventoryLabelY = 34 + menu.type.rowCount() * 18;

        switch (menu.type.rowCount()) {
        case 1 -> backgroundTexture = BG_1x9;
        case 2 -> backgroundTexture = BG_2x9;
        case 3 -> backgroundTexture = BG_3x9;
        case 4 -> backgroundTexture = BG_4x9;
        default -> throw new NotImplementedException();
        }
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new IconButton(getGuiLeft() + 3, getGuiTop() + 3, 16, 16, BACK_SPRITE, null,
                input -> handleButtonPress(AbstractFilterMenu.BACK_BUTTON_ID)));

        int xPos = getGuiLeft() + WIDTH - 25;
        int yPos = getGuiTop() + 27 + menu.type.rowCount() * 18;

        if (getMenu().type.canMatchComponents()) {
            // TODO: Change to NBT...
            addRenderableWidget(new ToggleIconButton(xPos, yPos, 16, 16,
                    (b) -> b ? ICON_MATCH_COMPONENTS : ICON_IGNORE_COMPONENTS,
                    (b) -> b ? FiltersLang.FILTER_MATCH_COMPONENTS : FiltersLang.FILTER_IGNORE_COMPONENTS,
                    getMenu()::shouldCompareTags,
                    (b) -> handleButtonPress(EnderSoulFilterMenu.SHOULD_COMPARE_TAGS_BUTTON_ID)));

            xPos -= 18;
        }

        addRenderableWidget(
                new ToggleIconButton(xPos, yPos, 16, 16, (b) -> b ? ICON_DENY_LIST : ICON_ALLOW_LIST,
                        (b) -> b ? FiltersLang.FILTER_DENY_LIST : FiltersLang.FILTER_ALLOW_LIST, getMenu()::isInverted,
                        (b) -> handleButtonPress(EnderSoulFilterMenu.IS_INVERTED_BUTTON_ID)));

        xPos -= 18;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, getGuiLeft(), getGuiTop(), 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemstack, Slot slot, @Nullable String countString) {
        super.renderSlotContents(graphics, itemstack, slot, countString);

        if (slot instanceof SoulFilterSlot soulFilterSlot) {
            var soul = soulFilterSlot.getResource();
            if (!soul.hasEntity()) {
                return;
            }

            ItemStack renderStack = getRenderStack(soul);
            super.renderSlotContents(graphics, renderStack, slot, countString);
        }

        super.renderSlotContents(graphics, itemstack, slot, countString);
    }

    @Override
    protected boolean renderCustomTooltip(GuiGraphicsExtractor graphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof SoulFilterSlot soulFilterSlot) {
            var soul = soulFilterSlot.getResource();
            if (!soul.hasEntity()) {
                return true;
            }

            ItemStack renderStack = getRenderStack(soul);
            // TODO: Maybe add extra tooltip to show there is entity NBT?
            graphics.setTooltipForNextFrame(font, getTooltipFromContainerItem(renderStack), renderStack.getTooltipImage(), renderStack, x, y);
            return true;
        }

        return super.renderCustomTooltip(graphics, x, y);
    }

    private ItemStack getRenderStack(Soul soul) {
        return SpawnEggItem.byId(soul.entityType())
            .map(itemHolder -> itemHolder.value().getDefaultInstance())
            .orElse(SoulVialItem.forSoul(soul));
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput) {
//        if (getMenu().getFilter() instanceof ItemFilterCapability itemFilterCapability) {
//            if (slot != null && slot.index < itemFilterCapability.getEntries().size()) {
//                if (!itemFilterCapability.getEntries().get(slot.index).isEmpty()) {
//                    itemFilterCapability.setEntry(slotId, ItemStack.EMPTY);
//                }
//            }
        super.slotClicked(slot, slotId, buttonNum, containerInput);
//        }
    }
}

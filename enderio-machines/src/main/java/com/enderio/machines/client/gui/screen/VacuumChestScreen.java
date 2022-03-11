package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.menu.VacuumChestMenu;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VacuumChestScreen extends EIOScreen<VacuumChestMenu>{

	private static final ResourceLocation VACUMM_CHEST_BG = EIOMachines.loc("textures/gui/vacuum_chest.png");

	public VacuumChestScreen(VacuumChestMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle, true);
		this.inventoryLabelY = this.imageHeight - 94;
	}
	
	@Override
	protected void init() {
		super.init();
		 addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 86, () -> menu.getBlockEntity().getRedstoneControl(),
		            control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
		 addRenderableWidget(new Button(leftPos + imageWidth - 8 - 12 - 20 - 2, topPos + 86 - 5, 20, 9, new TextComponent("\u2303"), (b) -> this.getMenu().getBlockEntity().increasseRange()));
		 addRenderableWidget(new Button(leftPos + imageWidth - 8 - 12 - 20 - 2, topPos + 86 + 6, 20, 9, new TextComponent("\u2304"), (b) -> this.getMenu().getBlockEntity().decreasseRange()));
	}

	@Override
	protected ResourceLocation getBackgroundImage() {
		return VACUMM_CHEST_BG;
	}

	@Override
	protected Vector2i getBackgroundImageSize() {
		return new Vector2i(176, 206);
	}
	
	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		this.font.draw(pPoseStack, new TranslatableComponent("enderio.gui.filter"), 8, this.imageHeight - 134, 4210752);
		super.renderLabels(pPoseStack, pMouseX, pMouseY);
	}
	
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		font.draw(pPoseStack, this.getMenu().getBlockEntity().getRange() +"", leftPos + imageWidth - 8 - 12 - 20 - 2 - 8, topPos + 86, 0);
	}

}

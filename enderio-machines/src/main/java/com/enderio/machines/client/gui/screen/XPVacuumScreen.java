package com.enderio.machines.client.gui.screen;

import com.enderio.base.client.gui.screen.EIOScreen;
import com.enderio.base.client.gui.widgets.EnumIconWidget;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.client.gui.widget.FluidStackStaticWidget;
import com.enderio.machines.common.menu.XPVacuumMenu;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class XPVacuumScreen extends EIOScreen<XPVacuumMenu>{

	private static final ResourceLocation XP_VACUUM_BG = EIOMachines.loc("textures/gui/xp_vacuum.png");

	public XPVacuumScreen(XPVacuumMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle, true);
		this.inventoryLabelY = this.imageHeight - 50;
	}
	
	@Override
	protected void init() {
		super.init();
		addRenderableOnly(new FluidStackStaticWidget(this, getMenu().getBlockEntity()::getFluidTank, leftPos + 27, topPos + 22, 32, 32));
		addRenderableWidget(new EnumIconWidget<>(this, leftPos + imageWidth - 8 - 12, topPos + 50, () -> menu.getBlockEntity().getRedstoneControl(),
				control -> menu.getBlockEntity().setRedstoneControl(control), EIOLang.REDSTONE_MODE));
		addRenderableWidget(new Button(leftPos + imageWidth - 8 - 12 - 20 - 2, topPos + 50 - 5, 20, 9, new TextComponent("\u2303"), (b) -> this.getMenu().getBlockEntity().increasseRange()));
		addRenderableWidget(new Button(leftPos + imageWidth - 8 - 12 - 20 - 2, topPos + 50 + 6, 20, 9, new TextComponent("\u2304"), (b) -> this.getMenu().getBlockEntity().decreasseRange()));
	}

	@Override
	protected ResourceLocation getBackgroundImage() {
		return XP_VACUUM_BG;
	}

	@Override
	protected Vector2i getBackgroundImageSize() {
		return new Vector2i(176, 166);
	}
	
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTicks) {
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTicks);
		font.draw(pPoseStack, this.getMenu().getBlockEntity().getRange() +"", leftPos + imageWidth - 8 - 12 - 20 - 2 - 8, topPos + 50, 0);
	}

}

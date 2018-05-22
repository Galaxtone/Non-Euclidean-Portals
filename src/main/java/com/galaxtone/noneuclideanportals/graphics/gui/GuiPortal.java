package com.galaxtone.noneuclideanportals.graphics.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiPortal extends GuiScreen {

	//private Portal.Side portal;

	public GuiPortal() {
		//this.portal = Selection.getPortal();
	}

	@Override
	public void updateScreen() {
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		
		this.fontRendererObj.drawString("Portal configuration (Id: 0)", 0, 0, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}

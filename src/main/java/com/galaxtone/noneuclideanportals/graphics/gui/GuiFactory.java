package com.galaxtone.noneuclideanportals.graphics.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

@SuppressWarnings("deprecation")
public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraft) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiConfig.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
}

package com.galaxtone.noneuclideanportals.graphics.gui;

import java.util.ArrayList;
import java.util.List;

import com.galaxtone.noneuclideanportals.Main;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {

	public GuiConfig(GuiScreen parent) {
		super(parent, getConfigElements(), Main.modid, false, false, I18n.format("config." + Main.modid + ".title", Main.name));
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		
		Configuration configuration = Main.config.configuration;
		for (String name : configuration.getCategoryNames()) {
			ConfigCategory category = configuration.getCategory(name);
			category.setLanguageKey(String.format("config.%s.%s.title", Main.modid, name));
			
			if (name.indexOf(".") < 0) elements.add(new ConfigElement(category));
		}
		
		return elements;
	}
}

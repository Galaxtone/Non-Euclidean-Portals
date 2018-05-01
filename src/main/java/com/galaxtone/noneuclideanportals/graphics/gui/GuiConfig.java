package com.galaxtone.noneuclideanportals.graphics.gui;

import java.util.ArrayList;
import java.util.List;

import com.galaxtone.noneuclideanportals.Main;
import com.galaxtone.noneuclideanportals.utils.LocaleHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {

	public GuiConfig(GuiScreen parent) {
		super(parent, getConfigElements(), Main.modid, false, false, LocaleHelper.translate(LocaleHelper.getKey("config", "title")));
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> elements = new ArrayList<IConfigElement>();
		
		Configuration configuration = Main.config.configuration;
		for (String name : configuration.getCategoryNames()) {
			ConfigCategory category = configuration.getCategory(name);
			category.setLanguageKey(LocaleHelper.getKey("config", name, "title"));
			
			if (name.indexOf(".") < 0) elements.add(new ConfigElement(category));
		}
		
		return elements;
	}
}

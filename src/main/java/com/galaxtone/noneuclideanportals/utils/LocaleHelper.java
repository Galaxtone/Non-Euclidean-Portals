package com.galaxtone.noneuclideanportals.utils;

import com.galaxtone.noneuclideanportals.Main;

import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class LocaleHelper {

	public static String translate(String key) {
		return I18n.translateToLocal(key);
	}

	public static String translateFormat(String key, Object... properties) {
		return I18n.translateToLocalFormatted(key, properties);
	}

	public static String getKey(String name, String... properties) {
		String key = name + "." + Main.modid;
		for (int i = 0; i < properties.length; i++) key += "." + properties[i];
		
		return key;
	}
}

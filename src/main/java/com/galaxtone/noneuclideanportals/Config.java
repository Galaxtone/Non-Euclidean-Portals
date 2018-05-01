package com.galaxtone.noneuclideanportals;

import java.io.File;

import com.galaxtone.noneuclideanportals.utils.LocaleHelper;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {

	public final Configuration configuration;

	private int maxRecursiveLimit;
	private int reachDistance;

	public Config(File file) {
		File configFile = new File(file, Main.modid + ".cfg");
		this.configuration = new Configuration(configFile);
		
		this.reload();
	}

	public void reload() {
		this.reachDistance = this.getInteger("reach_distance", "general", 8, 4, 64);
		this.maxRecursiveLimit = this.getInteger("max_recursive_limit", "rendering", 4, 0, 16);
	}

	public int getMaxRecursiveLimit() {
		return this.maxRecursiveLimit;
	}

	public int getReachDistance() {
		return this.reachDistance;
	}

	private int getInteger(String name, String category, int defaultValue, int minValue, int maxValue) {
		String key = LocaleHelper.getKey("config", category, name);
		return this.configuration.getInt(name, category, defaultValue, minValue, maxValue, LocaleHelper.translate(key), key);
	}
}

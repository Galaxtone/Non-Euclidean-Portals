package com.galaxtone.noneuclideanportals;

import java.io.File;

//import com.galaxtone.noneuclideanportals.utils.LocaleHelper; (Removed due to JamiesWhiteShirt#8888 from Minecraft Mod Development Discord Server D:<)

import net.minecraftforge.common.config.Configuration;

public class Config {

	public final Configuration configuration;

	/* Rendering */
	private int maxRecursiveLimit;

	/* General */
	private int reachDistance;
	private int selectionLimit;

	public Config(File file) {
		File configFile = new File(file, Main.modid + ".cfg");
		this.configuration = new Configuration(configFile);
		
		this.reload();
	}

	public void reload() {
		this.maxRecursiveLimit = this.getInteger("max_recursive_limit", "rendering", 4, 0, 16, "The limit for how many times you can render a portal through another portal.");
		
		this.reachDistance = this.getInteger("reach_distance", "general", 8, 4, 64, "Reach distance used for wand selection.");
		this.selectionLimit = this.getInteger("selection_limit", "general", 8, 4, 16, "The limit for how big selection can be in any direction.");
	}

	public int getMaxRecursiveLimit() {
		return this.maxRecursiveLimit;
	}

	public int getReachDistance() {
		return this.reachDistance;
	}

	public int getSelectionLimit() {
		return this.selectionLimit;
	}

	private int getInteger(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
		return this.configuration.getInt(name, category, defaultValue, minValue, maxValue, comment, String.format("config.%s.%s.%s", Main.modid, category, name));
	}
}

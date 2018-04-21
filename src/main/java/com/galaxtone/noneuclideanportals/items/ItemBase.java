package com.galaxtone.noneuclideanportals.items;

import javax.annotation.Nullable;

import com.galaxtone.noneuclideanportals.Main;

import net.minecraft.item.Item;

public class ItemBase extends Item {

	public final String oreId;

	public ItemBase(String name, @Nullable String oreId) {
		this.setUnlocalizedName(Main.modid + "." + name);
		this.setRegistryName(Main.modid + ":" + name);
		this.setCreativeTab(Main.creativeTab);
		
		this.oreId = oreId;
	}

	public ItemBase(String name) {
		this(name, null);
	}
}

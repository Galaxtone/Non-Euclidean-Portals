package com.galaxtone.noneuclideanportals;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.galaxtone.noneuclideanportals.graphics.Portal;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class WorldData extends WorldSavedData {

	public List<Portal> portals = Collections.synchronizedList(new LinkedList<Portal>());

	public WorldData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		/*
		 * Integer x
		 * Integer y
		 * Integer z
		 * Byte    width
		 * Byte    height
		 * Byte    axis
		 * Byte    destination
		 */
		
		NBTTagList portalsTag = new NBTTagList();
		compound.setTag("portals", portalsTag);
		
		for (Portal portal : this.portals) {
			portalsTag.appendTag(portalTag);
		}
	}

	public static WorldData get(World world) {
		MapStorage storage = world.getMapStorage();
		WorldData data = (WorldData) storage.getOrLoadData(WorldData.class, Main.modid);
		
		if (data == null) {
			data = new WorldData(Main.modid);
			storage.setData(Main.modid, data);
		}
		return data;
	}
}

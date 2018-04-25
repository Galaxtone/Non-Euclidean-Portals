package com.galaxtone.noneuclideanportals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.galaxtone.noneuclideanportals.graphics.Portal;
import com.galaxtone.noneuclideanportals.graphics.PortalSide;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class WorldData extends WorldSavedData {

	public List<Portal> portals = Collections.synchronizedList(new LinkedList<Portal>());
	public WorldData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList portalsTag = compound.getTagList("portals", 10);
		
		for (int i = 0; i < portalsTag.tagCount(); i++) {
			NBTTagCompound portalTag  = portalsTag.getCompoundTagAt(i);
			
			portalTag.setInteger("X", (int) portal.plane.minX);
			portalTag.setInteger("Y", (int) portal.plane.minY);
			portalTag.setInteger("Z", (int) portal.plane.minZ);
			
			double length = portal.plane.maxZ - portal.plane.minX;
			portalTag.setByte("width", (byte) (portal.axis == Axis.X ? length : portal.plane.maxX - portal.plane.minX));
			portalTag.setByte("height", (byte) (portal.axis == Axis.X ? length : portal.plane.maxY - portal.plane.minY));
			portalTag.setByte("axis", (byte) portal.axis.ordinal());

			PortalSide front = portal.frontSide;
			PortalSide back = portal.backSide;
			
			portalTag.setShort("front", (short) (front.destination == null ? 0 : front.destination.portal.id + 1));
			portalTag.setShort("back", (short) (back.destination == null ? 0 : back.destination.portal.id + 1));
			
			portalsTag.appendTag(portalTag);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList portalsTag = new NBTTagList();
		compound.setTag("portals", portalsTag);
		
		for (Portal portal : this.portals) {
			NBTTagCompound portalTag = new NBTTagCompound();
			portalTag.setInteger("X", (int) portal.plane.minX);
			portalTag.setInteger("Y", (int) portal.plane.minY);
			portalTag.setInteger("Z", (int) portal.plane.minZ);
			
			double length = portal.plane.maxZ - portal.plane.minX;
			portalTag.setByte("width", (byte) (portal.axis == Axis.X ? length : portal.plane.maxX - portal.plane.minX));
			portalTag.setByte("height", (byte) (portal.axis == Axis.X ? length : portal.plane.maxY - portal.plane.minY));
			portalTag.setByte("axis", (byte) portal.axis.ordinal());

			PortalSide front = portal.frontSide;
			PortalSide back = portal.backSide;
			
			portalTag.setShort("front", front.destination == null ? 0 : front.destination.portal.id + 1);
			portalTag.setShort("back", back.destination == null ? 0 : back.destination.portal.id + 1);
			
			portalsTag.appendTag(portalTag);
		}
		
		return compound;
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

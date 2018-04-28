package com.galaxtone.noneuclideanportals;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.galaxtone.noneuclideanportals.graphics.Portal;
import com.galaxtone.noneuclideanportals.utils.PortalReference;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
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
		portals.removeAll(null);
		NBTTagList portalsTag = compound.getTagList("portals", 10);

		PortalReference[] frontSidePortals = new PortalReference[portals.size()];
		PortalReference[] backSidePortals = new PortalReference[portals.size()];
		for (int i = 0; i < portalsTag.tagCount(); i++) {
			NBTTagCompound portalTag  = portalsTag.getCompoundTagAt(i);

			int minX = portalTag.getInteger("X");
			int minY = portalTag.getInteger("Y");
			int minZ = portalTag.getInteger("Z");
			
			Axis axis = EnumFacing.getFront(portalTag.getByte("axis") * 2).getAxis();
			int width = portalTag.getInteger("width");
			int height = portalTag.getInteger("height");
			
			int sizeX = 1;
			int sizeY = 1;
			int sizeZ = 1;
			
			if (axis == Axis.X) sizeZ = width;
			else sizeX = width;
			if (axis == Axis.Y) sizeZ = height;
			else sizeY = height;
			
			
			Portal.create(this, new AxisAlignedBB(minX, minY, minZ, minX + sizeX, minY + sizeY, sizeZ), axis);

			frontSidePortals[i] = new PortalReference(portalTag.getCompoundTag("front"));
			backSidePortals[i] = new PortalReference(portalTag.getCompoundTag("back"));
		}
		
		Iterator<Portal> portalsIter = portals.iterator();
		for (int i = 0; portalsIter.hasNext(); i++) {
			Portal portal = portalsIter.next();
			frontSidePortals[i].to(this, portal.frontSide);
			backSidePortals[i].to(this, portal.backSide);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		for (int i = 0; i < 50; i++) {
			System.out.println("INTENSE SEXUAL GROAN AAAAAAAH MAKE ME FEEL LOVED SENPAI MORE INTENSE SEXUAL GROANS P%O@#%((G$*G#GHV#O*G$YOHEHIC@Y#*V@#($V#JEIQF$E");
		}
		NBTTagList portalsTag = new NBTTagList();
		for (Portal portal : this.portals) {
			NBTTagCompound portalTag = new NBTTagCompound();
			portalTag.setInteger("X", (int) portal.plane.minX);
			portalTag.setInteger("Y", (int) portal.plane.minY);
			portalTag.setInteger("Z", (int) portal.plane.minZ);
			
			double length = portal.plane.maxZ - portal.plane.minX;
			portalTag.setByte("axis", (byte) portal.axis.ordinal());
			portalTag.setByte("width", (byte) (portal.axis == Axis.X ? length : portal.plane.maxX - portal.plane.minX));
			portalTag.setByte("height", (byte) (portal.axis == Axis.X ? length : portal.plane.maxY - portal.plane.minY));
			
			NBTTagCompound frontSideTag = new NBTTagCompound();
			NBTTagCompound backSideTag = new NBTTagCompound();
			
			new PortalReference(portal.frontSide).to(frontSideTag);
			new PortalReference(portal.backSide).to(backSideTag);
			
			portalTag.setTag("front", frontSideTag);
			portalTag.setTag("back", backSideTag);
			portalsTag.appendTag(portalTag);
		}

		compound.setTag("portals", portalsTag);
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

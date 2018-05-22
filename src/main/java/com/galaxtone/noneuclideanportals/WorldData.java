package com.galaxtone.noneuclideanportals;

import java.util.Iterator;

import com.galaxtone.noneuclideanportals.Portal.Reference;
import com.galaxtone.noneuclideanportals.network.PacketPortal;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class WorldData extends WorldSavedData {

	public WorldData(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		Portal.list.removeAll(Portal.list);
		
		NBTTagList portalsTag = compound.getTagList("portals", 10);
		int tagCount = portalsTag.tagCount();
		
		int[] skipTable = new int[tagCount];
		int skipped = 0;
		
		for (int i = 0; i < tagCount; i++) {
			NBTTagCompound portalTag = portalsTag.getCompoundTagAt(i);
			
			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

			buffer.writeInt(portalTag.getInteger("X"));
			buffer.writeInt(portalTag.getInteger("Y"));
			buffer.writeInt(portalTag.getInteger("Z"));

			buffer.writeByte(portalTag.getByte("axis"));
			buffer.writeByte(portalTag.getByte("width"));
			buffer.writeByte(portalTag.getByte("height"));

			NBTTagCompound frontTag = portalTag.getCompoundTag("front");
			NBTTagCompound backTag = portalTag.getCompoundTag("front");

			buffer.writeByte(frontTag.getByte("direction"));
			buffer.writeByte(backTag.getByte("direction"));

			buffer.writeShort(frontTag.getShort("id"));
			buffer.writeShort(backTag.getShort("id"));
			
			skipTable[i] = skipped;
			
			try {
				Portal portal = PacketPortal.readPortal(buffer);
				
				portal.index = Portal.list.size();
				Portal.list.add(portal);
			} catch (Exception exception) {
				Main.logger.warn("Invalid portal NBT data (#%d): %s", i, exception.getMessage());
				
				skipTable[i] = -1;
				skipped++;
				
				continue;
			}
		}
		
		Iterator<Portal> iterator = Portal.list.iterator();
		while (iterator.hasNext()) {
			Portal portal = (Portal) iterator.next();
			
			if (portal.front.reference != null) {
				int frontId = portal.front.reference.index;
				if (skipTable[frontId] > 0 && frontId < tagCount) {
					Reference.create(portal.front, AxisDirection.POSITIVE, frontId - skipTable[frontId]);
				} else {
					portal.front.reference = null;
				}
			}
			
			if (portal.back.reference != null) {
				int backId = portal.back.reference.index;
				if (backId < tagCount) {
					Reference.create(portal.back, AxisDirection.NEGATIVE, backId - skipTable[backId]);
				} else {
					portal.back.reference = null;
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagList portalsTag = new NBTTagList();
		for (Portal portal : Portal.list) {
			PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
			PacketPortal.writePortal(buffer, portal);
			
			NBTTagCompound portalTag = new NBTTagCompound();
			portalTag.setInteger("X", buffer.readInt());
			portalTag.setInteger("Y", buffer.readInt());
			portalTag.setInteger("Z", buffer.readInt());
			
			portalTag.setByte("axis", buffer.readByte());
			portalTag.setByte("width", buffer.readByte());
			portalTag.setByte("height", buffer.readByte());
			
			NBTTagCompound frontTag = new NBTTagCompound();
			NBTTagCompound backTag = new NBTTagCompound();
			
			frontTag.setByte("direction", buffer.readByte());
			backTag.setByte("direction", buffer.readByte());
			
			frontTag.setShort("id", buffer.readShort());
			backTag.setShort("id", buffer.readShort());
			
			portalTag.setTag("front", frontTag);
			portalTag.setTag("back", backTag);
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
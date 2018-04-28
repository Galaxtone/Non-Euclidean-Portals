package com.galaxtone.noneuclideanportals.utils;

import com.galaxtone.noneuclideanportals.WorldData;
import com.galaxtone.noneuclideanportals.graphics.PortalSide;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.AxisDirection;

public class PortalReference {
	public final boolean front;
	public final short id;
	
	public PortalReference(NBTTagCompound compound) {
		this.front = compound.getBoolean("front");
		this.id = compound.getShort("id");
	}
	
	public PortalReference(PortalSide side) {
		this.front = side.direction == AxisDirection.POSITIVE;
		this.id = (short) (side.portal == null ? 0 : side.portal.id + 1);
	}

	public void to(NBTTagCompound compound) {
		compound.setBoolean("front", this.front);
		compound.setShort("id", this.id);
	}

	public void to(WorldData data, PortalSide side) {
		if (this.id == 0) return;
		side.destination = data.portals.get(this.id - 1).getSideFromDirection(this.front ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE);;
	}
}

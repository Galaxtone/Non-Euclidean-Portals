package com.galaxtone.noneuclideanportals.graphics;

import com.galaxtone.noneuclideanportals.WorldData;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Portal {

	public final short id;

	public final PortalSide frontSide;
	public final PortalSide backSide;

	public final AxisAlignedBB plane;
	public final Axis axis;

	private Portal(WorldData data, AxisAlignedBB plane, Axis axis) {
		this.plane = plane;
		this.axis = axis;
		
		this.frontSide = new PortalSide(AxisDirection.POSITIVE, this);
		this.backSide = new PortalSide(AxisDirection.NEGATIVE, this);
		
		this.id = (short) data.portals.size();
		data.portals.add(this);
	}

	public static void create(WorldData data) {
		Selection current = Selection.getCurrent();
		new Portal(data, current.plane, current.axis);
	}

	public static void create(WorldData data, AxisAlignedBB plane, Axis axis) {
		new Portal(data, plane, axis);
	}

	public PortalSide getSideFromDirection(AxisDirection direction) {
		return direction == AxisDirection.POSITIVE ? this.frontSide : this.backSide;
	}

	public PortalSide getSideFromEntity(Entity entity) {
		Vec3d pos = entity.getPositionVector();
		return this.axis == Axis.X && pos.xCoord > this.plane.minX + 0.5 ||
				this.axis == Axis.Y && pos.yCoord > this.plane.minY + 0.5 ||
				this.axis == Axis.Z && pos.zCoord > this.plane.minZ + 0.5 ? this.frontSide : this.backSide;
	}

	public void update(World world, PortalSide portal) {
		for (Entity entity : world.getEntitiesWithinAABB(Entity.class, portal.scanPlane)) {
			if (entity instanceof EntityPlayerMP && !world.isRemote) continue;
			
		}
	}
}

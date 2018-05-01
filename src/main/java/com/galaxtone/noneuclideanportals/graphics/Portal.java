package com.galaxtone.noneuclideanportals.graphics;

import com.galaxtone.noneuclideanportals.Main;
import com.galaxtone.noneuclideanportals.RenderHandler;
import com.galaxtone.noneuclideanportals.WorldData;
import com.galaxtone.noneuclideanportals.utils.AABBHelper;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Portal {

	public final short id;

	public final PortalSide frontSide;
	public final PortalSide backSide;

	public final AxisAlignedBB plane;
	public final Axis axis;

	public static PortalSide getClosestHoveredPortal() {
		Entity renderEntity = RenderHandler.getRenderEntity();
		Vec3d pos = renderEntity.getPositionVector();
		
		double squareThresold = renderEntity.rayTrace(Main.config.getReachDistance(), RenderHandler.getPartialTicks()).hitVec.squareDistanceTo(pos);
		
		WorldData data = WorldData.get(renderEntity.worldObj);
		PortalSide closestSide = null;
		for (Portal portal : data.portals) {
			PortalSide side = portal.getSideFromPosition(pos);
			RayTraceResult ray = AABBHelper.intersectsRay(renderEntity, Main.config.getReachDistance(), side.flatPlane);
			if (ray == null) continue;
			
			double squareDistance = ray.hitVec.squareDistanceTo(pos);
			if (squareDistance < squareThresold) {
				squareThresold = squareDistance;
				closestSide  = side;
			}
		}
		
		return closestSide;
	}

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

	public PortalSide getSideFromPosition(Vec3d pos) {
		return this.axis == Axis.X && pos.xCoord > this.plane.minX + 0.5 ||
				this.axis == Axis.Z && pos.zCoord > this.plane.minZ + 0.5 ||
				this.axis == Axis.Y && pos.yCoord > this.plane.minY + 0.5 ? this.frontSide : this.backSide;
	}

	public void update(World world, PortalSide portal) {
		for (Entity entity : world.getEntitiesWithinAABB(Entity.class, portal.scanPlane)) {
			if (entity instanceof EntityPlayerMP && !world.isRemote) continue;
			
		}
	}
}

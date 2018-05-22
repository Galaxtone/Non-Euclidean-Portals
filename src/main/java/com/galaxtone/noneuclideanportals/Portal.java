package com.galaxtone.noneuclideanportals;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.galaxtone.noneuclideanportals.graphics.RenderHandler;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public final class Portal {

	public static List<Portal> list = Collections.synchronizedList(new LinkedList<Portal>());
	public int index = -1;

	public final Side front;
	public final Side back;

	public final BlockPos pos;
	public final Axis axis;
	public final byte width;
	public final byte height;

	public Portal(AxisAlignedBB plane, Axis axis) {
		
		this.pos = new BlockPos(plane.minX, plane.minY, plane.minZ);
		this.axis = axis;

		byte sizeX = (byte) (plane.maxX - plane.minX);
		byte sizeY = (byte) (plane.maxY - plane.minY);
		byte sizeZ = (byte) (plane.maxZ - plane.minZ);

		if (sizeX > 1 && sizeY > 1 && sizeZ > 1) throw new IllegalArgumentException("Bounding box is not a flat plane.");
		if (sizeX > 16 || sizeY > 16 || sizeZ > 16) throw new IllegalArgumentException("Plane exceeds maximum size constraints.");

		this.width = axis == Axis.X ? sizeZ : sizeX; // invalid
		this.height = axis == Axis.Y ? sizeZ : sizeY; // invalid

		this.front = new Side(this, AxisDirection.POSITIVE, plane);
		this.back = new Side(this, AxisDirection.NEGATIVE, plane);
	}

	public static Portal from(BlockPos pos, Axis axis, int width, int height) {
		int minX = pos.getX();
		int minY = pos.getY();
		int minZ = pos.getZ();
		
		int sizeX = 1;
		int sizeY = 1;
		int sizeZ = 1;
		
		if (axis == Axis.X) sizeZ = width;
		else sizeX = width;
		
		if (axis == Axis.Y) sizeZ = width;
		else sizeY = width;
		
		return new Portal(new AxisAlignedBB(minX, minY, minZ, minX + sizeX, minY + sizeY, minZ + sizeZ), axis);
	}

	public static Side getClosestSide() {
		Entity player = RenderHandler.getRenderEntity();
		
		double reachDistance = Main.config.getReachDistance();
		float partialTicks = RenderHandler.getPartialTicks();
		
		Vec3d rotation = player.getLook(partialTicks);
		Vec3d start = player.getPositionEyes(partialTicks);
		Vec3d end = rotation.scale(reachDistance).add(start);
		
		RayTraceResult ray = player.worldObj.rayTraceBlocks(start, end, false, false, true);
		double threshold = (ray == null ? reachDistance * reachDistance : ray.hitVec.squareDistanceTo(start));
		
		Side closestSide = null;
		
		for (Portal portal : Portal.list) {
			Side side = portal.getSide(start);
			RayTraceResult portalRay = side.plane.calculateIntercept(start, end);
			if (portalRay == null) continue;
			
			double distance = ray.hitVec.squareDistanceTo(start);
			if (distance < threshold) {
				threshold = distance;
				closestSide  = side;
			}
		}
		
		return closestSide;
	}

	/*public void update(World world, Side portal) {
		for (Entity entity : world.getEntitiesWithinAABB(Entity.class, portal.scanPlane)) {
			if (entity instanceof EntityPlayerMP && !world.isRemote) continue;
			
		}
	}*/

	public Side getSide(Vec3d pos) {
		return this.axis == Axis.X && pos.xCoord > this.pos.getX() + 0.5 ||
				this.axis == Axis.Z && pos.zCoord > this.pos.getY() + 0.5 ||
				this.axis == Axis.Y && pos.yCoord > this.pos.getZ() + 0.5 ? this.front : this.back;
	}

	public Side getSide(AxisDirection direction) {
		return direction == AxisDirection.POSITIVE ? this.front : this.back;
	}

	public final class Side {

		public final AxisAlignedBB plane;

		public final AxisDirection direction;
		public final Portal portal;

		public Side destination;
		public Reference reference;

		private Side(Portal portal, AxisDirection direction, AxisAlignedBB plane) {
			double offset = -direction.getOffset() * 0.5;
			
			this.direction = direction;
			this.portal = portal;
			
			switch (portal.axis) {
				case X:
					this.plane = plane.expand(-0.5, 0, 0).offset(offset, 0, 0);
					break;
				case Y:
					this.plane = plane.expand(0, -0.5, 0).offset(0, offset, 0);
					break;
				case Z:
					this.plane = plane.expand(0, 0, -0.5).offset(0, 0, offset);
					break;
				default:
					this.plane = null;
			}
		}
	}

	public final static class Reference {

		public final Side parent;
		public final AxisDirection direction;
		public final int index;

		private Reference(Side side) {
			this.parent = side;
			side.reference = this;
			
			if (side.destination == null) throw new IllegalArgumentException("Cannot create reference to null destination");
			
			this.direction = side.destination.direction;
			this.index = side.destination.portal.index;
		}

		private Reference(Side side, AxisDirection direction, int index) {
			this.parent = side;
			side.reference = this;
			
			this.direction = direction;
			this.index = index;
		}

		public static Reference create(Side side, AxisDirection direction, int id) {
			return new Reference(side, direction, id);
		}
	}
}

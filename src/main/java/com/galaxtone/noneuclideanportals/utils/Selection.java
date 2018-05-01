package com.galaxtone.noneuclideanportals.utils;

import javax.annotation.Nonnull;

import com.galaxtone.noneuclideanportals.Main;
import com.galaxtone.noneuclideanportals.RenderHandler;
import com.galaxtone.noneuclideanportals.graphics.Portal;
import com.galaxtone.noneuclideanportals.graphics.PortalSide;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Selection {

	public enum Type {
		NONE,
		PORTAL,
		BLOCK
	}

	private static final int limit = 8;

	private static Type type = Type.NONE;

	private static ItemStack currentItem;
	private static BlockPos primaryPos;
	private static BlockPos secondaryPos;
	private static Axis prioritizedAxis;

	private static PortalSide portal;

	private static Selection current;

	public final AxisAlignedBB plane;
	public final Axis axis;
	
	private Selection(@Nonnull AxisAlignedBB plane, @Nonnull Axis axis) {
		this.plane = plane;
		this.axis = axis;
	}

	public static void stop() {
		currentItem = null;
		primaryPos = null;
		secondaryPos = null;
		prioritizedAxis = null;
	}

	public static void updatePortalSelection() {
		portal = Portal.getClosestHoveredPortal();
	}

	public static void updateBlockSelection() {
		RayTraceResult ray = RenderHandler.getRenderEntity().rayTrace(Main.config.getReachDistance(), RenderHandler.getPartialTicks());
		BlockPos pos = ray.getBlockPos();
		Axis sideAxis = ray.sideHit.getAxis();
		
		if (pos == secondaryPos && sideAxis == prioritizedAxis) return;
		secondaryPos = pos;
		prioritizedAxis = sideAxis;
		
		int minX = Math.min(primaryPos.getX(), secondaryPos.getX());
		int minY = Math.min(primaryPos.getY(), secondaryPos.getY());
		int minZ = Math.min(primaryPos.getZ(), secondaryPos.getZ());
		int maxX = Math.max(primaryPos.getX(), secondaryPos.getX()) + 1;
		int maxY = Math.max(primaryPos.getY(), secondaryPos.getY()) + 1;
		int maxZ = Math.max(primaryPos.getZ(), secondaryPos.getZ()) + 1;
		
		int width = maxX - minX;
		int height = maxY - minY;
		int length = maxZ - minZ;
		
		int widthOffset = limit - width;
		int heightOffset = limit - height;
		int lengthOffset = limit - length;
		
		if (width > limit) {
			if (minX == primaryPos.getX()) maxX += widthOffset;
			else minX -= widthOffset;
		}
		if (height > limit) {
			if (minY == primaryPos.getY()) maxY += heightOffset;
			else minY -= heightOffset;
		}
		if (length > limit) {
			if (minZ == primaryPos.getZ()) maxZ += lengthOffset;
			else minZ -= lengthOffset;
		}
		
		int least = Math.min(Math.min(width, length), height);
		
		Axis axis = prioritizedAxis;
		
		if (prioritizedAxis == Axis.X && width == least) {
			if (minX == primaryPos.getX()) maxX -= width - 1;
			else minX += width - 1;
		} else if (prioritizedAxis == Axis.Z && length == least) {
			if (minZ == primaryPos.getZ()) maxZ -= length - 1;
			else minZ += length - 1;
		} else if (prioritizedAxis == Axis.Y && height == least) {
			if (minY == primaryPos.getY()) maxY -= height - 1;
			else minY += height - 1;
		} else if (width == least) {
			if (minX == primaryPos.getX()) maxX -= width - 1;
			else minX += width - 1;
			axis = Axis.X;
		} else if (length == least) {
			if (minZ == primaryPos.getZ()) maxZ -= length - 1;
			else minZ += length - 1;
			axis = Axis.Z;
		} else {
			if (minY == primaryPos.getY()) maxY -= height - 1;
			else minY += height - 1;
			axis = Axis.Y;
		}
		
		current = new Selection(new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ), axis);
	}

	public static Type getType() {
		return type;
	}

	public static void setType(@Nonnull Type newType) {
		type = newType;
	}

	public static ItemStack getCurrentItem() {
		return currentItem;
	}

	public static Selection getCurrent() {
		return current;
	}

	public static PortalSide getPortal() {
		return portal;
	}

	public static void start(ItemStack heldItem, BlockPos pos) {
		currentItem = heldItem;
		primaryPos = pos;
	}
}

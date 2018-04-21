package com.galaxtone.noneuclideanportals.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Selection {

	private static final int limit = 8;

	public ItemStack stack;
	public BlockPos primaryPos;
	public BlockPos secondaryPos;
	public Axis prioritizedAxis;

	public AxisAlignedBB plane;
	public Axis axis;

	public static Selection instance = new Selection();
	
	private Selection() {
		this.plane = null;
		this.axis = null;
	}

	public void cancel() {
		this.stack = null;
		this.primaryPos = null;
		this.secondaryPos = null;
	}

	public void recalculate() {
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
		this.axis = prioritizedAxis;
		if (prioritizedAxis == Axis.X && width == least) {
			if (minX == primaryPos.getX()) maxX -= width;
			else minX += width;
		} else if (prioritizedAxis == Axis.Z && length == least) {
			if (minZ == primaryPos.getZ()) maxZ -= length;
			else minZ += length;
		} else if (prioritizedAxis == Axis.Y && height == least) {
			if (minY == primaryPos.getY()) maxY -= height;
			else minY += height;
		} else if (width == least) {
			if (minX == primaryPos.getX()) maxX -= width;
			else minX += width;
			axis = Axis.X;
		} else if (length == least) {
			if (minZ == primaryPos.getZ()) maxZ -= length;
			else minZ += length;
			axis = Axis.Z;
		} else {
			if (minY == primaryPos.getY()) maxY -= height;
			else minY += height;
			axis = Axis.Y;
		}
		
		this.plane = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
}

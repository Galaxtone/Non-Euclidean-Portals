package com.galaxtone.noneuclideanportals.utils;

import com.galaxtone.noneuclideanportals.RenderHandler;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AABBHelper {
    
	public static RayTraceResult intersectsRay(Entity entity, double reachDistance, AxisAlignedBB plane) {
		float partialTicks = RenderHandler.getPartialTicks();
		
		Vec3d rotation = entity.getLook(partialTicks);
		Vec3d start = entity.getPositionEyes(partialTicks);
		Vec3d end = rotation.scale(reachDistance).add(start);
		
		return plane.calculateIntercept(start, end);
	}
}

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

	public World world;

	public final PortalSide frontSide;
	public final PortalSide backSide;

	public final AxisAlignedBB plane;
	public final Axis axis;

	public Portal(World world, Selection selection) {
		this.world = world;
		
		this.plane = selection.plane;
		this.axis = selection.axis;
		
		this.frontSide = new PortalSide(AxisDirection.POSITIVE, this);
		this.backSide = new PortalSide(AxisDirection.NEGATIVE, this);
		
		WorldData data = WorldData.get(world);
		this.id = (short) data.portals.size();
		data.portals.add(this);
	}

	public PortalSide getSideFromEntity(Entity entity) {
		Vec3d pos = entity.getPositionVector();
		if (this.axis == Axis.X && pos.xCoord < this.plane.minX + 0.5 ||
				this.axis == Axis.Y && pos.yCoord < this.plane.minY + 0.5 ||
				this.axis == Axis.Z && pos.zCoord < this.plane.minZ + 0.5) {
				return this.backSide;
		}
		return this.frontSide;
	}

	public void update(PortalSide portal) {
		for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, portal.scanPlane)) {
			if (entity instanceof EntityPlayerMP && !this.world.isRemote) continue;
			
		}
	}
}

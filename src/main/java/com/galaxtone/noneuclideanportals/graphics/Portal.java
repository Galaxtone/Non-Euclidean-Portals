package com.galaxtone.noneuclideanportals.graphics;

import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Portal {

	public final int id;

	public World world;

	public final PortalSide frontSide;
	public final PortalSide backSide;

	public final AxisAlignedBB plane;
	public final Axis axis;

	public Portal(World world, Selection selection) {
		if (selection.plane == null || selection.axis == null) throw new IllegalArgumentException("Selection's values cannot be null");
		this.world = world;
		
		this.plane = selection.plane;
		this.axis = selection.axis;
		
		this.frontSide = new PortalSide(AxisDirection.POSITIVE, this);
		this.backSide = new PortalSide(AxisDirection.NEGATIVE, this);
	}

	public PortalSide getSideFromDirection(AxisDirection direction) {
		if (direction == AxisDirection.POSITIVE) return frontSide;
		else if (direction == AxisDirection.NEGATIVE) return backSide;
		return null;
	}

	@SideOnly(Side.CLIENT)
	public void render(PortalSide portal, float partialTicks) {
		ICamera camera = new Frustum();
		if (!camera.isBoundingBoxInFrustum(portal.flatPlane)) return;
		
		// TODO https://cdn.discordapp.com/attachments/294069306608582656/436437528728567809/unknown.png
	}

	public void update(PortalSide portal) {
		for (Entity entity : this.world.getEntitiesWithinAABB(Entity.class, portal.scanPlane)) {
			if (entity instanceof EntityPlayerMP && !this.world.isRemote) continue;
			
		}
	}
}

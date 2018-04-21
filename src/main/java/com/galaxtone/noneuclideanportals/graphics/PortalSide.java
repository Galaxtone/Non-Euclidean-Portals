package com.galaxtone.noneuclideanportals.graphics;

import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class PortalSide {

	public final AxisDirection direction;
	public final Portal portal;
	public PortalSide destination;

	public final AxisAlignedBB portalPlane;
	public final AxisAlignedBB flatPlane;
	public final AxisAlignedBB scanPlane;

	public PortalSide(AxisDirection direction, Portal portal) {
		this.direction = direction;
		this.portal = portal;
		
		this.portalPlane = portal.plane;
		if (portal.axis == Axis.X) {
			this.flatPlane = this.portalPlane.expand(-0.5, 0, 0).offset(-direction.getOffset()/2, 0, 0);
			this.scanPlane = this.portalPlane.addCoord(3, 0, 0);
		} else if (portal.axis == Axis.Y) {
			this.flatPlane = this.portalPlane.expand(0, -0.5, 0).offset(-direction.getOffset()/2, 0, 0);
			this.scanPlane = this.portalPlane.addCoord(0, 3, 0);
		} else if (portal.axis == Axis.Z) {
			this.flatPlane = this.portalPlane.expand(0, 0, -0.5).offset(-direction.getOffset()/2, 0, 0);
			this.scanPlane = this.portalPlane.addCoord(0, 0, 3);
		} else throw new IllegalArgumentException("Portal axis is null, If you see this please report it to the mod author.");
	}
}
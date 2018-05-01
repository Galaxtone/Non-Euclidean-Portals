package com.galaxtone.noneuclideanportals;

import org.lwjgl.opengl.GL11;

import com.galaxtone.noneuclideanportals.graphics.Portal;
import com.galaxtone.noneuclideanportals.graphics.PortalSide;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHandler {

	private static final Tessellator tessellator = Tessellator.getInstance();
	private static final VertexBuffer buffer = tessellator.getBuffer();

	//private static final FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);

	private static float partialTicks;
	private static Entity renderEntity;
	private static Vec3d offset;

	public static void update(float newPartialTicks) {
		partialTicks = newPartialTicks;
		renderEntity = Main.minecraft.getRenderViewEntity();
		offset = new Vec3d(
				renderEntity.prevPosX + (renderEntity.posX - renderEntity.prevPosX) * partialTicks,
				renderEntity.prevPosY + (renderEntity.posY - renderEntity.prevPosY) * partialTicks,
				renderEntity.prevPosZ + (renderEntity.posZ - renderEntity.prevPosZ) * partialTicks);
	}

	public static float getPartialTicks() {
		return partialTicks;
	}

	public static Entity getRenderEntity() {
		return renderEntity;
	}

	private static void drawQuad(Axis axis, AxisAlignedBB plane) {
		plane = plane.offset(-offset.xCoord, -offset.yCoord, -offset.zCoord);
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		if (axis == Axis.X) {
			buffer.pos(plane.minX, plane.minY, plane.minZ).endVertex();
			buffer.pos(plane.minX, plane.minY, plane.maxZ).endVertex();
			buffer.pos(plane.minX, plane.maxY, plane.maxZ).endVertex();
			buffer.pos(plane.minX, plane.maxY, plane.minZ).endVertex();
		} else if (axis == Axis.Z) {
			buffer.pos(plane.minX, plane.minY, plane.minZ).endVertex();
			buffer.pos(plane.maxX, plane.minY, plane.minZ).endVertex();
			buffer.pos(plane.maxX, plane.maxY, plane.minZ).endVertex();
			buffer.pos(plane.minX, plane.maxY, plane.minZ).endVertex();
		} else if (axis == Axis.Y) {
			buffer.pos(plane.minX, plane.minY, plane.minZ).endVertex();
			buffer.pos(plane.maxX, plane.minY, plane.minZ).endVertex();
			buffer.pos(plane.maxX, plane.minY, plane.maxZ).endVertex();
			buffer.pos(plane.minX, plane.minY, plane.maxZ).endVertex();
		}
		tessellator.draw();
	}

	private static void drawQuad(Axis axis, AxisAlignedBB plane, double offset) {
		
		if (axis == Axis.X) plane = plane.offset(offset, 0, 0);
		else if (axis == Axis.Y) plane = plane.offset(0, offset, 0);
		else if (axis == Axis.Z) plane = plane.offset(0, 0, offset);
		
		drawQuad(axis, plane);
	}

	private static void drawBoxOutline(AxisAlignedBB box) {
		box = box.offset(-offset.xCoord, -offset.yCoord, -offset.zCoord);
		
		buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		buffer.pos(box.minX, box.minY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.minY, box.minZ).endVertex();
		tessellator.draw();
		
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(box.minX, box.minY, box.minZ).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).endVertex();
		tessellator.draw();

		buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		buffer.pos(box.minX, box.maxY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).endVertex();
		tessellator.draw();
	}

	public static void renderBlockSelection() {
		Selection current = Selection.getCurrent();
		
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		GlStateManager.color(0.8F, 0.0F, 0.8F, 0.3F);

		GlStateManager.disableCull();
		drawQuad(current.axis, current.plane, 0.5);
		GlStateManager.enableCull();
		
		drawBoxOutline(current.plane);
		
		GlStateManager.enableTexture2D();
	}

	public static void renderPortals(boolean flag) {
		WorldData data = WorldData.get(renderEntity.worldObj);
		Vec3d pos = ActiveRenderInfo.projectViewFromEntity(renderEntity, partialTicks);
		for (Portal portal : data.portals) {
			PortalSide side = portal.getSideFromPosition(pos);
			
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			
			if (flag && side == Selection.getPortal()) {
				GlStateManager.color(0.25F, 0.25F, 0.25F);
			} else {
				GlStateManager.color(0F, 0F, 0F);
			}
			
			drawQuad(portal.axis, side.flatPlane);
			
			GlStateManager.enableTexture2D();
			
			/*GlStateManager.colorMask(false, false, false, false);
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			
			GL11.glStencilFunc(GL11.GL_NOTEQUAL, recursionLevel, mask);
			GL11.glStencilOp(GL11.GL_INCR, GL11.GL_KEEP, GL11.GL_KEEP);
			GL11.glStencilMask(0xFF);
			
			GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixBuffer);
			
			Matrix4f projMat = (Matrix4f) new Matrix4f().load(projectionMatrixBuffer.asReadOnlyBuffer());
			
			Quaternion clip = new Quaternion();
			Quaternion quat = new Quaternion();
			quat.x = (Math.signum(clip.x) + projMat.m20) / projMat.m00;
			quat.y = (Math.signum(clip.y) + projMat.m21) / projMat.m11;
			quat.z = -1.0F;
			quat.w = (1.0F + projMat.m22) / projMat.m23;
			
			float dot = 2f / Quaternion.dot(clip, quat);
			projMat.m02 = clip.x * dot;
			projMat.m12 = clip.y * dot;
			projMat.m22 = clip.z * dot + 1F;
			projMat.m32 = 1f;
		    
			projMat.store(projectionMatrixBuffer);
			
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GL11.glLoadMatrix(projectionMatrixBuffer.asReadOnlyBuffer());
			// TODO https://cdn.discordapp.com/attachments/294069306608582656/436437528728567809/unknown.png
			// Draw 5 quads based on portal plane, axis and portal side direction.*/
		}
	}
}

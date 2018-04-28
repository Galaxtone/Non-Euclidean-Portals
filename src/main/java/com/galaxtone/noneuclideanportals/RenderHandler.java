package com.galaxtone.noneuclideanportals;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.galaxtone.noneuclideanportals.graphics.Portal;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHandler {

	private static final Tessellator tessellator = Tessellator.getInstance();
	private static final VertexBuffer buffer = tessellator.getBuffer();

	public static final FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);

	private static Entity renderEntity;
	private static Vec3d offset;

	public static void update(float partialTick) {
		renderEntity = Main.minecraft.getRenderViewEntity();
		offset = new Vec3d(
				renderEntity.prevPosX + (renderEntity.posX - renderEntity.prevPosX) * partialTick,
				renderEntity.prevPosY + (renderEntity.posY - renderEntity.prevPosY) * partialTick,
				renderEntity.prevPosZ + (renderEntity.posZ - renderEntity.prevPosZ) * partialTick);
	}

	private static RayTraceResult ray;

	public static void renderWandSelection() {
		RayTraceResult mouseRay = Main.minecraft.objectMouseOver;
		if (mouseRay.typeOfHit == Type.BLOCK) ray = mouseRay;
		if (ray == null) return;
		
		Selection.update(ray.getBlockPos(), ray.sideHit.getAxis());

		Selection current = Selection.getCurrent();
		
		double x = current.plane.minX - offset.xCoord;
		double y = current.plane.minY - offset.yCoord;
		double z = current.plane.minZ - offset.zCoord;
		double x2 = current.plane.maxX - offset.xCoord;
		double y2 = current.plane.maxY - offset.yCoord;
		double z2 = current.plane.maxZ - offset.zCoord;
		
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		
		GlStateManager.color(0.8F, 0.0F, 0.8F, 0.3F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		if (current.axis == Axis.X) {
			buffer.pos(x + 0.5, y, z2).endVertex();
			buffer.pos(x + 0.5, y2, z2).endVertex();
			buffer.pos(x + 0.5, y2, z).endVertex();
			buffer.pos(x + 0.5, y, z).endVertex();
			buffer.pos(x2 + 0.5, y, z).endVertex();
			buffer.pos(x2 + 0.5, y2, z).endVertex();
			buffer.pos(x2 + 0.5, y2, z2).endVertex();
			buffer.pos(x2 + 0.5, y, z2).endVertex();
		} else if (current.axis == Axis.Z) {
			buffer.pos(x2, y, z2 + 0.5).endVertex();
			buffer.pos(x2, y2, z2 + 0.5).endVertex();
			buffer.pos(x, y2, z2 + 0.5).endVertex();
			buffer.pos(x, y, z2 + 0.5).endVertex();
			buffer.pos(x, y, z + 0.5).endVertex();
			buffer.pos(x, y2, z + 0.5).endVertex();
			buffer.pos(x2, y2, z + 0.5).endVertex();
			buffer.pos(x2, y, z + 0.5).endVertex();
		} else if (current.axis == Axis.Y) {
			buffer.pos(x, y + 0.5, z).endVertex();
			buffer.pos(x2, y + 0.5, z).endVertex();
			buffer.pos(x2, y + 0.5, z2).endVertex();
			buffer.pos(x, y + 0.5, z2).endVertex();
			buffer.pos(x, y2 + 0.5, z2).endVertex();
			buffer.pos(x2, y2 + 0.5, z2).endVertex();
			buffer.pos(x2, y2 + 0.5, z).endVertex();
			buffer.pos(x, y2 + 0.5, z).endVertex();
		}
		tessellator.draw();
		
		buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		buffer.pos(x, y, z).endVertex();
		buffer.pos(x2, y, z).endVertex();
		buffer.pos(x2, y, z2).endVertex();
		buffer.pos(x, y, z2).endVertex();
		buffer.pos(x, y2, z2).endVertex();
		buffer.pos(x, y, z).endVertex();
		buffer.pos(x2, y, z).endVertex();
		buffer.pos(x, y, z).endVertex();
		buffer.pos(x, y, z2).endVertex();
		buffer.pos(x2, y, z2).endVertex();
		buffer.pos(x2, y, z).endVertex();
		buffer.pos(x2, y, z2).endVertex();
		buffer.pos(x, y, z2).endVertex();
		buffer.pos(x, y2, z).endVertex();
		buffer.pos(x2, y2, z).endVertex();
		buffer.pos(x, y2, z).endVertex();
		buffer.pos(x, y2, z2).endVertex();
		buffer.pos(x2, y2, z2).endVertex();
		buffer.pos(x2, y2, z).endVertex();
		buffer.pos(x2, y2, z2).endVertex();
		buffer.pos(x, y2, z2).endVertex();
		tessellator.draw();
		
		GlStateManager.enableTexture2D();
	}

	public static void renderPortals() { /* Yo, this is not finished therefore not functional */
		WorldData data = WorldData.get(renderEntity.worldObj);
		//System.out.println(data.portals.size());
		for (Portal portal : data.portals) {
			//PortalSide side = portal.getSideFromEntity(renderEntity);
			//System.out.println(portal.plane);
			//if (side.destination == null) continue;
			
			double x = portal.plane.minX - offset.xCoord;
			double y = portal.plane.minY - offset.yCoord;
			double z = portal.plane.minZ - offset.zCoord;
			double x2 = portal.plane.maxX - offset.xCoord;
			double y2 = portal.plane.maxY - offset.yCoord;
			double z2 = portal.plane.maxZ - offset.zCoord;
			
			GlStateManager.disableCull();
			GlStateManager.disableTexture2D();
			
			GlStateManager.color(0F, 0F, 0F);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
			if (portal.axis == Axis.X) {
				buffer.pos(x, y, z2).endVertex();
				buffer.pos(x, y2, z2).endVertex();
				buffer.pos(x, y2, z).endVertex();
				buffer.pos(x, y, z).endVertex();
			} else if (portal.axis == Axis.Z) {
				buffer.pos(x2, y, z2).endVertex();
				buffer.pos(x2, y2, z2).endVertex();
				buffer.pos(x, y2, z2).endVertex();
				buffer.pos(x, y, z2).endVertex();
			} else if (portal.axis == Axis.Y) {
				buffer.pos(x, y, z).endVertex();
				buffer.pos(x2, y, z).endVertex();
				buffer.pos(x2, y, z2).endVertex();
				buffer.pos(x, y, z2).endVertex();
			}
			tessellator.draw();
			
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

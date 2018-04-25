package com.galaxtone.noneuclideanportals;

import org.lwjgl.opengl.GL11;

import com.galaxtone.noneuclideanportals.utils.Selection;

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

	@SuppressWarnings("unused")
		if (true) return;
		
		GlStateManager.disableTexture2D();
		
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		
		GL11.glStencilMask(0xFF);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
		GL11.glStencilMask(0xFF);
		
		GL11.glColorMask(false, false, false, false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(-0.5, 1, 4);
		GL11.glVertex3d(-0.5, 2, 4);
		GL11.glVertex3d(0.5, 2, 4);
		GL11.glVertex3d(0.5, 1, 4);
		GL11.glEnd();
		
		GL11.glColorMask(true, true, true, true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
		
		GL11.glColor3d(1.0, 0.0, 0.0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(-1 - offsetX, 200 - offsetY, 5 - offsetZ);
		GL11.glVertex3d(-1 - offsetX, 203 - offsetY, 5 - offsetZ);
		GL11.glVertex3d(1 - offsetX, 203 - offsetY, 5 - offsetZ);
		GL11.glVertex3d(1 - offsetX, 200 - offsetY, 5 - offsetZ);
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GlStateManager.enableTexture2D();
	}
}

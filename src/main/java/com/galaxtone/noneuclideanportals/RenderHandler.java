package com.galaxtone.noneuclideanportals;

import org.lwjgl.opengl.GL11;

import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHandler {

	private static final Tessellator tessellator = Tessellator.getInstance();
	private static final VertexBuffer buffer = tessellator.getBuffer();

	private static float offsetX;
	private static float offsetY;
	private static float offsetZ;

	public static void calculateOffsets(float partialTicks) {
		Entity entity = Main.minecraft.getRenderViewEntity();
		offsetX = (float) (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks);
		offsetY = (float) (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
		offsetZ = (float) (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
	}

	private static RayTraceResult ray;

	public static void renderWandSelection() {
		RayTraceResult mouseRay = Main.minecraft.objectMouseOver;
		if (mouseRay.typeOfHit == Type.BLOCK) ray = mouseRay;
		if (ray == null) return;
		
		Axis axis = ray.sideHit.getAxis();
		BlockPos pos = ray.getBlockPos();
		if (axis != Selection.instance.prioritizedAxis || !pos.equals(Selection.instance.secondaryPos)) {
			Selection.instance.prioritizedAxis = axis;
			Selection.instance.secondaryPos = pos;
			Selection.instance.recalculate();
		}

		double x = Selection.instance.plane.minX - offsetX;
		double y = Selection.instance.plane.minY - offsetY;
		double z = Selection.instance.plane.minZ - offsetZ;
		double x2 = Selection.instance.plane.maxX - offsetX;
		double y2 = Selection.instance.plane.maxY - offsetY;
		double z2 = Selection.instance.plane.maxZ - offsetZ;
		
		GlStateManager.enableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();
		
		GlStateManager.color(0.8F, 0.0F, 0.8F, 0.3F);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		if (Selection.instance.axis == Axis.X) {
			buffer.pos(x + 0.5, y, z2).endVertex();
			buffer.pos(x + 0.5, y2, z2).endVertex();
			buffer.pos(x + 0.5, y2, z).endVertex();
			buffer.pos(x + 0.5, y, z).endVertex();
			buffer.pos(x2 + 0.5, y, z).endVertex();
			buffer.pos(x2 + 0.5, y2, z).endVertex();
			buffer.pos(x2 + 0.5, y2, z2).endVertex();
			buffer.pos(x2 + 0.5, y, z2).endVertex();
		} else if (Selection.instance.axis == Axis.Z) {
			buffer.pos(x2, y, z2 + 0.5).endVertex();
			buffer.pos(x2, y2, z2 + 0.5).endVertex();
			buffer.pos(x, y2, z2 + 0.5).endVertex();
			buffer.pos(x, y, z2 + 0.5).endVertex();
			buffer.pos(x, y, z + 0.5).endVertex();
			buffer.pos(x, y2, z + 0.5).endVertex();
			buffer.pos(x2, y2, z + 0.5).endVertex();
			buffer.pos(x2, y, z + 0.5).endVertex();
		} else if (Selection.instance.axis == Axis.Y) {
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
	public static void renderStencilStuff() {
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

package com.galaxtone.noneuclideanportals;

import com.galaxtone.noneuclideanportals.graphics.RenderHandler;
import com.galaxtone.noneuclideanportals.items.ItemWand;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventHandler {

	@SubscribeEvent
	public static void configChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(Main.modid)) {
			Main.config.reload();
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(ItemWand.instance);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(ItemWand.instance, 0, new ModelResourceLocation(ItemWand.instance.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		RenderHandler.update(event.getPartialTicks());
		
		ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		boolean flag = heldItem != null && heldItem.getItem() == ItemWand.instance;
		
		if (flag) Selection.updatePortalSelection();
		RenderHandler.renderPortals(flag);
		
		ItemStack currentItem = Selection.getCurrentItem();
		if (currentItem != null) {
			if (heldItem == currentItem) {
				Selection.updateBlockSelection();
				RenderHandler.renderBlockSelection();
			} else Selection.stop();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderPlayer(RenderLivingEvent.Pre<AbstractClientPlayer> event) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180F, 1F, 0F, 0F);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderPlayer(RenderLivingEvent.Post<AbstractClientPlayer> event) {
		GlStateManager.popMatrix();
	}
}
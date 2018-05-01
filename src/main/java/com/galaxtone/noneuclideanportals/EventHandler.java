package com.galaxtone.noneuclideanportals;

import com.galaxtone.noneuclideanportals.items.ItemBase;
import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
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

		for (ItemBase item : Register.itemList) registry.register(item);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for (ItemBase item : Register.itemList) ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		RenderHandler.update(event.getPartialTicks());
		
		ItemStack heldItem = Main.minecraft.thePlayer.getHeldItemMainhand();
		ItemStack currentItem = Selection.getCurrentItem();
		if (currentItem != null) {
			if (heldItem == currentItem) RenderHandler.renderWandSelection();
			else Selection.stop();
		}
		
		RenderHandler.renderPortals();
	}
}
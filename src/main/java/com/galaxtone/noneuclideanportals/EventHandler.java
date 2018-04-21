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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventHandler {

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
		RenderHandler.calculateOffsets(event.getPartialTicks());
		
		ItemStack stack = Main.minecraft.thePlayer.getHeldItemMainhand();
		if (stack == Selection.instance.stack) {
			if (stack != null) RenderHandler.renderWandSelection();
		} else if (Selection.instance.stack != null) Selection.instance.stack = null;
	}
}
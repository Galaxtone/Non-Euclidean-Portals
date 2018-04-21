package com.galaxtone.noneuclideanportals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Main.modid, name = Main.name, version = Main.version)
public class Main {

	public static final Minecraft minecraft = Minecraft.getMinecraft();

	public static final String modid = "noneuclideanportals";
	public static final String name = "Non-Euclidean Portals";
	public static final String version = "0.3";

	public static final CreativeTabs creativeTab = new CreativeTabs(modid) {
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Register.wandItem;
		}
	};

	//public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(Main.modid);

	@Instance
	public static Main instance = new Main();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void init(FMLPreInitializationEvent event) {
		Framebuffer buffer = minecraft.getFramebuffer();
		if (!buffer.isStencilEnabled()) buffer.enableStencil();
	}
}
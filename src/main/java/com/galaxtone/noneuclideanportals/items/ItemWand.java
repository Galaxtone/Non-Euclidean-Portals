package com.galaxtone.noneuclideanportals.items;

import com.galaxtone.noneuclideanportals.utils.Selection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemWand extends ItemBase {

	public ItemWand() {
		super("wand");
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote || hand != EnumHand.MAIN_HAND) return EnumActionResult.PASS;
		if (!player.capabilities.isCreativeMode) {
			((EntityPlayerMP) player).addChatMessage(new TextComponentString(TextFormatting.RED + "You must be in creative mode and opped to use this item!"));
			
			player.setHeldItem(hand, null);
			return EnumActionResult.FAIL;
		}
		
		if (stack == Selection.instance.stack) {
			Selection.stop();
			
			Selection current = Selection.getCurrent();
			System.out.println(current.plane);
		} else {
			Selection.start(stack, pos);
		}
		
        return EnumActionResult.PASS;
	}
}

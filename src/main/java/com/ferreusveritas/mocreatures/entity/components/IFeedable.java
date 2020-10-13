package com.ferreusveritas.mocreatures.entity.components;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IFeedable {
	
	public boolean feed(EntityPlayer player, ItemStack itemStack);
	
}

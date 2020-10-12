package com.ferreusveritas.mocreatures.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IProcessInteract {
	
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack);
	
}

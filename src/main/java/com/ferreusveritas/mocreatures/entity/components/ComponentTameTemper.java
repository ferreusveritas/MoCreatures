package com.ferreusveritas.mocreatures.entity.components;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ComponentTameTemper <T extends EntityAnimalComp> extends ComponentTame<T> {
	
	public ComponentTameTemper(Class clazz, T animal) {
		super(clazz, animal);
	}
	
	@Override
	protected boolean attemptTaming(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		//TODO:  Temper taming work
		return false;
	}
	
}

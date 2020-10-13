package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ComponentTameFood<T extends EntityAnimalComp> extends ComponentTame<T> implements IFeedable {
	
	private static class PerClassValues<T> {
		public final BiFunction<T, ItemStack, Boolean> foodTame;
		
		public PerClassValues(Class clazz, BiFunction<T, ItemStack, Boolean> foodTame) {
			this.foodTame = foodTame;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, BiFunction<T, ItemStack, Boolean> foodTame) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, foodTame));
	}
	
	private final PerClassValues<T> values;
	
	public ComponentTameFood(Class<? extends T> clazz, T animal, BiFunction<T, ItemStack, Boolean> foodTame) {
		super(clazz, animal);
		values = getValues(clazz, foodTame);
	}
	
	@Override
	public boolean feed(EntityPlayer player, ItemStack itemStack) {
		if(player != null && !isTamed() && values.foodTame != null && values.foodTame.apply(animal, itemStack) ) {
			if (!animal.world.isRemote) {
				if (tameChance() && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(animal, player)) {
					setTamedBy(player);
					tameSuccess();
				}
				else {
					tameFail();
				}
			}
			return true;
		}
		return false;
	}
	
}

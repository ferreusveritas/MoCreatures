package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ComponentTameTemper <T extends EntityAnimalComp> extends ComponentTame<T> implements IFeedable {
	
	private static class PerClassValues<T> {
		public final BiFunction<T, ItemStack, Integer> foodBribe;
		
		public PerClassValues(Class clazz, BiFunction<T, ItemStack, Integer> foodBribe) {
			this.foodBribe = foodBribe != null ? foodBribe : (a, i) -> 0;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, BiFunction<T, ItemStack, Integer> foodBribe) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, foodBribe));
	}
	
	private final PerClassValues<T> values;
	
	protected int temper;
	protected int maxTemper;
	
	public ComponentTameTemper(Class clazz, T animal, int initTemper, int maxTemper, BiFunction<T, ItemStack, Integer> foodBribe) {
		super(clazz, animal);
		this.temper = initTemper;
		this.maxTemper = maxTemper;
		values = getValues(clazz, foodBribe);
	}
	
	@Override
	public boolean feed(EntityPlayer player, ItemStack itemStack) {
		int amount = values.foodBribe.apply(animal, itemStack);
		
		if(amount > 0) {
			incTemper(amount);
			return true;
		}
		
		return false;
	}

	public boolean attemptTaming(EntityPlayer player) {
		int chance = Math.max(1, getMaxTemper() - getTemper());
		if (player.world.rand.nextInt(chance * 8) == 0) {
			setTamedBy(player);
			tameSuccess();
		} else {
			tameFail();
		}
		return false;
	}
	
	/** How difficult is the creature to be tamed? the Higher the number, the more difficult */
	public int getMaxTemper() {
		return maxTemper;
	}
	
	public int getTemper() {
		return temper;
	}
	
	public void setTemper(int i) {
		temper = MathHelper.clamp(i, 0, getMaxTemper());
	}
	
	public void incTemper(int i) {
		setTemper(getTemper() + i);
	}
	
}

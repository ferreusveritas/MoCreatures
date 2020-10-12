package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ComponentHealFood<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	private static class PerClassValues<T> {
		public final BiFunction<T, ItemStack, Integer> healAmountFn;
		public final boolean requiresTame;
		
		public PerClassValues(Class clazz, boolean requiresTame, BiFunction<T, ItemStack, Integer> healAmountFn) {
			this.healAmountFn = healAmountFn;
			this.requiresTame = requiresTame;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, boolean requiresTame, BiFunction<T, ItemStack, Integer> healAmountFn) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, requiresTame, healAmountFn));
	}
	
	private final PerClassValues<T> values;
	
	public ComponentHealFood(Class clazz, T animal, boolean requiresTame) {
		super(animal);
		values = getValues(clazz, requiresTame, (a, i) -> healAmount(i));
	}
	
	public ComponentHealFood(Class clazz, T animal, boolean requiresTame, BiFunction<T, ItemStack, Integer> healAmount) {
		super(animal);
		values = getValues(clazz, requiresTame, healAmount);
	}
	
	public int healAmount(ItemStack itemstack) {
		return Util.healAmount(itemstack);
	}
	
	private ComponentTame tameable;
	
	@Override
	public void link() {
		super.link();
		if(values.requiresTame) {
			tameable = animal.getComponent(ComponentTame.class);
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		int healAmount = values.healAmountFn.apply(animal, itemStack);
		if(healAmount > 0) {
			if(attemptHealing(healAmount)) {
				consumeItemFromStack(player, itemStack);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean attemptHealing(int healAmount) {
		if(!values.requiresTame || (tameable != null && tameable.isTamed())) {
			animal.heal(healAmount);
			return true;
		}
		return false;
	}
	
}

package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.item.ItemStack;

public class ComponentHeal<T extends EntityAnimalComp> extends Component<T> {
	
	private static class PerClassValues<T> {
		public final float consumeHunger;
		public final Function<T, Boolean> chance;
		
		public PerClassValues(Class clazz, float consumeHunger, Function<T, Boolean> chance) {
			this.consumeHunger = consumeHunger;
			this.chance = chance;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, float consumeHunger, Function<T, Boolean> chance) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, consumeHunger, chance));
	}
	
	private final PerClassValues<T> values;
	
	public ComponentHeal(Class clazz, T animal, float consumeHunger, Function<T, Boolean> chance) {
		super(animal);
		values = getValues(clazz, consumeHunger, chance);
	}
	
	public int healAmount(ItemStack itemstack) {
		return Util.healAmount(itemstack);
	}
	
	private ComponentHunger hunger;
	
	@Override
	public void link() {
		super.link();
		hunger = animal.getComponent(ComponentHunger.class);
	}
	
	public boolean healChance() {
		return values.chance.apply(animal);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!animal.world.isRemote && (animal.getHealth() <= animal.getMaxHealth()) && !animal.isCorpse() && healChance()) {
			if(hunger != null) {
				hunger.burn(values.consumeHunger);
			}
			animal.heal(1);
		}
	}
	
}

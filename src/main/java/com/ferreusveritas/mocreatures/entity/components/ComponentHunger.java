package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ComponentHunger<T extends EntityAnimalComp> extends Component<T> implements IFeedable {
	
	private static class PerClassValues<T> {
		public final BiFunction<T, ItemStack, Integer> nourishment;
		
		public PerClassValues(Class clazz, BiFunction<T, ItemStack, Integer> nourishment) {
			this.nourishment = nourishment;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, BiFunction<T, ItemStack, Integer> nourishment) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, nourishment));
	}
	
	private final PerClassValues<T> values;
	private float currentFullness;
	private float maxFullness;
	
	public ComponentHunger(Class<? extends T> clazz, T animal, float initFullness, float maxFullness, BiFunction<T, ItemStack, Integer> nourishment) {
		super(animal);
		values = getValues(clazz, nourishment);
		this.currentFullness = initFullness;
		this.maxFullness = maxFullness;
	}
	
	@Override
	public boolean feed(EntityPlayer player, ItemStack itemStack) {
		int nourishment = values.nourishment.apply(animal, itemStack);
		
		if(nourishment > 0) {
			if(get() < getMax()) {
				inc(nourishment);
				return true;
			}
		}
		
		return false;
	}
	
	public float getMax() {
		return maxFullness;
	}
	
	public float get() {
		return currentFullness;
	}
	
	public void set(float fullness) {
		currentFullness = Math.max(fullness, 0.0f);
	}
	
	public void setPercent(float percent) {
		set(getMax() * (percent * 0.01f));
	}
	
	public float getPercent() {
		return get() / getMax() * 100.0f;
	}
	
	public void inc(float val) {
		set(get() + val);
	}
	
	public void burn(float val) {
		inc(-val);
	}
	
	public boolean isHungry() {
		return getPercent() <= 30.0f;
	}
	
	public boolean isWellFed() {
		return getPercent() >= 90.0f;
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setFloat("Hunger", get());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		set(compound.getFloat("Hunger"));
	}
	
}

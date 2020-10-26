package com.ferreusveritas.mocreatures.entity.components;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.item.ItemStack;

public class Components {
	
	public static <T extends EntityAnimalComp> Function<T, Component> Heal(Class clazz, float consumeHunger, Function<T, Boolean> chance) { return animal -> new ComponentHeal(clazz, animal, consumeHunger, chance); }	
	public static <T extends EntityAnimalComp> Function<T, Component> Hunger(Class clazz, Function<T, Float> initFullness, float maxFullness, BiFunction<T, ItemStack, Integer> nourishment) { return animal -> new ComponentHunger(clazz, animal, initFullness.apply(animal), maxFullness,  nourishment); }
	public static <T extends EntityAnimalComp> Function<T, Component> FoodTame(Class clazz, BiFunction<T, ItemStack, Boolean> foodTame) { return animal -> new ComponentTameFood(clazz, animal, foodTame); }
	public static <T extends EntityAnimalComp> Function<T, Component> TemperTame(Class clazz, int initTemper, int maxTemper, BiFunction<T, ItemStack, Integer> foodBribe) { return animal -> new ComponentTameTemper(clazz, animal, initTemper, maxTemper, foodBribe); }
	public static <T extends EntityAnimalComp> Function<T, Component> Gender(Class clazz) { return animal -> new ComponentGender(clazz, animal); }
	public static <T extends EntityAnimalComp> Function<T, Component> Ride(Class clazz) { return animal -> new ComponentRide(clazz, animal); }
	public static <T extends EntityAnimalComp> Function<T, Component> WaterRide(Class clazz) { return animal -> new ComponentRideWater(clazz, animal); }
	public static <T extends EntityAnimalComp> Function<T, Component> Chest(Class clazz, String chestName) { return animal -> new ComponentChest(clazz, animal, chestName); }
	public static <T extends EntityAnimalComp> Function<T, Component> Feed(Class clazz, boolean requiresTame) { return animal -> new ComponentFeed(clazz, animal, requiresTame); }
	public static <T extends EntityAnimalComp> Function<T, Component> SitStand(Class clazz) { return animal -> new ComponentStandSit(clazz, animal); }
	public static <T extends EntityAnimalComp> Function<T, Component> Sit(Class clazz) { return animal -> new ComponentSit(clazz, animal); }
	public static <T extends EntityAnimalComp> Function<T, Component> WatchfulEggLaying(Class clazz, int eggNum) { return animal -> new ComponentWatchfulEggLaying(clazz, animal, eggNum); }

}

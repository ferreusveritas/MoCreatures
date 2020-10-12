package com.ferreusveritas.mocreatures.util;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.world.World;

public class Util {
	
	public static EntityLivingBase getAnyLivingEntity(Entity from, double d, Predicate<EntityLivingBase> predicate) {
		List<EntityLiving> list = from.world.getEntitiesWithinAABB(EntityLiving.class, from.getEntityBoundingBox().expand(d, d, d), a -> predicate.and(e -> e != from).test(a));
		return list.size() > 0 ? list.get(0) : null;
	}
	
	public static EntityLivingBase getClosestLivingEntity(Entity from, double d, Predicate<EntityLivingBase> predicate) {
		double closestDistance = -1.0;
		EntityLivingBase closestEntity = null;
		
		List<EntityLiving> list =  from.world.getEntitiesWithinAABB(EntityLiving.class, from.getEntityBoundingBox().expand(d, d, d), a -> predicate.and(e -> e != from).test(a));
		for(EntityLiving entity : list) {
			double thisDistance = from.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0D) || (thisDistance < (d * d))) && ((closestDistance == -1.0) || (thisDistance < closestDistance)) && ((EntityLivingBase) from).canEntityBeSeen(entity)) {
				closestDistance = thisDistance;
				closestEntity = (EntityLivingBase) entity;
			}
		}
		
		return closestEntity;
	}
	
	public static EntityItem getClosestItem(Entity entity, double d, Predicate<EntityItem> predicate) {
		double closestDistance = -1.0;
		EntityItem closestItem = null;
		World world = entity.world;
		
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, entity.getEntityBoundingBox().expand(d, d, d), e -> predicate.test(e));
		
		for(EntityItem entityItem : list) {
			double thisDistance = entityItem.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0) || (thisDistance < (d * d))) && ((closestDistance == -1.0) || (thisDistance < closestDistance))) {
				closestDistance = thisDistance;
				closestItem = entityItem;
			}
		}
		
		return closestItem;
	}
	
	public static void getMyOwnPath(EntityLiving entity, Entity to, float f) {
		Path pathentity = entity.getNavigator().getPathToEntityLiving(to);
		if (pathentity != null) {
			entity.getNavigator().setPath(pathentity, 1D);
		}
	}
	
	public static int healAmount(ItemStack itemstack) {
		return itemstack.getItem() instanceof ItemFood ? ((ItemFood)itemstack.getItem()).getHealAmount(itemstack) : 0;
	}
	
}

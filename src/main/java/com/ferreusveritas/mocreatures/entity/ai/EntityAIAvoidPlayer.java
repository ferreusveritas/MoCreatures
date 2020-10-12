package com.ferreusveritas.mocreatures.entity.ai;


import java.util.List;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.Vec3d;

public class EntityAIAvoidPlayer extends EntityAIBase
{
	private final Predicate<Entity> canBeSeenSelector;
	/** The entity we are attached to */
	protected EntityCreature entity;
	private final double farSpeed;
	private final double nearSpeed;
	protected EntityPlayer closestPlayer;
	private final float avoidDistance;
	/** The PathEntity of our entity */
	private Path path;
	/** The PathNavigate of our entity */
	private final PathNavigate navigation;
	/** Class of entity this behavior seeks to avoid */
	private final Predicate <EntityPlayer> avoidTargetSelector;
	
	public EntityAIAvoidPlayer(EntityCreature entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
		this(entityIn, avoidDistanceIn, farSpeedIn, nearSpeedIn, Predicates.alwaysTrue());
	}
	
	public EntityAIAvoidPlayer(EntityCreature entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn, Predicate <EntityPlayer> avoidTargetSelectorIn) {
		canBeSeenSelector = new Predicate<Entity>() {
			@Override
			public boolean apply(@Nullable Entity ent) {
				return ent.isEntityAlive() && entity.getEntitySenses().canSee(ent) && !entity.isOnSameTeam(ent);
			}
		};
		entity = entityIn;
		avoidTargetSelector = avoidTargetSelectorIn;
		avoidDistance = avoidDistanceIn;
		farSpeed = farSpeedIn;
		nearSpeed = nearSpeedIn;
		navigation = entityIn.getNavigator();
		setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		Predicate predicate = Predicates.and(canBeSeenSelector, avoidTargetSelector);
		List<EntityPlayer> list = entity.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(avoidDistance, 3.0, avoidDistance), predicate);
		
		if (!list.isEmpty()) {
			closestPlayer = list.get(0);
			
			Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, new Vec3d(closestPlayer.posX, closestPlayer.posY, closestPlayer.posZ));
			
			if (vec3d == null || closestPlayer.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < closestPlayer.getDistanceSq(entity)) {
				return false;
			}
			
			if(entity instanceof EntityAnimalComp) {
				((EntityAnimalComp) entity).atAttention();
			}
			
			path = navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
			return path != null;
		}
		
		return false;
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return !navigation.noPath();
	}
	
	@Override
	public void startExecuting() {
		navigation.setPath(path, farSpeed);
	}
	
	@Override
	public void resetTask() {
		closestPlayer = null;
		
		if(entity instanceof EntityAnimalComp) {
			((EntityAnimalComp) entity).atEase();
		}
	}
	
	@Override
	public void updateTask() {
		entity.getNavigator().setSpeed(entity.getDistanceSq(closestPlayer) < 49.0D ? nearSpeed : farSpeed);
	}
	
}
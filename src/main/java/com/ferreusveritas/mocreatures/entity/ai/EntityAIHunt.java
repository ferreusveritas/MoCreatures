package com.ferreusveritas.mocreatures.entity.ai;

import com.google.common.base.Predicate;
import com.ferreusveritas.mocreatures.entity.MoCEntityAnimal;
import com.ferreusveritas.mocreatures.entity.passive.EntityBigCat;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIHunt extends EntityAINearestAttackableTargetMoC {

	private EntityCreature hunter;

	public EntityAIHunt(EntityCreature entity, Class<? extends EntityCreature> classTarget, int chance, boolean checkSight, boolean onlyNearby, Predicate<EntityLivingBase> predicate) {
		super(entity, classTarget, chance, checkSight, onlyNearby, predicate);
		this.hunter = entity;
	}

	public EntityAIHunt(EntityCreature entityCreature, Class<? extends EntityCreature> classTarget, boolean checkSight) {
		this(entityCreature, classTarget, checkSight, false);
	}

	public EntityAIHunt(EntityCreature entity, Class<? extends EntityCreature> classTarget, boolean checkSight, boolean onlyNearby) {
		this(entity, classTarget, 10, checkSight, onlyNearby, null);

	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {

		if(hunter instanceof EntityBigCat) {
			return ((EntityBigCat) hunter).getIsHunting() && super.shouldExecute();
		}
		if(hunter instanceof EntityBigCat) {
			return ((MoCEntityAnimal) hunter).getIsHunting() && super.shouldExecute();
		}

		return false;
	}
}

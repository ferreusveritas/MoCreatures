package com.ferreusveritas.mocreatures.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.passive.EntityPredatorMount;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTargetMoC extends EntitiAITargetMoC {

	protected final Class<? extends EntityLivingBase> targetClass;
	private final int targetChance;
	/** Instance of EntityAINearestAttackableTargetSorter. */
	protected final EntityAINearestAttackableTargetMoC.Sorter theNearestAttackableTargetSorter;
	/**
	 * This filter is applied to the Entity search.  Only matching entities will be targetted.  (null -> no
	 * restrictions)
	 */
	protected Predicate<EntityLivingBase> targetEntitySelector;
	protected EntityLivingBase targetEntity;
	private EntityCreature theAttacker;

	public EntityAINearestAttackableTargetMoC(EntityCreature creature, Class<? extends EntityLivingBase> classTarget, boolean checkSight) {
		this(creature, classTarget, checkSight, false);
	}

	public EntityAINearestAttackableTargetMoC(EntityCreature creature, Class<? extends EntityLivingBase> classTarget, boolean checkSight, boolean onlyNearby) {
		this(creature, classTarget, 10, checkSight, onlyNearby, null);
	}

	public EntityAINearestAttackableTargetMoC(EntityCreature creature, Class<? extends EntityLivingBase> classTarget, int chance, boolean checkSight, boolean onlyNearby,
			final Predicate<EntityLivingBase> targetSelector) {
		super(creature, checkSight, onlyNearby);
		theAttacker = creature;
		targetClass = classTarget;
		targetChance = chance;
		theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetMoC.Sorter(creature);
		setMutexBits(1);
		targetEntitySelector = new Predicate<EntityLivingBase>() {

			@Override
			public boolean apply(EntityLivingBase entitylivingbaseIn) {
				if (targetSelector != null && !targetSelector.apply(entitylivingbaseIn)) {
					return false;
				} else {
					if (entitylivingbaseIn instanceof EntityPlayer) {
						double d0 = EntityAINearestAttackableTargetMoC.this.getTargetDistance();

						if (entitylivingbaseIn.isSneaking()) {
							d0 *= 0.8;
						}

						if (entitylivingbaseIn.isInvisible()) {
							float f = ((EntityPlayer) entitylivingbaseIn).getArmorVisibility();

							if (f < 0.1F) {
								f = 0.1F;
							}

							d0 *= 0.7F * f;
						}

						if (entitylivingbaseIn.getDistance(EntityAINearestAttackableTargetMoC.this.taskOwner) > d0) {
							return false;
						}
					}

					return EntityAINearestAttackableTargetMoC.this.isSuitableTarget(entitylivingbaseIn, false);
				}
			}
		};
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		
		if(theAttacker instanceof IMoCEntity) {
			IMoCEntity attacker = (IMoCEntity)theAttacker;
			if (theAttacker != null && (attacker.isMovementCeased() || !attacker.isNotScared())) {
				return false;
			}
		}

		if(theAttacker instanceof EntityPredatorMount) {
			EntityPredatorMount attacker = (EntityPredatorMount)theAttacker;
			if (theAttacker != null && (attacker.isMovementCeased() || !attacker.isNotScared())) {
				return false;
			}
		}
		
		if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) {
			return false;
		} else {
			double d0 = getTargetDistance();
			List<EntityLivingBase> list =
				taskOwner.world.getEntitiesWithinAABB(targetClass, taskOwner.getEntityBoundingBox().expand(d0, 4.0D, d0),
				Predicates.and(targetEntitySelector, EntitySelectors.NOT_SPECTATING));
			Collections.sort(list, theNearestAttackableTargetSorter);

			if (list.isEmpty()) {
				return false;
			} else {
				targetEntity = (EntityLivingBase) list.get(0);
				if (targetEntity instanceof EntityPlayer) {
					if(theAttacker instanceof IMoCEntity && !((IMoCEntity)theAttacker).shouldAttackPlayers()) {
						return false;
					}
					if(theAttacker instanceof EntityPredatorMount && !((EntityPredatorMount)theAttacker).shouldAttackPlayers()) {
						return false;
					}
				}
				return true;
			}
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		taskOwner.setAttackTarget(targetEntity);
		super.startExecuting();
	}

	public static class Sorter implements Comparator<Entity> {

		private final Entity entity;

		public Sorter(Entity entityIn) {
			this.entity = entityIn;
		}

		public int compare(Entity p_compare_1_, Entity p_compare_2_) {
			double d0 = entity.getDistanceSq(p_compare_1_);
			double d1 = entity.getDistanceSq(p_compare_2_);
			return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
		}
	}

}

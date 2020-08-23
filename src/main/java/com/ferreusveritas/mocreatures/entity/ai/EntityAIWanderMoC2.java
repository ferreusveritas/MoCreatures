package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.MoCEntityMob;
import com.ferreusveritas.mocreatures.entity.ambient.EntityAnt;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAIWanderMoC2 extends EntityAIBase {

	private EntityCreature entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private double speed;
	private int executionChance;
	private boolean mustUpdate;

	public EntityAIWanderMoC2(EntityCreature creatureIn, double speedIn) {
		this(creatureIn, speedIn, 120);
	}

	public EntityAIWanderMoC2(EntityCreature creatureIn, double speedIn, int chance) {
		this.entity = creatureIn;
		this.speed = speedIn;
		this.executionChance = chance;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (this.entity instanceof IMoCEntity && ((IMoCEntity) this.entity).isMovementCeased()) {
			return false;
		}
		if (this.entity.isBeingRidden() && !(this.entity instanceof EntityAnt || this.entity instanceof MoCEntityMob)) {
			return false;
		}

		if (!this.mustUpdate) {
			if (this.entity.getIdleTime() >= 100) {
				//System.out.println("exiting path finder !mustUpdate + Age > 100" + this.entity);
				return false;
			}

			if (this.entity.getRNG().nextInt(this.executionChance) != 0) {
				//System.out.println(this.entity + "exiting due executionChance, age = " + this.entity.getIdleTime() + ", executionChance = " + this.executionChance );
				return false;
			}
		}

		Vec3d targetPos = RandomPositionGeneratorMoCFlyer.findRandomTarget(this.entity, 10, 12);
		
		if (targetPos != null && entity instanceof IMoCEntity && this.entity.getNavigator() instanceof PathNavigateFlyer) {
			int distToFloor = MoCTools.distanceToFloor(this.entity);
			int finalYHeight = distToFloor + MathHelper.floor(targetPos.y - this.entity.posY);
			if ((finalYHeight < ((IMoCEntity) this.entity).minFlyingHeight())) {
				//System.out.println("vector height " + finalYHeight + " smaller than min flying height " + ((IMoCEntity) this.entity).minFlyingHeight());
				return false;
			}
			if ((finalYHeight > ((IMoCEntity) this.entity).maxFlyingHeight())){
				//System.out.println("vector height " + finalYHeight + " bigger than max flying height " + ((IMoCEntity) this.entity).maxFlyingHeight());
				return false;
			}

		}

		if (targetPos == null) {
			//System.out.println("exiting path finder null targetPos");
			return false;
		} else {
			//System.out.println("found vector " + targetPos.x + ", " +  targetPos.y + ", " + targetPos.z);
			this.xPosition = targetPos.x;
			this.yPosition = targetPos.y;
			this.zPosition = targetPos.z;
			this.mustUpdate = false;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return !entity.getNavigator().noPath() && !entity.isBeingRidden();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		//System.out.println(this.entity + "moving to " + this.xPosition + ", " + this.yPosition + ", " + this.zPosition);
		entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}

	/**
	 * Makes task to bypass chance
	 */
	public void makeUpdate() {
		//System.out.println(entity + " has forced update");
		this.mustUpdate = true;
	}

	/**
	 * Changes task random possibility for execution
	 */
	public void setExecutionChance(int newchance) {
		this.executionChance = newchance;
	}
}

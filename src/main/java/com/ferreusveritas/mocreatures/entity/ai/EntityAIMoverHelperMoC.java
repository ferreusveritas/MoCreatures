package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;
import com.ferreusveritas.mocreatures.MoCTools;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityAIMoverHelperMoC extends EntityMoveHelper {

	EntityCreature theCreature;
	protected EntityMoveHelper.Action action = EntityMoveHelper.Action.WAIT;

	public boolean isUpdating() {
		return action == EntityMoveHelper.Action.MOVE_TO;
	}

	public double getSpeed() {
		return speed;
	}

	/**
	 * Sets the speed and location to move to
	 */
	public void setMoveTo(double x, double y, double z, double speedIn) {
		posX = x;
		posY = y;
		posZ = z;
		speed = speedIn;
		action = EntityMoveHelper.Action.MOVE_TO;
	}

	public void strafe(float forward, float strafe) {
		action = EntityMoveHelper.Action.STRAFE;
		moveForward = forward;
		moveStrafe = strafe;
		speed = 0.25D;
	}

	public void onUpdateMoveOnGround() {
		if (this.action == EntityMoveHelper.Action.STRAFE) {
			float f = (float)entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
			float f1 = (float)speed * f;
			float f2 = this.moveForward;
			float f3 = this.moveStrafe;
			float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);

			if (f4 < 1.0F) {
				f4 = 1.0F;
			}

			f4 = f1 / f4;
			f2 = f2 * f4;
			f3 = f3 * f4;
			float f5 = MathHelper.sin(entity.rotationYaw * 0.017453292f);
			float f6 = MathHelper.cos(entity.rotationYaw * 0.017453292f);
			float f7 = f2 * f6 - f3 * f5;
			float f8 = f3 * f6 + f2 * f5;
			PathNavigate pathnavigate = entity.getNavigator();

			if (pathnavigate != null) {
				NodeProcessor nodeprocessor = pathnavigate.getNodeProcessor();

				if (nodeprocessor != null && nodeprocessor.getPathNodeType(this.entity.world, MathHelper.floor(this.entity.posX + (double)f7), MathHelper.floor(this.entity.posY), MathHelper.floor(this.entity.posZ + (double)f8)) != PathNodeType.WALKABLE) {
					this.moveForward = 1.0F;
					this.moveStrafe = 0.0F;
					f1 = f;
				}
			}

			entity.setAIMoveSpeed(f1);
			entity.setMoveForward(moveForward);
			entity.setMoveStrafing(moveStrafe);
			action = EntityMoveHelper.Action.WAIT;
		}
		else if (action == EntityMoveHelper.Action.MOVE_TO) {
			action = EntityMoveHelper.Action.WAIT;
			double d0 = posX - entity.posX;
			double d1 = posZ - entity.posZ;
			double d2 = posY - entity.posY;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;

			if (d3 < 2.5) {
				this.entity.setMoveForward(0.0f);
				return;
			}

			float f9 = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
			this.entity.rotationYaw = limitAngle(entity.rotationYaw, f9, 20.0F);
			this.entity.setAIMoveSpeed((float)(speed * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

			if (d2 > (double)entity.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, entity.width)) {
				entity.getJumpHelper().setJumping();
			}
		}
		else {
			entity.setMoveForward(0.0F);
		}
	}

	/**
	 * Limits the given angle to a upper and lower limit.
	 */
	protected float limitAngle(float angle, float lower, float upper) {
		float f = MathHelper.wrapDegrees(lower - angle);
		f = angle + MathHelper.clamp(f, -upper, upper);

		if (f < 0.0F) {
			return f + 360.0f;
		}
		if (f > 360.0F) {
			return f - 360.0f;
		}

		return f;
	}

	public double getX() {
		return posX;
	}

	public double getY() {
		return posY;
	}

	public double getZ() {
		return posZ;
	}

	public static enum Action {
		WAIT,
		MOVE_TO,
		STRAFE;
	}

	public EntityAIMoverHelperMoC(EntityLiving entityliving) {
		super(entityliving);
		theCreature = (EntityCreature) entityliving;
	}

	@Override
	public void onUpdateMoveHelper() {
		boolean isFlyer = ((IMoCEntity) theCreature).isFlyer();
		boolean isSwimmer = this.theCreature.isInWater(); 
		float fLimitAngle = 90F;
		if (!isFlyer && !isSwimmer) {
			onUpdateMoveOnGround();
			return;
		}

		/*
		 * Flying specific movement code
		 */
		if (isFlyer && !theCreature.isBeingRidden()) {
			this.flyingMovementUpdate();
		}

		/*
		 * Water movement code
		 */
		if (isSwimmer) {
			this.swimmerMovementUpdate();
			fLimitAngle = 30F;
		}
		
		/*
		 * Common movement code
		 */
		
		if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.theCreature.getNavigator().noPath()) {
			//System.out.println("move?");
			double delX = this.posX - this.theCreature.posX;
			double delY = this.posY - this.theCreature.posY;
			double delZ = this.posZ - this.theCreature.posZ;
			double distance = MathHelper.sqrt(delX * delX + delY * delY + delZ * delZ);
			if (distance < 0.1) {
				this.entity.setMoveForward(0.0F);
				this.theCreature.getNavigator().clearPath();
				return;
			}
			//System.out.println("distance to objective = " + d3 + "objective: X = " + this.posX + ", Y = " + this.posY + ", Z = " + this.posZ);
			delY /= distance;
			float f = (float) (Math.atan2(delZ, delX) * 180.0D / Math.PI) - 90.0F;
			this.theCreature.rotationYaw = this.limitAngle(this.theCreature.rotationYaw, f, fLimitAngle);
			this.theCreature.renderYawOffset = this.theCreature.rotationYaw;
			float f1 = (float) (this.speed * this.theCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			this.theCreature.setAIMoveSpeed(this.theCreature.getAIMoveSpeed() + (f1 - this.theCreature.getAIMoveSpeed()) * 0.125F);
			double d4 = Math.sin((double) (this.theCreature.ticksExisted + this.theCreature.getEntityId()) * 0.75D) * 0.01D;
			double d5 = Math.cos((double) (this.theCreature.rotationYaw * (float) Math.PI / 180.0F));
			double d6 = Math.sin((double) (this.theCreature.rotationYaw * (float) Math.PI / 180.0F));
			this.theCreature.motionX += d4 * d5;
			this.theCreature.motionZ += d4 * d6;
			//d4 = Math.sin((double)(this.theCreature.ticksExisted + this.theCreature.getEntityId()) * 0.75D) * 0.05D;
			this.theCreature.motionY += d4 * (d6 + d5) * 0.25D;
			this.theCreature.motionY += (double) this.theCreature.getAIMoveSpeed() * delY * 1.5D;
		}
	}

	/**
	 * Makes flying creatures reach the proper flying height
	 */
	private void flyingMovementUpdate() {

		//Flying alone
		if (((IMoCEntity) theCreature).getIsFlying()) {
			int distY = MoCTools.distanceToFloor(this.theCreature);
			if (distY <= ((IMoCEntity) theCreature).minFlyingHeight()
					&& (this.theCreature.collidedHorizontally || this.theCreature.world.rand.nextInt(100) == 0)) {
				this.theCreature.motionY += 0.02D;
			}
			if (distY > ((IMoCEntity) theCreature).maxFlyingHeight() || this.theCreature.world.rand.nextInt(150) == 0) {
				this.theCreature.motionY -= 0.02D;
			}

		} else {
			if (this.theCreature.motionY < 0) {
				this.theCreature.motionY *= 0.6D;
			}
		}

	}

	/**
	 * Makes creatures in the water float to the right depth
	 */
	private void swimmerMovementUpdate() {
		
		if (theCreature.isBeingRidden()) {
			return;
		}

		double distToSurface = MoCTools.distanceToSurface(theCreature);
		double divingDepth = ((IMoCEntity) theCreature).getDivingDepth();

		double delta = distToSurface - divingDepth;
		
		if (delta > 0) { //We're too deep.  Move up a bit
			if (theCreature.motionY < 0) {
				theCreature.motionY = 0;
			}
			theCreature.motionY += 0.008;
		} else if (delta < -1) { //We're not deep enough.  Move down a bit
			if (theCreature.motionY > 0) {
				theCreature.motionY = 0;
			}
			theCreature.motionY -= 0.008;
		}
		
		if (!theCreature.getNavigator().noPath() && theCreature.collidedHorizontally) {
			if (theCreature instanceof MoCEntityAquatic) {
				theCreature.motionY = 0.05D;
			} else {
				((IMoCEntity) theCreature).forceEntityJump();
			}
		}

		if ((this.theCreature.getAttackTarget() != null && ((this.theCreature.getAttackTarget().posY < (this.posY - 0.5D)) && this.theCreature
				.getDistance(this.theCreature.getAttackTarget()) < 10F))) {
			if (this.theCreature.motionY < -0.1) {
				this.theCreature.motionY = -0.1;
			}
			return;
		}
	}
}

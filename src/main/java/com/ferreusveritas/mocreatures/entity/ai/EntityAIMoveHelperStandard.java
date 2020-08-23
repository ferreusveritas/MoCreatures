package com.ferreusveritas.mocreatures.entity.ai;



import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.MathHelper;

public class EntityAIMoveHelperStandard extends EntityMoveHelper {

	public EntityCreature theCreature;
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
			float f5 = MathHelper.sin(entity.rotationYaw * 0.017453292F);
			float f6 = MathHelper.cos(entity.rotationYaw * 0.017453292F);
			float f7 = f2 * f6 - f3 * f5;
			float f8 = f3 * f6 + f2 * f5;
			PathNavigate pathnavigate = entity.getNavigator();

			if (pathnavigate != null) {
				NodeProcessor nodeprocessor = pathnavigate.getNodeProcessor();

				if (nodeprocessor != null && nodeprocessor.getPathNodeType(entity.world, MathHelper.floor(entity.posX + (double)f7), MathHelper.floor(entity.posY), MathHelper.floor(entity.posZ + (double)f8)) != PathNodeType.WALKABLE) {
					moveForward = 1.0F;
					moveStrafe = 0.0F;
					f1 = f;
				}
			}

			entity.setAIMoveSpeed(f1);
			entity.setMoveForward(moveForward);
			entity.setMoveStrafing(moveStrafe);
			action = EntityMoveHelper.Action.WAIT;
		}
		else if (action == EntityMoveHelper.Action.MOVE_TO) {
			this.action = EntityMoveHelper.Action.WAIT;
			double d0 = posX - entity.posX;
			double d1 = posZ - entity.posZ;
			double d2 = posY - entity.posY;
			double d3 = d0 * d0 + d2 * d2 + d1 * d1;

			if (d3 < 2.5) {
				entity.setMoveForward(0.0F);
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

	public EntityAIMoveHelperStandard(EntityLiving entityliving) {
		super(entityliving);
		this.theCreature = (EntityCreature) entityliving;
	}

	@Override
	public void onUpdateMoveHelper() {
		onUpdateMoveOnGround();
	}

	
	//Common code for entities in the air or water.  Not grounded.
	public void floatingMoveHelper(float fLimitAngle) {
		if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.theCreature.getNavigator().noPath()) {
			//System.out.println("move?");
			double delX = posX - theCreature.posX;
			double delY = posY - theCreature.posY;
			double delZ = posZ - theCreature.posZ;
			double distance = MathHelper.sqrt(delX * delX + delY * delY + delZ * delZ);
			if (distance < 0.1) {
				entity.setMoveForward(0.0F);
				theCreature.getNavigator().clearPath();
				return;
			}
			//System.out.println("distance to objective = " + d3 + "objective: X = " + this.posX + ", Y = " + this.posY + ", Z = " + this.posZ);
			delY /= distance;
			float f = (float) (Math.atan2(delZ, delX) * 180.0D / Math.PI) - 90.0F;
			this.theCreature.rotationYaw = limitAngle(theCreature.rotationYaw, f, fLimitAngle);
			this.theCreature.renderYawOffset = theCreature.rotationYaw;
			float f1 = (float) (speed * theCreature.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			this.theCreature.setAIMoveSpeed(theCreature.getAIMoveSpeed() + (f1 - theCreature.getAIMoveSpeed()) * 0.125f);
			double d4 = Math.sin((double) (theCreature.ticksExisted + theCreature.getEntityId()) * 0.75) * 0.01;
			double d5 = Math.cos((double) (theCreature.rotationYaw * (float) Math.PI / 180.0f));
			double d6 = Math.sin((double) (theCreature.rotationYaw * (float) Math.PI / 180.0f));
			this.theCreature.motionX += d4 * d5;
			this.theCreature.motionZ += d4 * d6;
			//d4 = Math.sin((double)(this.theCreature.ticksExisted + this.theCreature.getEntityId()) * 0.75D) * 0.05D;
			this.theCreature.motionY += d4 * (d6 + d5) * 0.25;
			this.theCreature.motionY += (double) theCreature.getAIMoveSpeed() * delY * 1.5;
		}
	}

}

package com.ferreusveritas.mocreatures.entity.components;

import javax.annotation.Nonnull;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.MathHelper;

public class ComponentRideWater<T extends EntityAnimalComp> extends ComponentRide<T> {
	
	public ComponentRideWater(Class clazz, T animal) {
		super(clazz, animal);
	}	
	
	/** Moves the entity based on the specified heading.  Args: strafe, forward */
	public boolean travel(float strafe, float vertical, float forward) {
		
		if (animal.isInWater()) {
			EntityLivingBase passenger = (EntityLivingBase)animal.getControllingPassenger();
			if (passenger instanceof EntityLivingBase) {
				return isTamed() ? 
						moveEntityWithRiderTamed(strafe, vertical, forward, passenger) : 
							moveEntityWithRiderUntamed(strafe, vertical, forward, passenger);
			}
			
			//Not being ridden
			animal.moveRelative(strafe, vertical, forward, 0.1F);
			animal.move(MoverType.SELF, animal.motionX, animal.motionY, animal.motionZ);
			animal.motionX *= 0.9;
			animal.motionY *= 0.9;
			animal.motionZ *= 0.9;
			
			if (animal.getAttackTarget() == null) {
				//motionY -= 0.005D;
			}
			
			//Update limb swinging
			animal.prevLimbSwingAmount = animal.limbSwingAmount;
			double dX = animal.posX - animal.prevPosX;
			double dZ = animal.posZ - animal.prevPosZ;
			float delta = MathHelper.sqrt(dX * dX + dZ * dZ) * 4.0F;
			if (delta > 1.0F) {
				delta = 1.0F;
			}
			animal.limbSwingAmount += (delta - animal.limbSwingAmount) * 0.4F;
			animal.limbSwing += animal.limbSwingAmount;
			
			return true;
		}
		
		//Not in water
		return false; // regular movement
	}
	
	public boolean isSwimming() {
		return ((animal.isInsideOfMaterial(Material.WATER)));
	}
	
	protected void moveJump() {
		
		//TODO: Test and pick one
		
		/*if (animal.isJumpPending() && !animal.isJumping() && animal.onGround) {
			animal.motionY += animal.getCustomJump() * (animal.isBeingRidden() ? 2.0 : 1.0);
			animal.velocityChanged = true;
			animal.setJumping(true);
			animal.setJumpPending(false);
		}*/
		
		if (animal.onGround) {
			animal.setJumping(false);
		}
		
		if (animal.isJumpPending()) {
			if (isSwimming()) {
				animal.motionY += animal.getCustomJump();
			}
			animal.setJumpPending(false);
		}
		
	}
	
	protected void moveDive() {
		//So it doesn't sink on its own
		if (animal.motionY < 0D && isSwimming()) {
			animal.motionY = 0D;
		}
		
		if (animal.isDivePending()) {
			animal.setDivePending(false);
			animal.motionY -= 0.3D;
		}
	}
	
	protected float strafePenalty() {
		return 0.35f;
	}
	
	public boolean moveEntityWithRiderTamed(float strafe, float vertical, float forward, @Nonnull EntityLivingBase passenger) {
		super.moveEntityWithRiderTamed(strafe, vertical, forward, passenger);
		animal.moveRelative(strafe, vertical, forward, 0.1F);
		return true;
	}
	
	public boolean moveEntityWithRiderUntamed(float strafe, float vertical, float forward, EntityLivingBase passenger) {
		if (!isSwimming()) {
			animal.removePassengers(); //Bucks rider if the creature isn't swimming
			return true;
		}
		return super.moveEntityWithRiderUntamed(strafe, vertical, forward, passenger);
	}
	
}

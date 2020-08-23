package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;

import net.minecraft.entity.EntityLiving;

public class EntityAIMoveHelperFlyer extends EntityAIMoveHelperStandard {

	public EntityAIMoveHelperFlyer(EntityLiving entityliving) {
		super(entityliving);
	}

	@Override
	public void onUpdateMoveHelper() {

		if (!theCreature.isBeingRidden()) {
			flyingMovementUpdate();
		}
		
		floatingMoveHelper(30.0f);
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

}

package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;

import net.minecraft.entity.EntityLiving;

public class EntityAIMoveHelperFish extends EntityAIMoveHelperStandard {

	public EntityAIMoveHelperFish(EntityLiving entityliving) {
		super(entityliving);
	}

	@Override
	public void onUpdateMoveHelper() {
		if (!theCreature.isInWater()) {
			super.onUpdateMoveHelper();
			return;
		}

		swimmerMovementUpdate();

		floatingMoveHelper(30.0f);
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
				.getDistance(theCreature.getAttackTarget()) < 10F))) {
			if (theCreature.motionY < -0.1) {
				theCreature.motionY = -0.1;
			}
			return;
		}
	}
}

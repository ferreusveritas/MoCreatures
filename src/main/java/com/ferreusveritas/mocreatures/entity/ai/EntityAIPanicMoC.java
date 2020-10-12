package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;

public class EntityAIPanicMoC extends EntityAIPanic {

	public EntityAIPanicMoC(EntityCreature creature, double speedIn) {
		super(creature, speedIn);
	}
	
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		if (creature instanceof IMoCEntity && ((IMoCEntity) creature).isNotScared()) {
			return false;
		}
		return super.shouldExecute();
	}
	
}

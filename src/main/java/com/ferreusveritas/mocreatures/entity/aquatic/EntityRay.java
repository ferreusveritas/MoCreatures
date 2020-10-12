package com.ferreusveritas.mocreatures.entity.aquatic;

import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;

import net.minecraft.world.World;

public class EntityRay extends MoCEntityAquatic {

	public EntityRay(World world) {
		super(world);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIWanderMoC2(this, 1.0D, 80));
	}

	public boolean isPoisoning() {
		return false;
	}

	@Override
	public float getAdjustedYOffset() {
		if (!this.isInWater()) {
			return 0.09F;
		}
		return 0.15F;
	}

	@Override
	public boolean canBeTrappedInNet() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return this.height * 0.15D * getSizeFactor();
	}

	@Override
	public float getSizeFactor() {
		return 1.0f;
		/*float f = getEdad() * 0.01F;
		if (f > 1.5F) {
			f = 1.5F;
		}
		return f;*/
	}

	@Override
	protected boolean usesNewAI() {
		return true;
	}

	@Override
	public float getAIMoveSpeed() {
		return 0.06F;
	}

	@Override
	public boolean isMovementCeased() {
		return !isInWater();
	}

	@Override
	protected double minDivingDepth() {
		return 3D;
	}

	@Override
	protected double maxDivingDepth() {
		return 6.0D;
	}

	@Override
	public int getMaxEdad() {
		return 90;
	}

	public boolean isMantaRay() {
		return false;
	}
}

package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.World;

public class EntityMantaRay extends EntityRay {

	public EntityMantaRay(World world) {
		super(world);
		setSize(1.8F, 1F);
		//setEdad(80 + (this.rand.nextInt(100)));
		setTexture("mantray");
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
	}

	@Override
	public int getMaxEdad() {
		return 180;
	}

	@Override
	public boolean isMantaRay() {
		return true;
	}
}

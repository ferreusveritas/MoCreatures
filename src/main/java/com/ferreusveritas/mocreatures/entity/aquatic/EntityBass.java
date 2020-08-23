package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityBass extends EntityMediumFish{

	public EntityBass(World world) {
		super(world);
		setType(MediumFishType.Bass);
		setTexture("mediumfish_bass");
	}

	@Override
	protected int getEggNumber() {
		return EnumEgg.Bass.eggNum;
	}

}

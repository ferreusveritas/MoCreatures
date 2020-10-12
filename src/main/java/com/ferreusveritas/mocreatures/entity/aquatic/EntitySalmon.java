package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntitySalmon extends EntityMediumFish {
	
	public EntitySalmon(World world) {
		super(world);
		setType(MediumFishType.Salmon);
		setTexture("mediumfish_salmon");
	}
	
	@Override
	protected int getEggNumber() {
		return EnumEgg.Salmon.eggNum;
	}
	
}

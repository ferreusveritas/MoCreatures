package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityManderin extends EntitySmallFish{

	public EntityManderin(World world) {
		super(world);
		setType(SmallFishType.Manderin);
		setTexture("smallfish_manderin");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.Manderin;
	}
}

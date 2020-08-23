package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityClownFish extends EntitySmallFish{

	public EntityClownFish(World world) {
		super(world);
		setType(SmallFishType.ClownFish);
		setTexture("smallfish_clownfish");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.ClownFish;
	}

}

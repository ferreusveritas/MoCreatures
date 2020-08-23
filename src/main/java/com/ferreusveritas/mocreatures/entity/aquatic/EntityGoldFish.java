package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityGoldFish extends EntitySmallFish{

	public EntityGoldFish(World world) {
		super(world);
		setType(SmallFishType.GoldFish);
		setTexture("smallfish_goldfish");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.GoldFish;
	}
}

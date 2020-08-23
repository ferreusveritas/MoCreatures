package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityAngelFish extends EntitySmallFish{

	public EntityAngelFish(World world) {
		super(world);
		setType(SmallFishType.AngelFish);
		setTexture("smallfish_angelfish");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.AngelFish;
	}

}

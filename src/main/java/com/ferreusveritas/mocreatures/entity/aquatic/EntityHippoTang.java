package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityHippoTang extends EntitySmallFish{

	public EntityHippoTang(World world) {
		super(world);
		setType(SmallFishType.HippoTang);
		setTexture("smallfish_hippotang");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.HippoTang;
	}
}

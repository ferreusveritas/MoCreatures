package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityAnchovy extends EntitySmallFish{

	public EntityAnchovy(World world) {
		super(world);
		setType(SmallFishType.Anchovy);
		setTexture("smallfish_anchovy");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.Anchovy;
	}
}

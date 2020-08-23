package com.ferreusveritas.mocreatures.entity.aquatic;

import com.ferreusveritas.mocreatures.MoCTools;

import net.minecraft.world.World;

public class EntityAngler extends EntitySmallFish{

	public EntityAngler(World world) {
		super(world);
		setType(SmallFishType.Angler);
		setTexture("smallfish_angler");
	}

	@Override
	protected EnumEgg getEgg() {
		return EnumEgg.Angler;
	}

	@Override
	protected double minDivingDepth() {
		return MoCTools.distanceToSeaFloor(this) + MoCTools.distanceToSurface(this) - 3.0;
	}

	@Override
	protected double maxDivingDepth() {
		return MoCTools.distanceToSeaFloor(this) + MoCTools.distanceToSurface(this) - 1.0;
	}
}

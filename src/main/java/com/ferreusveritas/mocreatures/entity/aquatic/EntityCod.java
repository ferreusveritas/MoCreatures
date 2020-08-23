package com.ferreusveritas.mocreatures.entity.aquatic;

import net.minecraft.world.World;

public class EntityCod extends EntityMediumFish {

	public EntityCod(World world) {
		super(world);
		setType(MediumFishType.Cod);
		setTexture("mediumfish_cod");
	}

	@Override
	protected int getEggNumber() {
		return EnumEgg.Cod.eggNum;
	}

}

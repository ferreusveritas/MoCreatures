package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityGrizzlyBear extends EntityBear{
	
	public EntityGrizzlyBear(World world) {
		super(world);
		setSize(1.5F, 1.5F);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("bearbrown.png");
	}
	
	@Override
	public float getSpeciesSize() {
		return 1.85F;
	}
	
	@Override
	public float calculateMaxHealth() {
		return 40;
	}
	
	public double getFollowRange() {
		int factor = 1;
		if (world.getDifficulty().getDifficultyId() > 1) {
			factor = 2;
		}
		return 6D * factor;
	}
	
	@Override
	public double calculateAttackDmg() {
		int factor = (world.getDifficulty().getDifficultyId());
		return 3 * factor;
	}
	
	@Override
	public boolean shouldAttackPlayers() {
		return (getBrightness() < 0.4F) && super.shouldAttackPlayers();
	}
	
}

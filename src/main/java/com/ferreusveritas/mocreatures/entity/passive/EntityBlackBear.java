package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBlackBear extends EntityBear {
	
	public EntityBlackBear(World world) {
		super(world);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("bearblack.png");
	}
	
	@Override
	public float getSpeciesSize() {
		return 1.4F;
	}
	
	@Override
	public float calculateMaxHealth() {
		return 30;
	}
	
	@Override
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
		return 2 * factor;
	}
	
	@Override
	public boolean shouldAttackPlayers() {
		return false;
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityBlackBear bearcub = new EntityBlackBear(world);
		bearcub.tame.setOwnerId(getOwnerId());
		return bearcub;
	}
	
}

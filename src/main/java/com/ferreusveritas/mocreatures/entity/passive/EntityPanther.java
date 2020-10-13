package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPanther extends EntityBigCat {
	
	public EntityPanther(World world) {
		super(world);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("bcpuma.png");
	}
	
	@Override
	public void setupAttributes() {
		super.setupAttributes();
	}
	
	@Override
	public float calculateMaxHealth() {
		return isAdult() ? 25.0f : 10.0f;
	}
	
	@Override
	public double calculateAttackDmg() {
		return 6D;
	}
	
	@Override
	public double getFollowRange() {
		return 8D;
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		if (!isAdult() || (entity instanceof EntityPanther)) {
			return false;
		}
		return entity.height < 1.5F && entity.width < 1.5F;
	}
	
	@Override
	public float getMoveSpeed() {
		return 1.6F;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityPanther cub = new EntityPanther(world);
		cub.tame.setOwnerId(getOwnerId());
		return cub;
	}
	
}

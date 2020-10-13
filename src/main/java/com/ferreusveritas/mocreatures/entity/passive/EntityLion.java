package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.Gender;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLion extends EntityBigCat {
	
	public enum LionType {
		None,
		Common,
		White
	}
	
	LionType type = LionType.None;
	
	public EntityLion(World world) {
		super(world);
	}
	
	public void setLionType(LionType lType) {
		type = lType;
	}
	
	public LionType getLionType() {
		return type;
	}
	
	@Override
	public void setupAttributes() {
		if (getLionType() == LionType.None) {
			setLionType(rand.nextInt(20) == 0 ? LionType.White : LionType.Common);
		}
		
		super.setupAttributes();
	}
	
	@Override
	public ResourceLocation getTexture() {
		String texture;
		
		switch (getLionType()) {
			default:
			case Common: texture = getGender() == Gender.Male ? "bcmalelion.png" : "bcfemalelion.png"; break;
			case White: texture = "bcwhitelion.png"; break;
		}
		
		return MoCreatures.proxy.getTexture(texture);
	}
	
	@Override
	public boolean hasMane() {
		return isAdult() && getGender() == Gender.Male;
	}
	
	@Override
	public float calculateMaxHealth() {
		return isAdult() ? (getGender() == Gender.Male ? 35.0f : 30.0f) : 12.0f;
	}
	
	@Override
	public double calculateAttackDmg() {
		return 7.0;
	}
	
	@Override
	public double getFollowRange() {
		return 8.0;
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		if (!isAdult() || (entity instanceof EntityLion)) {
			return false;
		}
		return entity.height < 2F && entity.width < 2F;
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityLion cub = new EntityLion(world);
		cub.tame.setOwnerId(getOwnerId());
		return cub;
	}
	
}

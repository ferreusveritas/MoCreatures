package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTiger extends EntityBigCat {
	
	public enum TigerType {
		None,
		Common,
		White
	}
	
	private TigerType type;
	
	public EntityTiger(World world) {
		super(world);
	}
	
	public TigerType getTigerType() {
		return type;
	}
	
	public void setTigerType(TigerType tType) {
		type = tType;
	}
	
	@Override
	public void setupAttributes() {
		if (getTigerType() == TigerType.None) {
			setTigerType(rand.nextInt(20) == 0 ? TigerType.White : TigerType.Common);
		}
		super.setupAttributes();
	}
	
	@Override
	public ResourceLocation getTexture() {
		
		String texture;
		
		switch(getTigerType()) {
			default:
			case Common: texture = "bctiger.png"; break;
			case White: texture = "bcwhitetiger.png"; break;
		}
		
		return MoCreatures.proxy.getTexture(texture);
	}
	
	@Override
	public float calculateMaxHealth() {
		return 35.0f;
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
		if (!isAdult() || (entity instanceof EntityTiger)) {
			return false;
		}
		return entity.height < 2F && entity.width < 2F;
	}
	
	@Override
	public float getMoveSpeed() {
		return 1.5F;
	}
	
}

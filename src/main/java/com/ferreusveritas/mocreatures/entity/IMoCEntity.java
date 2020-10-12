package com.ferreusveritas.mocreatures.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public interface IMoCEntity {
	
	void selectType();
	
	boolean getIsTamed();
	
	boolean getIsAdult();
	
	void setAdult(boolean flag);
	
	boolean checkSpawningBiome();
	
	boolean getCanSpawnHere();
	
	void performAnimation(int i);
	
	void makeEntityJump();
	
	void makeEntityDive();
	
	float getSizeFactor();
	
	float getAdjustedYOffset();
	
	void setArmorType(int i);
	
	int getType();
	
	void setType(int i);
	
	float rollRotationOffset();
	
	float pitchRotationOffset();
	
	float yawRotationOffset();
	
	float getAdjustedZOffset();
	
	float getAdjustedXOffset();
	
	ResourceLocation getTexture();
	
	boolean canAttackTarget(EntityLivingBase entity);
	
	boolean getIsSitting(); // is the entity sitting, for animations and AI
	
	boolean isNotScared(); //relentless creature that attacks others used for AI
	
	boolean isMovementCeased(); //to deactivate path / wander behavior AI
	
	boolean shouldAttackPlayers();
	
	double getDivingDepth();
	
	boolean isDiving();
	
	void forceEntityJump();
	
	int maxFlyingHeight();
	
	int minFlyingHeight();
	
	boolean isFlyer();
	
	boolean getIsFlying();
	
	String getClazzString();
}

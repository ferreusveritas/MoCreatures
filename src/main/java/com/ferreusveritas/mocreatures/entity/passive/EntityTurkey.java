package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityAnimal;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityTurkey extends MoCEntityAnimal {
	
	public EntityTurkey(World world) {
		super(world);
		setSize(0.8F, 1.0F);
		this.texture = "turkey.png";
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
		this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.MELON_SEEDS, false));
		this.tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}
	
	@Override
	public void selectType() {
		if (getType() == 0) {
			setType(this.rand.nextInt(2) + 1);
		}
	}
	
	@Override
	public ResourceLocation getTexture() {
		if (getType() == 1) {
			return MoCreatures.proxy.getTexture("turkey.png");
		} else {
			return MoCreatures.proxy.getTexture("turkeyfemale.png");
		}
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_TURKEY_HURT;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_TURKEY_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return MoCSoundEvents.ENTITY_TURKEY_AMBIENT;
	}
	
	@Override
	protected Item getDropItem() {
		boolean flag = (this.rand.nextInt(2) == 0);
		if (flag) {
			return Items.CHICKEN;
		}
		return Items.FEATHER;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.8D;
		}
	}
	
	@Override
	public boolean isMyHealFood(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.PUMPKIN_SEEDS;
	}
	
	@Override
	public int getTalkInterval() {
		return 400;
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}
}

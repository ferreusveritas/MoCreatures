package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.MoCEntityAnimal;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIAvoidPlayer;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityCrocodile extends MoCEntityAnimal {
	
	public float biteProgress;
	public float spin;
	public int spinInt;
	private boolean waterbound;
	private static final DataParameter<Boolean> IS_RESTING = EntityDataManager.<Boolean>createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> EATING_PREY = EntityDataManager.<Boolean>createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_BITING = EntityDataManager.<Boolean>createKey(EntityCrocodile.class, DataSerializers.BOOLEAN);
	
	public EntityCrocodile(World world) {
		super(world);
		texture = "crocodile.png";
		setSize(1.4F, 0.6F); //it was 2.0, 0.6F
		setAge(50 + rand.nextInt(50));
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(3, new EntityAIAvoidPlayer(this, 4.0f, 0.8, 0.8));
		tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(7, new EntityAIWanderMoC2(this, 0.9D));
		tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		targetTasks.addTask(1, new EntityAIHunt(this, EntityAnimal.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
		
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_RESTING, Boolean.valueOf(false));
		dataManager.register(EATING_PREY, Boolean.valueOf(false));
		dataManager.register(IS_BITING, Boolean.valueOf(false));
	}
	
	public boolean getIsBiting() {
		return ((Boolean)dataManager.get(IS_BITING)).booleanValue();
	}
	
	public boolean getIsSitting() {
		return ((Boolean)dataManager.get(IS_RESTING)).booleanValue();
	}
	
	public boolean getHasCaughtPrey() {
		return ((Boolean)dataManager.get(EATING_PREY)).booleanValue();
	}
	
	public void setBiting(boolean flag) {
		dataManager.set(IS_BITING, Boolean.valueOf(flag));
	}
	
	public void setIsSitting(boolean flag) {
		dataManager.set(IS_RESTING, Boolean.valueOf(flag));
	}
	
	public void setHasCaughtPrey(boolean flag) {
		dataManager.set(EATING_PREY, Boolean.valueOf(flag));
	}
	
	@Override
	protected void jump() {
		
		if (isSwimming()) {
			if (getHasCaughtPrey()) {
				return;
			}
			
			motionY = 0.3D;
			isAirBorne = true;
			
		} else if (getAttackTarget() != null || getHasCaughtPrey()) {
			super.jump();
		}
	}
	
	@Override
	public boolean isMovementCeased() {
		return getIsSitting();
	}
	
	@Override
	public void onLivingUpdate() {
		if (getIsSitting()) {
			rotationPitch = -5F;
			if (!isSwimming() && biteProgress < 0.3F && rand.nextInt(5) == 0) {
				biteProgress += 0.005F;
			}
			if (getAttackTarget() != null) {
				setIsSitting(false);
			}
			if (!world.isRemote && getAttackTarget() != null || isSwimming() || getHasCaughtPrey() || rand.nextInt(500) == 0)// isInsideOfMaterial(Material.WATER)
			{
				setIsSitting(false);
				biteProgress = 0;
			}
			
		} else {
			if (!world.isRemote && (rand.nextInt(500) == 0) && getAttackTarget() == null && !getHasCaughtPrey() && !isSwimming()) {
				setIsSitting(true);
				getNavigator().clearPath();
			}
			
		}
		
		if (rand.nextInt(500) == 0 && !getHasCaughtPrey() && !getIsSitting()) {
			crocBite();
		}
		
		//TODO replace with move to water AI
		if (!world.isRemote && rand.nextInt(500) == 0 && !waterbound && !getIsSitting() && !isSwimming()) {
			MoCTools.MoveToWater(this);
		}
		
		if (waterbound) {
			if (!isInsideOfMaterial(Material.WATER)) {
				MoCTools.MoveToWater(this);
			} else {
				waterbound = false;
			}
		}
		
		if (getHasCaughtPrey()) {
			if (isBeingRidden()) {
				setAttackTarget(null);
				
				biteProgress = 0.4F;
				setIsSitting(false);
				
				if (!isInsideOfMaterial(Material.WATER)) {
					waterbound = true;
					if (getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) getRidingEntity()).getHealth() > 0) {
						((EntityLivingBase) getRidingEntity()).deathTime = 0;
					}
					
					if (!world.isRemote && rand.nextInt(50) == 0) {
						getRidingEntity().attackEntityFrom(DamageSource.causeMobDamage(this), 2);
					}
				}
			} else {
				setHasCaughtPrey(false);
				biteProgress = 0F;
				waterbound = false;
			}
			
			if (isSpinning()) {
				spinInt += 3;
				if ((spinInt % 20) == 0) {
					MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_CROCODILE_ROLL);
				}
				if (spinInt > 80) {
					spinInt = 0;
					getRidingEntity().attackEntityFrom(DamageSource.causeMobDamage(this), 4); //TODO ADJUST
					
				}
				
				//the following if to be removed from SMP
				
				if (!world.isRemote && isBeingRidden() && getRidingEntity() instanceof EntityPlayer) {
					//TODO 4FIX
					//MoCreatures.mc.gameSettings.thirdPersonView = 1;
				}
			}
		}
		
		super.onLivingUpdate();
	}
	
	@Override
	public boolean isNotScared() {
		return true;
	}
	
	public void crocBite() {
		if (!getIsBiting()) {
			setBiting(true);
			biteProgress = 0.0F;
		}
	}
	
	@Override
	public void onUpdate() {
		if (getIsBiting() && !getHasCaughtPrey())// && biteProgress <0.3)
		{
			biteProgress += 0.1F;
			if (biteProgress == 0.4F) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_CROCODILE_JAWSNAP);
			}
			if (biteProgress > 0.6F) {
				
				setBiting(false);
				biteProgress = 0.0F;
			}
		}
		
		super.onUpdate();
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (getHasCaughtPrey()) {
			return false;
		}
		
		//TODO FIX!!!!
		/*if (!world.isRemote && entityIn.getRidingEntity() == null && rand.nextInt(3) == 0) {
            entityIn.mountEntity(this);
            setHasCaughtPrey(true);
            return false;
        } */
		
		crocBite();
		setHasCaughtPrey(false);
		return super.attackEntityAsMob(entityIn);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (isBeingRidden()) {
			
			Entity entity = damagesource.getTrueSource();
			if (entity != null && getRidingEntity() == entity) {
				if (rand.nextInt(2) != 0) {
					return false;
				} else {
					unMount();
				}
			}
			
		}
		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();
			
			if (isBeingRidden() && getRidingEntity() == entity) {
				if ((entity != this) && entity instanceof EntityLivingBase && super.shouldAttackPlayers()) {
					setAttackTarget((EntityLivingBase) entity);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return !(entity instanceof EntityCrocodile);
	}
	
	@Override
	public void updatePassenger(Entity passenger) {
		if (!isBeingRidden()) {
			return;
		}
		int direction = 1;
		
		double dist = getAge() * 0.01F + passenger.width - 0.4D;
		double newPosX = posX - (dist * Math.cos((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
		double newPosZ = posZ - (dist * Math.sin((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
		passenger.setPosition(newPosX, posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
		
		if (spinInt > 40) {
			direction = -1;
		} else {
			direction = 1;
		}
		
		((EntityLivingBase) passenger).renderYawOffset = rotationYaw * direction;
		((EntityLivingBase) passenger).prevRenderYawOffset = rotationYaw * direction;
	}
	
	@Override
	public double getMountedYOffset() {
		return height * 0.35D;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_CROCODILE_DEATH;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_CROCODILE_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		if (getIsSitting()) {
			return MoCSoundEvents.ENTITY_CROCODILE_RESTING;
		}
		return MoCSoundEvents.ENTITY_CROCODILE_AMBIENT;
	}
	
	@Override
	protected Item getDropItem() {
		return MoCItems.hideCroc;
	}
	
	public boolean isSpinning() {
		return getHasCaughtPrey() && (isBeingRidden()) && (isSwimming());
	}
	
	@Override
	public void onDeath(DamageSource damagesource) {
		
		unMount();
		MoCTools.checkForTwistedEntities(world);
		super.onDeath(damagesource);
	}
	
	public void unMount() {
		
		if (isBeingRidden()) {
			if (getRidingEntity() instanceof EntityLiving && ((EntityLivingBase) getRidingEntity()).getHealth() > 0) {
				((EntityLivingBase) getRidingEntity()).deathTime = 0;
			}
			
			dismountRidingEntity();
			setHasCaughtPrey(false);
		}
	}
	
	@Override
	public int getTalkInterval() {
		return 400;
	}
	
	@Override
	public boolean isAmphibian() {
		return true;
	}
	
	@Override
	public boolean isSwimming() {
		return isInWater();
	}
	
	@Override
	public boolean isReadyToHunt() {
		return isNotScared() && !isMovementCeased() && !isBeingRidden() && !getHasCaughtPrey();
	}
}

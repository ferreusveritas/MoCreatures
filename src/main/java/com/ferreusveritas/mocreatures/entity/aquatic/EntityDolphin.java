package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.List;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHeart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityDolphin extends EntityPredatorMountAquatic {
	
	public int gestationtime;
	private static final DataParameter<Boolean> IS_HUNGRY = EntityDataManager.<Boolean>createKey(EntityDolphin.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HAS_EATEN = EntityDataManager.<Boolean>createKey(EntityDolphin.class, DataSerializers.BOOLEAN);
	
	public EntityDolphin(World world) {
		super(world);
		setSize(1.5f, 0.8f);
		//setEdad(60 + rand.nextInt(100));
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIPanicMoC(this, 1.3));
		tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0, 30));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}
	
	@Override
	public void selectType() {
		super.selectType();
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("dolphin.png");
	}
	
	@Override
	public int getMaxTemper() {
		return 100;
	}
	
	public int getInitialTemper() {
		return 100;
	}
	
	@Override
	public double getCustomSpeed() {
		return 2.0;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_HUNGRY, Boolean.valueOf(false));
		dataManager.register(HAS_EATEN, Boolean.valueOf(false));
	}
	
	public boolean getIsHungry() {
		return (((Boolean)dataManager.get(IS_HUNGRY)).booleanValue());
	}
	
	public boolean getHasEaten() {
		return (((Boolean)dataManager.get(HAS_EATEN)).booleanValue());
	}
	
	public void setIsHungry(boolean flag) {
		dataManager.set(IS_HUNGRY, Boolean.valueOf(flag));
	}
	
	public void setHasEaten(boolean flag) {
		dataManager.set(HAS_EATEN, Boolean.valueOf(flag));
	}
	
	//TODO
	/*@Override
    protected void attackEntity(Entity entity, float f) {
        if (attackTime <= 0 && (f < 3.5D) && (entity.getEntityBoundingBox().maxY > getEntityBoundingBox().minY)
                && (entity.getEntityBoundingBox().minY < getEntityBoundingBox().maxY) && (getEdad() >= 100)) {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5);
        }
    }*/
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i) && (world.getDifficulty().getDifficultyId() > 0)) {
			Entity entity = damagesource.getTrueSource();
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityliving = (EntityLivingBase) entity;
				if (isRidingOrBeingRiddenBy(entity)) {
					return true;
				}
				if (entity != this/* && getEdad() >= 100*/) {
					setAttackTarget(entityliving);
				}
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isBeingRidden();
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_DOLPHIN_DEATH;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_DOLPHIN_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return MoCSoundEvents.ENTITY_DOLPHIN_AMBIENT;
	}
	
	@Override
	protected SoundEvent getUpsetSound() {
		return MoCSoundEvents.ENTITY_DOLPHIN_UPSET;
	}
	
	@Override
	protected Item getDropItem() {
		return Items.FISH;
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		/*final Boolean tameResult = processTameInteract(player, hand);
		if (tameResult != null) {
			return tameResult;
		}*/
		
		final ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && (stack.getItem() == Items.FISH)) {
			consumeItemFromStack(player, stack);
			if (!world.isRemote) {
				setTemper(getTemper() + 25);
				if (getTemper() > getMaxTemper()) {
					setTemper(getMaxTemper() - 1);
				}
				
				if ((getHealth() + 15) > getMaxHealth()) {
					setHealth(getMaxHealth());
				}
				
				if (!isAdult()) {
					//setEdad(getEdad() + 1);
				}
			}
			
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			
			return true;
		}
		if (!stack.isEmpty() && (stack.getItem() == Items.COOKED_FISH) && isTamed() && isAdult()) {
			consumeItemFromStack(player, stack);
			if ((getHealth() + 25) > getMaxHealth()) {
				setHealth(getMaxHealth());
			}
			setHasEaten(true);
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			return true;
		}
		if (!isBeingRidden()) {
			if (!world.isRemote && player.startRiding(this)) {
				player.rotationYaw = rotationYaw;
				player.rotationPitch = rotationPitch;
				player.posY = posY;
			}
			
			return true;
		}
		
		return super.processInteract(player, hand);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if (!world.isRemote) {
			
			//TODO
			/*if (!getIsHungry() && (rand.nextInt(100) == 0)) {
                setIsHungry(true);
            }*/
			// fixes growth
			if (!isAdult() && (rand.nextInt(50) == 0))
			{
				/*setEdad(getEdad() + 1);
				if (getEdad() >= 150)
				{
					setAdult(true);
				}*/
			}
			//TODO
			if ((!isBeingRidden()) && (deathTime == 0) && (!isTamed() || getIsHungry())) {
				EntityItem entityitem = getClosestFish(this, 12D);
				if (entityitem != null) {
					MoveToNextEntity(entityitem);
					EntityItem entityitem1 = getClosestFish(this, 2D);
					if ((rand.nextInt(20) == 0) && (entityitem1 != null) && (deathTime == 0)) {
						
						entityitem1.setDead();
						setTemper(getTemper() + 25);
						if (getTemper() > getMaxTemper()) {
							setTemper(getMaxTemper() - 1);
						}
						setHealth(getMaxHealth());
					}
				}
			}
			if (!ReadyforParenting(this)) {
				return;
			}
			int i = 0;
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(8D, 2D, 8D));
			for (int j = 0; j < list.size(); j++) {
				Entity entity = list.get(j);
				if (entity instanceof EntityDolphin) {
					i++;
				}
			}
			
			if (i > 1) {
				return;
			}
			List<Entity> list1 = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(4D, 2D, 4D));
			for (int k = 0; k < list1.size(); k++) {
				Entity entity1 = list1.get(k);
				if (!(entity1 instanceof EntityDolphin) || (entity1 == this)) {
					continue;
				}
				EntityDolphin entitydolphin = (EntityDolphin) entity1;
				if (!ReadyforParenting(this) || !ReadyforParenting(entitydolphin)) {
					continue;
				}
				if (rand.nextInt(100) == 0) {
					gestationtime++;
				}
				if (gestationtime % 3 == 0) {
					MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()),
							new TargetPoint(world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
				}
				if (gestationtime <= 50) {
					continue;
				}
				EntityDolphin babydolphin = new EntityDolphin(world);
				babydolphin.setPosition(posX, posY, posZ);
				if (world.spawnEntity(babydolphin)) {
					MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
					setHasEaten(false);
					entitydolphin.setHasEaten(false);
					gestationtime = 0;
					entitydolphin.gestationtime = 0;
					//babydolphin.setEdad(35);
					babydolphin.setAdult(false);
					//babydolphin.setOwnerId(getOwnerId());
					babydolphin.setTamed(true);
					/*EntityPlayer entityplayer = world.getPlayerEntityByUUID(getOwnerId());
					if (entityplayer != null) {
						MoCTools.tameWithName(entityplayer, babydolphin);
					}*/
					break;
				}
			}
		}
	}
	
	public boolean ReadyforParenting(EntityDolphin entitydolphin) {
		EntityLivingBase passenger = (EntityLivingBase)getControllingPassenger();
		return (entitydolphin.getRidingEntity() == null) && (passenger == null) && entitydolphin.isTamed()
				&& entitydolphin.getHasEaten() && entitydolphin.isAdult();
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	
	@Override
	public float getAIMoveSpeed() {
		return 0.15F;
	}
	
	@Override
	public boolean isMovementCeased() {
		return !isInWater();
	}
	
	@Override
	protected double minDivingDepth() {
		return 0.4D;
	}
	
	@Override
	protected double maxDivingDepth() {
		return 4.0D;
	}
	
	@Override
	public int getMaxEdad() {
		return 160;
	}
	
	@Override
	public void updatePassenger(Entity passenger) {
		double dist = (0.8D);
		double newPosX = posX + (dist * Math.sin(renderYawOffset / 57.29578F));
		double newPosZ = posZ - (dist * Math.cos(renderYawOffset / 57.29578F));
		passenger.setPosition(newPosX, posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
	}
	
	@Override
	public double getMountedYOffset() {
		return 100.0f * 0.01F * (height * 0.3D);
		//return getEdad() * 0.01F * (height * 0.3D);
	}
	
	
}

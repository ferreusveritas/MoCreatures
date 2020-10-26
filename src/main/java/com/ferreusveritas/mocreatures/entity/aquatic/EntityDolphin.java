package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.List;
import java.util.UUID;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ITame;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIMoverHelperMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.components.ComponentFeed;
import com.ferreusveritas.mocreatures.entity.components.ComponentGender;
import com.ferreusveritas.mocreatures.entity.components.ComponentHeal;
import com.ferreusveritas.mocreatures.entity.components.ComponentHunger;
import com.ferreusveritas.mocreatures.entity.components.ComponentLoader;
import com.ferreusveritas.mocreatures.entity.components.ComponentRideWater;
import com.ferreusveritas.mocreatures.entity.components.ComponentTameTemper;
import com.ferreusveritas.mocreatures.entity.components.Components;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHeart;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDolphin extends EntityAnimalComp implements IModelRenderInfo, ITame, IGender {
	
	private static Class thisClass = EntityDolphin.class;
	
	public static ComponentLoader<EntityDolphin> loader = new ComponentLoader<>(
			Components.TemperTame(thisClass, 50, 100, (a, i) -> a.isEdible(i) ? 25 : 0),
			Components.Gender(thisClass),
			Components.Heal(thisClass, 0.5f, a -> a.isHungry() ? false : a.world.rand.nextInt(a.isWellFed() ? 100 : 250) == 0),
			Components.Hunger(thisClass, a -> a.rand.nextFloat() * 6.0f, 12.0f, (a, i) -> a.foodNourishment(i) ),
			Components.Feed(thisClass, false),
			Components.WaterRide(thisClass)
		);
	
	public EntityDolphin(World world) {
		super(world);
		
		setNewDivingDepth();
		navigatorWater = new PathNavigateSwimmer(this, world);
		moveHelper = new EntityAIMoverHelperMoC(this);
		
		setSize(1.5f, 0.8f);
		
		tame = getComponent(ComponentTameTemper.class);
		gender = getComponent(ComponentGender.class);
		heal = getComponent(ComponentHeal.class);
		hunger = getComponent(ComponentHunger.class);
		feed = getComponent(ComponentFeed.class);
		ride = getComponent(ComponentRideWater.class);
	}
	
	@Override
	public void onLivingUpdate() {
		updateNavigation();
		updateDiving();
		
		super.onLivingUpdate();
		
		seekOutFoodItems();
		parenting();
	}
	
	protected void updateNavigation() {
		if(isServer()) {
			getNavigator().onUpdateNavigation();
			
			//updates diving depth after finishing movement
			if (!getNavigator().noPath()) { // && !updateDivingDepth)
				if (!updateDivingDepth) {
					float targetDepth = (float) (MoCTools.distanceToSurface(moveHelper.getX(), moveHelper.getY(), moveHelper.getZ(), world));
					setNewDivingDepth(targetDepth);
					updateDivingDepth = true;
				}
			} else {
				updateDivingDepth = false;
			}
			
			if (isMovementCeased() || rand.nextInt(200) == 0) {
				getNavigator().clearPath();
			}
		}
	}
	
	/** Gets called every tick from main Entity class */
	@Override
	public void onEntityUpdate() {
		
		int maxAir = 800;
		
		//Drowning in air code
		int air = getAir();
		super.onEntityUpdate();
		
		if (isEntityAlive()) {
			if(!isInWater()) {
				--air;
				setAir(air);
				if(maxAir - air > 20) {
					getNavigator().clearPath();
				}
				
				if (getAir() == -30) {
					setAir(0);
					motionY += 0.3D;
					motionX += rand.nextDouble() / 10D;
					motionZ += rand.nextDouble() / 10D;
					attackEntityFrom(DamageSource.DROWN, 1.0F);
				}
			} else {
				setAir(maxAir);
			}
		}
		
	}
	
	@Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
		return type == EnumCreatureType.WATER_CREATURE;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Attributes
	////////////////////////////////////////////////////////////////
	
	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}
	
	@Override
	public void setupAttributes() {
		gender.resolveGender(rand);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackStrength());
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(getFollowRange());
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		setHealth(getMaxHealth());
		super.setupAttributes();
	}
	
	public double getAttackStrength() {
		return 3.0;
	}
	
	public float getMoveSpeed() {
		return 0.7F;
	}
	
	public float calculateMaxHealth() {
		return isAdult() ? 16.0f : 10.0f;
	}
	
	/** The damage the creature is capable of*/
	public double calculateAttackDmg() {
		return 5.0;
	}
	
	/** Returns the distance at which the animal attacks prey */
	public double getFollowRange() {
		return 8.0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	protected PathNavigate navigatorWater;
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIPanicMoC(this, 1.3));
		tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0, 30));
	}
	
	@Override
	public float getAIMoveSpeed() {
		return 0.15F;
	}
	
	@Override
	public PathNavigate getNavigator() {
		if (isInWater()) {
			return navigatorWater;
		}
		return navigator;
	}
	
	@Override
	public boolean isMovementCeased() {
		return ((!isSwimming() && !isBeingRidden()) || isBeingRidden() || isSitting());
	}
	
	@Override
	public boolean isNotScared() {
		return isAdult();
	}
	
	@Override
	public boolean shouldAttackPlayers() {
		return !isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Movement Special
	////////////////////////////////////////////////////////////////
	
	protected boolean jumpPending;
	protected boolean isEntityJumping;
	
	@Override
	public boolean canBeCollidedWith() {
		return !isBeingRidden();
	}
	
	/** mount jumping power */
	@Override
	public double getCustomJump() {
		return 0.4D;
	}
	
	@Override
	protected void jump() {	}
	
	public void setIsJumping(boolean flag) {
		isEntityJumping = flag;
	}
	
	public boolean getIsJumping() {
		return isEntityJumping;
	}
	
	/** Checks that the entity is not colliding with any blocks / liquids */
	@Override
	public boolean isNotColliding() {
		return world.checkNoEntityCollision(getEntityBoundingBox(), this);
	}
	
	@Override
	public void fall(float f, float f1) { }
	
	public boolean gettingOutOfWater() {
		return world.isAirBlock(new BlockPos((int) posX, ((int) posY) + 1, (int) posZ));
	}
	
	
	////////////////////////////////////////////////////////////////
	// Death
	////////////////////////////////////////////////////////////////
	
	@Override
	protected Item getDropItem() {
		return Items.FISH;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Spawning
	////////////////////////////////////////////////////////////////
	
	/** Checks if the entity's current position is a valid location to spawn this entity. */
	@Override
	public boolean getCanSpawnHere() {
		return MoCreatures.entityMap.get(getClass()).getFrequency() > 0 && world.checkNoEntityCollision(getEntityBoundingBox());
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	
	@Override
	protected boolean canDespawn() {
		return MoCreatures.proxy.forceDespawns && !isTamed();
	}
	
	/** Makes the entity despawn if requirements are reached changed so the entities now last longer */
	@Override
	protected void despawnEntity() {
		EntityPlayer player = world.getClosestPlayerToEntity(this, -1.0D);
		if (player != null) {
			double dX = player.posX - posX;
			double dY = player.posY - posY;
			double dZ = player.posZ - posZ;
			double distanceCubed = dX * dX + dY * dY + dZ * dZ;
			
			final double maxDist = 25.0f;
			final double maxDistIdle = 10.0f;
			
			if (canDespawn() && distanceCubed > (maxDist * maxDist * maxDist)) { 
				setDead();
			}
			//changed from 600
			if (idleTime > 1800 && rand.nextInt(800) == 0 && canDespawn() && distanceCubed > (maxDistIdle * maxDistIdle * maxDistIdle)) {
				setDead();
			} else if (distanceCubed < (maxDistIdle * maxDistIdle * maxDistIdle)) {
				idleTime = 0;
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Swimming
	////////////////////////////////////////////////////////////////
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	public boolean isInWater() {
		return world.handleMaterialAcceleration(getEntityBoundingBox().expand(0.0D, -0.2D, 0.0D), Material.WATER, this);
	}
	
	public boolean isSwimming() {
		return ((isInsideOfMaterial(Material.WATER)));
	}
	
	/*@Override
	public boolean handleWaterMovement() {
		return world.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
	}*/
	
	@Override
	public boolean isPushedByWater() {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Diving
	////////////////////////////////////////////////////////////////
	
	private boolean diving;
	private int divingCount;
	private boolean updateDivingDepth = false;
	private double divingDepth;
	protected boolean divePending;
	
	public void updateDiving() {
		if (!diving) {
			if (!isBeingRidden() && getAttackTarget() == null && !navigator.noPath() && rand.nextInt(500) == 0) {
				diving = true;
			}
		} else {
			divingCount++;
			if (divingCount > 100 || isBeingRidden()) {
				diving = false;
				divingCount = 0;
			}
		}
	}
	
	public boolean isDiving() {
		return diving;
	}
	
	public void makeEntityDive() {
		divePending = true;
	}
	
	/** The distance the entity will float under the surface. 0F = surface 1.0F = 1 block under */
	public double getDivingDepth() {
		return (float) divingDepth;
	}
	
	/** Sets diving depth. if setDepth given = 0.0D, will then choose a random value within proper range */
	protected void setNewDivingDepth(double setDepth) {
		double min = minDivingDepth();
		double max = maxDivingDepth();
		divingDepth = setDepth != 0.0D ? MathHelper.clamp(setDepth, min, max) : MathHelper.clampedLerp(min, max, rand.nextDouble());
	}
	
	protected void setNewDivingDepth() {
		setNewDivingDepth(0.0D);
	}
	
	protected double minDivingDepth() {
		return 0.5;
	}
	
	protected double maxDivingDepth() {
		return isInCoralReef() ? floorDepth() : 3.0;
	}
	
	protected boolean isInCoralReef() {
		Biome biome = world.getBiome(new BlockPos(posX, posY, posZ));
		return biome instanceof biomesoplenty.common.biome.overworld.BiomeGenCoralReef;
	}
	
	protected double floorDepth() {
		return MoCTools.distanceToSeaFloor(this) + MoCTools.distanceToSurface(this) - 1.0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Hunting/Attacking
	////////////////////////////////////////////////////////////////
	
	protected int huntingCounter;
	
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
	public boolean canAttackTarget(EntityLivingBase entity) {
		return height >= entity.height && width >= entity.width;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if (isNotScared()) {
			setAttackTarget(getAttackTarget());
		}
		
		if (super.attackEntityFrom(source, amount) && (world.getDifficulty().getDifficultyId() > 0)) {
			Entity entity = source.getTrueSource();
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityliving = (EntityLivingBase) entity;
				if (isRidingOrBeingRiddenBy(entity)) {
					return true;
				}
				if (isAdult()) {
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
	public boolean attackEntityAsMob(Entity entityIn) {
		if (entityIn.isInWater()) {
			super.attackEntityAsMob(entityIn);
			int attackDamage = ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
			boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
			if (flag) {
				applyEnchantments(this, entityIn);
			}
			return flag;
		}
		return false;
	}
	
	public boolean isReadyToHunt() {
		return isAdult() && !isMovementCeased();
	}
	
	public boolean getIsHunting() {
		return huntingCounter != 0;
	}
	
	public void setIsHunting(boolean hunt) {
		huntingCounter = hunt ? rand.nextInt(30) + 1 : 0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	//
	//--COMPONENTS--
	//
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void setupComponents() {
		loader.assemble(this);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Taming
	////////////////////////////////////////////////////////////////
	
	protected final ComponentTameTemper tame;
	
	public boolean isTamed() { return tame.isTamed(); }
	@Override public void setTamedBy(EntityPlayer player) { tame.setTamedBy(player); }
	@Override public UUID getOwnerId() { return tame.getOwnerId(); }
	@Override public Entity getOwner() { return tame.getOwner(); }
	
	
	////////////////////////////////////////////////////////////////
	// Age and Gender
	////////////////////////////////////////////////////////////////
	
	protected final ComponentGender gender;
	public int gestationtime;
	
	@Override public Gender getGender() { return gender.getGender(); }
	@Override public void setGender(Gender g) { gender.setGender(g); }
	
	@Override
	public EntityAgeable createChild(EntityAgeable otherMate) {
		EntityDolphin babydolphin = new EntityDolphin(world);
		babydolphin.setGrowingAge(-childhoodDuration());
		babydolphin.tame.setOwnerId(getOwnerId());
		babydolphin.setupAttributes();
		return babydolphin;
	}
	
	public int childhoodDuration() {
		return 20 * 60 * 20;//20 minutes
	}
	
	public boolean ReadyforParenting() {
		return (getRidingEntity() == null) && //Not riding an entity
				(getControllingPassenger() == null) && //Not being ridden
				isTamed() && //Tamed
				isWellFed() && //Full belly
				isAdult(); //Is an adult
	}
	
	public void parenting() {
		
		if (isServer() && (getGender() == Gender.Female) && (rand.nextInt(100) == 0) && ReadyforParenting()) { //Update approximately every 5 seconds on average
			if(world.getEntitiesWithinAABB(EntityDolphin.class, getEntityBoundingBox().expand(8, 2, 8), e -> true).size() > 2) {
				return; //Make sure we don't overcrowd
			}
			
			List<EntityDolphin> maleDolphins = world.getEntitiesWithinAABB(EntityDolphin.class, getEntityBoundingBox().expand(4, 2, 4), e -> e != this && e.getGender() == Gender.Male);
			if(maleDolphins.size() >= 1) {
				EntityDolphin maleDolphin = maleDolphins.get(0);
				if (maleDolphin.ReadyforParenting()) {
					gestationtime++;
					if (gestationtime % 3 == 0) {
						MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()), new TargetPoint(world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
					}
					if (gestationtime > 50) { //Apx 4 minutes on average
						EntityDolphin babydolphin = (EntityDolphin) createChild(maleDolphin);
						babydolphin.setPosition(posX, posY, posZ);
						babydolphin.setGrowingAge(-childhoodDuration());
						if (world.spawnEntity(babydolphin)) {
							gestationtime = 0;
							hunger.setPercent(10.0f);
							maleDolphin.hunger.setPercent(10.0f);
							MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
						}
					}
				}
			}
		}
		
	}
	
	////////////////////////////////////////////////////////////////
	// Healing
	////////////////////////////////////////////////////////////////
	
	protected final ComponentHeal heal;
	
	
	////////////////////////////////////////////////////////////////
	// Hunger
	////////////////////////////////////////////////////////////////
	
	protected final ComponentHunger hunger;
	
	public boolean isHungry() { return hunger.isHungry(); }
	public boolean isWellFed() { return hunger.isWellFed(); }
	
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	protected final ComponentFeed feed;
	
	protected boolean isEdible(ItemStack stack) {
		return stack.getItem() == Items.FISH;
	}
	
	protected int foodNourishment(ItemStack stack) {
		return isEdible(stack) ? 4 : 0;
	}
	
	public boolean isMyFavoriteFood(ItemStack stack) {
		return false;
	}
	
	protected void seekOutFoodItems() {
		//Search out food
		if (isServer() && !isWellFed() && !isBeingRidden() && !isCorpse() && (!isTamed() || isHungry()) ) {
			EntityItem edibleItem = Util.getClosestItem(this, 12.0, e -> e.getItem().getItem() == Items.FISH && e.isInWater());
			if (edibleItem != null) {
				MoveToNextEntity(edibleItem);
				edibleItem = Util.getClosestItem(this, 2.0, e -> e.getItem().getItem() == Items.FISH && e.isInWater());
				if ((edibleItem != null) && (rand.nextInt(20) == 0)) {
					feed.feed(edibleItem);
				}
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Riding
	////////////////////////////////////////////////////////////////
	
	protected final ComponentRideWater ride;
	
	public boolean rideableEntity() {
		return true;
	}
	
	@Override
	public void travel(float strafe, float vertical, float forward) {
		if(ride.travel(strafe, vertical, forward)) {
			return;
		}
		super.travel(strafe, vertical, forward);
	}
	
	public boolean isRideable() {
		return ride.isRideable();
	}
	
	/** The speed multiplier used when mounted */
	@Override
	public double getCustomSpeed() {
		return 2.0;
	}
	
	@Override
	public void updatePassenger(Entity passenger) {
		double dist = 0.4;
		double newPosX = posX + (dist * Math.sin(Math.toRadians(renderYawOffset)));
		double newPosZ = posZ - (dist * Math.cos(Math.toRadians(renderYawOffset)));
		passenger.setPosition(newPosX, posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
	}
	
	@Override
	public double getMountedYOffset() {
		return (height * 0.3D);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Sitting
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Chest
	////////////////////////////////////////////////////////////////
	
	
	
	////////////////////////////////////////////////////////////////
	//
	//--CLIENT SIDE--
	//
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Sounds
	////////////////////////////////////////////////////////////////
	
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
	
	protected SoundEvent getUpsetSound() {
		return MoCSoundEvents.ENTITY_DOLPHIN_UPSET;
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block block) { }
	
	/** Get number of ticks, at least during which the living entity will be silent. */
	@Override
	public int getTalkInterval() {
		return 300;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Animations
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Rendering
	////////////////////////////////////////////////////////////////
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("dolphin.png");
	}
	
	@Override
	public float rollRotationOffset() {
		return 0F;
	}
	
	@Override
	public float pitchRotationOffset() {
		return 0F;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public float yawRotationOffset() {
		double d4 = 0F;
		if ((motionX != 0D) || (motionZ != 0D)) {
			d4 = Math.sin(ticksExisted * 0.5D) * 8D;
		}
		return (float) (d4);//latOffset;
	}
	
	@Override
	public float getSizeFactor() {
		return isAdult() ? 1.0f : 0.5f;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////////////////////////////////////////////
	// Utility..  Move to helper functions
	////////////////////////////////////////////////////////////////
	
	public float wrapAngle(float f, float f1, float f2) {
		float f3 = f1;
		for (f3 = f1 - f; f3 < -180F; f3 += 360F) { }
		for (; f3 >= 180F; f3 -= 360F) { }
		f3 = MathHelper.clamp(f3, -f2, f2);
		return f + f3;
	}
	
	public void faceItem(int x, int y, int z, float f) {
		double dX = x - posX;
		double dZ = z - posZ;
		double dY = y - posY;
		double dist = MathHelper.sqrt((dX * dX) + (dZ * dZ));
		float yaw = (float) ((Math.atan2(dZ, dX) * 180D) / 3.141592653589) - 90F;
		float pitch = (float) ((Math.atan2(dY, dist) * 180D) / 3.141592653589);
		rotationPitch = -wrapAngle(rotationPitch, pitch, f);
		rotationYaw = wrapAngle(rotationYaw, yaw, f);
	}
	
	protected boolean MoveToNextEntity(Entity entity) {
		if (entity != null) {
			int x = MathHelper.floor(entity.posX);
			int y = MathHelper.floor(entity.posY);
			int z = MathHelper.floor(entity.posZ);
			faceItem(x, y, z, 30F);
			if (posX < x) {
				double d = entity.posX - posX;
				if (d > 0.5) {
					motionX += 0.05;
				}
			} else {
				double d1 = posX - entity.posX;
				if (d1 > 0.5) {
					motionX -= 0.05;
				}
			}
			if (posZ < z) {
				double d2 = entity.posZ - posZ;
				if (d2 > 0.5) {
					motionZ += 0.05;
				}
			} else {
				double d3 = posZ - entity.posZ;
				if (d3 > 0.5) {
					motionZ -= 0.05;
				}
			}
			return true;
		}
		
		return false;
	}
	
}

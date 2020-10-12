package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.List;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIMoverHelperMoC;
import com.ferreusveritas.mocreatures.entity.passive.EntityPredatorMount;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityPredatorMountAquatic extends EntityPredatorMount implements IModelRenderInfo, IGender {
	
	protected boolean divePending;
	protected boolean jumpPending;
	protected boolean isEntityJumping;
	protected int outOfWater;
	private boolean diving;
	private int divingCount;
	private int mountCount;
	public boolean fishHooked;
	protected boolean riderIsDisconnecting;
	protected float moveSpeed;
	protected PathNavigate navigatorWater;
	private boolean updateDivingDepth = false;
	private double divingDepth;
	protected int temper;
	
	public EntityPredatorMountAquatic(World world) {
		super(world);
		outOfWater = 0;
		setTemper(50);
		setNewDivingDepth();
		riderIsDisconnecting = false;
		navigatorWater = new PathNavigateSwimmer(this, world);
		moveHelper = new EntityAIMoverHelperMoC(this);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData par1EntityLivingData) {
		selectType();
		return super.onInitialSpawn(difficulty, par1EntityLivingData);
	}
	
	public void selectType() {
		super.selectType();
	}
	
	public double getAttackStrength() {
		return 3.0;
	}
	
	public double getFollowRange() {
		return 8.0;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	public boolean isAdult() {
		return getGrowingAge() >= 0;
	}
	
	public void setAdult(boolean flag) {
		setGrowingAge(flag ? 0 : -10000);
	}
	
	public int getTemper() {
		return temper;
	}
	
	public void setTemper(int i) {
		temper = i;
	}
	
	/**
	 * How difficult is the creature to be tamed? the Higher the number, the
	 * more difficult
	 */
	public int getMaxTemper() {
		return 100;
	}
	
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
	
	@Override
	protected boolean canDespawn() {
		if (MoCreatures.proxy.forceDespawns) {
			return !isTamed();
		} else {
			return false;
		}
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block block) { }
	
	@Override
	public void fall(float f, float f1) { }
	
	public EntityItem getClosestFish(Entity entity, double d) {
		double d1 = -1D;
		EntityItem entityitem = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(d, d, d));
		for (int i = 0; i < list.size(); i++) {
			Entity entity1 = list.get(i);
			if (!(entity1 instanceof EntityItem)) {
				continue;
			}
			EntityItem entityitem1 = (EntityItem) entity1;
			if ((entityitem1.getItem().getItem() != Items.FISH) || !entityitem1.isInWater()) {
				continue;
			}
			double d2 = entityitem1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1))) {
				d1 = d2;
				entityitem = entityitem1;
			}
		}
		
		return entityitem;
	}
	
	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}
	
	public boolean gettingOutOfWater() {
		int i = (int) posX;
		int j = (int) posY;
		int k = (int) posZ;
		return world.isAirBlock(new BlockPos(i, j + 1, k));
	}
	
	/**
	 * mount jumping power
	 */
	public double getCustomJump() {
		return 0.4D;
	}
	
	public void setIsJumping(boolean flag) {
		isEntityJumping = flag;
	}
	
	public boolean getIsJumping() {
		return isEntityJumping;
	}
	
	/*@Override
    public boolean handleWaterMovement() {
        return world.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
    }*/
	
	protected boolean MoveToNextEntity(Entity entity) {
		if (entity != null) {
			int i = MathHelper.floor(entity.posX);
			int j = MathHelper.floor(entity.posY);
			int k = MathHelper.floor(entity.posZ);
			faceItem(i, j, k, 30F);
			if (posX < i) {
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
			if (posZ < k) {
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
		} else {
			return false;
		}
	}
	
	/**
	 * Speed used to move the mob around
	 *
	 * @return
	 */
	public double getCustomSpeed() {
		return 1.5D;
	}
	
	@Override
	public boolean isInWater() {
		return world.handleMaterialAcceleration(getEntityBoundingBox().expand(0.0D, -0.2D, 0.0D), Material.WATER, this);
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	public boolean isDiving() {
		return diving;
	}
	
	@Override
	protected void jump() {	}
	
	// used to pick up objects while riding an entity
	public void Riding() {
		if ((isBeingRidden()) && (getRidingEntity() instanceof EntityPlayer)) {
			EntityPlayer entityplayer = (EntityPlayer) getRidingEntity();
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D));
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					Entity entity = list.get(i);
					if (entity.isDead) {
						continue;
					}
					entity.onCollideWithPlayer(entityplayer);
					if (!(entity instanceof EntityMob)) {
						continue;
					}
					float f = getDistance(entity);
					if ((f < 2.0F) && entity instanceof EntityMob && (rand.nextInt(10) == 0)) {
						attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity),
								(float) ((EntityMob) entity).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
					}
				}
			}
			/*
             if (entityplayer.isSneaking()) {
                 makeEntityDive();
             }
			 */
		}
	}
	
	public boolean isMovementCeased() {
		return ((!isSwimming() && !isBeingRidden()) || isBeingRidden() || isSitting());
	}
	
	@Override
	public void onLivingUpdate() {
		if (!world.isRemote) {
			if (isBeingRidden()) {
				Riding();
				mountCount = 1;
			}
			
			if (mountCount > 0) {
				if (++mountCount > 50) {
					mountCount = 0;
				}
			}
			
			/*if (getEdad() == 0) setEdad(getMaxEdad() - 10); //fixes tiny creatures spawned by error
			if (!getIsAdult() && (rand.nextInt(300) == 0)) {
				setEdad(getEdad() + 1);
				if (getEdad() >= getMaxEdad()) {
					setAdult(true);
				}
			}*/
			
			getNavigator().onUpdateNavigation();
			
			//updates diving depth after finishing movement
			if (!getNavigator().noPath())// && !updateDivingDepth)
			{
				if (!updateDivingDepth) {
					float targetDepth =
							(float) (MoCTools.distanceToSurface(moveHelper.getX(), moveHelper.getY(), moveHelper.getZ(), world));
					setNewDivingDepth(targetDepth);
					updateDivingDepth = true;
				}
			} else {
				updateDivingDepth = false;
			}
			
			if (isMovementCeased() || rand.nextInt(200) == 0) {
				getNavigator().clearPath();
			}
			
			/*if (isInWater() && onGroundHorizontally && rand.nextInt(10) == 0)
            {
                motionY += 0.05D;
            }*/
			
			/*
             if (getIsTamed() && rand.nextInt(100) == 0) {
                 MoCServerPacketHandler.sendHealth(getEntityId(),
                 world.provider.getDimensionType().getId(), getHealth());
             }
			 */
			
			/*if (forceUpdates() && rand.nextInt(500) == 0) {
                MoCTools.forceDataSync(this);
            }*/
			
			if (isFisheable() && !fishHooked && rand.nextInt(30) == 0) {
				getFished();
			}
			
			if (fishHooked && rand.nextInt(200) == 0) {
				fishHooked = false;
				
				List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(2, 2, 2));
				for (int i = 0; i < list.size(); i++) {
					Entity entity1 = list.get(i);
					
					if (entity1 instanceof EntityFishHook) {
						if (((EntityFishHook) entity1).caughtEntity == this) {
							((EntityFishHook) entity1).caughtEntity = null;
						}
					}
				}
			}
		}
		
		moveSpeed = getMoveSpeed();
		
		if (isSwimming()) {
			//floating();
			outOfWater = 0;
			setAir(800);
		} else {
			outOfWater++;
			motionY -= 0.1D;
			if (outOfWater > 20) {
				getNavigator().clearPath();
			}
			if (outOfWater > 300 && (outOfWater % 40) == 0) {
				motionY += 0.3D;
				motionX = (float) (Math.random() * 0.2D - 0.1D);
				motionZ = (float) (Math.random() * 0.2D - 0.1D);
				attackEntityFrom(DamageSource.DROWN, 1);
			}
		}
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
		super.onLivingUpdate();
	}
	
	public boolean isSwimming() {
		return ((isInsideOfMaterial(Material.WATER)));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setByte("Gender", (byte) getGender().ordinal());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setGender(Gender.fromByte(nbttagcompound.getByte("Gender")));
	}
	
	/**
	 * Makes the entity despawn if requirements are reached changed to the
	 * entities now last longer
	 */
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
	
	public float getMoveSpeed() {
		return 0.7F;
	}
	
	public void makeEntityDive() {
		divePending = true;
	}
	
	@Override
	public float getSizeFactor() {
		return 1.0F;
	}
	
	/**
	 * Checks if the entity's current position is a valid location to spawn this
	 * entity.
	 */
	@Override
	public boolean getCanSpawnHere() {
		return MoCreatures.entityMap.get(getClass()).getFrequency() > 0 && world.checkNoEntityCollision(getEntityBoundingBox());
	}
	
	
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		
		if (isNotScared()) {
			Entity tempEntity = getAttackTarget();
			setAttackTarget((EntityLivingBase) tempEntity);
			return super.attackEntityFrom(damagesource, i);
		}
		
		return super.attackEntityFrom(damagesource, i);
	}
	
	protected boolean canBeTrappedInNet() {
		return false;
	}
	
	protected void dropMyStuff() {
	}
	
	/**
	 * Used to heal the animal
	 *
	 * @param itemstack
	 * @return
	 */
	protected boolean isMyHealFood(ItemStack itemstack) {
		return false;
	}
	
	@Override
	public float pitchRotationOffset() {
		return 0F;
	}
	
	@Override
	public float rollRotationOffset() {
		return 0F;
	}
	
	/**
	 * The act of getting Hooked into a fish Hook.
	 */
	private void getFished() {
		EntityPlayer entityplayer1 = world.getClosestPlayerToEntity(this, 18D);
		if (entityplayer1 != null) {
			EntityFishHook fishHook = entityplayer1.fishEntity;
			if (fishHook != null && fishHook.caughtEntity == null) {
				float f = fishHook.getDistance(this);
				if (f > 1) {
					MoCTools.getPathToEntity(this, fishHook, f);
				} else {
					fishHook.caughtEntity = this;
					fishHooked = true;
				}
			}
		}
	}
	
	/**
	 * Is this aquatic entity prone to be fished with a fish Hook?
	 *
	 * @return
	 */
	protected boolean isFisheable() {
		return false;
	}
	
	public float getAdjustedZOffset() {
		return 0F;
	}
	
	public float getAdjustedXOffset() {
		return 0F;
	}
	
	
	/**
	 * Finds an entity described in entitiesToInclude at d distance
	 *
	 * @param d
	 * @return
	 */
	protected EntityLivingBase getBoogey(double d) {
		EntityLivingBase entityliving = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(d, 4D, d));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entitiesToInclude(entity)) {
				entityliving = (EntityLivingBase) entity;
			}
		}
		return entityliving;
	}
	
	/**
	 * Used in getBoogey to specify what kind of entity to look for
	 *
	 * @param entity
	 * @return
	 */
	public boolean entitiesToInclude(Entity entity) {
		return ((entity.getClass() != getClass()) && (entity instanceof EntityLivingBase) && ((entity.width >= 0.5D) || (entity.height >= 0.5D)));
	}
	
	public boolean isNotScared() {
		return false;
	}
	
	@Override
	public boolean isSitting() {
		return false;
	}
	
	public boolean shouldAttackPlayers() {
		return !isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	/**
	 * Moves the entity based on the specified heading.  Args: strafe, forward
	 */
	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (isInWater()) {
			if (isBeingRidden()) {
				EntityLivingBase passenger = (EntityLivingBase)getControllingPassenger();
				if (passenger != null)moveWithRider(strafe, vertical, forward, passenger); //riding movement
				return;
			}
			moveRelative(strafe, vertical, forward, 0.1F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.9;
			motionY *= 0.9;
			motionZ *= 0.9;
			
			if (getAttackTarget() == null) {
				//motionY -= 0.005D;
			}
			prevLimbSwingAmount = limbSwingAmount;
			double d2 = posX - prevPosX;
			double d3 = posZ - prevPosZ;
			float f7 = MathHelper.sqrt(d2 * d2 + d3 * d3) * 4.0F;
			
			if (f7 > 1.0F)
			{
				f7 = 1.0F;
			}
			
			limbSwingAmount += (f7 - limbSwingAmount) * 0.4F;
			limbSwing += limbSwingAmount;
		} else {
			super.travel(strafe, vertical, forward);
		}
	}
	
	/**
	 ** Riding Code
	 * @param strafe
	 * @param forward
	 */
	public void moveWithRider(float strafe, float vertical, float forward, EntityLivingBase passenger) {
		if (passenger == null)
		{
			return;
		}
		//Buckles rider if out of water
		if (isBeingRidden() && !isTamed() && !isSwimming()) {
			removePassengers();
			return;
		}
		
		if (isBeingRidden() && !isTamed() && passenger instanceof EntityLivingBase) {
			moveWithRiderUntamed(strafe, vertical, forward, passenger);
			return;
		}
		
		if ((isBeingRidden()) && isTamed() && passenger instanceof EntityLivingBase) {
			prevRotationYaw = rotationYaw = passenger.rotationYaw;
			rotationPitch = passenger.rotationPitch * 0.5F;
			setRotation(rotationYaw, rotationPitch);
			rotationYawHead = renderYawOffset = rotationYaw;
			strafe = passenger.moveStrafing * 0.35F;
			forward = passenger.moveForward * (float) (getCustomSpeed() / 5D);
			if (jumpPending) {
				if (isSwimming()) {
					motionY += getCustomJump();
				}
				jumpPending = false;
			}
			//So it doesn't sink on its own
			if (motionY < 0D && isSwimming()) {
				motionY = 0D;
			}
			if (divePending) {
				divePending = false;
				motionY -= 0.3D;
			}
			setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			super.travel(strafe, vertical, forward);
			moveRelative(strafe, vertical, forward, 0.1F);
		}
	}
	
	public void moveWithRiderUntamed(float strafe, float vertical, float forward, EntityLivingBase passenger) {
		//Riding behaviour if untamed
		if ((isBeingRidden()) && !isTamed()) {
			if ((rand.nextInt(5) == 0) && !getIsJumping() && jumpPending) {
				motionY += getCustomJump();
				setIsJumping(true);
				jumpPending = false;
			}
			if (rand.nextInt(10) == 0) {
				motionX += rand.nextDouble() / 30D;
				motionZ += rand.nextDouble() / 10D;
			}
			//if (!world.isRemote) {
			move(MoverType.SELF, motionX, motionY, motionZ);
			//}
			if (!world.isRemote && rand.nextInt(100) == 0) {
				passenger.motionY += 0.9D;
				passenger.motionZ -= 0.3D;
				passenger.dismountRidingEntity();
			}
			if (onGround) {
				setIsJumping(false);
			}
			if (!world.isRemote && this instanceof IMoCTameable && passenger instanceof EntityPlayer) {
				int chance = (getMaxTemper() - getTemper());
				if (chance <= 0) {
					chance = 1;
				}
				if (rand.nextInt(chance * 8) == 0) {
					//MoCTools.tameWithName((EntityPlayer) passenger, (IMoCTameable) this);
				}
				
			}
		}
	}
	
	/**
	 * Whether or not the current entity is in lava
	 */
	@Override
	public boolean isNotColliding() {
		return world.checkNoEntityCollision(getEntityBoundingBox(), this);
	}
	
	/**
	 * Get number of ticks, at least during which the living entity will be silent.
	 */
	@Override
	public int getTalkInterval() {
		return 300;
	}
	
	/**
	 * Get the experience points the entity currently has.
	 */
	@Override
	protected int getExperiencePoints(EntityPlayer player) {
		return 1 + world.rand.nextInt(3);
	}
	
	/**
	 * Gets called every tick from main Entity class
	 */
	@Override
	public void onEntityUpdate() {
		int i = getAir();
		super.onEntityUpdate();
		
		if (isEntityAlive() && !isInWater()) {
			--i;
			setAir(i);
			
			if (getAir() == -30) {
				setAir(0);
				attackEntityFrom(DamageSource.DROWN, 1.0F);
				motionX += rand.nextDouble() / 10D;
				motionZ += rand.nextDouble() / 10D;
			}
		} else {
			setAir(300);
		}
	}
	
	@Override
	public boolean isPushedByWater() {
		return false;
	}
	
	@Override
	public PathNavigate getNavigator() {
		if (isInWater()) {
			return navigatorWater;
		}
		return navigator;
	}
	
	/**
	 * The distance the entity will float under the surface. 0F = surface 1.0F = 1 block under
	 * @return
	 */
	public double getDivingDepth() {
		return (float) divingDepth;
	}
	
	/**
	 * Sets diving depth. if setDepth given = 0.0D, will then choose a random value within proper range
	 * @param setDepth
	 */
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public float yawRotationOffset() {
		double d4 = 0F;
		if ((motionX != 0D) || (motionZ != 0D)) {
			d4 = Math.sin(ticksExisted * 0.5D) * 8D;
		}
		return (float) (d4);//latOffset;
	}
	
	public int getMaxEdad() {
		return 100;
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (!entityIn.isInWater()) {
			return false;
		}
		boolean flag =
				entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
						.getAttributeValue()));
		if (flag) {
			applyEnchantments(this, entityIn);
		}
		return flag;
	}
	
	protected SoundEvent getUpsetSound() {
		return SoundEvents.ENTITY_GENERIC_HURT;
	}
	
	@Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
		return type == EnumCreatureType.WATER_CREATURE;
	}
	
	@Nullable
	public Entity getControllingPassenger() {
		return getPassengers().isEmpty() ? null : (Entity)getPassengers().get(0);
	}
	
	@Override
	public ResourceLocation getTexture() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

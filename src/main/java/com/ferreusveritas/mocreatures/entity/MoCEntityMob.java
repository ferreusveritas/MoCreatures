package com.ferreusveritas.mocreatures.entity;

import java.util.List;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIMoverHelperMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.ai.PathNavigateFlyer;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHealth;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class MoCEntityMob extends EntityMob implements IMoCEntity
{
	
	protected boolean divePending;
	protected int maxHealth;
	protected float moveSpeed;
	protected String texture;
	protected PathNavigate navigatorWater;
	protected PathNavigate navigatorFlyer;
	protected EntityAIWanderMoC2 wander;
	
	protected static final DataParameter<Boolean> ADULT = EntityDataManager.<Boolean>createKey(MoCEntityMob.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(MoCEntityMob.class, DataSerializers.VARINT);
	protected static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(MoCEntityMob.class, DataSerializers.VARINT);
	protected static final DataParameter<String> NAME_STR = EntityDataManager.<String>createKey(MoCEntityMob.class, DataSerializers.STRING);
	
	public MoCEntityMob(World world) {
		super(world);
		texture = "blank.jpg";
		moveHelper = new EntityAIMoverHelperMoC(this);
		navigatorWater = new PathNavigateSwimmer(this, world);
		navigatorFlyer = new PathNavigateFlyer(this, world);
		tasks.addTask(4, wander = new EntityAIWanderMoC2(this, 1.0D, 80));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackStrenght());
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData par1EntityLivingData) {
		selectType();
		return super.onInitialSpawn(difficulty, par1EntityLivingData);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture(texture);
	}
	
	protected double getAttackStrenght() {
		return 2D;
	}
	
	/**
	 * Put your code to choose a texture / the mob type in here. Will be called
	 * by default MocEntity constructors.
	 */
	@Override
	public void selectType() {
		setType(1);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(ADULT, Boolean.valueOf(false));
		dataManager.register(TYPE, Integer.valueOf(0));
		dataManager.register(AGE, Integer.valueOf(45));
		dataManager.register(NAME_STR, "");
	}
	
	@Override
	public void setType(int i) {
		dataManager.set(TYPE, Integer.valueOf(i));
	}
	
	@Override
	public int getType() {
		return ((Integer)dataManager.get(TYPE)).intValue();
	}
	
	@Override
	public boolean getIsAdult() {
		return ((Boolean)dataManager.get(ADULT)).booleanValue();
	}
	
	@Override
	public void setAdult(boolean flag) {
		dataManager.set(ADULT, Boolean.valueOf(flag));
	}
	
	@Override
	public boolean getIsTamed() {
		return false;
	}
	
	public int getEdad() {
		return dataManager.get(AGE);
	}
	
	public void setEdad(int i) {
		dataManager.set(AGE, Integer.valueOf(i));
	}
	
	public boolean getCanSpawnHereLiving() {
		return world.checkNoEntityCollision(getEntityBoundingBox())
				&& world.getCollisionBoxes(this, getEntityBoundingBox()).size() == 0
				&& !world.containsAnyLiquid(getEntityBoundingBox());
	}
	
	public boolean getCanSpawnHereCreature() {
		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(getEntityBoundingBox().minY);
		int k = MathHelper.floor(posZ);
		return getBlockPathWeight(new BlockPos(i, j, k)) >= 0.0F;
	}
	
	@Override
	public boolean getCanSpawnHere() {
		return (MoCreatures.entityMap.get(getClass()).getFrequency() > 0 && super.getCanSpawnHere());
	}
	
	public boolean getCanSpawnHereMob() {
		int i = MathHelper.floor(posX);
		int j = MathHelper.floor(getEntityBoundingBox().minY);
		int k = MathHelper.floor(posZ);
		BlockPos pos = new BlockPos(i, j, k);
		if (world.getLightFor(EnumSkyBlock.SKY, pos) > rand.nextInt(32)) {
			return false;
		}
		int l = world.getLightFromNeighbors(pos);
		if (world.isThundering()) {
			int i1 = world.getSkylightSubtracted();
			world.setSkylightSubtracted(10);
			l = world.getLightFromNeighbors(pos);
			world.setSkylightSubtracted(i1);
		}
		return l <= rand.nextInt(8);
	}
	
	// TODO move this to a class accessible by MocEntityMob and MoCentityAnimals
	protected EntityLivingBase getClosestEntityLiving(Entity entity, double d) {
		double d1 = -1D;
		EntityLivingBase entityliving = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(d, d, d));
		for (int i = 0; i < list.size(); i++) {
			Entity entity1 = list.get(i);
			
			if (entitiesToIgnore(entity1)) {
				continue;
			}
			double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)) && ((EntityLivingBase) entity1).canEntityBeSeen(entity)) {
				d1 = d2;
				entityliving = (EntityLivingBase) entity1;
			}
		}
		
		return entityliving;
	}
	
	//TODO REMOVE
	public boolean entitiesToIgnore(Entity entity) {
		return ((!(entity instanceof EntityLiving)) || (entity instanceof EntityMob) || (entity instanceof MoCEntityEgg)
				|| (entity instanceof EntityPlayer && getIsTamed())
				|| (getIsTamed() && (entity instanceof MoCEntityAnimal && ((MoCEntityAnimal) entity).getIsTamed()))
				|| ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)));
	}
	
	@Override
	public boolean checkSpawningBiome() {
		return true;
	}
	
	@Override
	public void onLivingUpdate() {
		if (!world.isRemote) {
			
			/*if (forceUpdates() && rand.nextInt(1000) == 0) {
                MoCTools.forceDataSync(this);
            }*/
			
			if (getIsTamed() && rand.nextInt(200) == 0) {
				MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHealth(getEntityId(), getHealth()), new TargetPoint(
						world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
			}
			
			if (isHarmedByDaylight()) {
				if (world.isDaytime()) {
					float var1 = getBrightness();
					if (var1 > 0.5F
							&& world.canBlockSeeSky(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY),
									MathHelper.floor(posZ))) && rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
						setFire(8);
					}
				}
			}
			/*
			if (getEdad() == 0) setEdad(getMaxEdad() - 10); //fixes tiny creatures spawned by error
			if (!getIsAdult() && (rand.nextInt(300) == 0)) {
				setEdad(getEdad() + 1);
				if (getEdad() >= getMaxEdad()) {
					setAdult(true);
				}
			}*/
			
			if (getIsFlying() && getNavigator().noPath() && !isMovementCeased() && getAttackTarget() == null && rand.nextInt(20) == 0) {
				wander.makeUpdate();
			}
		}
		
		getNavigator().onUpdateNavigation();
		super.onLivingUpdate();
	}
	
	protected int getMaxEdad() {
		return 100;
	}
	
	protected boolean isHarmedByDaylight() {
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (!world.isRemote && getIsTamed()) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHealth(getEntityId(), getHealth()), new TargetPoint(
					world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
		}
		return super.attackEntityFrom(damagesource, i);
	}
	
	/**
	 * Boolean used to select pathfinding behavior
	 *
	 * @return
	 */
	public boolean isFlyer() {
		return false;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		//nbttagcompound = MoCTools.getEntityData(this);
		nbttagcompound.setBoolean("Adult", getIsAdult());
		nbttagcompound.setInteger("Edad", getEdad());
		nbttagcompound.setInteger("TypeInt", getType());
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		//nbttagcompound = MoCTools.getEntityData(this);
		setAdult(nbttagcompound.getBoolean("Adult"));
		setEdad(nbttagcompound.getInteger("Edad"));
		setType(nbttagcompound.getInteger("TypeInt"));
		
	}
	
	@Override
	public void fall(float f, float f1) {
		if (!isFlyer()) {
			super.fall(f, f1);
		}
	}
	
	@Override
	public boolean isOnLadder() {
		if (isFlyer()) {
			return false;
		} else {
			return super.isOnLadder();
		}
	}
	
	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (!isFlyer()) {
			super.travel(strafe, vertical, forward);
			return;
		}
		moveEntityWithHeadingFlyer(strafe, vertical, forward);
	}
	
	public void moveEntityWithHeadingFlyer(float strafe, float vertical, float forward) {
		if (isServerWorld()) {
			
			moveRelative(strafe, vertical, forward, 0.1F);
			move(MoverType.SELF, motionX, motionY, motionZ);
			motionX *= 0.9;
			motionY *= 0.9;
			motionZ *= 0.9;
		} else {
			super.travel(strafe, vertical, forward);
		}
	}
	
	
	
	/**
	 * Used to synchronize the attack animation between server and client
	 *
	 * @param attackType
	 */
	@Override
	public void performAnimation(int attackType) {
	}
	
	public float getMoveSpeed() {
		return 0.7f;
	}
	
	@Override
	public void makeEntityJump() {
		//TODO
	}
	
	@Override
	public void makeEntityDive() {
		divePending = true;
	}
	
	@Override
	protected boolean canDespawn() {
		return !getIsTamed();
	}
	
	@Override
	public void setDead() {
		// Server check required to prevent tamed entities from being duplicated on client-side
		if (!world.isRemote && (getIsTamed()) && (getHealth() > 0)) {
			return;
		}
		super.setDead();
	}
	
	@Override
	public float getSizeFactor() {
		return 1.0F;
	}
	
	@Override
	public float getAdjustedYOffset() {
		return 0F;
	}
	
	/*protected void getPathOrWalkableBlock(Entity entity, float f) {
        Path pathentity = navigator.getPathToPos(entity.getPosition());
        if ((pathentity == null) && (f > 12F)) {
            int i = MathHelper.floor(entity.posX) - 2;
            int j = MathHelper.floor(entity.posZ) - 2;
            int k = MathHelper.floor(entity.getEntityBoundingBox().minY);
            for (int l = 0; l <= 4; l++) {
                for (int i1 = 0; i1 <= 4; i1++) {
                    if (((l < 1) || (i1 < 1) || (l > 3) || (i1 > 3))
                            && world.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isNormalCube()
                            && !world.getBlockState(new BlockPos(i + l, k, j + i1)).isNormalCube()
                            && !world.getBlockState(new BlockPos(i + l, k + 1, j + i1)).isNormalCube()) {
                        setLocationAndAngles((i + l) + 0.5F, k, (j + i1) + 0.5F, rotationYaw, rotationPitch);
                        return;
                    }
                }

            }
        } else {
            navigator.setPath(pathentity, 16F);
        }
    }*/
	
	@Override
	public void setArmorType(int i) {
	}
	
	public int getArmorType() {
		return 0;
	}
	
	@Override
	public float pitchRotationOffset() {
		return 0F;
	}
	
	@Override
	public float rollRotationOffset() {
		return 0F;
	}
	
	@Override
	public float yawRotationOffset() {
		return 0F;
	}
	
	@Override
	public float getAdjustedZOffset() {
		return 0F;
	}
	
	@Override
	public float getAdjustedXOffset() {
		return 0F;
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return false;
	}
	
	@Override
	public boolean getIsSitting() {
		return false;
	}
	
	@Override
	public boolean isNotScared() {
		return true;
	}
	
	@Override
	public boolean isMovementCeased() {
		return false;
	}
	
	@Override
	public boolean shouldAttackPlayers() {
		return world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	@Override
	public double getDivingDepth() {
		return 0;
	}
	
	@Override
	public boolean isDiving() {
		return false;
	}
	
	@Override
	public void forceEntityJump() {
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag =
				entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
						.getAttributeValue()));
		if (flag) {
			applyEnchantments(this, entityIn);
		}
		
		return flag;
	}
	
	@Override
	public int maxFlyingHeight() {
		return 5;
	}
	
	@Override
	public int minFlyingHeight() {
		return 1;
	}
	
	@Override
	public PathNavigate getNavigator() {
		if (isInWater() && isAmphibian()) {
			return navigatorWater;
		}
		if (isFlyer()) {
			return navigatorFlyer;
		}
		return navigator;
	}
	
	public boolean isAmphibian() {
		return false;
	}
	
	@Override
	public boolean getIsFlying() {
		return isFlyer();
	}
	
	/**
	 * Returns true if the entity is of the @link{EnumCreatureType} provided
	 *
	 * @param type The EnumCreatureType type this entity is evaluating
	 * @param forSpawnCount If this is being invoked to check spawn count caps.
	 * @return If the creature is of the type provided
	 */
	@Override
	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
		if (type == EnumCreatureType.MONSTER) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getClazzString() {
		return EntityList.getEntityString(this);
	}
	
}

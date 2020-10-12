package com.ferreusveritas.mocreatures.entity.passive;

import java.util.List;
import java.util.function.Predicate;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFollowAdult;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class EntityGoat extends EntityAnimal implements IModelRenderInfo {
	
	public enum GoatType {
		None,
		White,
		Tan,
		Spotted,
		SpottedGrey,
		Grey,
		Brown;
		
		public static GoatType fromByte(int byt) {
			return values()[byt % 7];
		}
	}
	
	
	private boolean hungry;
	private boolean swingLeg;
	private boolean swingEar;
	private boolean swingTail;
	private boolean bleat;
	private boolean eating;
	private int bleatcount;
	private int attacking;
	public int movecount;
	private int chargecount;
	private int tailcount; // 90 to -45
	private int earcount; // 20 to 40 default = 30
	private int eatcount;
	private static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityGoat.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> GENDER = EntityDataManager.<Byte>createKey(EntityGoat.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_CHARGING = EntityDataManager.<Boolean>createKey(EntityGoat.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_UPSET = EntityDataManager.<Boolean>createKey(EntityGoat.class, DataSerializers.BOOLEAN);
	
	public EntityGoat(World world) {
		super(world);
		setSize(0.8F, 1F);
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIPanicMoC(this, 1.0D));
		tasks.addTask(4, new EntityAIFollowAdult(this, 1.0D));
		tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(6, new EntityAIWanderMoC2(this, 1.0D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TYPE, (byte)GoatType.None.ordinal());
		dataManager.register(GENDER, (byte)Gender.None.ordinal());
		dataManager.register(IS_CHARGING, Boolean.valueOf(false));
		dataManager.register(IS_UPSET, Boolean.valueOf(false));
	}
	
	Gender getGender() {
		return Gender.fromByte(dataManager.get(GENDER));
	}
	
	public void setGender(Gender g) {
		dataManager.set(GENDER, (byte)g.ordinal());
	}
	
	public GoatType getType() {
		return GoatType.fromByte(dataManager.get(TYPE));
	}
	
	public void setType(GoatType type) {
		dataManager.set(TYPE, (byte)type.ordinal());
	}
	
	public boolean isAdultMale() {
		return isAdult() && getGender() == Gender.Male;
	}
	
	public boolean getUpset() {
		return ((Boolean)dataManager.get(IS_UPSET)).booleanValue();
	}
	
	public boolean getCharging() {
		return ((Boolean)dataManager.get(IS_CHARGING)).booleanValue();
	}
	
	public void setUpset(boolean flag) {
		dataManager.set(IS_UPSET, Boolean.valueOf(flag));
	}
	
	public void setCharging(boolean flag) {
		dataManager.set(IS_CHARGING, Boolean.valueOf(flag));
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		selectType();
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	public void selectType() {
		/*
		 * type 1 = baby
		 * type 2 = female
		 * type 3 = female 2
		 * type 4 = female 3
		 * type 5 = male 1
		 * type 6 = male 2
		 * type 7 = male 3
		 */
		
		boolean snowy = BiomeDictionary.hasType(world.getBiome(new BlockPos(posX, posY, posZ)), Type.SNOWY);
		
		if(rand.nextInt(100) <= 15) {
			setGrowingAge(-24000);
		}
		
		if(getGender() == Gender.None) {
			setGender(rand.nextInt(2) == 0 ? Gender.Male : Gender.Female);
		}
		
		if(getType() == GoatType.None) {
			if(snowy) {
				setType(GoatType.White);
			} else {
				setType(GoatType.fromByte(rand.nextInt(GoatType.values().length - 1) + 1));
			}
		}
		
	}
	
	public void calm() {
		setAttackTarget(null);
		setUpset(false);
		setCharging(false);
		attacking = 0;
		chargecount = 0;
	}
	
	public boolean isAdult() {
		return getGrowingAge() > 0;
	}
	
	@Override
	protected void jump() {
		
		if(isAdult()) {
			motionY = 0.4;
		} else {
			motionY = getGender() == Gender.Female ? 0.45 : 0.5;
		}
		
		if (isPotionActive(MobEffects.JUMP_BOOST)) {
			motionY += (getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
		}
		if (isSprinting()) {
			float f = rotationYaw * 0.01745329F;
			motionX -= MathHelper.sin(f) * 0.2F;
			motionZ += MathHelper.cos(f) * 0.2F;
		}
		isAirBorne = true;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (world.isRemote) {
			if (rand.nextInt(100) == 0) {
				setSwingEar(true);
			}
			
			if (rand.nextInt(80) == 0) {
				setSwingTail(true);
			}
			
			if (rand.nextInt(50) == 0) {
				setEating(true);
			}
		}
		if (getBleating()) {
			bleatcount++;
			if (bleatcount > 15) {
				bleatcount = 0;
				setBleating(false);
			}
			
		}
		
		if ((hungry) && (rand.nextInt(20) == 0)) {
			hungry = false;
		}
		
		if (getUpset()) {
			attacking += (rand.nextInt(4)) + 2;
			if (attacking > 75) {
				attacking = 75;
			}
			
			if (rand.nextInt(200) == 0 || getAttackTarget() == null) {
				calm();
			}
			
			if (!getCharging() && rand.nextInt(35) == 0) {
				swingLeg();
			}
			
			if (!getCharging()) {
				getNavigator().clearPath();
			}
			
			if (getAttackTarget() != null)// && rand.nextInt(100)==0)
			{
				faceEntity(getAttackTarget(), 10F, 10F);
				if (rand.nextInt(80) == 0) {
					setCharging(true);
				}
			}
		}
		
		if (getCharging()) {
			chargecount++;
			if (chargecount > 120) {
				chargecount = 0;
			}
			if (getAttackTarget() == null) {
				calm();
			}
		}
		
		if (!getUpset() && !getCharging()) {
			EntityPlayer entityplayer1 = world.getClosestPlayerToEntity(this, 24D);
			if (entityplayer1 != null) {// Behaviour that happens only close to player :)
				
				// is there food around? only check with player near
				EntityItem entityitem = getClosestItem(this, 10.0, e -> true);
				if (entityitem != null) {
					float f = entityitem.getDistance(this);
					if (f > 2.0F) {
						int x = MathHelper.floor(entityitem.posX);
						int y = MathHelper.floor(entityitem.posY);
						int z = MathHelper.floor(entityitem.posZ);
						faceLocation(x, y, z, 30F);
						
						getMyOwnPath(entityitem, f);
						return;
					}
					if ((f < 2.0F) && (entityitem != null) && (deathTime == 0) && rand.nextInt(50) == 0) {
						MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GOAT_EATING);
						setEating(true);
						
						entityitem.setDead();
						return;
					}
				}
				
				// find other goat to play!
				if (isAdultMale() && rand.nextInt(200) == 0) {
					EntityGoat entitytarget = (EntityGoat) getClosestEntityLiving(14.0);
					if (entitytarget != null) {
						setUpset(true);
						setAttackTarget(entitytarget);
						entitytarget.setUpset(true);
						entitytarget.setAttackTarget(this);
					}
				}
				
			}// end of close to player behavior
		}// end of !upset !charging
	}
	
	public void faceLocation(int i, int j, int k, float f) {
		double var4 = i + 0.5D - this.posX;
		double var8 = k + 0.5D - this.posZ;
		double var6 = j + 0.5D - this.posY;
		double var14 = MathHelper.sqrt(var4 * var4 + var8 * var8);
		float var12 = (float) (Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
		float var13 = (float) (-(Math.atan2(var6, var14) * 180.0D / Math.PI));
		this.rotationPitch = -this.updateRotation2(this.rotationPitch, var13, f);
		this.rotationYaw = this.updateRotation2(this.rotationYaw, var12, f);
	}
	
	//TODO: Move to utils function
	public EntityItem getClosestItem(Entity entity, double d, Predicate<EntityItem> predicate) {
		double closestDistance = -1.0;
		EntityItem closestItem = null;
		
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(d, d, d), e -> predicate.test(e));
		
		for(EntityItem entityItem : list) {
			double thisDistance = entityItem.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((d < 0.0) || (thisDistance < (d * d))) && ((closestDistance == -1.0) || (thisDistance < closestDistance))) {
				closestDistance = thisDistance;
				closestItem = entityItem;
			}
		}
		
		return closestItem;
	}
	
	protected EntityLivingBase getClosestEntityLiving(double d) {
		return Util.getClosestLivingEntity(this, d, e -> entitiesToIgnore(e));
	}
	
	
	public void getMyOwnPath(Entity entity, float f) {
		Path pathentity = getNavigator().getPathToEntityLiving(entity);
		if (pathentity != null) {
			getNavigator().setPath(pathentity, 1D);
		}
	}
	
	/**
	 * Arguments: current rotation, intended rotation, max increment.
	 */
	private float updateRotation2(float par1, float par2, float par3) {
		float var4;
		
		for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F) { }
		
		while (var4 >= 180.0F) {
			var4 -= 360.0F;
		}
		
		if (var4 > par3) {
			var4 = par3;
		}
		
		if (var4 < -par3) {
			var4 = -par3;
		}
		
		return par1 + var4;
	}
	
	public boolean isMyFavoriteFood(ItemStack stack) {
		return !stack.isEmpty() && MoCTools.isItemEdible(stack.getItem());
	}
	
	@Override
	public int getTalkInterval() {
		if (hungry) {
			return 80;
		}
		
		return 200;
	}
	
	
	public boolean entitiesToIgnore(Entity entity) {
		return ((!(entity instanceof EntityGoat)) || !isAdultMale() );
	}
	
	public boolean isMovementCeased() {
		return getUpset() && !getCharging();
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		attacking = 30;
		if (entityIn instanceof EntityGoat) {
			MoCTools.bigsmack(this, entityIn, 0.4F);
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GOAT_SMACK);
			if (rand.nextInt(3) == 0) {
				calm();
				((EntityGoat) entityIn).calm();
			}
			return false;
		}
		MoCTools.bigsmack(this, entityIn, 0.8F);
		if (rand.nextInt(3) == 0) {
			calm();
		}
		return super.attackEntityAsMob(entityIn);
	}
	
	public boolean isNotScared() {
		return isAdultMale();
	}
	
	private void swingLeg() {
		if (!getSwingLeg()) {
			setSwingLeg(true);
			movecount = 0;
		}
	}
	
	public boolean getSwingLeg() {
		return swingLeg;
	}
	
	public void setSwingLeg(boolean flag) {
		swingLeg = flag;
	}
	
	public boolean getSwingEar() {
		return swingEar;
	}
	
	public void setSwingEar(boolean flag) {
		swingEar = flag;
	}
	
	public boolean getSwingTail() {
		return swingTail;
	}
	
	public void setSwingTail(boolean flag) {
		swingTail = flag;
	}
	
	public boolean getEating() {
		return eating;
	}
	
	public void setEating(boolean flag) {
		eating = flag;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();
			
			if (entity != this && entity instanceof EntityLivingBase && shouldAttackPlayers() && isAdultMale()) {
				setAttackTarget((EntityLivingBase) entity);
				setUpset(true);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean shouldAttackPlayers() {
		return false;
	}
	
	@Override
	public void onUpdate() {
		
		if (getSwingLeg()) {
			movecount += 5;
			if (movecount == 30) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GOAT_DIGG);
			}
			
			if (movecount > 100) {
				setSwingLeg(false);
				movecount = 0;
			}
		}
		
		if (getSwingEar()) {
			earcount += 5;
			if (earcount > 40) {
				setSwingEar(false);
				earcount = 0;
			}
		}
		
		if (getSwingTail()) {
			tailcount += 15;
			if (tailcount > 135) {
				setSwingTail(false);
				tailcount = 0;
			}
		}
		
		if (getEating()) {
			eatcount += 1;
			if (eatcount == 2) {
				EntityPlayer entityplayer1 = world.getClosestPlayerToEntity(this, 3D);
				if (entityplayer1 != null) {
					MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GOAT_EATING);
				}
			}
			if (eatcount > 25) {
				setEating(false);
				eatcount = 0;
			}
		}
		
		super.onUpdate();
	}
	
	public int legMovement() {
		if (!getSwingLeg()) {
			return 0;
		}
		
		if (movecount < 21) {
			return movecount * -1;
		}
		if (movecount < 70) {
			return movecount - 40;
		}
		return -movecount + 100;
	}
	
	public int earMovement() {
		// 20 to 40 default = 30
		if (!getSwingEar()) {
			return 0;
		}
		if (earcount < 11) {
			return earcount + 30;
		}
		if (earcount < 31) {
			return -earcount + 50;
		}
		return earcount - 10;
	}
	
	public int tailMovement() {
		// 90 to -45
		if (!getSwingTail()) {
			return 90;
		}
		
		return tailcount - 45;
	}
	
	public int mouthMovement() {
		if (!getEating()) {
			return 0;
		}
		if (eatcount < 6) {
			return eatcount;
		}
		if (eatcount < 16) {
			return -eatcount + 10;
		}
		return eatcount - 20;
	}
	
	@Override
	public void fall(float f, float f1) {
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		final ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
			if(!isAdult()) {
				return false;
			}
			if (getGender() == Gender.Male) {
				setUpset(true);
				setAttackTarget(player);
				return false;
			}
			
			useItem(player, hand, stack);
			player.addItemStackToInventory(new ItemStack(Items.MILK_BUCKET));
			return true;
		}
		
		if (!stack.isEmpty() && (MoCTools.isItemEdible(stack.getItem()))) {
			useItem(player, hand, stack);
			setHealth(getMaxHealth());
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GOAT_EATING);
			return true;
		}
		
		return super.processInteract(player, hand);
	}
	
	public boolean getBleating() {
		return bleat && (getAttacking() == 0);
	}
	
	public void setBleating(boolean flag) {
		bleat = flag;
	}
	
	public int getAttacking() {
		return attacking;
	}
	
	public void setAttacking(int flag) {
		attacking = flag;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_GOAT_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		setBleating(true);
		if (!isAdult()) {
			return MoCSoundEvents.ENTITY_GOAT_AMBIENT_BABY;
		}
		if (getGender() == Gender.Female) {
			return MoCSoundEvents.ENTITY_GOAT_AMBIENT_FEMALE;
		}
		
		return MoCSoundEvents.ENTITY_GOAT_AMBIENT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_GOAT_DEATH;
	}
	
	@Override
	protected Item getDropItem() {
		return Items.LEATHER;
	}
	
	@Override
	public float getAIMoveSpeed() {
		return 0.15F;
	}
	
	public static void useItem(EntityPlayer player, EnumHand hand, ItemStack stack) {
		stack.shrink(1);
		if (stack.isEmpty()) {
			player.setHeldItem(hand, ItemStack.EMPTY);
		}
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Rendering
	////////////////////////////////////////////////////////////////
	
	@Override
	public ResourceLocation getTexture() {
		switch(getType()) {
			default:
			case None:
			case White: return MoCreatures.proxy.getTexture("goat1.png");
			case Tan: return MoCreatures.proxy.getTexture("goat2.png");
			case Spotted:return MoCreatures.proxy.getTexture("goat3.png");
			case SpottedGrey:return MoCreatures.proxy.getTexture("goat4.png");
			case Grey:return MoCreatures.proxy.getTexture("goat5.png");
			case Brown: return MoCreatures.proxy.getTexture("goat6.png");
		}
	}
	
	@Override
	public float rollRotationOffset() {
		return 0;
	}
	
	@Override
	public float pitchRotationOffset() {
		return 0;
	}
	
	@Override
	public float yawRotationOffset() {
		return 0;
	}
	
	@Override
	public float getSizeFactor() {
		return 1.0f;
	}
	
}

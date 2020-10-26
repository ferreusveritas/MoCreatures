package com.ferreusveritas.mocreatures.entity.passive;

import java.util.UUID;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.Animation;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ITame;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIAvoidPlayer;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.components.ComponentFeed;
import com.ferreusveritas.mocreatures.entity.components.ComponentGender;
import com.ferreusveritas.mocreatures.entity.components.ComponentHeal;
import com.ferreusveritas.mocreatures.entity.components.ComponentHunger;
import com.ferreusveritas.mocreatures.entity.components.ComponentLoader;
import com.ferreusveritas.mocreatures.entity.components.ComponentRide;
import com.ferreusveritas.mocreatures.entity.components.ComponentSit;
import com.ferreusveritas.mocreatures.entity.components.ComponentTame;
import com.ferreusveritas.mocreatures.entity.components.Components;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityKomodo extends EntityAnimalComp implements IGender, ITame, IModelRenderInfo {
	
	private static Class thisClass = EntityKomodo.class;
	
	public static ComponentLoader<EntityKomodo> loader = new ComponentLoader<>(
			Components.FoodTame(thisClass, (a, s) -> s.getItem() == Items.CHICKEN),
			Components.Gender(thisClass),
			Components.Heal(thisClass, 0.5f, a -> a.isHungry() ? false : a.world.rand.nextInt(a.isWellFed() ? 100 : 250) == 0),
			Components.Hunger(thisClass, a -> a.rand.nextFloat() * 6.0f, 12.0f, (a, i) -> a.foodNourishment(i) ),
			Components.Feed(thisClass, false),
			Components.Ride(thisClass),
			Components.Sit(thisClass)
			);
	
	public boolean alerted;
	
	public EntityKomodo(World world) {
		super(world);
		setSize(1.6F, 0.5F);
		stepHeight = 1.0F;
		//setGrowingAge(rand.nextInt(4) == 0 ? -childhoodDuration() : 0);
		
		tame = getComponent(ComponentTame.class);
		gender = getComponent(ComponentGender.class);
		heal = getComponent(ComponentHeal.class);
		hunger = getComponent(ComponentHunger.class);
		feed = getComponent(ComponentFeed.class);
		ride = getComponent(ComponentRide.class);
		sit = getComponent(ComponentSit.class);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if (!world.isRemote) {
			updateSitting();
		} else {//animation counters, not needed on server
			updateAnimations();
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Attributes
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}
	
	@Override
	public void setupAttributes() {
		if(getGender() == Gender.None) {
			setGender(rand.nextInt(2) == 0 ? Gender.Male : Gender.Female); 
		}
		
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(calculateAttackDmg());
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(getFollowRange());
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		setHealth(getMaxHealth());
		
		super.setupAttributes();
	}
	
	public float calculateMaxHealth() {
		return 20.0f;
	}
	
	public double calculateAttackDmg() {
		return 1.0;
	}
	
	public float getMoveSpeed() {
		return 0.18f;
	}
	
	/** Returns the distance at which the animal attacks prey */
	public double getFollowRange() {
		return 6.0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(2, new EntityAIPanicMoC(this, 1.1));
		tasks.addTask(3, new EntityAIAvoidPlayer(this, 4.0f, 1.1, 1.1, e -> !isTamed()));
		tasks.addTask(4, new EntityAIAttackMelee(this, 1.0, true));
		tasks.addTask(7, new EntityAIWanderMoC2(this, 0.9));
		tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
		targetTasks.addTask(1, new EntityAIHunt(this, EntityAnimal.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}
	@Override
	public void atAttention() {
		alerted = true;
		setSitting(false);
	}
	
	@Override
	public void atEase() {
		alerted = false;
	}
	
	@Override
	public float getAIMoveSpeed() {
		return isSprinting() ? 0.37F : 0.18F;
	}
	
	public boolean isNotScared() {
		return isAdult();
	}
	
	public boolean shouldAttackPlayers() {
		return !isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Movement Special
	////////////////////////////////////////////////////////////////
	
	public boolean isMovementCeased() {
		return isSitting() || isBeingRidden();
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isBeingRidden();
	}
	
	
	////////////////////////////////////////////////////////////////
	// Death
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
		boolean gravidFemale = isAdult() && getGender() == Gender.Female && rand.nextInt(5) == 0;
		
		if (gravidFemale) {
			entityDropItem(new ItemStack(MoCItems.mocegg, rand.nextInt(2) + 1, 33), 0.0F);
		}
		
		entityDropItem(new ItemStack(MoCItems.hideCroc, 1, 0), 0.0F);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Spawning
	////////////////////////////////////////////////////////////////
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}
	
	@Override
	public boolean getCanSpawnHere() {
		return getCanSpawnHereCreature() && getCanSpawnHereLiving();
	}
	
	public boolean getCanSpawnHereCreature() {
		BlockPos pos = new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY), MathHelper.floor(posZ));
		return getBlockPathWeight(pos) >= 0.0F;
	}
	
	public boolean getCanSpawnHereLiving() {
		return world.checkNoEntityCollision(getEntityBoundingBox())
				&& world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
				&& !world.containsAnyLiquid(getEntityBoundingBox());
	}
	
	
	////////////////////////////////////////////////////////////////
	// Swimming
	////////////////////////////////////////////////////////////////
	
	/*@Override
	public boolean isAmphibian() {
		return true;
	}
	 */
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	public boolean isSwimming() {
		return isInWater();
	}
	
	
	////////////////////////////////////////////////////////////////
	// Hunting/Attacking
	////////////////////////////////////////////////////////////////
	
	protected int huntingCounter;
	
	public void updateHunting() {
		
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return !(entity instanceof EntityKomodo) && super.canAttackTarget(entity);
	}
	
	public boolean isReadyToHunt() {
		return isNotScared() && !isMovementCeased() && !isBeingRidden();
	}
	
	public boolean getIsHunting() {
		return huntingCounter != 0;
	}
	
	public void setIsHunting(boolean flag) {
		huntingCounter = flag ? rand.nextInt(30) + 1 : 0;
	}
	
	@Override
	protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
		((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 150, 0));
		super.applyEnchantments(entityLivingBaseIn, entityIn);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();
			
			if ((entity != null && isTamed() && entity instanceof EntityPlayer) || !(entity instanceof EntityLivingBase)) {
				return false;
			}
			
			if ((isBeingRidden()) && (entity == getRidingEntity())) {
				return false;
			}
			
			if ((entity != this) && (shouldAttackPlayers())) {
				setAttackTarget((EntityLivingBase) entity);
			}
			
			return true;
		}
		return false;
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	// --COMPONENTS--
	//
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void setupComponents() {
		loader.assemble(this);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Taming
	////////////////////////////////////////////////////////////////
	
	protected final ComponentTame tame;
	
	public boolean isTamed() { return tame.isTamed(); }
	@Override public void setTamedBy(EntityPlayer player) { tame.setTamedBy(player); }
	@Override public UUID getOwnerId() { return tame.getOwnerId(); }
	@Override public Entity getOwner() { return tame.getOwner(); }
	
	
	////////////////////////////////////////////////////////////////
	// Age and Gender
	////////////////////////////////////////////////////////////////
	
	protected final ComponentGender gender;
	
	@Override public Gender getGender() { return gender.getGender(); }
	@Override public void setGender(Gender g) { gender.setGender(g); }
	
	public int childhoodDuration() {
		return 20 * 60 * 20;//20 minutes
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityKomodo hatchling = new EntityKomodo(world);
		hatchling.tame.setOwnerId(getOwnerId());
		return hatchling;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Hunger
	////////////////////////////////////////////////////////////////
	
	protected final ComponentHunger hunger;
	
	public boolean isHungry() { return hunger.isHungry(); }
	public boolean isWellFed() { return hunger.isWellFed(); }
	
	
	////////////////////////////////////////////////////////////////
	// Healing
	////////////////////////////////////////////////////////////////
	
	protected final ComponentHeal heal;
	
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	protected final ComponentFeed feed;
	
	protected boolean isEdible(ItemStack stack) {
		return MoCTools.isItemEdibleforCarnivores(stack.getItem());
	}
	
	protected int foodNourishment(ItemStack stack) {
		return isEdible(stack) ? Util.healAmount(stack) * 2 : 0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Riding
	////////////////////////////////////////////////////////////////
	
	protected final ComponentRide ride;
	
	@Override
	public void travel(float strafe, float vertical, float forward) {
		if(isSitting() || ride.travel(strafe, vertical, forward)) {
			return;
		}
		super.travel(strafe, vertical, forward);
	}
	
	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);
	}
	
	@Override
	public double getMountedYOffset() {
		return 0.45;
	}
	
	public boolean isRideable() {
		return ride.isRideable();
	}
	
	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Sitting
	////////////////////////////////////////////////////////////////
	
	protected final ComponentSit sit;
	
	private int sitCounter;
	
	protected void updateSitting() {
		
		if(sitCounter > 0 && (++sitCounter > 150 || isBeingRidden())) {
			setSitting(false);
			sitCounter = 0;
		}
		
		/*if(sitCounter == 0 && isSitting()) {
			setSitting(false);
		}*/
		
		if (!isSwimming() && !isMovementCeased() && rand.nextInt(isTamed() ? 250 : 500) == 0) {
			sit();
		}
	}
	
	private void sit() {
		sitCounter = 1;
		setSitting(true);
		getNavigator().clearPath();
	}
	
	@Override
	public boolean isSitting() {
		return sit.isSitting();
	}
	
	public void setSitting(boolean sitting) {
		sit.setSitting(sitting);
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	// --CLIENT SIDE--
	//
	////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////
	// Sounds
	////////////////////////////////////////////////////////////////
	
	@Override
	protected SoundEvent getDeathSound() {
		doAnimation(Animation.openMouth);
		return MoCSoundEvents.ENTITY_SNAKE_DEATH;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		doAnimation(Animation.openMouth);
		return MoCSoundEvents.ENTITY_SNAKE_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		doAnimation(Animation.openMouth);
		return MoCSoundEvents.ENTITY_SNAKE_AMBIENT;
	}
	
	@Override
	public int getTalkInterval() {
		return 500;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Animations
	////////////////////////////////////////////////////////////////
	
	//TODO: Animation tracker object instead of raw value juggling
	public int tailCounter;
	public int tongueCounter;
	public int mouthCounter;
	
	@Override
	public void doAnimation(Animation animation) {
		switch(animation) {
			case openMouth: mouthCounter = 1; return;
			case moveTail: tailCounter = 1; return;
			case flickTongue: tongueCounter = 1; return;
			default: return;
		}
	}
	
	protected void updateAnimations() {
		
		if (rand.nextInt(100) == 0) {
			doAnimation(Animation.moveTail);
		}
		
		if (rand.nextInt(100) == 0) {
			doAnimation(Animation.flickTongue);
		}

		if (tailCounter > 0 && ++tailCounter > 60) {
			tailCounter = 0;
		}
		
		if (mouthCounter > 0 && ++mouthCounter > 30) {
			mouthCounter = 0;
		}
		
		if (tongueCounter > 0 && ++tongueCounter > 20) {
			tongueCounter = 0;
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Rendering
	////////////////////////////////////////////////////////////////
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("komododragon.png");
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
		return isAdult() ? 1.2f : 0.5f;
	}
	
}

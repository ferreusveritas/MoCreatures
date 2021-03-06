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
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFollowAdult;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIOwnableFollowOwner;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.components.ComponentChest;
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

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class EntityBigCat extends EntityAnimalComp implements IGender, ITame, IModelRenderInfo {
	
	private static Class thisClass = EntityBigCat.class;
	
	public static ComponentLoader<EntityBigCat> loader = new ComponentLoader<>(
			Components.FoodTame(thisClass, (a, s) -> s.getItem() == Items.BEEF),
			Components.Gender(thisClass),
			Components.Heal(thisClass, 0.5f, a -> a.isHungry() ? false : a.world.rand.nextInt(a.isWellFed() ? 100 : 250) == 0),
			Components.Hunger(thisClass, a -> a.rand.nextFloat() * 6.0f, 12.0f, (a, i) -> a.foodNourishment(i) ),
			Components.Feed(thisClass, false),
			Components.Ride(thisClass),
			Components.Chest(thisClass, "BigCatChest"),
			Components.Sit(thisClass)
			);
	
	public boolean alerted;
	
	public EntityBigCat(World world) {
		super(world);
		setGrowingAge(0);
		setSize(1.4F, 1.3F);
		stepHeight = 1.0F;
		
		tame = getComponent(ComponentTame.class);
		gender = getComponent(ComponentGender.class);
		heal = getComponent(ComponentHeal.class);
		hunger = getComponent(ComponentHunger.class);
		feed = getComponent(ComponentFeed.class);
		ride = getComponent(ComponentRide.class);
		chest = getComponent(ComponentChest.class);
		sit = getComponent(ComponentSit.class);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		updateNavigation();
		
		updateAnimations();
		
		seekOutFoodItems();
	}
	
	protected void updateNavigation() {
		if(isServer()) {
			if (isMovementCeased()) {
				getNavigator().clearPath();
			}
			updateHunting();
			getNavigator().onUpdateNavigation();
			setSprinting(getAttackTarget() != null);
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
		return isAdult() ? 20.0f : 10.0f;
	}
	
	public double calculateAttackDmg() {
		return 5.0;
	}
	
	/** Returns the distance at which the animal attacks prey */
	public double getFollowRange() {
		return 6.0;
	}
	
	public float getMoveSpeed() {
		return 1.6f;
	}
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, true));
		tasks.addTask(4, new EntityAIFollowAdult(this, 1.0));
		tasks.addTask(5, new EntityAIOwnableFollowOwner(this, 1.0, 2.0f, 10.0f));
		tasks.addTask(2, new EntityAIWanderMoC2(this, 0.8, 30));
		targetTasks.addTask(3, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
		targetTasks.addTask(4, new EntityAIHunt(this, EntityAnimal.class, true));
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
	
	@Override
	public boolean isMovementCeased() {
		return isSitting() || isBeingRidden();
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
	
	@Override
	public void fall(float f, float f1) {
		float i = (float) (Math.ceil(f - 3.0f) / 2.0f);
		if (isServer() && (i > 0)) {
			i /= 2;
			if (i > 1F) {
				attackEntityFrom(DamageSource.FALL, i);
			}
			if ((this.isBeingRidden()) && (i > 1F)) {
				for (Entity entity : getRecursivePassengers()) {
					entity.attackEntityFrom(DamageSource.FALL, i);
				}
			}
			IBlockState iblockstate = world.getBlockState(new BlockPos(posX, posY - 0.2D - prevRotationYaw, this.posZ));
			Block block = iblockstate.getBlock();
			
			if (iblockstate.getMaterial() != Material.AIR && !isSilent()) {
				SoundType soundtype = block.getSoundType(iblockstate, world, new BlockPos(posX, posY - 0.2D - prevRotationYaw, posZ), this);
				world.playSound((EntityPlayer)null, posX, posY, posZ, soundtype.getStepSound(), getSoundCategory(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
			}
		}
	}
	
	public boolean isOnAir() {
		return (world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 0.2D), MathHelper.floor(posZ))) && 
				world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1.2D), MathHelper.floor(posZ)))
				);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Healing
	////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	protected boolean isEdible(ItemStack stack) {
		return MoCTools.isItemEdibleforCarnivores(stack.getItem());
	}
	
	protected int foodNourishment(ItemStack stack) {
		return isEdible(stack) ? Util.healAmount(stack) * 2 : 0;
	}
	
	public boolean isMyFavoriteFood(ItemStack stack) {
		return false;
	}
	
	protected void seekOutFoodItems() {
		if ( !isWellFed() && !isCorpse() && !isBeingRidden() && !isMovementCeased() && (!isTamed() || isHungry()) ) {
			EntityItem edibleItem = Util.getClosestItem(this, 12.0, e -> isEdible(e.getItem()) );
			if (edibleItem != null) {
				float distance = edibleItem.getDistance(this);
				if (distance > 2.0F) {
					Util.getMyOwnPath(this, edibleItem, distance);
				} else {
					feed.feed(edibleItem);
				}
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Death
	////////////////////////////////////////////////////////////////
	
	@Override
	protected Item getDropItem() {
		return MoCItems.bigcatclaw;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Spawning
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Swimming
	////////////////////////////////////////////////////////////////
	
	public boolean isSwimming() {
		return isInWater();
	}
	
	
	////////////////////////////////////////////////////////////////
	// Hunting/Attacking
	////////////////////////////////////////////////////////////////
	
	protected int huntingCounter;
	
	public void updateHunting() {
		
		if (MoCreatures.proxy.enableHunters && isReadyToHunt() && !getIsHunting() && rand.nextInt(500) == 0) {
			setIsHunting(true);
		}
		
		if (getIsHunting() && ++huntingCounter > 50) {
			setIsHunting(false);
		}
		
	}
	
	public boolean isReadyToHunt() {
		return isAdult() && isNotScared() && !isMovementCeased();
	}
	
	public boolean getIsHunting() {
		return huntingCounter != 0;
	}
	
	public void setIsHunting(boolean hunt) {
		huntingCounter = hunt ? rand.nextInt(30) + 1 : 0;
	}
	
	// Method used for receiving damage from another source
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if ((isBeingRidden()) && (entity == getRidingEntity())) {
			return false;
		}
		
		if (super.attackEntityFrom(damagesource, i)) {
			if (entity != null && isTamed() && entity instanceof EntityPlayer) {
				return false;
			}
			if (world.getDifficulty() != EnumDifficulty.PEACEFUL) {
				setAttackTarget(entity);
			}
			return true;
		} else {
			return false;
		}
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
	
	
	////////////////////////////////////////////////////////////////
	// Riding
	////////////////////////////////////////////////////////////////
	
	protected final ComponentRide ride;
	
	@Override
	public double getCustomSpeed() {
		return 2.0;
	}
	
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
		/*double dist = getSizeFactor() * (0.1D);
		double newPosX = this.posX + (dist * Math.sin(this.renderYawOffset / 57.29578F));
		double newPosZ = this.posZ - (dist * Math.cos(this.renderYawOffset / 57.29578F));
		passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);*/
	}
	
	@Override
	public double getMountedYOffset() {
		return 0.75 * getSizeFactor();
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
	// Chest
	////////////////////////////////////////////////////////////////
	
	protected final ComponentChest chest;
	
	public boolean isChested() {
		return chest.isChested();
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
	public int getTalkInterval() {
		return 400;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		doAnimation(Animation.openMouth);
		return isAdult() ? MoCSoundEvents.ENTITY_LION_DEATH : MoCSoundEvents.ENTITY_LION_DEATH_BABY;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		doAnimation(Animation.openMouth);
		return isAdult() ? MoCSoundEvents.ENTITY_LION_HURT : MoCSoundEvents.ENTITY_LION_HURT_BABY;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		doAnimation(Animation.openMouth);
		return isAdult() ? MoCSoundEvents.ENTITY_LION_AMBIENT : MoCSoundEvents.ENTITY_LION_AMBIENT_BABY;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Animations
	////////////////////////////////////////////////////////////////
	
	public int mouthCounter;
	public int tailCounter;
	
	@Override
	public void doAnimation(Animation animation) {
		switch(animation) {
			case openMouth: mouthCounter = 1; return;
			case moveTail: tailCounter = 1; return;
			default: return;
		}
	}
	
	protected void updateAnimations() {
		if(isClient()) {
			if (mouthCounter > 0 && ++mouthCounter > 30) {
				mouthCounter = 0;
			}
			
			if (rand.nextInt(250) == 0) {
				doAnimation(Animation.moveTail);
			}
			
			if (tailCounter > 0 && ++tailCounter > 10 && rand.nextInt(15) == 0) {
				tailCounter = 0;
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Rendering
	////////////////////////////////////////////////////////////////
	
	@Override
	public ResourceLocation getTexture() {
		return null;
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
		return isAdult() ? 1.2f : 0.6f * getSpeciesSize();
	}
	
	public float getSpeciesSize() {
		return 1.0F;
	}
	
	public boolean hasMane() {
		return false;
	}
	
	public boolean hasSaberTeeth() {
		return false;
	}
	
}

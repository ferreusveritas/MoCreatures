package com.ferreusveritas.mocreatures.entity.passive;

import java.util.UUID;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ITame;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFollowAdult;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIOwnableFollowOwner;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.components.ComponentChest;
import com.ferreusveritas.mocreatures.entity.components.ComponentFeed;
import com.ferreusveritas.mocreatures.entity.components.ComponentGender;
import com.ferreusveritas.mocreatures.entity.components.ComponentHeal;
import com.ferreusveritas.mocreatures.entity.components.ComponentHunger;
import com.ferreusveritas.mocreatures.entity.components.ComponentLoader;
import com.ferreusveritas.mocreatures.entity.components.ComponentRide;
import com.ferreusveritas.mocreatures.entity.components.ComponentStandSit;
import com.ferreusveritas.mocreatures.entity.components.ComponentStandSit.Posture;
import com.ferreusveritas.mocreatures.entity.components.ComponentTame;
import com.ferreusveritas.mocreatures.entity.components.ComponentTameFood;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public abstract class EntityBear extends EntityAnimalComp implements IGender, ITame, IModelRenderInfo {
	
	public static ComponentLoader<EntityBear> loader = new ComponentLoader<>(
			animal -> new ComponentTameFood<>(EntityBear.class, animal, (a, s) -> animal.isMyFavoriteFood(s) ),
			animal -> new ComponentGender<>(EntityBear.class, animal),
			animal -> new ComponentHeal<>(EntityBear.class, animal, 0.5f, a -> a.isHungry() ? false : a.world.rand.nextInt(a.isWellFed() ? 100 : 250) == 0),
			animal -> new ComponentHunger<>(EntityBear.class, animal, animal.rand.nextFloat() * 6.0f, 12.0f, (a, i) -> animal.isEdible(i) ? Util.healAmount(i) : 0),
			animal -> new ComponentFeed<>(EntityBear.class, animal, false),
			animal -> new ComponentRide<>(EntityBear.class, animal),
			animal -> new ComponentChest<>(EntityBear.class, animal, "BigBearChest"),
			animal -> new ComponentStandSit<>(EntityBear.class, animal)
			);
	
	public EntityBear(World world) {
		super(world);
		setSize(1.2F, 1.5F);
		setGrowingAge(rand.nextInt(4) == 0 ? -24000 : 0);
		stepHeight = 1.0F;
		
		tame = getComponent(ComponentTame.class);
		gender = getComponent(ComponentGender.class);
		heal = getComponent(ComponentHeal.class);
		hunger = getComponent(ComponentHunger.class);
		feed = getComponent(ComponentFeed.class);
		ride = getComponent(ComponentRide.class);
		chest = getComponent(ComponentChest.class);
		standSit = getComponent(ComponentStandSit.class);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		updateAnimations();
		
		updateHunting();
		
		if(isServer()) {
			
			updateSitting();
			
			// Bear will stand if close to a vulnerable player
			if (!isTamed() && getIsStanding() && isAdult() && (rand.nextInt(200) == 0) && shouldAttackPlayers()) {
				EntityPlayer entityplayer1 = world.getClosestPlayerToEntity(this, 4D);
				if ((entityplayer1 != null && canEntityBeSeen(entityplayer1) && !entityplayer1.capabilities.disableDamage)) {
					setStand();
				}
			}
			
			seekOutFoodItems();
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
		gender.resolveGender(rand);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(calculateAttackDmg());
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(getFollowRange());
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		setHealth(getMaxHealth());
		super.setupAttributes();
	}
	
	public float calculateMaxHealth() {
		return isAdult() ? 30 : 12;
	}
	
	/** The damage the creature is capable of*/
	public double calculateAttackDmg() {
		return 5.0;
	}
	
	/** Returns the distance at which the animal attacks prey */
	public double getFollowRange() {
		return 8;
	}
	
	public float getMoveSpeed() {
		return 0.16f;
	}
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIPanicMoC(this, 1.0));
		tasks.addTask(3, new EntityAIOwnableFollowOwner(this, 1.0, 4.0f, 10.0f));
		tasks.addTask(4, new EntityAIFollowAdult(this, 1.0));
		tasks.addTask(5, new EntityAIAttackMelee(this, 1.0, true));
		tasks.addTask(6, new EntityAIWanderMoC2(this, 1.0));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
		targetTasks.addTask(1, new EntityAIHunt(this, EntityAnimal.class, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}
	
	@Override
	public void atAttention() {
		setSitting(false);
	}
	
	@Override
	public float getAIMoveSpeed() {
		return isSprinting() ? 0.37F : 0.18F;
	}
	
	@Override
	public boolean isMovementCeased() {
		return isSitting();
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
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	protected boolean isEdible(ItemStack stack) {
		return stack.getItem() instanceof ItemFood;
	}
	
	public boolean isMyFavoriteFood(ItemStack stack) {
		return stack.getItem() == Items.PUMPKIN_PIE; //TODO: Change to honey using Ore Dictionary
	}
	
	protected int foodNourishment(ItemStack stack) {
		return Util.healAmount(stack);
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
		return MoCItems.animalHide;
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
	
	private int attackCounter;
	
	protected void updateHunting() {
		if (attackCounter > 0 && ++attackCounter > 9) {
			attackCounter = 0;
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();
			if (isRidingOrBeingRiddenBy(entity)) {
				return true;
			}
			setAttackTarget(entity);
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return !(entity instanceof EntityBear) && super.canAttackTarget(entity);
	}
	
	private void startAttack() {
		if (isServer() && attackCounter == 0 && getPosture() == Posture.Standing) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0),
					new TargetPoint(world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
			attackCounter = 1;
		}
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		startAttack();
		return super.attackEntityAsMob(entityIn);
	}
	
	public float getAttackSwing() {
		if (attackCounter == 0)
			return 0;
		return 1.5F + (attackCounter / 10F - 10F) * 5F;
	}
	
	public boolean isReadyToHunt() {
		return isAdult() && isNotScared() && !isMovementCeased();
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
		return isSitting() ? 0.0 : 1.0;
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
		double dist = getSizeFactor() * 0.25;
		double radians = Math.toRadians(renderYawOffset);
		double newPosX = posX + (dist * Math.sin(radians));
		double newPosZ = posZ - (dist * Math.cos(radians));
		passenger.setPosition(newPosX, posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
	}
	
	@Override
	public double getMountedYOffset() {
		return 1.15 * getSizeFactor();
	}
	
	public boolean isRideable() {
		return ride.isRideable();
	}
	
	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Standing/Sitting
	////////////////////////////////////////////////////////////////
	
	protected final ComponentStandSit standSit;
	private int standingCounter;
	
	protected void updateSitting() {
		
		// Sit now and then if the bear isn't tamed
		if (isAdult() && !isTamed() && (rand.nextInt(600) == 0)) {
			setPosture(Posture.Sitting);
		}
		
		// Bear cubs sit more
		if (!isAdult() && (rand.nextInt(300) == 0)) {
			setPosture(Posture.Sitting);
		}
		
		if(getPosture() == Posture.Sitting && !isTamed()) {
			// Sitting non tamed bears will resume on fours stance every now and then.  Will also resume if the navigation path is set
			if (rand.nextInt(800) == 0 || !getNavigator().noPath()) {
				setPosture(Posture.AllFours);
			}
		}
		
		if (standingCounter > 0 && ++standingCounter > 100) {
			standingCounter = 0;
			setPosture(Posture.AllFours);
		}
		
	}
	
	@Override
	public boolean isSitting() {
		return getPosture() == Posture.Sitting;
	}
	
	public void setSitting(boolean sitting) {
		if(sitting) {
			getNavigator().clearPath();
		}
		setPosture(sitting ? Posture.Sitting : Posture.AllFours);
	}
	
	public Posture getPosture() {
		return standSit.getPosture();
	}
	
	public void setPosture(Posture posture) {
		standSit.setPosture(posture);
	}
	
	public boolean getIsStanding(){
		return getPosture() == Posture.Standing;
	}
	
	public void setStand(){
		standingCounter = 1;
		setPosture(Posture.Standing);
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
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_BEAR_DEATH;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		openMouth();
		return MoCSoundEvents.ENTITY_BEAR_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		openMouth();
		return MoCSoundEvents.ENTITY_BEAR_AMBIENT;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Animations
	////////////////////////////////////////////////////////////////
	
	public int mouthCounter;
	
	protected void updateAnimations() {
		if (mouthCounter > 0 && ++mouthCounter > 20) {
			mouthCounter = 0;
		}
	}
	
	private void openMouth() {
		mouthCounter = 1;
	}
	
	public void performAnimation(int i) {
		attackCounter = 1;
	}
	
	protected void eatingAnimal() {
		openMouth();
		MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
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
		return (isAdult() ? 1.0f : 0.5f) * getSpeciesSize();
	}
	
	/**
	 * Factor size for the bear species, grizzlys are bigger and pandas smaller
	 *
	 * @return the factor size
	 */
	public float getSpeciesSize() {
		return 1.0f;
	}
	
}

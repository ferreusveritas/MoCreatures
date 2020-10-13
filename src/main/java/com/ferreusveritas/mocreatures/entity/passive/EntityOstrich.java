package com.ferreusveritas.mocreatures.entity.passive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;
import com.ferreusveritas.mocreatures.entity.ITame;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFollowAdult;
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
import com.ferreusveritas.mocreatures.entity.components.ComponentTameFood;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityOstrich extends EntityAnimalComp implements IGender, ITame, IModelRenderInfo {
	
	public static ComponentLoader<EntityOstrich> loader = new ComponentLoader<>(
			animal -> new ComponentTameFood<>(EntityOstrich.class, animal, (a, s) -> s.getItem() == Items.CHICKEN),
			animal -> new ComponentGender<>(EntityOstrich.class, animal),
			animal -> new ComponentHeal<>(EntityOstrich.class, animal, 0.5f, a -> a.isHungry() ? false : a.world.rand.nextInt(a.isWellFed() ? 100 : 250) == 0),
			animal -> new ComponentHunger<>(EntityOstrich.class, animal, animal.rand.nextFloat() * 6.0f, 12.0f, (a, i) -> animal.foodNourishment(i) ),
			animal -> new ComponentFeed<>(EntityOstrich.class, animal, false),
			animal -> new ComponentRide<>(EntityOstrich.class, animal),
			animal -> new ComponentChest(EntityOstrich.class, animal, "OstrichChest"),
			animal -> new ComponentSit<>(EntityOstrich.class, animal)
			);
	
	
	private static final DataParameter<Byte> FLAG_COLOR = EntityDataManager.<Byte>createKey(EntityOstrich.class, DataSerializers.BYTE);
	
	public EntityOstrich(World world) {
		super(world);
		setSize(1.0F, 1.6F);
		eggCounter = rand.nextInt(1000) + 1000;
		stepHeight = 1.0F;
		canLayEggs = false;
		
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
		dataManager.register(EGG_WATCH, Boolean.valueOf(false));
		dataManager.register(FLAG_COLOR, Byte.valueOf((byte) -1));
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if (getHiding()) {
			prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
		}
		
		updateAnimations();
		
		if (sprintCounter > 0 && ++sprintCounter > 300) {
			sprintCounter = 0;
		}
		
		if (isServer()) {
			updateSitting();
			updateEggWatching();
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		return 
				eggLayingInteraction(player, hand, stack) || 
				flagInteraction(player, hand, stack) || 
				handleHelmetInteraction(player, hand, stack) ||
				super.processInteract(player, hand);
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
	
	public double calculateAttackDmg() {
		return 3.0;
	}
	
	//TODO: Change max health when animals becomes an adult
	public float calculateMaxHealth() {
		return isAdult() ? ( getGender() == Gender.Male ? 30.0f : 24.0f) : 12.0f; 
	}
	
	/** Returns the distance at which the animal attacks prey */
	public double getFollowRange() {
		return 6.0;
	}
	
	public float getMoveSpeed() {
		return 0.25f;
	}
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(4, new EntityAIFollowAdult(this, 1.0D));
		tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		tasks.addTask(6, new EntityAIWanderMoC2(this, 1.0D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
	}
	
	@Override
	public boolean isNotScared() {
		return isAdult() && getGender() == Gender.Male && getAttackTarget() != null;
	}
	
	public boolean shouldAttackPlayers() {
		return !isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Movement Special
	////////////////////////////////////////////////////////////////
	
	public int sprintCounter;
	
	@Override
	public void setJumpPending(boolean val) {
		if(val) {
			if(jumpCounter > 5) {
				jumpCounter = 1;
			}
			if(jumpCounter == 0) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_WINGFLAP);
				super.setJumpPending(val);
				jumpCounter = 1;
			}
		} else {
			super.setJumpPending(val);
		}
	}
	
	@Override
	public boolean isMovementCeased() {
		return (getHiding() || isBeingRidden());
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isBeingRidden();
	}
	
	public boolean isOnAir() {
		return (world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 0.2D), MathHelper.floor(posZ))) && 
				world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1.2D), MathHelper.floor(posZ))) );
	}
	
	
	////////////////////////////////////////////////////////////////
	// Healing
	////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	public boolean isMyHealFood(ItemStack par1ItemStack) {
		return MoCTools.isItemEdible(par1ItemStack.getItem());
	}
	
	
	////////////////////////////////////////////////////////////////
	// Death
	////////////////////////////////////////////////////////////////
	
	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		dropFlag();
		dropHelmet();
	}
	
	@Override
	protected Item getDropItem() {
		return Items.FEATHER;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Spawning
	////////////////////////////////////////////////////////////////
	
	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
		
		//spawns in deserts and plains
		//return getCanSpawnHereCreature() && getCanSpawnHereLiving();
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Swimming
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Hunting/Attacking
	////////////////////////////////////////////////////////////////
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		OstrichHelmetType helm = getHelmet();
		
		if (isTamed() && helm != OstrichHelmetType.None) {
			amount -= helm.dmgReduction;
			if (amount <= 0) {
				amount = 1;
			}
		}
		
		if (super.attackEntityFrom(source, amount)) {
			Entity entity = source.getTrueSource();
			
			if (!(entity instanceof EntityLivingBase) || ((isBeingRidden()) && (entity == getRidingEntity()))
					|| (entity instanceof EntityPlayer && isTamed())) {
				return false;
			}
			
			if (shouldAttackPlayers() && isAdult() && getGender() == Gender.Male) {
				setAttackTarget(entity);
				flapWings();
			}
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (entityIn instanceof EntityPlayer && !shouldAttackPlayers()) {
			return false;
		}
		openMouth();
		flapWings();
		return super.attackEntityAsMob(entityIn);
	}
	
	
	////////////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////////////
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setEggWatching(nbttagcompound.getBoolean("EggWatch"));
		setFlagColor(nbttagcompound.getByte("FlagColor"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("EggWatch", isEggWatching());
		nbttagcompound.setByte("FlagColor", (byte) getFlagColor());
	}
	
	
	
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
	
	protected final ComponentTame tame;
	
	public boolean isTamed() { return tame.isTamed(); }
	@Override public void setTamedBy(EntityPlayer player) { tame.setTamedBy(player); }
	@Override public UUID getOwnerId() { return tame.getOwnerId(); }
	@Override public Entity getOwner() { return tame.getOwner(); }
	
	
	////////////////////////////////////////////////////////////////
	//Age and Gender
	////////////////////////////////////////////////////////////////
	
	protected final ComponentGender gender;
	
	@Override public Gender getGender() { return gender.getGender(); }
	@Override public void setGender(Gender g) { gender.setGender(g); }
	
	public int childhoodDuration() {
		return 20 * 60 * 20;//20 minutes
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityOstrich chick = new EntityOstrich(world);
		chick.tame.setOwnerId(getOwnerId());
		return chick;
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
		return MoCTools.isItemEdible(stack.getItem());
	}
	
	protected int foodNourishment(ItemStack stack) {
		return isEdible(stack) ?  Util.healAmount(stack) : 0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Riding
	////////////////////////////////////////////////////////////////
	
	protected final ComponentRide ride;
	
	@Override
	public double getCustomSpeed() {
		double OstrichSpeed = getGender() == Gender.Male ? 1.1f : 0.8f;
		
		if (sprintCounter > 0 && sprintCounter < 200) {
			OstrichSpeed *= 1.5D;
		}
		if (sprintCounter > 200) {
			OstrichSpeed *= 0.5D;
		}
		return OstrichSpeed;
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
	//Sitting
	////////////////////////////////////////////////////////////////
	
	protected final ComponentSit sit;
	private int hidingCounter;
	
	protected void updateSitting() {
		
		if (getHiding()) {
			//wild shy ostriches will hide their heads only for a short term
			//tamed ostriches will keep their heads hidden until the whip is used again
			if (++hidingCounter > 500 && !isTamed()) {
				setSitting(false);
				hidingCounter = 0;
			}
			
		}
		
	}
	
	@Override
	public boolean isSitting() {
		return sit.isSitting();
	}
	
	public void setSitting(boolean sitting) {
		sit.setSitting(sitting);
	}
	
	public boolean getHiding() {
		return isSitting();
	}
	
	
	////////////////////////////////////////////////////////////////
	//Chest
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Chest
	////////////////////////////////////////////////////////////////
	
	protected final ComponentChest chest;
	
	public boolean isChested() {
		return chest.isChested();
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	//--CLIENT SIDE--
	//
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	//Sounds
	////////////////////////////////////////////////////////////////
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		openMouth();
		return MoCSoundEvents.ENTITY_OSTRICH_HURT;
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		openMouth();
		return isAdult() ? MoCSoundEvents.ENTITY_OSTRICH_AMBIENT : MoCSoundEvents.ENTITY_OSTRICH_AMBIENT_BABY;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		openMouth();
		return MoCSoundEvents.ENTITY_OSTRICH_DEATH;
	}
	
	
	////////////////////////////////////////////////////////////////
	//Animations
	////////////////////////////////////////////////////////////////
	
	public int mouthCounter;
	public int wingCounter;
	public int jumpCounter;
	
	
	protected void updateAnimations() {
		if (mouthCounter > 0 && ++mouthCounter > 20) {
			mouthCounter = 0;
		}
		
		if (wingCounter > 0 && ++wingCounter > 80) {
			wingCounter = 0;
		}
		
		if (jumpCounter > 0 && ++jumpCounter > 8) {
			jumpCounter = 0;
		}
	}
	
	private void openMouth() {
		mouthCounter = 1;
	}
	
	private void flapWings() {
		wingCounter = 1;
	}
	
	
	////////////////////////////////////////////////////////////////
	//Rendering
	////////////////////////////////////////////////////////////////
	
	@Override
	public ResourceLocation getTexture() {
		if(!isAdult()) {
			return MoCreatures.proxy.getTexture("ostrichc.png");
		}
		return MoCreatures.proxy.getTexture(getGender() == Gender.Male ? "ostricha.png" : "ostrichb.png");
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
		return getGender() == Gender.Male ? 1.3f : 1.1f; 
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	//--OSTRICH SPECIFIC--
	//
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Flag
	////////////////////////////////////////////////////////////////
	
	public boolean flagInteraction(EntityPlayer player, EnumHand hand, ItemStack stack) {
		
		if (isTamed() && isChested() && isAdult() && !stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.WOOL)) {
			int colorInt = stack.getItemDamage();
			consumeItemFromStack(player, stack);
			MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
			dropFlag();
			setFlagColor((byte) colorInt);
			return true;
		}
		
		return false;
	}
	
	public int getFlagColor() {
		return dataManager.get(FLAG_COLOR).intValue();
	}
	
	public void setFlagColor(int i) {
		dataManager.set(FLAG_COLOR, Byte.valueOf((byte) i));
	}
	
	/**
	 * Drops a block of the color of the flag if carrying one
	 */
	private void dropFlag() {
		if (isServer() && getFlagColor() != -1) {
			int color = getFlagColor();
			EntityItem entityitem = new EntityItem(world, posX, posY, posZ, new ItemStack(Blocks.WOOL, 1, color));
			entityitem.setPickupDelay(10);
			world.spawnEntity(entityitem);
			setFlagColor((byte) -1);
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Egg Laying
	////////////////////////////////////////////////////////////////
	
	private static final DataParameter<Boolean> EGG_WATCH = EntityDataManager.<Boolean>createKey(EntityOstrich.class, DataSerializers.BOOLEAN);
	private int eggCounter;
	public boolean canLayEggs;
	
	public void updateEggWatching() {
		
		//egg laying
		if (canLayEggs && isAdult() && (getGender() == Gender.Female) && !isEggWatching() && --eggCounter <= 0 && rand.nextInt(5) == 0) {
			EntityPlayer entityplayer1 = world.getClosestPlayerToEntity(this, 12.0);
			if (entityplayer1 != null) {
				double distP = MoCTools.getSqDistanceTo(entityplayer1, posX, posY, posZ);
				if (distP < 10D) {
					int OstrichEggType = 30;
					EntityOstrich maleOstrich = getClosestMaleOstrich(this, 8.0);
					if (maleOstrich != null && rand.nextInt(100) < MoCreatures.proxy.ostrichEggDropChance) {
						MoCEntityEgg entityegg = new MoCEntityEgg(world, OstrichEggType);
						entityegg.setPosition(posX, posY, posZ);
						world.spawnEntity(entityegg);
						
						if (!isTamed()) {
							setEggWatching(true);
							if (maleOstrich != null) {
								maleOstrich.setEggWatching(true);
							}
							openMouth();
						}
						
						//TODO change sound
						MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
						//finds a male and makes it eggWatch as well
						//MoCEntityOstrich entityOstrich = (MoCEntityOstrich) getClosestSpecificEntity(this, MoCEntityOstrich.class, 12D);
						eggCounter = rand.nextInt(2000) + 2000;
						canLayEggs = false;
					}
				}
			}
		}
		
		
		//egg protection
		if (isEggWatching()) {
			//look for and protect eggs and move close
			MoCEntityEgg myEgg = (MoCEntityEgg) Util.getAnyLivingEntity(this, 8.0, EntityOstrich::ostrichEggFilter);
			if (myEgg != null) {// There's an egg
				if (MoCTools.getSqDistanceTo(myEgg, posX, posY, posZ) > 4.0) { // But it's too far away to protect it
					navigator.setPath(navigator.getPathToPos(myEgg.getPosition()), 16.0f); //So move closer to it
				}
			} else { // didn't find egg.  It was there before so it must have been taken
				setEggWatching(false);
				
				EntityPlayer eggStealer = world.getClosestPlayerToEntity(this, 10.0);
				if (eggStealer != null) {
					if (!isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
						setAttackTarget(eggStealer);
						flapWings();
					}
				}
			}
		}
	}
	
	public boolean isEggWatching() {
		return dataManager.get(EGG_WATCH).booleanValue();
	}
	
	public void setEggWatching(boolean flag) {
		dataManager.set(EGG_WATCH, Boolean.valueOf(flag));
	}
	
	public boolean eggLayingInteraction(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (!isTamed() && !stack.isEmpty() && isAdult() && getGender() == Gender.Female && stack.getItem() == Items.MELON_SEEDS) {
			consumeItemFromStack(player, stack);
			openMouth();
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			canLayEggs = true;
			return true;
		}
		return false;
	}
	
	private static boolean adultMaleOstrichFilter(Entity entity) {
		if(entity instanceof EntityOstrich) {
			EntityOstrich ostrichFound = (EntityOstrich) entity;
			return ostrichFound.isAdult() && ostrichFound.isAdult();
		}
		return false;
	}
	
	protected EntityOstrich getClosestMaleOstrich(Entity entity, double distance) {
		double closestDistance = -1.0;
		EntityOstrich closestOstrich = null;
		
		List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(distance, distance, distance), EntityOstrich::adultMaleOstrichFilter);
		for (int i = 0; i < list.size(); i++) {
			Entity ostrichFound = list.get(i);
			
			double thisDistance = ostrichFound.getDistanceSq(entity.posX, entity.posY, entity.posZ);
			if (((distance < 0.0) || (thisDistance < (distance * distance))) && ((closestDistance == -1.0) || (thisDistance < closestDistance))) {
				closestDistance = thisDistance;
				closestOstrich = (EntityOstrich) ostrichFound;
			}
		}
		
		return closestOstrich;
	}
	
	private static boolean ostrichEggFilter(Entity entity) {
		return ((entity instanceof MoCEntityEgg) && (((MoCEntityEgg) entity).eggType == 30));
	}
	
	
	////////////////////////////////////////////////////////////////
	// Helmet
	////////////////////////////////////////////////////////////////
	
	public enum OstrichHelmetType {
		None(0),
		Leather(1),
		Iron(2),
		Golden(3),
		Diamond(4),
		Hide(2),
		Fur(2),
		Reptile(3);
		
		public final int dmgReduction;
		
		private static Map<Item, OstrichHelmetType> helmetMap; 
		
		private OstrichHelmetType(int dmgReduction) {
			this.dmgReduction = dmgReduction;
		}
		
		public static OstrichHelmetType fromItem(ItemStack stack) {
			if(helmetMap == null) {
				helmetMap = new HashMap<>();
				helmetMap.put(Items.LEATHER_HELMET, Leather);
				helmetMap.put(Items.IRON_HELMET, Iron);
				helmetMap.put(Items.GOLDEN_HELMET, Golden);
				helmetMap.put(Items.DIAMOND_HELMET, Diamond);
				helmetMap.put(MoCItems.helmetHide, Hide);
				helmetMap.put(MoCItems.helmetFur, Fur);
				helmetMap.put(MoCItems.helmetCroc,Reptile);
			}
			
			return helmetMap.getOrDefault(stack.getItem(), None);
		}
		
	}
	
	public OstrichHelmetType getHelmet() {
		return OstrichHelmetType.fromItem(getItemStackFromSlot(EntityEquipmentSlot.HEAD));
	}
	
	public boolean handleHelmetInteraction(EntityPlayer player, EnumHand hand, ItemStack stack) {
		
		if (isTamed() && isAdult() && !stack.isEmpty()) {
			Item item = stack.getItem();
			
			if (item instanceof ItemArmor && ((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD) {
				final ItemArmor itemArmor = (ItemArmor) stack.getItem();
				OstrichHelmetType helm = OstrichHelmetType.fromItem(stack);
				
				if (helm != OstrichHelmetType.None) {
					player.setHeldItem(hand, ItemStack.EMPTY);
					dropHelmet();
					setItemStackToSlot(itemArmor.armorType, stack);
					MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_OFF);
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * Drops the helmet
	 */
	public void dropHelmet() {
		if (isServer()) {
			final ItemStack itemStack = getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemArmor) {
				setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
				entityDropItem(itemStack.copy(), 0.0f);
			}
		}
	}
	
}

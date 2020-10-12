package com.ferreusveritas.mocreatures.entity.passive;

import java.util.List;
import java.util.function.Predicate;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.inventory.MoCAnimalChest;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

@Deprecated
public class EntityPredatorMount extends EntityTameable implements IGender {
	
	private static final DataParameter<Byte> GENDER = EntityDataManager.<Byte>createKey(EntityPredatorMount.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> RIDEABLE = EntityDataManager.<Boolean>createKey(EntityPredatorMount.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> CHESTED = EntityDataManager.<Boolean>createKey(EntityPredatorMount.class, DataSerializers.BOOLEAN);
	
	private boolean hasEaten;
	protected int huntingCounter;
	public MoCAnimalChest localchest;
	
	
	public EntityPredatorMount(World worldIn) {
		super(worldIn);
	}
	
	/** Initializes datawatchers for entity. Each datawatcher is used to sync server data to client. */
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(GENDER, (byte)Gender.None.ordinal());
		dataManager.register(RIDEABLE, false);
		dataManager.register(CHESTED, false);
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		selectType();
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	public void selectType() {
		
		if(getGender() == Gender.None) {
			setGender(rand.nextInt(2) == 0 ? Gender.Male : Gender.Female);
		}
		
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackStrength());
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(getFollowRange());
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMoveSpeed());
		setHealth(getMaxHealth());
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}
	
	public boolean isMovementCeased() {
		return isSitting() || isBeingRidden();
	}
	
	
	////////////////////////////////////////////////////////////////
	// Interaction
	////////////////////////////////////////////////////////////////
	
	private boolean checkOwnership(EntityPlayer player, EnumHand hand) {
		return ( (isTamed() && MoCTools.isThisPlayerAnOP(player)) || (player.getUniqueID() == getOwnerId()) );
	}
	
	/** Interaction method that runs if the animal is tame */
	public boolean processInteractTame(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return !stack.isEmpty() && (
				processInteractHealFood(player, hand, stack) || 
				processInteractAmulet(player, hand, stack) || 
				processInteractShear(player, hand, stack) ||
				processInteractSaddle(player, hand, stack) ||
				processInteractChest(player, hand, stack) ||
				processInteractStartRiding(player)
				);
	}
	
	/** Interaction method that runs if the animal is wild */
	public boolean processInteractWild(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return (
				processInteractTameWild(player, hand, stack) || 
				processInteractFeedWild(player, hand, stack)
				);
	}
	
	/** Interaction method that runs regardless of tame/wild state */
	public boolean processInteractCommon(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return false;
	}
	
	/** Interaction method for attemping to make a wild animal tame */
	public boolean processInteractTameWild(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return false;
	}
	
	/** Interaction method for feeding a wild animal */
	public boolean processInteractFeedWild(EntityPlayer player, EnumHand hand, ItemStack stack) {
		return false;
	}
	
	/** Interaction method for feeding a tame animal to heal it */
	public boolean processInteractHealFood(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if ((getHealth() != getMaxHealth()) && isMyHealFood(stack)) {
			consumeItemFromStack(player, stack);
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			if (!world.isRemote) {
				setHealth(getMaxHealth());
			}
			return true;
		}
		return false;
	}
	
	/** Interaction method for storing a tame animal in an amulet */
	public boolean processInteractAmulet(EntityPlayer player, EnumHand hand, ItemStack stack) {
		//stores in petAmulet
		if (stack.getItem() == MoCItems.petamulet && stack.getItemDamage() == 0) {
			player.setHeldItem(hand, ItemStack.EMPTY);
			if (!world.isRemote) {
				//petData.setInAmulet(this.getOwnerPetId(), true);
				dropMyStuff();
				//MoCTools.dropAmulet(this, 2, player);
				isDead = true;
			}
			return true;
		}
		
		return false;
	}
	
	/** Interaction method for shearing an animal */
	public boolean processInteractShear(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (stack.getItem() == Items.SHEARS) {
			if (!world.isRemote) {
				dropMyStuff();
			}
			return true;
		}
		return false;
	}
	
	public boolean processInteractSaddle(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (isAdult() && !getIsRideable() && (stack.getItem() == Items.SADDLE)) {
			consumeItemFromStack(player, stack);
			setRideable(true);
			return true;
		}
		return false;
	}
	
	public boolean processInteractChest(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (isAdult() && !getIsChested() && (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))) {
			consumeItemFromStack(player, stack);
			setIsChested(true);
			MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
			return true;
		}
		
		if (getIsChested() && player.isSneaking()) {
			if (localchest == null) {
				localchest = new MoCAnimalChest(getChestName(), 18);
			}
			if (!world.isRemote) {
				player.displayGUIChest(localchest);
			}
			return true;
		}
		return false;
	}
	
	public void prepareForRiding() { }
	
	public boolean processInteractStartRiding(EntityPlayer player) {
		if (getIsRideable() && isAdult() && !isBeingRidden()) {
			if (!world.isRemote && player.startRiding(this)) {
				player.rotationYaw = rotationYaw;
				player.rotationPitch = rotationPitch;
				prepareForRiding();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		
		if(isTamed()) {
			if(checkOwnership(player, hand)) {
				if(processInteractTame(player, hand, stack)) {
					return true;
				}
			}
		} else {
			if(processInteractWild(player, hand, stack)) {
				return true;
			}
		}
		
		if(processInteractCommon(player, hand, stack)) {
			return true;
		}
		
		return super.processInteract(player, hand);
	}
	
	
	public void setGender(Gender g) {
		dataManager.set(GENDER, (byte)g.ordinal());
	}
	
	public Gender getGender() {
		return Gender.fromByte(dataManager.get(GENDER));
	}
	
	public float calculateMaxHealth() {
		return 10.0f;
	}
	
	public boolean isAdult() {
		return getGrowingAge() > 0;
	}
	
	public float getSpeciesSize() {
		return 1.0f;
	}
	
	public float getMoveSpeed() {
		return 1.0f;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Equipment
	////////////////////////////////////////////////////////////////
	
	public boolean rideableEntity() {
		return true;
	}
	
	public boolean getIsRideable() {
		return dataManager.get(RIDEABLE);
	}
	
	public void setRideable(boolean flag) {
		dataManager.set(RIDEABLE, flag);
	}
	
	public boolean canCarryChest() {
		return true;
	}
	
	public boolean getIsChested() {
		return dataManager.get(CHESTED);
	}
	
	public void setIsChested(boolean flag) {
		dataManager.set(CHESTED, flag);
	}
	
	public String getChestName() {
		return "PredatorChest";
	}
	
	public void dropArmor() { }
	
	protected void dropMyStuff() {
		if (!world.isRemote) {
			dropArmor();
			MoCTools.dropSaddle(this, world);
			
			if (getIsChested()) {
				MoCTools.dropInventory(this, localchest);
				MoCTools.dropCustomItem(this, world, new ItemStack(Blocks.CHEST, 1));
				setIsChested(false);
			}
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	public void setHasEaten(boolean flag) {
		hasEaten = flag;
	}
	
	public boolean getHasEaten() {
		return hasEaten;
	}
	
	protected boolean isMyHealFood(ItemStack stack) {
		return MoCTools.isItemEdibleforCarnivores(stack.getItem());
	}
	
	public boolean isMyFavoriteFood(ItemStack stack) {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Attacking
	////////////////////////////////////////////////////////////////
	
	public boolean isNotScared() {
		return isAdult();
	}
	
	public double calculateAttackDmg() {
		return 5.0;
	}
	
	public double getAttackStrength() {
		return 2;
	}
	
	/**
	 * Returns the distance at which the animal attacks prey
	 *
	 * @return
	 */
	public double getFollowRange() {
		return 6.0;
	}
	
	public boolean canAttackTarget(EntityLivingBase entity) {
		return height >= entity.height && width >= entity.width;
	}
	
	public boolean shouldAttackPlayers() {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Hunting
	////////////////////////////////////////////////////////////////
	
	public boolean isReadyToHunt() {
		return isAdult() && !isMovementCeased();
	}
	
	public boolean getIsHunting() {
		return huntingCounter != 0;
	}
	
	public void setIsHunting(boolean flag) {
		if (flag) {
			huntingCounter = rand.nextInt(30) + 1;
		} else {
			huntingCounter = 0;
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////////////
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setByte("Gender", (byte) getGender().ordinal());
		nbttagcompound.setBoolean("Saddle", getIsRideable());
		nbttagcompound.setBoolean("Chested", getIsChested());
		if (getIsChested() && localchest != null) {
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < localchest.getSizeInventory(); i++) {
				// grab the current item stack
				ItemStack localstack = localchest.getStackInSlot(i);
				if (localstack != null && !localstack.isEmpty()) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					localstack.writeToNBT(tag);
					nbttaglist.appendTag(tag);
				}
			}
			nbttagcompound.setTag("Items", nbttaglist);
		}
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setGender(Gender.fromByte(nbttagcompound.getByte("Gender")));
		setRideable(nbttagcompound.getBoolean("Saddle"));
		setIsChested(nbttagcompound.getBoolean("Chested"));
		if (getIsChested()) {
			NBTTagList items = nbttagcompound.getTagList("Items", 10);
			localchest = new MoCAnimalChest(getChestName(), 18);
			
			for(NBTBase baseTag : items) {
				if(baseTag.getId() == NBT.TAG_COMPOUND) {
					NBTTagCompound tag = (NBTTagCompound) baseTag;
					int j = tag.getByte("Slot");
					if (j >= 0 && j < localchest.getSizeInventory()) {
						localchest.setInventorySlotContents(j, new ItemStack(tag));
					}
				}
			}
		}
		
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
	
	public void getMyOwnPath(Entity entity, float f) {
		Path pathentity = getNavigator().getPathToEntityLiving(entity);
		if (pathentity != null) {
			getNavigator().setPath(pathentity, 1D);
		}
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	public float getSizeFactor() {
		return 1.0f;
	}
	
}

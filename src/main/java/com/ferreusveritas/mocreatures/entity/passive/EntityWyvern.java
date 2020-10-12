package com.ferreusveritas.mocreatures.entity.passive;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityTameableAnimal;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.inventory.MoCAnimalChest;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityWyvern extends MoCEntityTameableAnimal {

	public MoCAnimalChest localchest;
	public ItemStack localstack;
	public int mouthCounter;
	public int wingFlapCounter;
	public int diveCounter;

	protected EntityAIWanderMoC2 wander;
	private int transformType;
	private int transformCounter;
	private int tCounter;
	private float fTransparency;
	private static final DataParameter<Boolean> RIDEABLE = EntityDataManager.<Boolean>createKey(EntityWyvern.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> CHESTED = EntityDataManager.<Boolean>createKey(EntityWyvern.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SITTING = EntityDataManager.<Boolean>createKey(EntityWyvern.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> FLYING = EntityDataManager.<Boolean>createKey(EntityWyvern.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> ARMOR_TYPE = EntityDataManager.<Integer>createKey(EntityWyvern.class, DataSerializers.VARINT);

	private static Map<Integer, WyvernType> map = new HashMap<>();
	private static Map<EnumEgg, WyvernType> eggmap = new HashMap<>();

	public static enum WyvernType {
		None(0, EnumEgg.None, 40, 5),
		Jungle(1, EnumEgg.WyvernJungle, 40, 5),
		Swamp(2, EnumEgg.WyvernSwamp, 40, 5),
		Savanna(3, EnumEgg.WyvernSavanna, 40, 5),
		Sand(4, EnumEgg.WyvernSand, 40, 5),
		Mother(5, EnumEgg.WyvernMother, 80, 12),
		Undead(6, EnumEgg.WyvernUndead, 60, 8),
		Light(7, EnumEgg.WyvernLight, 60, 8),
		Dark(8, EnumEgg.WyvernDark, 60, 8),
		Arctic(9, EnumEgg.WyvernArctic, 40, 5),
		Cave(10, EnumEgg.WyvernCave, 40, 5),
		Mountain(11, EnumEgg.WyvernMountain, 40, 5),
		Sea(12, EnumEgg.WyvernSea, 40, 5);

		public final int type;
		public final EnumEgg egg;
		public final int hp;
		public final int attack;

		private WyvernType(int type, EnumEgg egg, int hp, int attack) {
			this.type = type;
			this.egg = egg;
			this.hp = hp;
			this.attack = attack;
			map.put(type, this);
			eggmap.put(egg, this);
		}
	}

	public EntityWyvern(World world) {
		super(world);
		setSize(1.9F, 1.7F);
		setAdult(false);
		setTamed(false);
		this.stepHeight = 1.0F;

		if (this.rand.nextInt(6) == 0) {
			setAge(50 + this.rand.nextInt(50));
		} else {
			setAge(80 + this.rand.nextInt(20));
		}
	}

	public void setType(WyvernType type) {
		setType(type.type);
	}

	public WyvernType getWyvern() {
		return map.getOrDefault(getType(), WyvernType.None);
	}

	public static WyvernType getWyvern(EnumEgg egg) {
		return eggmap.getOrDefault(egg, WyvernType.None);
	}
		
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(4, this.wander = new EntityAIWanderMoC2(this, 1.0D, 80));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAIHunt(this, EntityAnimal.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(RIDEABLE, Boolean.valueOf(false)); // rideable: 0 nothing, 1 saddle
		this.dataManager.register(SITTING, Boolean.valueOf(false)); // rideable: 0 nothing, 1 saddle
		this.dataManager.register(CHESTED, Boolean.valueOf(false));
		this.dataManager.register(FLYING, Boolean.valueOf(false));
		this.dataManager.register(ARMOR_TYPE, Integer.valueOf(0));// armor 0 by default, 1 metal, 2 gold, 3 diamond, 4 crystaline
	}

	public boolean getIsFlying() {
		return ((Boolean)this.dataManager.get(FLYING)).booleanValue();
	}

	public void setIsFlying(boolean flag) {
		this.dataManager.set(FLYING, Boolean.valueOf(flag));
	}

	@Override
	public int getArmorType() {
		return ((Integer)this.dataManager.get(ARMOR_TYPE)).intValue();
	}

	@Override
	public void setArmorType(int i) {
		this.dataManager.set(ARMOR_TYPE, Integer.valueOf(i));
	}

	@Override
	public boolean getIsRideable() {
		return ((Boolean)this.dataManager.get(RIDEABLE)).booleanValue();
	}

	@Override
	public void setRideable(boolean flag) {
		this.dataManager.set(RIDEABLE, Boolean.valueOf(flag));
	}

	public boolean getIsChested() {
		return ((Boolean)this.dataManager.get(CHESTED)).booleanValue();
	}

	public void setIsChested(boolean flag) {
		this.dataManager.set(CHESTED, Boolean.valueOf(flag));
	}

	@Override
	public boolean getIsSitting() {
		return ((Boolean)this.dataManager.get(SITTING)).booleanValue();
	}

	public void setSitting(boolean flag) {
		this.dataManager.set(SITTING, Boolean.valueOf(flag));
	}

	@Override
	public void selectType() {
		if (getType() == 0) {
			if (rand.nextInt(5) == 0) {
				setType(5);
			} else {
				int i = this.rand.nextInt(100);
				if (i <= 12) {
					setType(WyvernType.Jungle);
				} else if (i <= 24) {
					setType(WyvernType.Swamp);
				} else if (i <= 36) {
					setType(WyvernType.Savanna);
				} else if (i <= 48) {
					setType(WyvernType.Sand);
				} else if (i <= 60) {
					setType(WyvernType.Arctic);
				} else if (i <= 72) {
					setType(WyvernType.Cave);
				} else if (i <= 84) {
					setType(WyvernType.Mountain);
				} else if (i <= 95) {
					setType(WyvernType.Sea);
				} else {
					setType(WyvernType.Mother);
				}
			}
		}
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
		this.setHealth(getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(calculateAttackDmg());
	}

	@Override
	public boolean isNotScared() {
		return true;
	}

	public double calculateMaxHealth() {
		return getWyvern().hp;
	}

	public double calculateAttackDmg() {
		return getWyvern().attack;
	}

	/**
	 * 1-4 regular wyverns
	 * 5 mother wyvern
	 * 6 undead
	 * 7 light
	 * 8 darkness
	 * 9-12 extra wyverns
	 */
	@Override
	public ResourceLocation getTexture() {
		if (this.transformCounter != 0 && this.transformType > 5) {
			String newText = "wyverndark.png";
			if (this.transformType == 6) {
				newText = "wyvernundead.png";
			}
			if (this.transformType == 7) {
				newText = "wyvernlight.png";
			}
			if (this.transformType == 8) {
				newText = "wyverndark.png";
			}

			if ((this.transformCounter % 5) == 0) {
				return MoCreatures.proxy.getTexture(newText);
			}
			if (this.transformCounter > 50 && (this.transformCounter % 3) == 0) {
				return MoCreatures.proxy.getTexture(newText);
			}
			if (this.transformCounter > 75 && (this.transformCounter % 4) == 0) {
				return MoCreatures.proxy.getTexture(newText);
			}
		}

		switch (getWyvern()) {
			case Jungle: return MoCreatures.proxy.getTexture("wyvernjungle.png");
			case Swamp: return MoCreatures.proxy.getTexture("wyvernmix.png");
			case Sand: return MoCreatures.proxy.getTexture("wyvernsand.png");
			case Savanna: return MoCreatures.proxy.getTexture("wyvernsun.png");
			case Mother: return MoCreatures.proxy.getTexture("wyvernmother.png");
			case Undead: return MoCreatures.proxy.getTexture("wyvernundead.png");
			case Light: return MoCreatures.proxy.getTexture("wyvernlight.png");
			case Dark: return MoCreatures.proxy.getTexture("wyverndark.png");
			case Arctic: return MoCreatures.proxy.getTexture("wyvernarctic.png");
			case Cave: return MoCreatures.proxy.getTexture("wyverncave.png");
			case Mountain: return MoCreatures.proxy.getTexture("wyvernmountain.png");
			case Sea: return MoCreatures.proxy.getTexture("wyvernsea.png");
			default: return MoCreatures.proxy.getTexture("wyvernsun.png");
		}
	}

	public void transform(int tType) {
		if (!this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), tType),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		this.transformType = tType;
		this.transformCounter = 1;
	}

	@Override
	public void onLivingUpdate() {

		if (this.wingFlapCounter > 0 && ++this.wingFlapCounter > 20) {
			this.wingFlapCounter = 0;
		}
		if (this.wingFlapCounter == 5 && !this.world.isRemote) {
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_WYVERN_WINGFLAP);
		}

		if (this.transformCounter > 0) {
			if (this.transformCounter == 40) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_TRANSFORM);
			}

			if (++this.transformCounter > 100) {
				this.transformCounter = 0;
				if (this.transformType != 0) {
					setType(this.transformType);
					selectType();
				}
			}
		}

		if (!this.world.isRemote) {
			if (!isMovementCeased() && !this.getIsTamed() && this.rand.nextInt(300) == 0) {
				setIsFlying(!getIsFlying());
			}
			if (isMovementCeased() && getIsFlying()) {
				setIsFlying(false);
			}

			if (getAttackTarget() != null && (!this.getIsTamed() || this.getRidingEntity() != null) && !isMovementCeased() && this.rand.nextInt(20) == 0) {
				setIsFlying(true);
			}

			if (getIsFlying() && this.getNavigator().noPath() && !isMovementCeased() && this.getAttackTarget() == null && rand.nextInt(30) == 0) {
				this.wander.makeUpdate();
			}

			if (this.motionY > 0.5) { // prevent large boundingbox checks
				this.motionY = 0.5;
			}

			if (isOnAir()) {
				float myFlyingSpeed = MoCTools.getMyMovementSpeed(this);
				int wingFlapFreq = (int) (25 - (myFlyingSpeed * 10));
				if (!this.isBeingRidden() || wingFlapFreq < 5) {
					wingFlapFreq = 5;
				}
				if (this.rand.nextInt(wingFlapFreq) == 0) {
					wingFlap();
				}
			}

		} else {

			if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
				this.mouthCounter = 0;
			}

			if (this.diveCounter > 0 && ++this.diveCounter > 5) {
				this.diveCounter = 0;
			}
		}
		super.onLivingUpdate();
	}

	public void wingFlap() {
		if (this.wingFlapCounter == 0) {
			this.wingFlapCounter = 1;
			if (!this.world.isRemote) {
				MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3),
						new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
			}
		}
	}

	@Override
	public float getSizeFactor() {
		return getAge() * 0.01F;
	}

	@Override
	public boolean isFlyingAlone() {
		return getIsFlying() && !this.isBeingRidden();
	}

	@Override
	public int maxFlyingHeight() {
		if (getIsTamed())
			return 5;
		return 18;
	}

	@Override
	public int minFlyingHeight() {
		return 1;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final Boolean tameResult = this.processTameInteract(player, hand);
		if (tameResult != null) {
			return tameResult;
		}

		final ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && (stack.getItem() == MoCItems.whip) && getIsTamed() && (!this.isBeingRidden())) {
			setSitting(!getIsSitting());
			return true;
		}

		if (!stack.isEmpty() && !getIsRideable() && getAge() > 90 && this.getIsTamed()
				&& (stack.getItem() == Items.SADDLE)) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			setRideable(true);
			return true;
		}

		if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.IRON_HORSE_ARMOR) {
			if (getArmorType() == 0) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
			}
			dropArmor();
			setArmorType((byte) 1);
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}

			return true;
		}

		if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.GOLDEN_HORSE_ARMOR) {
			if (getArmorType() == 0) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
			}
			dropArmor();
			setArmorType((byte) 2);
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			return true;
		}

		if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.DIAMOND_HORSE_ARMOR) {
			if (getArmorType() == 0) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
			}
			dropArmor();
			setArmorType((byte) 3);
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			return true;
		}

		if (!stack.isEmpty() && getIsTamed() && getAge() > 90 && !getIsChested() && (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			setIsChested(true);
			MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
			return true;
		}

		if (getIsChested() && player.isSneaking()) {
			if (this.localchest == null) {
				this.localchest = new MoCAnimalChest("WyvernChest", 9);
			}
			if (!this.world.isRemote) {
				player.displayGUIChest(this.localchest);
			}
			return true;
		}

		if (this.getIsRideable() && getAge() > 90 && (!this.getIsChested() || !player.isSneaking()) && !this.isBeingRidden()) {
			if (!this.world.isRemote && player.startRiding(this)) {
				player.rotationYaw = this.rotationYaw;
				player.rotationPitch = this.rotationPitch;
				setSitting(false);
			}

			return true;
		}

		return super.processInteract(player, hand);
	}

	/**
	 * Drops the current armor
	 */
	@Override
	public void dropArmor() {
		if (!this.world.isRemote) {
			int armorType = getArmorType();
			if (armorType != 0) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_OFF);
			}

			if (armorType == 1) {
				EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.IRON_HORSE_ARMOR, 1));
				entityitem.setPickupDelay(10);
				this.world.spawnEntity(entityitem);
			}
			if (armorType == 2) {
				EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.GOLDEN_HORSE_ARMOR, 1));
				entityitem.setPickupDelay(10);
				this.world.spawnEntity(entityitem);
			}
			if (armorType == 3) {
				EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.DIAMOND_HORSE_ARMOR, 1));
				entityitem.setPickupDelay(10);
				this.world.spawnEntity(entityitem);
			}
			setArmorType((byte) 0);
		}
	}

	@Override
	public boolean rideableEntity() {
		return true;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_WYVERN_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		openMouth();
		return MoCSoundEvents.ENTITY_WYVERN_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		openMouth();
		return MoCSoundEvents.ENTITY_WYVERN_AMBIENT;
	}

	@Override
	public int getTalkInterval() {
		return 400;
	}

	@Override
	public boolean isMovementCeased() {
		return (this.isBeingRidden()) || getIsSitting();
	}

	@Override
	public boolean isFlyer() {
		return true;
	}

	@Override
	public void fall(float f, float f1) { }

	@Override
	public double getMountedYOffset() {
		return this.height * 0.85 * getSizeFactor();
	}

	@Override
	public void updatePassenger(Entity passenger) {
		double dist = getSizeFactor() * (0.3D);
		double newPosX = this.posX - (dist * Math.cos((MoCTools.realAngle(this.renderYawOffset - 90F)) / 57.29578F));
		double newPosZ = this.posZ - (dist * Math.sin((MoCTools.realAngle(this.renderYawOffset - 90F)) / 57.29578F));
		passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if (entityIn instanceof EntityPlayer && !shouldAttackPlayers()) {
			return false;
		}
		openMouth();
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
		if (entityIn instanceof EntityPlayer && this.rand.nextInt(3) == 0) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 0));
		}

		super.applyEnchantments(entityLivingBaseIn, entityIn);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if (this.isRidingOrBeingRiddenBy(entity)) {
			return false;
		}
		if (super.attackEntityFrom(damagesource, i)) {
			if (entity != null && getIsTamed() && entity instanceof EntityPlayer) {
				return false;
			}

			if ((entity != this) && (super.shouldAttackPlayers())) {
				setAttackTarget((EntityLivingBase) entity);
			}
			return true;
		}
		return false;
	}

	/*@Override
    public boolean entitiesToIgnore(Entity entity) {
        return (super.entitiesToIgnore(entity) || (entity instanceof MoCEntityWyvern) || (entity instanceof EntityPlayer));
    }*/

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Saddle", getIsRideable());
		nbttagcompound.setBoolean("Chested", getIsChested());
		nbttagcompound.setInteger("ArmorType", getArmorType());
		nbttagcompound.setBoolean("isSitting", getIsSitting());
		if (getIsChested() && this.localchest != null) {
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < this.localchest.getSizeInventory(); i++) {
				this.localstack = this.localchest.getStackInSlot(i);
				if (this.localstack != null && !this.localstack.isEmpty()) {
					NBTTagCompound nbttagcompound1 = new NBTTagCompound();
					nbttagcompound1.setByte("Slot", (byte) i);
					this.localstack.writeToNBT(nbttagcompound1);
					nbttaglist.appendTag(nbttagcompound1);
				}
			}
			nbttagcompound.setTag("Items", nbttaglist);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setRideable(nbttagcompound.getBoolean("Saddle"));
		setIsChested(nbttagcompound.getBoolean("Chested"));
		setArmorType(nbttagcompound.getInteger("ArmorType"));
		setSitting(nbttagcompound.getBoolean("isSitting"));
		if (getIsChested()) {
			NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
			this.localchest = new MoCAnimalChest("WyvernChest", 14);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				int j = nbttagcompound1.getByte("Slot") & 0xff;
				if ((j >= 0) && j < this.localchest.getSizeInventory()) {
					this.localchest.setInventorySlotContents(j, new ItemStack(nbttagcompound1));
				}
			}
		}
	}

	@Override
	public boolean isMyHealFood(ItemStack stack) {
		return !stack.isEmpty() && (stack.getItem() == Items.BEEF || stack.getItem() == Items.CHICKEN);
	}

	private void openMouth() {
		if (!this.world.isRemote) {
			this.mouthCounter = 1;
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}

	}

	@Override
	public void performAnimation(int animationType) {
		if (animationType == 1) //opening mouth
		{
			this.mouthCounter = 1;
		}
		if (animationType == 2) //diving mount
		{
			this.diveCounter = 1;
		}
		if (animationType == 3) {
			this.wingFlapCounter = 1;
		}
		if (animationType > 5 && animationType < 9) //transform 6 - 8
		{
			this.transformType = animationType;
			this.transformCounter = 1;
		}
	}

	@Override
	public void makeEntityDive() {
		if (!this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 2),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		super.makeEntityDive();
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		int chance = MoCreatures.proxy.wyvernEggDropChance;
		if (getType() == 5) { //mother wyverns drop eggs more frequently
			chance = MoCreatures.proxy.motherWyvernEggDropChance;
		}
		if (this.rand.nextInt(100) < chance) {
			entityDropItem(new ItemStack(MoCItems.mocegg, 1, getType() + 49), 0.0F);
		}
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isBeingRidden();
	}

	@Override
	public void dropMyStuff() {
		if (!this.world.isRemote) {
			dropArmor();
			MoCTools.dropSaddle(this, this.world);

			if (getIsChested()) {
				MoCTools.dropInventory(this, this.localchest);
				MoCTools.dropCustomItem(this, this.world, new ItemStack(Blocks.CHEST, 1));
				setIsChested(false);
			}
		}
	}

	@Override
	public float getAdjustedYOffset() {
		if (getIsSitting()) {
			return 0.4F;
		}
		return 0F;
	}

	@Override
	public double getCustomSpeed() {
		if (this.isBeingRidden()) {
			return 1.0D;
		}
		return 0.8D;
	}
	
	@Override
	public int getMaxAge() {
		WyvernType type = getWyvern();
		
		if (type == WyvernType.Mother) {
			return 180;
		}
		if (type == WyvernType.Undead || type == WyvernType.Light || type == WyvernType.Dark) {
			return 160;
		}
		return 120;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return getWyvern() == WyvernType.Undead ? EnumCreatureAttribute.UNDEAD : super.getCreatureAttribute();
	}

	@Override
	public boolean isReadyToHunt() {
		return !this.isMovementCeased() && !this.isBeingRidden();
	}

	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return !(entity instanceof EntityWyvern) && entity.height <= 1D && entity.width <= 1D;
	}

	@Override
	protected double flyerThrust() {
		return 0.6D;
	}

	@Override
	public float getAIMoveSpeed() {
		if (getIsFlying()) {
			return 0.4F;
		}
		return super.getAIMoveSpeed();
	}
	
	@Override
	protected float flyerFriction() {
		
		switch(getWyvern()) {
			case Arctic: return 0.94F;
			case Cave: return 0.94F;
			case Dark: return 0.96F;
			case Jungle: return 0.94F;
			case Light: return 0.96F;
			case Mother: return 0.96F;
			case Mountain: return 0.94F;
			case None: return 0.94F;
			case Sand: return 0.94F;
			case Savanna: return 0.94F;
			case Sea: return 0.94F;
			case Swamp: return 0.94F;
			case Undead: return 0.96F;
			default: return 0.94F;
			
		}

	}

	@Override
	public void makeEntityJump() {
		wingFlap();
		super.makeEntityJump();
	}

	@Override
	public boolean shouldAttackPlayers() {
		return !getIsTamed() && super.shouldAttackPlayers();
	}

	@Override
	public void onDeath(DamageSource damagesource) {
		if (!this.world.isRemote) {
			if (this.getType() == 6) {
				MoCTools.spawnMaggots(this.world, this);
			}
		}
		super.onDeath(damagesource);

	}

	public float tFloat() {

		if (++this.tCounter > 30) {
			this.tCounter = 0;
			this.fTransparency = (this.rand.nextFloat() * (0.4F - 0.2F) + 0.15F);
		}

		if (this.getAge() < 10) {
			return 0F;
		}
		return fTransparency;
	}

	@Override
	protected boolean canBeTrappedInNet() {
		return this.getIsTamed();
	}
}

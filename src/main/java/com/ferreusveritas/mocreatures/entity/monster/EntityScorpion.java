package com.ferreusveritas.mocreatures.entity.monster;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityMob;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIAvoidPlayer;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;
import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityScorpion extends MoCEntityMob {

	private boolean isPoisoning;
	private int poisontimer;
	public int mouthCounter;
	public int armCounter;

	private static final DataParameter<Boolean> IS_PICKED = EntityDataManager.<Boolean>createKey(EntityScorpion.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> HAS_BABIES = EntityDataManager.<Boolean>createKey(EntityScorpion.class, DataSerializers.BOOLEAN);

	private static Map<Integer, ScorpionType> map = new HashMap<>();
	private static Map<EnumEgg, ScorpionType> eggmap = new HashMap<>();

	public static enum ScorpionType {
		None(0, EnumEgg.None),
		Dirt(1, EnumEgg.DirtScorpion),
		Cave(2, EnumEgg.CaveScorpion),
		Nether(3, EnumEgg.NetherScorpion),
		Frost(4, EnumEgg.FrostScorpion),
		Undead(5, EnumEgg.UndeadScorpion);

		public final int type;
		public final EnumEgg egg;

		private ScorpionType(int type, EnumEgg egg) {
			this.type = type;
			this.egg = egg;
			map.put(type, this);
			eggmap.put(egg, this);
		}
	}

	public EntityScorpion(World world) {
		super(world);
		setSize(1.4f, 0.9f);
		this.poisontimer = 0;
		setAdult(true);
		setEdad(20);

		if (!this.world.isRemote) {
			setHasBabies(rand.nextInt(4) == 0);
		}
	}

	public void setType(ScorpionType type) {
		setType(type.type);
	}

	public ScorpionType getScorpion() {
		return getScorpion(getType());
	}

	public static ScorpionType getScorpion(int type) {
		return map.getOrDefault(type, ScorpionType.None);
	}
	
	public static ScorpionType getScorpion(EnumEgg egg) {
		return eggmap.getOrDefault(egg, ScorpionType.None);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0, true));
		this.tasks.addTask(2, new EntityAIRestrictSun(this));
		this.tasks.addTask(7, new EntityAIFleeSun(this, 1.0));
		this.tasks.addTask(5, new EntityAIAvoidPlayer(this, 4.0f, 1.2, 1.2));
		this.tasks.addTask(6, new EntityAILeapAtTarget(this, 0.4f));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}

	@Override
	public void selectType() {
		checkSpawningBiome();

		if (getScorpion() == ScorpionType.None) {
			setType(ScorpionType.Dirt);
		}
	}

	@Override
	public ResourceLocation getTexture() {
		switch(getScorpion()) {
			case Dirt: return MoCreatures.proxy.getTexture("scorpiondirt.png");
			case Cave:return MoCreatures.proxy.getTexture("scorpioncave.png");
			case Nether:return MoCreatures.proxy.getTexture("scorpionnether.png");
			case Frost:return MoCreatures.proxy.getTexture("scorpionfrost.png");
			case Undead: return MoCreatures.proxy.getTexture("scorpionundead.png");
			default: return MoCreatures.proxy.getTexture("scorpiondirt.png");
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_PICKED, Boolean.valueOf(false));
		this.dataManager.register(HAS_BABIES, Boolean.valueOf(false)); 
	}

	public boolean getHasBabies() {
		return ((Boolean)this.dataManager.get(HAS_BABIES)).booleanValue();
	}

	public boolean getIsPicked() {
		return ((Boolean)this.dataManager.get(IS_PICKED)).booleanValue();
	}

	public boolean getIsPoisoning() {
		return this.isPoisoning;
	}

	public void setHasBabies(boolean flag) {
		this.dataManager.set(HAS_BABIES, Boolean.valueOf(flag));
	}

	public void setPicked(boolean flag) {
		this.dataManager.set(IS_PICKED, Boolean.valueOf(flag));
	}

	public void setPoisoning(boolean flag) {
		if (flag && !this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		this.isPoisoning = flag;
	}

	@Override
	public void performAnimation(int animationType) {
		if (animationType == 0) {//tail animation
			setPoisoning(true);
		} else if (animationType == 1) { //arm swinging
			this.armCounter = 1;
			//swingArm();
		} else if (animationType == 3) { //movement of mouth
			this.mouthCounter = 1;
		}
	}

	@Override
	public float getMoveSpeed() {
		return 0.8F;
	}

	@Override
	public boolean isOnLadder() {
		return this.collidedHorizontally;
	}

	public boolean climbing() {
		return !this.onGround && isOnLadder();
	}

	@Override
	public void onLivingUpdate() {

		if (!this.onGround && (this.getRidingEntity() != null)) {
			this.rotationYaw = this.getRidingEntity().rotationYaw;
		}

		if (this.mouthCounter != 0 && this.mouthCounter++ > 50) {
			this.mouthCounter = 0;
		}

		if (!this.world.isRemote && (this.armCounter == 10 || this.armCounter == 40)) {
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_CLAW);
		}

		if (this.armCounter != 0 && this.armCounter++ > 24) {
			this.armCounter = 0;
		}

		if (!this.world.isRemote && !this.isBeingRidden() && this.getIsAdult() && !this.getHasBabies() && this.rand.nextInt(100) == 0) {
			MoCTools.findMobRider(this);
			/*List list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(4D, 2D, 4D));
            for (int i = 0; i < list.size(); i++) {
                Entity entity = (Entity) list.get(i);
                if (!(entity instanceof EntityMob)) {
                    continue;
                }
                EntityMob entitymob = (EntityMob) entity;
                if (entitymob.getRidingEntity() == null
                        && (entitymob instanceof EntitySkeleton || entitymob instanceof EntityZombie || entitymob instanceof MoCEntitySilverSkeleton)) {
                    entitymob.mountEntity(this);
                    break;
                }
            }*/
		}

		if (getIsPoisoning()) {
			this.poisontimer++;
			if (this.poisontimer == 1) {
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_STING);
			}
			if (this.poisontimer > 50) {
				this.poisontimer = 0;
				setPoisoning(false);
			}
		}
		super.onLivingUpdate();
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();

			if (entity != null && entity != this && entity instanceof EntityLivingBase && this.shouldAttackPlayers() && getIsAdult()) {
				setAttackTarget((EntityLivingBase) entity);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean entitiesToIgnore(Entity entity) {
		return ((super.entitiesToIgnore(entity)) || (this.getIsTamed() && entity instanceof EntityScorpion && ((EntityScorpion) entity)
				.getIsTamed()));
	}

	@Override
	protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
		boolean isPlayer = (entityIn instanceof EntityPlayer);
		if (!getIsPoisoning() && this.rand.nextInt(5) == 0 && entityIn instanceof EntityLivingBase) {
			setPoisoning(true);

			switch(getScorpion()) {
				case Dirt:
				case Cave: 
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 70, 0));
					break;
				case Frost:
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 70, 0));
					break;
				case Nether:
					if (!this.world.isRemote && isPlayer && !this.world.provider.doesWaterVaporize()) {
						((EntityLivingBase) entityIn).setFire(15);
					}
					break;
				default: break;
			}
		} else {
			swingArm();
		}
		super.applyEnchantments(entityLivingBaseIn, entityIn);
	}

	public void swingArm() {
		if (!this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	public boolean swingingTail() {
		return getIsPoisoning() && this.poisontimer < 15;
	}

	@Override
	public void onDeath(DamageSource damagesource) {
		super.onDeath(damagesource);
		if (!this.world.isRemote && getIsAdult() && getHasBabies()) {
			int k = this.rand.nextInt(5);
			for (int i = 0; i < k; i++) {
				EntityPetScorpion entityscorpy = new EntityPetScorpion(this.world);
				entityscorpy.setPosition(this.posX, this.posY, this.posZ);
				entityscorpy.setAdult(false);
				entityscorpy.setAge(20);
				entityscorpy.setType(getType());
				this.world.spawnEntity(entityscorpy);
				MoCTools.playCustomSound(entityscorpy, SoundEvents.ENTITY_CHICKEN_EGG);
			}
		}
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_SCORPION_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_SCORPION_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (!this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		return MoCSoundEvents.ENTITY_SCORPION_AMBIENT;
	}

	@Override
	protected Item getDropItem() {
		if (!getIsAdult()) {
			return Items.STRING;
		}
		
		return null;
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		if (!flag) {
			return;
		}
		Item item = this.getDropItem();

		if (item != null) {
			if (this.rand.nextInt(3) == 0) {
				this.dropItem(item, 1);
			}
		}

	}

	@Override
	public boolean getCanSpawnHere() {
		return (isValidLightLevel() && MoCreatures.entityMap.get(this.getClass()).getFrequency() > 0) && getCanSpawnHereLiving()
				&& getCanSpawnHereCreature();
	}

	@Override
	public boolean checkSpawningBiome() {
		if (this.world.provider.doesWaterVaporize()) {
			setType(ScorpionType.Nether);
			this.isImmuneToFire = true;
			return true;
		}

		int x = MathHelper.floor(this.posX);
		int y = MathHelper.floor(getEntityBoundingBox().minY);
		int z = MathHelper.floor(this.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		Biome currentbiome = MoCTools.Biomekind(this.world, pos);

		if (BiomeDictionary.hasType(currentbiome, Type.SNOWY)) {
			setType(ScorpionType.Frost);
		} else if (!this.world.canBlockSeeSky(pos) && (this.posY < 50D)) {
			setType(ScorpionType.Cave);
			return true;
		}

		return true;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setHasBabies(nbttagcompound.getBoolean("Babies"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Babies", getHasBabies());
	}

	@Override
	public int getTalkInterval() {
		return 300;
	}

	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	public float getAdjustedYOffset() {
		return 30F;
	}

	@Override
	protected int getMaxEdad() {
		return 120;
	}

	@Override
	public boolean isNotScared() {
		return getIsAdult() || this.getEdad() > 70;
	}

	@Override
	public double getMountedYOffset() {
		return (this.height * 0.75D) - 0.15D;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		double dist = (0.2D);
		double newPosX = this.posX + (dist * Math.sin(this.renderYawOffset / 57.29578F));
		double newPosZ = this.posZ - (dist * Math.cos(this.renderYawOffset / 57.29578F));
		passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
		passenger.rotationYaw = this.rotationYaw;
	}
}

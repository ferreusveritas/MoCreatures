package com.ferreusveritas.mocreatures.entity.passive;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityAnimal;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIAvoidPlayer;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIHunt;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * Biome - specific Forest Desert plains Swamp Jungle Tundra Taiga Extreme Hills
 * Ocean
 *
 * swamp: python, bright green, #1 plains: coral, cobra #1, #2, #3, #4 desert:
 * rattlesnake , #2 jungle: all except rattlesnake hills: all except python,
 * bright green, bright orange tundra-taiga: none ocean: leave alone
 *
 */

public class EntitySnake extends MoCEntityAnimal {

	private float fTongue;
	private float fMouth;
	private boolean isBiting;
	private float fRattle;
	private boolean isPissed;
	private int hissCounter;

	private int movInt;
	private boolean isNearPlayer;
	public float bodyswing;

	public static final String snakeNames[] = {"Dark", "Spotted", "Orange", "Green", "Coral", "Cobra", "Rattle", "Python"};

	private static Map<Integer, SnakeType> map = new HashMap<>();
	private static Map<EnumEgg, SnakeType> eggmap = new HashMap<>();

	public static enum SnakeType {
		None(0, EnumEgg.None, false, false),
		Dark(1, EnumEgg.DarkSnake, false, false),// 1 small blackish/dark snake (passive)
		Spotted(2, EnumEgg.SpottedSnake, false, false),// 2 dark green /brown snake (passive)
		Orange(3, EnumEgg.OrangeSnake, true, true),// 3 bright orangy snake aggressive venomous swamp, jungle, forest
		Green(4, EnumEgg.GreenSnake, true, true),// 4 bright green snake aggressive venomous swamp, jungle, forest
		Coral(5, EnumEgg.CoralSnake, true, true),// 5 coral (aggressive - venomous) small / plains, forest
		Cobra(6, EnumEgg.Cobra, true, true),// 6 cobra (aggressive - venomous - spitting) plains, forest
		Rattle(7, EnumEgg.RattleSnake, true, true),// 7 rattlesnake (aggressive - venomous) desert
		Python(8, EnumEgg.Python, true, false);// 8 python (aggressive - non venomous) big - swamp

		public final int type;
		public final EnumEgg egg;
		public final boolean aggressive;
		public final boolean venomous;

		private SnakeType(int type, EnumEgg egg, boolean aggressive, boolean venomous) {
			this.type = type;
			this.egg = egg;
			this.aggressive = aggressive;
			this.venomous = venomous;
			map.put(type, this);
			eggmap.put(egg, this);
		}
	}

	public static SnakeType getSnake(int type) {
		return map.getOrDefault(type, SnakeType.None);
	}

	public static SnakeType getSnake(EnumEgg egg) {
		return eggmap.getOrDefault(egg, SnakeType.None);
	}

	public EntitySnake(World world) {
		super(world);
		setSize(1.4F, 0.5F);
		this.bodyswing = 2F;
		this.movInt = this.rand.nextInt(10);
		setAge(50 + this.rand.nextInt(50));
	}

	public void setType(SnakeType type) {
		setType(type.type);
	}
	
	public SnakeType getSnakeType() {
		return getSnake(getType());
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIPanicMoC(this, 0.8D));
		this.tasks.addTask(3, new EntityAIAvoidPlayer(this, 4.0f, 0.8, 0.8));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(5, new EntityAIWanderMoC2(this, 0.8D, 30));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.targetTasks.addTask(1, new EntityAIHunt(this, EntityAnimal.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public void selectType() {
		checkSpawningBiome();
		if (getType() == 0) {
			setType(this.rand.nextInt(8) + 1);
		}
	}

	@Override
	public ResourceLocation getTexture() {
		switch (getType()) {
			case 1: return MoCreatures.proxy.getTexture("snake1.png");
			case 2: return MoCreatures.proxy.getTexture("snake2.png");
			case 3: return MoCreatures.proxy.getTexture("snake3.png");
			case 4: return MoCreatures.proxy.getTexture("snake4.png");
			case 5: return MoCreatures.proxy.getTexture("snake5.png");
			case 6: return MoCreatures.proxy.getTexture("snake6.png");
			case 7: return MoCreatures.proxy.getTexture("snake7.png");
			case 8: return MoCreatures.proxy.getTexture("snake8.png");
			default: return MoCreatures.proxy.getTexture("snake1.png");
		}
	}

	@Override
	public void fall(float f, float f1) {
	}

	@Override
	public boolean isOnLadder() {
		return this.collidedHorizontally;
	}

	@Override
	// snakes can't jump
	protected
	void jump() {
		if (this.isInWater()) {
			super.jump();
		}
	}

	@Override
	protected boolean canDespawn() {
		if (MoCreatures.proxy.forceDespawns) {
			return !getIsTamed();
		} else {
			return false;
		}
	}

	public boolean pickedUp() {
		return (this.getRidingEntity() != null);
	}

	@Override
	public boolean isNotScared() {
		return getSnakeType().aggressive && getAge() > 50;
	}

	/**
	 * returns true when is climbing up
	 *
	 * @return
	 */
	public boolean isClimbing() {
		return isOnLadder() && this.motionY > 0.01F;
	}

	public boolean isResting() {
		return (!getNearPlayer() && this.onGround && (this.motionX < 0.01D && this.motionX > -0.01D) && (this.motionZ < 0.01D && this.motionZ > -0.01D));
	}

	public boolean getNearPlayer() {
		return (this.isNearPlayer || this.isBiting());
	}

	public int getMovInt() {
		return this.movInt;
	}

	@Override
	public boolean swimmerEntity() {
		return false;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	public void setNearPlayer(boolean flag) {
		this.isNearPlayer = flag;
	}

	@Override
	public double getYOffset() {
		if (this.getRidingEntity() instanceof EntityPlayer) {
			return 0.1F;
		}

		return super.getYOffset();
	}

	public float getSizeF() {
		float factor = 1.0F;
		if (getType() == 1 || getType() == 2)// small shy snakes
		{
			factor = 0.8F;
		} else if (getType() == 5)// coral
		{
			factor = 0.6F;
		}
		if (getType() == 6)// cobra 1.1
		{
			factor = 1.1F;
		}
		if (getType() == 7)// rattlesnake
		{
			factor = 0.9F;
		}
		if (getType() == 8)// python
		{
			factor = 1.5F;
		}
		return this.getAge() * 0.01F * factor;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.world.isRemote) {
			if (getfTongue() != 0.0F) {
				setfTongue(getfTongue() + 0.2F);
				if (getfTongue() > 8.0F) {
					setfTongue(0.0F);
				}
			}

			if (getfMouth() != 0.0F && this.hissCounter == 0) //biting
			{
				setfMouth(getfMouth() + 0.1F);
				if (getfMouth() > 0.5F) {
					setfMouth(0.0F);
				}
			}

			if (getType() == 7 && getfRattle() != 0.0F) // rattling
			{
				setfRattle(getfRattle() + 0.2F);
				if (getfRattle() == 1.0F) {
					// TODO synchronize
					MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SNAKE_RATTLE);
				}
				if (getfRattle() > 8.0F) {
					setfRattle(0.0F);
				}
			}

			/**
			 * stick tongue
			 */
			if (this.rand.nextInt(50) == 0 && getfTongue() == 0.0F) {
				setfTongue(0.1F);
			}

			/**
			 * Open mouth
			 */
			if (this.rand.nextInt(100) == 0 && getfMouth() == 0.0F) {
				setfMouth(0.1F);
			}
			if (getType() == 7) {
				int chance = 0;
				if (getNearPlayer()) {
					chance = 30;
				} else {
					chance = 100;
				}

				if (this.rand.nextInt(chance) == 0) {
					setfRattle(0.1F);
				}
			}
			/**
			 * change in movement pattern
			 */
			if (!isResting() && !pickedUp() && this.rand.nextInt(50) == 0) {
				this.movInt = this.rand.nextInt(10);
			}

			/**
			 * Biting animation
			 */
			if (isBiting()) {
				this.bodyswing -= 0.5F;
				setfMouth(0.3F);

				if (this.bodyswing < 0F) {
					MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SNAKE_SNAP);
					this.bodyswing = 2.5F;
					setfMouth(0.0F);
					setBiting(false);
				}
			}

		}
		if (pickedUp()) {
			this.movInt = 0;
		}

		if (isResting()) {

			this.prevRenderYawOffset = this.renderYawOffset = this.rotationYaw = this.prevRotationYaw;

		}

		if (!this.onGround && (this.getRidingEntity() != null)) {
			this.rotationYaw = this.getRidingEntity().rotationYaw;// -90F;
		}

		if (this.world.getDifficulty().getDifficultyId() > 0 && getNearPlayer() && !getIsTamed() && isNotScared()) {

			this.hissCounter++;

			// TODO synchronize and get sound
			// hiss
			if (this.hissCounter % 25 == 0) {
				setfMouth(0.3F);
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SNAKE_ANGRY);
			}
			if (this.hissCounter % 35 == 0) {
				setfMouth(0.0F);
			}

			if (this.hissCounter > 100 && this.rand.nextInt(50) == 0) {
				// then randomly get pissed
				setPissed(true);
				this.hissCounter = 0;
			}
		}
		if (this.hissCounter > 500) {
			this.hissCounter = 0;
		}

	}

	/**
	 * from 0.0 to 4.0F 0.0 = inside mouth 2.0 = completely stuck out 3.0 =
	 * returning 4.0 = in.
	 *
	 * @return
	 */
	public float getfTongue() {
		return this.fTongue;
	}

	public void setfTongue(float fTongue) {
		this.fTongue = fTongue;
	}

	public float getfMouth() {
		return this.fMouth;
	}

	public void setfMouth(float fMouth) {
		this.fMouth = fMouth;
	}

	public float getfRattle() {
		return this.fRattle;
	}

	public void setfRattle(float fRattle) {
		this.fRattle = fRattle;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		/**
		 * this stops chasing the target randomly
		 */
		if (getAttackTarget() != null && this.rand.nextInt(300) == 0) {
			setAttackTarget(null);
		}

		EntityPlayer entityplayer1 = this.world.getClosestPlayerToEntity(this, 12D);
		if (entityplayer1 != null) {
			double distP = MoCTools.getSqDistanceTo(entityplayer1, this.posX, this.posY, this.posZ);
			if (isNotScared()) {
				if (distP < 5D) {
					setNearPlayer(true);
				} else {
					setNearPlayer(false);
				}

				/*if (entityplayer1.isBeingRidden()
                        && (entityplayer1.riddenByEntity instanceof MoCEntityMouse || entityplayer1.riddenByEntity instanceof MoCEntityBird)) {
                    PathEntity pathentity = this.navigator.getPathToEntityLiving(entityplayer1);
                    this.navigator.setPath(pathentity, 16F);
                    setPissed(false);
                    this.hissCounter = 0;
                }*/
			} else {
				setNearPlayer(false);
			}

		} else {
			setNearPlayer(false);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if ((getType() < 3 || getIsTamed()) && entityIn instanceof EntityPlayer) {
			return false;
		}

		if (entityIn instanceof EntityPlayer && !shouldAttackPlayers()) {
			return false;
		}
		setBiting(true);
		return super.attackEntityAsMob(entityIn);
	}

	@Override
	public void performAnimation(int i) {
		setBiting(true);
	}

	public boolean isBiting() {
		return this.isBiting;
	}

	public void setBiting(boolean flag) {
		if (flag && !this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		this.isBiting = flag;
	}

	public boolean isPissed() {
		return this.isPissed;
	}

	public void setPissed(boolean isPissed) {
		this.isPissed = isPissed;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {

		if (!getSnakeType().aggressive) {
			return super.attackEntityFrom(damagesource, i);
		}

		if (super.attackEntityFrom(damagesource, i)) {
			Entity entity = damagesource.getTrueSource();
			if (this.isRidingOrBeingRiddenBy(entity)) {
				return true;
			}
			if ((entity != this) && entity instanceof EntityLivingBase && (super.shouldAttackPlayers())) {
				setPissed(true);
				setAttackTarget((EntityLivingBase) entity);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		if (getAge() > 60) {
			int j = this.rand.nextInt(3);
			for (int l = 0; l < j; l++) {
				entityDropItem(new ItemStack(MoCItems.mocegg, 1, getSnakeType().egg.eggNum), 0.0F);
			}
		}
	}

	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		return !(entity instanceof EntitySnake) && entity.height < 0.5D && entity.width < 0.5D;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block par4) {
		if (isInsideOfMaterial(Material.WATER)) {
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SNAKE_SWIM);
		}
		// TODO - add sound for slither
		/*
		 * else { world.playSoundAtEntity(this, "snakeslither", 1.0F, 1.0F);
		 * }
		 */
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_SNAKE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_SNAKE_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MoCSoundEvents.ENTITY_SNAKE_AMBIENT;
	}

	@Override
	public boolean getCanSpawnHere() {
		return getCanSpawnHereCreature() && getCanSpawnHereLiving(); //&& checkSpawningBiome()
	}

	@Override
	public boolean checkSpawningBiome() {
		BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(getEntityBoundingBox().minY), this.posZ);
		/**
		 * swamp: python, bright green, #1 (done) plains: coral, cobra #1, #2,
		 * #3, #4 (everyone but 7) desert: rattlesnake , #2 jungle: all except
		 * rattlesnake forest: all except rattlesnake hills: all except python,
		 * bright green, bright orange, rattlesnake tundra-taiga: none ocean:
		 * leave alone
		 */

		/**
		 * Biome lists: Ocean Plains Desert Extreme Hills Forest Taiga Swampland
		 * River Frozen Ocean Frozen River Ice Plains Ice Mountains Mushroom
		 * Island Mushroom Island Shore Beach DesertHills ForestHills TaigaHills
		 * Extreme Hills Edge Jungle JungleHills
		 *
		 */
		try {
			Biome currentbiome = MoCTools.Biomekind(this.world, pos);
			int rnd = this.rand.nextInt(10);

			if (BiomeDictionary.hasType(currentbiome, Type.SNOWY)) {
				return false;
			}

			if (BiomeDictionary.hasType(currentbiome, Type.SANDY)) {
				setType(rnd < 5 ? SnakeType.Rattle : SnakeType.Spotted); // rattlesnake or spotted brownish ?
			}
			
			if (getSnakeType() == SnakeType.Rattle && !(BiomeDictionary.hasType(currentbiome, Type.SANDY))) {
				setType(SnakeType.Spotted);
			}
			if (BiomeDictionary.hasType(currentbiome, Type.HILLS)) {
				if (rnd < 4) {
					setType(SnakeType.Dark);
				} else if (rnd < 7) {
					setType(SnakeType.Coral);
				} else {
					setType(SnakeType.Cobra);
				}
			}
			if (BiomeDictionary.hasType(currentbiome, Type.SWAMP)) {
				// python or bright green bright orange
				if (rnd < 4) {
					setType(SnakeType.Rattle);
				} else if (rnd < 8) {
					setType(SnakeType.Green);
				} else {
					setType(SnakeType.Dark);
				}
			}
		} catch (Exception e) {
		}
		return true;
	}

	@Override
	public boolean isMyHealFood(ItemStack stack) {
		return !stack.isEmpty() && (stack.getItem() == Items.CHICKEN);
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public boolean isReadyToHunt() {
		return this.getIsAdult() && !this.isMovementCeased();
	}

	@Override
	protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
		if (isVenomous()) {
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 150, 2));
		}
		super.applyEnchantments(entityLivingBaseIn, entityIn);
	}

	private boolean isVenomous() {
		return getSnakeType().venomous;
	}

	@Override
	public boolean shouldAttackPlayers() {
		return this.isPissed() && super.shouldAttackPlayers();
	}

	@Override
	public int getTalkInterval() {
		return 400;
	}

	@Override
	public boolean isAmphibian() {
		return true;
	}

	@Override
	public boolean canRidePlayer()
	{
		return true;
	}

	@Override
	protected double maxDivingDepth()
	{
		return 1D * (this.getAge()/100D);
	}
}

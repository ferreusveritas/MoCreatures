package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityAnimal;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityTurtle extends MoCEntityAnimal {
	
	private boolean isSwinging;
	private boolean twistright;
	private int flopcounter;
	private static final DataParameter<Boolean> IS_UPSIDE_DOWN = EntityDataManager.<Boolean>createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HIDING = EntityDataManager.<Boolean>createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
	
	public EntityTurtle(World world) {
		super(world);
		setSize(0.6F, 0.4F);
		setAdult(false);
		setAge(60 + this.rand.nextInt(50));
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(5, new EntityAIWanderMoC2(this, 0.8D, 50));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(IS_UPSIDE_DOWN, Boolean.valueOf(false));; // rideable: 0 nothing, 1 saddle
		this.dataManager.register(IS_HIDING, Boolean.valueOf(false));; // rideable: 0 nothing, 1 saddle
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("turtle.png");
	}
	
	public boolean getIsHiding() {
		return ((Boolean)this.dataManager.get(IS_HIDING)).booleanValue();
	}
	
	public boolean getIsUpsideDown() {
		return ((Boolean)this.dataManager.get(IS_UPSIDE_DOWN)).booleanValue();
	}
	
	public void setIsHiding(boolean flag) {
		this.dataManager.set(IS_HIDING, Boolean.valueOf(flag));
	}
	
	public void setIsUpsideDown(boolean flag) {
		this.flopcounter = 0;
		this.swingProgress = 0.0F;
		this.dataManager.set(IS_UPSIDE_DOWN, Boolean.valueOf(flag));
	}
	
	@Override
	public double getYOffset() {
		if (this.getRidingEntity() instanceof EntityPlayer) {
			if (((EntityPlayer) this.getRidingEntity()).isSneaking()) {
				return -0.25D + ((300D - this.getAge()) / 500D);
			}
			return (300D - this.getAge()) / 500D;
		}
		
		return super.getYOffset();
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		if (getIsUpsideDown()) {
			flipflop(false);
			return true;
		}
		
		return super.processInteract(player, hand);
	}
	
	@Override
	protected void jump() {
		if (isInsideOfMaterial(Material.WATER)) {
			this.motionY = 0.3D;
			if (isSprinting()) {
				float f = this.rotationYaw * 0.01745329F;
				this.motionX -= MathHelper.sin(f) * 0.2F;
				this.motionZ += MathHelper.cos(f) * 0.2F;
			}
			this.isAirBorne = true;
		}
	}
	
	@Override
	public boolean isNotScared() {
		return true;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			if (!getIsUpsideDown() && !getIsTamed()) {
				EntityLivingBase entityliving = getBoogey(4D);
				if ((entityliving != null) && canEntityBeSeen(entityliving)) {
					if (!getIsHiding() && !isInWater()) {
						MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_TURTLE_ANGRY);
						setIsHiding(true);
					}
					
					this.getNavigator().clearPath();
				} else {
					
					setIsHiding(false);
					if (!hasPath() && this.rand.nextInt(50) == 0) {
						EntityItem entityitem = getClosestItem(this, 10D, Items.MELON, Items.REEDS);
						if (entityitem != null) {
							float f = entityitem.getDistance(this);
							if (f > 2.0F) {
								getMyOwnPath(entityitem, f);
							}
							if ((f < 2.0F) && (entityitem != null) && (this.deathTime == 0)) {
								entityitem.setDead();
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public boolean swimmerEntity() {
		return false;
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if (this.getRidingEntity() != null) {
			return false;
		}
		if (entity == null) {
			return super.attackEntityFrom(damagesource, i);
		}
		if (getIsHiding()) {
			if (this.rand.nextInt(10) == 0) {
				flipflop(true);
			}
			return false;
		} else {
			boolean flag = super.attackEntityFrom(damagesource, i);
			if (this.rand.nextInt(3) == 0) {
				flipflop(true);
			}
			return flag;
		}
	}
	
	public void flipflop(boolean flip) {
		setIsUpsideDown(flip);
		setIsHiding(false);
		this.getNavigator().clearPath();
	}
	
	@Override
	public boolean entitiesToIgnore(Entity entity) {
		return (entity instanceof EntityTurtle) || ((entity.height <= this.height) && (entity.width <= this.width))
				|| super.entitiesToIgnore(entity);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if ((this.getRidingEntity() != null) && (this.getRidingEntity() instanceof EntityPlayer)) {
			EntityPlayer entityplayer = (EntityPlayer) this.getRidingEntity();
			if (entityplayer != null) {
				this.rotationYaw = entityplayer.rotationYaw;
			}
		}
		//to make mega turtles if tamed
		if (getIsTamed() && getAge() < 300 && this.rand.nextInt(900) == 0) {
			setAge(getAge() + 1);
		}
		if (getIsUpsideDown() && isInWater()) {
			setIsUpsideDown(false);
		}
		if (getIsUpsideDown() && (this.getRidingEntity() == null) && this.rand.nextInt(20) == 0) {
			setSwinging(true);
			this.flopcounter++;
		}
		
		if (getIsSwinging()) {
			this.swingProgress += 0.2F;
			
			boolean flag = (this.flopcounter > (this.rand.nextInt(3) + 8));
			
			if (this.swingProgress > 2.0F && (!flag || this.rand.nextInt(20) == 0)) {
				setSwinging(false);
				this.swingProgress = 0.0F;
				if (this.rand.nextInt(2) == 0) {
					this.twistright = !this.twistright;
				}
				
			} else if (this.swingProgress > 9.0F && flag) {
				setSwinging(false);
				MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
				setIsUpsideDown(false);
			}
		}
	}
	
	public boolean getIsSwinging() {
		return this.isSwinging;
	}
	
	public void setSwinging(boolean flag) {
		this.isSwinging = flag;
	}
	
	@Override
	public boolean isMovementCeased() {
		return (getIsUpsideDown() || getIsHiding());
	}
	
	public int getFlipDirection() {
		if (this.twistright) {
			return 1;
		}
		return -1;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setIsUpsideDown(nbttagcompound.getBoolean("UpsideDown"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("UpsideDown", getIsUpsideDown());
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return MoCSoundEvents.ENTITY_TURTLE_HURT;
	}
	
	@Override
	protected SoundEvent getDeathSound() {
		return MoCSoundEvents.ENTITY_TURTLE_DEATH;
	}
	
	@Override
	protected Item getDropItem() { 
		return Items.AIR;
	}
	
	@Override
	public boolean isMyHealFood(ItemStack stack) {
		return !stack.isEmpty() && (stack.getItem() == Items.REEDS || stack.getItem() == Items.MELON);
	}
	
	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}
	
	@Override
	public boolean isPushedByWater() {
		return true;
	}
	
	@Override
	public boolean isAmphibian() {
		return true;
	}
	
	@Override
	public float getAIMoveSpeed() {
		if (isInWater()) {
			return 0.08F;
		}
		return 0.12F;
	}
	
	@Override
	protected double minDivingDepth() {
		return (getAge() + 8D) / 340D;
	}
	
	@Override
	protected double maxDivingDepth() {
		return 1D * (this.getAge() / 100D);
	}
	
	@Override
	public int getMaxAge() {
		return 120;
	}
	
	@Override
	public boolean canRidePlayer()
	{
		return true;
	}
}

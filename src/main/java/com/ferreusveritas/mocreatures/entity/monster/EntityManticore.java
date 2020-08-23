package com.ferreusveritas.mocreatures.entity.monster;

import com.ferreusveritas.mocreatures.entity.MoCEntityMob;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.passive.EntityManticorePet;
import com.ferreusveritas.mocreatures.entity.passive.EntityManticorePet.ManticoreType;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
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

public class EntityManticore extends MoCEntityMob {

	public int mouthCounter;
	public int tailCounter;
	public int wingFlapCounter;
	private boolean isPoisoning;
	private int poisontimer;

	public EntityManticore(World world) {
		super(world);
		setSize(1.4F, 1.6F);
		this.isImmuneToFire = true;
	}

	public void setType(ManticoreType type) {
		setType(type.type);
	}

	public ManticoreType getManticore() {
		return EntityManticorePet.getManticore(getType());
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	@Override
	public void selectType() {
		checkSpawningBiome();

		if (getManticore() == ManticoreType.None) {
			setType((this.rand.nextInt(2) * 2) + 2);
		}
	}

	@Override
	public boolean checkSpawningBiome() {
		if (this.world.provider.doesWaterVaporize()) {
			setType(ManticoreType.Common);
			this.isImmuneToFire = true;
			return true;
		}

		int x = MathHelper.floor(this.posX);
		int y = MathHelper.floor(getEntityBoundingBox().minY);
		int z = MathHelper.floor(this.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		Biome currentbiome = MoCTools.Biomekind(this.world, pos);

		if (BiomeDictionary.hasType(currentbiome, Type.SNOWY)) {
			setType(ManticoreType.Blue);
		}

		return true;
	}

	@Override
	public ResourceLocation getTexture() {
		switch (getManticore()) {
			case Common: return MoCreatures.proxy.getTexture("bcmanticore.png");
			case Dark: return MoCreatures.proxy.getTexture("bcmanticoredark.png");
			case Blue: return MoCreatures.proxy.getTexture("bcmanticoreblue.png");
			case Green: return MoCreatures.proxy.getTexture("bcmanticoregreen.png");
			default: return MoCreatures.proxy.getTexture("bcmanticoregreen.png");
		}
	}

	@Override
	public boolean isFlyer() {
		return true;
	}

	@Override
	public float getMoveSpeed() {
		return 0.9F;
	}

	@Override
	public void fall(float f, float f1) {
	}

	/*protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
    }*/

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		//startArmSwingAttack();
		return super.attackEntityAsMob(entityIn);
	}

	public boolean getIsRideable() {
		return false;
	}

	@Override
	protected boolean isHarmedByDaylight() {
		return true;
	}

	@Override
	public int maxFlyingHeight() {
		return 10;
	}

	@Override
	public int minFlyingHeight() {
		return 1;
	}

	@Override
	public void updatePassenger(Entity passenger) {
		double dist = (-0.1D);
		double newPosX = this.posX + (dist * Math.sin(this.renderYawOffset / 57.29578F));
		double newPosZ = this.posZ - (dist * Math.cos(this.renderYawOffset / 57.29578F));
		passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
		passenger.rotationYaw = this.rotationYaw;
	}

	@Override
	public double getMountedYOffset() {
		return (this.height * 0.75D) - 0.1D;
	}

	/*@Override
    public boolean getCanSpawnHere() {
        if (this.posY < 50D && !this.world.provider.doesWaterVaporize()) {
            setType(32);
        }
        return super.getCanSpawnHere();
    }*/

	/*@Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }*/

	private void openMouth() {
		this.mouthCounter = 1;
	}

	private void moveTail() {
		this.tailCounter = 1;
	}

	public boolean isOnAir() {
		return this.world.isAirBlock(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.2D), MathHelper
				.floor(this.posZ)));
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
			this.mouthCounter = 0;
		}

		if (this.tailCounter > 0 && ++this.tailCounter > 8) {
			this.tailCounter = 0;
		}

		if (this.wingFlapCounter > 0 && ++this.wingFlapCounter > 20) {
			this.wingFlapCounter = 0;
		}
	}

	@Override
	public void onLivingUpdate() {
		//if (true) return;
		super.onLivingUpdate();

		/**
		 * slow falling
		 */
		/*if (!this.onGround && (this.motionY < 0.0D)) {
            this.motionY *= 0.6D;
        }*/

		if (isOnAir() && isFlyer() && this.rand.nextInt(5) == 0) {
			this.wingFlapCounter = 1;
		}

		if (this.rand.nextInt(200) == 0) {
			moveTail();
		}

		if (!this.world.isRemote && isFlyer() && isOnAir()) {
			float myFlyingSpeed = MoCTools.getMyMovementSpeed(this);
			int wingFlapFreq = (int) (25 - (myFlyingSpeed * 10));
			if (!this.isBeingRidden() || wingFlapFreq < 5) {
				wingFlapFreq = 5;
			}
			if (this.rand.nextInt(wingFlapFreq) == 0) {
				wingFlap();
			}
		}

		if (isFlyer()) {
			if (this.wingFlapCounter > 0 && ++this.wingFlapCounter > 20) {
				this.wingFlapCounter = 0;
			}
			/*if (this.wingFlapCounter != 0 && this.wingFlapCounter % 5 == 0 && this.world.isRemote) {
                StarFX();
            }*/
			if (this.wingFlapCounter == 5 && !this.world.isRemote) {
				//System.out.println("playing sound");
				MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_WINGFLAP);
			}
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
		if (!this.world.isRemote) {
			if (isFlyer() && this.rand.nextInt(500) == 0) {
				wingFlap();
			}

			if (!this.isBeingRidden() && this.rand.nextInt(200) == 0) {
				MoCTools.findMobRider(this);
			}
		}
	}

	@Override
	public void makeEntityJump() {
		wingFlap();
		super.makeEntityJump();
	}

	public void wingFlap() {
		if (this.isFlyer() && this.wingFlapCounter == 0) {
			this.wingFlapCounter = 1;
			if (!this.world.isRemote) {
				MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3),
						new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
			}
		}
	}

	@Override
	public void performAnimation(int animationType) {
		/*if (animationType >= 23 && animationType < 60) //transform
        {
            this.transformType = animationType;
            this.transformCounter = 1;
        }*/
		if (animationType == 0) //tail animation
		{
			setPoisoning(true);
		} else if (animationType == 3) //wing flap
		{
			this.wingFlapCounter = 1;
		}
	}

	public boolean getIsPoisoning() {
		return this.isPoisoning;
	}

	public void setPoisoning(boolean flag) {
		if (flag && !this.world.isRemote) {
			MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0),
					new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
		}
		this.isPoisoning = flag;
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
	protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
		boolean isPlayer = (entityIn instanceof EntityPlayer);
		if (!getIsPoisoning() && this.rand.nextInt(5) == 0 && entityIn instanceof EntityLivingBase) {
			setPoisoning(true);
			switch (getManticore()) {
				case Common:
					if (isPlayer && !this.world.isRemote && !this.world.provider.doesWaterVaporize()) {
						((EntityLivingBase) entityIn).setFire(15);
					}
					break;
				case Dark:
				case Green:
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 70, 0));	
					break;
				case Blue:
					((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 70, 0));
					break;
				case None: break;
				default: break;
			}
		} else {
			openMouth();
		}
		super.applyEnchantments(entityLivingBaseIn, entityIn);
	}

	public boolean swingingTail() {
		return getIsPoisoning() && this.poisontimer < 15;
	}

	@Override
	protected SoundEvent getDeathSound() {
		openMouth();
		return MoCSoundEvents.ENTITY_LION_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		openMouth();
		return MoCSoundEvents.ENTITY_LION_HURT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		openMouth();
		return MoCSoundEvents.ENTITY_LION_AMBIENT;
	}

	/*@Override
    protected SoundEvent getDeathSound() {
        return "mocreatures:manticoredying";
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return "mocreatures:manticorehurt";
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return "mocreatures:manticore";
    }*/

	@Override
	public float getSizeFactor() {
		return 1.4F;
	}

	@Override
	protected Item getDropItem() {
		boolean flag = (this.rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);

		switch (getManticore()) {
			case Common:
				if (flag) {
					return MoCItems.scorpStingNether;
				}
				return MoCItems.chitinNether;
			case Dark:
				if (flag) {
					return MoCItems.scorpStingCave;
				}
				return MoCItems.chitinCave;

			case Blue:
				if (flag) {
					return MoCItems.scorpStingFrost;
				}
				return MoCItems.chitinFrost;
			case Green:
				if (flag) {
					return MoCItems.scorpStingDirt;
				}
				return MoCItems.chitin;

			default:
				return MoCItems.chitin;
		}
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		int chance = MoCreatures.proxy.rareItemDropChance;
		if (this.rand.nextInt(100) < chance) {
			entityDropItem(new ItemStack(MoCItems.mocegg, 1, getManticore().egg.eggNum), 0.0F);
		} else {
			super.dropFewItems(flag, x);
		}
	}
}

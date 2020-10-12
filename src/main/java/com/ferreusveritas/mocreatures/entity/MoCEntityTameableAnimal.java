package com.ferreusveritas.mocreatures.entity;

import java.util.List;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHeart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoCEntityTameableAnimal extends MoCEntityAnimal implements IMoCTameable {

	protected static final DataParameter<Boolean> TAMED = EntityDataManager.<Boolean>createKey(MoCEntityTameableAnimal.class, DataSerializers.BOOLEAN);
	private boolean hasEaten;
	private int gestationtime;

	public MoCEntityTameableAnimal(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TAMED, false);
	}

	@Override
	public void setTamed(boolean tamed) {
		this.dataManager.set(TAMED, tamed);
	}

	@Override
	public boolean getIsTamed() {
		return this.dataManager.get(TAMED);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if ((this.isBeingRidden() && entity == this.getRidingEntity()) || (this.getRidingEntity() != null && entity == this.getRidingEntity())) {
			return false;
		}

		return super.attackEntityFrom(damagesource, i);
	}

	private boolean checkOwnership(EntityPlayer player, EnumHand hand) {
		if (!this.getIsTamed() || MoCTools.isThisPlayerAnOP(player)) {
			return true;
		}

		return true;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final Boolean tameResult = this.processTameInteract(player, hand);
		if (tameResult != null) {
			return tameResult;
		}

		return super.processInteract(player, hand);
	}

	// This should always run first for all tameable animals
	public Boolean processTameInteract(EntityPlayer player, EnumHand hand) {
		if (!this.checkOwnership(player, hand)) {
			return false;
		}

		return null;
	}

	// Fixes despawn issue when chunks unload and duplicated mounts when disconnecting on servers
	@Override
	public void setDead() {
		if (!this.world.isRemote && getIsTamed() && getHealth() > 0) {
			return;
		}
		super.setDead();
	}

	@Override
	protected boolean canDespawn() {
		if (MoCreatures.proxy.forceDespawns) {
			return !getIsTamed();
		} else {
			return false;
		}
	}

	/**
	 * Play the taming effect, will either be hearts or smoke depending on
	 * status
	 */
	@Override
	public void playTameEffect(boolean par1) {
		EnumParticleTypes particleType = EnumParticleTypes.HEART;

		if (!par1) {
			particleType = EnumParticleTypes.SMOKE_NORMAL;
		}

		for (int i = 0; i < 7; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.world.spawnParticle(particleType, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + 0.5D
					+ this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, d0, d1, d2);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("Tamed", getIsTamed());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setTamed(nbttagcompound.getBoolean("Tamed"));
	}

	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
	}

	// Override to fix heart animation on clients
	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte par1) {
		if (par1 == 2) {
			this.limbSwingAmount = 1.5F;
			this.hurtResistantTime = this.maxHurtResistantTime;
			this.hurtTime = (this.maxHurtTime = 10);
			this.attackedAtYaw = 0.0F;
			playSound(getHurtSound(DamageSource.GENERIC), getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			attackEntityFrom(DamageSource.GENERIC, 0.0F);
		} else if (par1 == 3) {
			playSound(getDeathSound(), getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			setHealth(0.0F);
			onDeath(DamageSource.GENERIC);
		}
	}

	@Override
	public float getPetHealth() {
		return this.getHealth();
	}

	@Override
	public boolean isRiderDisconnecting() {
		return this.riderIsDisconnecting;
	}

	@Override
	public void setRiderDisconnecting(boolean flag) {
		this.riderIsDisconnecting = flag;
	}

	/**
	 * Used to spawn hearts at this location
	 */
	@Override
	public void spawnHeart() {
		double var2 = this.rand.nextGaussian() * 0.02D;
		double var4 = this.rand.nextGaussian() * 0.02D;
		double var6 = this.rand.nextGaussian() * 0.02D;

		this.world.spawnParticle(EnumParticleTypes.HEART, this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + 0.5D
				+ this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, var2, var4, var6);
	}

	/**
	 * ready to start breeding
	 */
	@Override
	public boolean readytoBreed() {
		return !this.isBeingRidden() && this.getRidingEntity() == null && this.getIsTamed() && this.getHasEaten() && this.getIsAdult();
	}

	@Override
	public String getOffspringClazz(IMoCTameable mate) {
		return "";
	}

	@Override
	public int getOffspringTypeInt(IMoCTameable mate) {
		return 0;
	}

	@Override
	public boolean compatibleMate(Entity mate) {
		return mate instanceof IMoCTameable;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		//breeding code
		if (!this.world.isRemote && readytoBreed() && this.rand.nextInt(100) == 0) {
			doBreeding();
		}

		if (this.getIsFlying()) {
			// Safety checks to prevent 'moving too fast' checks
			if (this.motionX > 0.5) {
				this.motionX = 0.5;
			}
			if (this.motionY > 0.5) {
				this.motionY = 0.5;
			}
			if (this.motionZ > 2.5) {
				this.motionZ = 2.5;
			}
		}
	}

	/**
	 * Breeding code
	 */
	protected void doBreeding() {
		int i = 0;

		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(8D, 3D, 8D));
		for (int j = 0; j < list.size(); j++) {
			Entity entity = list.get(j);
			if (compatibleMate(entity)) {
				i++;
			}
		}

		if (i > 1) {
			return;
		}

		List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(4D, 2D, 4D));
		for (int k = 0; k < list1.size(); k++) {
			Entity mate = list1.get(k);
			if (!(compatibleMate(mate)) || (mate == this)) {
				continue;
			}

			if (!this.readytoBreed()) {
				return;
			}

			if (!((IMoCTameable) mate).readytoBreed()) {
				return;
			}

			setGestationTime(getGestationTime()+1);
			if (!this.world.isRemote) {
				MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(this.getEntityId()),
						new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
			}

			if (getGestationTime() <= 50) {
				continue;
			}

			try {

				String offspringName = this.getOffspringClazz((IMoCTameable) mate);

				EntityLiving offspring = (EntityLiving) EntityList.createEntityByIDFromName(new ResourceLocation(MoCConstants.MOD_PREFIX + offspringName.toLowerCase()), this.world);
				if (offspring != null && offspring instanceof IMoCTameable) {
					IMoCTameable baby = (IMoCTameable) offspring;
					((EntityLiving) baby).setPosition(this.posX, this.posY, this.posZ);
					this.world.spawnEntity((EntityLiving) baby);
					baby.setAdult(false);
					//baby.setEdad(35);
					baby.setTamed(true);
					//baby.setOwnerId(this.getOwnerId());
					baby.setType(getOffspringTypeInt((IMoCTameable) mate));

					/*EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.getOwnerId());
					if (entityplayer != null) {
						MoCTools.tameWithName(entityplayer, baby);
					}*/
				}
				MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);

			} catch (Exception e) {
			}

			this.setHasEaten(false);
			this.setGestationTime(0);
			((IMoCTameable) mate).setHasEaten(false);
			((IMoCTameable) mate).setGestationTime(0);
			break;
		}
	}

	@Override
	public void setHasEaten(boolean flag) {
		hasEaten = flag;
	}

	/**
	 * used to determine if the entity has eaten and is ready to breed
	 */
	@Override
	public boolean getHasEaten() {
		return hasEaten;
	}

	@Override
	public void setGestationTime(int time) {
		gestationtime = time;
	}

	/**
	 * returns breeding timer
	 */
	@Override
	public int getGestationTime() {
		return gestationtime;
	}

}

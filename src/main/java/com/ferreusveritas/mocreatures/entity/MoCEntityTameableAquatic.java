package com.ferreusveritas.mocreatures.entity;

import java.util.List;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
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

@Deprecated
public class MoCEntityTameableAquatic extends MoCEntityAquatic implements IMoCTameable {
	
	protected static final DataParameter<Boolean> TAMED = EntityDataManager.<Boolean>createKey(MoCEntityTameableAquatic.class, DataSerializers.BOOLEAN);
	
	private boolean hasEaten;
	private int gestationtime;
	
	public MoCEntityTameableAquatic(World world) {
		super(world);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TAMED, false);
	}
	
	@Override
	public void setTamed(boolean flag) {
		dataManager.set(TAMED, flag);
	}
	
	@Override
	public boolean getIsTamed() {
		return dataManager.get(TAMED);
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if ((isBeingRidden() && entity == getRidingEntity()) || (getRidingEntity() != null && entity == getRidingEntity())) {
			return false;
		}
		if (usesNewAI()) {
			return super.attackEntityFrom(damagesource, i);
		}
		
		return super.attackEntityFrom(damagesource, i);
	}
	
	private boolean checkOwnership(EntityPlayer player, EnumHand hand) {
		if (!getIsTamed() || MoCTools.isThisPlayerAnOP(player)) {
			return true;
		}
		
		return true;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final Boolean tameResult = processTameInteract(player, hand);
		if (tameResult != null) {
			return tameResult;
		}
		
		return super.processInteract(player, hand);
	}
	
	// This should always run first for all tameable aquatics
	public Boolean processTameInteract(EntityPlayer player, EnumHand hand) {
		if (!checkOwnership(player, hand)) {
			return false;
		}
		
		final ItemStack stack = player.getHeldItem(hand);
		
		if (!stack.isEmpty() && getIsTamed() && isMyHealFood(stack)) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			if (!world.isRemote) {
				setHealth(getMaxHealth());
			}
			return true;
		}
		
		//stores in fishnet
		if (!stack.isEmpty() && stack.getItem() == MoCItems.fishnet && stack.getItemDamage() == 0 && canBeTrappedInNet()) {
			if (!world.isRemote) {
				/*MoCPetData petData = MoCreatures.instance.mapData.getPetData(getOwnerId());
				if (petData != null) {
					petData.setInAmulet(getOwnerPetId(), true);
				}*/
			}
			player.setHeldItem(hand, ItemStack.EMPTY);
			if (!world.isRemote) {
				MoCTools.dropAmulet(this, 1, player);
				isDead = true;
			}
			
			return true;
		}
		
		return null;
	}
	
	// Fixes despawn issue when chunks unload and duplicated mounts when disconnecting on servers
	@Override
	public void setDead() {
		if (!world.isRemote && getIsTamed() && getHealth() > 0 && !riderIsDisconnecting) {
			return;
		}
		super.setDead();
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
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			world.spawnParticle(particleType, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D
					+ rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2);
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
	
	/**
	 * If the rider should be dismounted from the entity when the entity goes
	 * under water
	 *
	 * @param rider The entity that is riding
	 * @return if the entity should be dismounted when under water
	 */
	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return !getIsTamed();
	}
	
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
	}
	
	// Override to fix heart animation on clients
	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte par1) {
		if (par1 == 2) {
			limbSwingAmount = 1.5F;
			hurtResistantTime = maxHurtResistantTime;
			hurtTime = (maxHurtTime = 10);
			attackedAtYaw = 0.0F;
			playSound(getHurtSound(DamageSource.GENERIC), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			attackEntityFrom(DamageSource.GENERIC, 0.0F);
		} else if (par1 == 3) {
			playSound(getDeathSound(), getSoundVolume(), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
			setHealth(0.0F);
			onDeath(DamageSource.GENERIC);
		}
	}
	
	@Override
	public float getPetHealth() {
		return getHealth();
	}
	
	@Override
	public boolean isRiderDisconnecting() {
		return riderIsDisconnecting;
	}
	
	@Override
	public void setRiderDisconnecting(boolean flag) {
		riderIsDisconnecting = flag;
	}
	
	/**
	 * Used to spawn hearts at this location
	 */
	@Override
	public void spawnHeart() {
		double var2 = rand.nextGaussian() * 0.02D;
		double var4 = rand.nextGaussian() * 0.02D;
		double var6 = rand.nextGaussian() * 0.02D;
		
		world.spawnParticle(EnumParticleTypes.HEART, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D
				+ rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, var2, var4, var6);
	}
	
	/**
	 * ready to start breeding
	 */
	@Override
	public boolean readytoBreed() {
		return !isBeingRidden() && getRidingEntity() == null && getIsTamed() && getHasEaten() && getIsAdult();
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
		if (!world.isRemote && readytoBreed() && rand.nextInt(100) == 0) {
			doBreeding();
		}
	}
	
	/**
	 * fixes despawning tamed creatures
	 */
	@Override
	public boolean isEntityInsideOpaqueBlock() {
		if (getIsTamed()) {
			return false;
		}
		
		return super.isEntityInsideOpaqueBlock();
	}
	
	/**
	 * Breeding code
	 */
	protected void doBreeding() {
		int i = 0;
		
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(8D, 3D, 8D));
		for (int j = 0; j < list.size(); j++) {
			Entity entity = list.get(j);
			if (compatibleMate(entity)) {
				i++;
			}
		}
		
		if (i > 1) {
			return;
		}
		
		List<Entity> list1 = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(4D, 2D, 4D));
		for (int k = 0; k < list1.size(); k++) {
			Entity mate = list1.get(k);
			if (!(compatibleMate(mate)) || (mate == this)) {
				continue;
			}
			
			if (!readytoBreed()) {
				return;
			}
			
			if (!((IMoCTameable) mate).readytoBreed()) {
				return;
			}
			
			setGestationTime(getGestationTime()+1);
			if (!world.isRemote) {
				MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()),
						new TargetPoint(world.provider.getDimensionType().getId(), posX, posY, posZ, 64));
			}
			
			if (getGestationTime() <= 50) {
				continue;
			}
			
			try {
				
				String offspringName = getOffspringClazz((IMoCTameable) mate);
				
				EntityLiving offspring = (EntityLiving) EntityList.createEntityByIDFromName(new ResourceLocation(MoCConstants.MOD_PREFIX + offspringName.toLowerCase()), world);
				if (offspring != null && offspring instanceof IMoCTameable) {
					IMoCTameable baby = (IMoCTameable) offspring;
					((EntityLiving) baby).setPosition(posX, posY, posZ);
					world.spawnEntity((EntityLiving) baby);
					baby.setAdult(false);
					//baby.setEdad(35);
					baby.setTamed(true);
					baby.setType(getOffspringTypeInt((IMoCTameable) mate));
					
					/*EntityPlayer entityplayer = world.getPlayerEntityByUUID(getOwnerId());
					if (entityplayer != null) {
						MoCTools.tameWithName(entityplayer, baby);
					}*/
				}
				MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
				
			} catch (Exception e) {
			}
			
			setHasEaten(false);
			setGestationTime(0);
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

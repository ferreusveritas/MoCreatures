package com.ferreusveritas.mocreatures.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCPetData;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHeart;
import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoCEntityTameableAquatic extends MoCEntityAquatic implements IMoCTameable {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(MoCEntityTameableAquatic.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Integer> PET_ID = EntityDataManager.<Integer>createKey(MoCEntityTameableAquatic.class, DataSerializers.VARINT);
	protected static final DataParameter<Boolean> TAMED = EntityDataManager.<Boolean>createKey(MoCEntityTameableAquatic.class, DataSerializers.BOOLEAN);

	private boolean hasEaten;
	private int gestationtime;

	public MoCEntityTameableAquatic(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		this.dataManager.register(PET_ID, -1);
		this.dataManager.register(TAMED, false);
	}

	@Override
	public int getOwnerPetId() {
		return this.dataManager.get(PET_ID);
	}

	@Override
	public void setOwnerPetId(int i) {
		this.dataManager.set(PET_ID, i);
	}

	@Nullable
	public UUID getOwnerId() {
		return this.dataManager.get(OWNER_UNIQUE_ID).orNull();
	}

	public void setOwnerId(@Nullable UUID uniqueId) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uniqueId));
	}

	@Override
	public void setTamed(boolean flag) {
		this.dataManager.set(TAMED, flag);
	}

	@Override
	public boolean getIsTamed() {
		return this.dataManager.get(TAMED);
	}

	@Nullable
	public EntityLivingBase getOwner() {
		try
		{
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException var2)
		{
			return null;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		Entity entity = damagesource.getTrueSource();
		if ((this.isBeingRidden() && entity == this.getRidingEntity()) || (this.getRidingEntity() != null && entity == this.getRidingEntity())) {
			return false;
		}
		if (usesNewAI()) {
			return super.attackEntityFrom(damagesource, i);
		}

		//this avoids damage done by Players to a tamed creature that is not theirs
		if (MoCreatures.proxy.enableOwnership && this.getOwnerId() != null && entity != null
		&& entity instanceof EntityPlayer && !((EntityPlayer) entity).getUniqueID().equals(this.getOwnerId())
		&& !MoCTools.isThisPlayerAnOP((EntityPlayer) entity)) {
			return false;
		}

		return super.attackEntityFrom(damagesource, i);
	}

	private boolean checkOwnership(EntityPlayer player, EnumHand hand) {
		if (!this.getIsTamed() || MoCTools.isThisPlayerAnOP(player)) {
			return true;
		}

		//if the player interacting is not the owner, do nothing!
		if (MoCreatures.proxy.enableOwnership && this.getOwnerId() != null
				&& !player.getUniqueID().equals(this.getOwnerId())) {
			player.sendMessage(new TextComponentTranslation(TextFormatting.RED + "This pet does not belong to you."));
			return false;
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

	// This should always run first for all tameable aquatics
	public Boolean processTameInteract(EntityPlayer player, EnumHand hand) {
		if (!this.checkOwnership(player, hand)) {
			return false;
		}

		final ItemStack stack = player.getHeldItem(hand);
		//before ownership check
		if (!stack.isEmpty() && getIsTamed() && ((stack.getItem() == MoCItems.scrollOfOwner)) && MoCreatures.proxy.enableResetOwnership
				&& MoCTools.isThisPlayerAnOP(player)) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			if (!this.world.isRemote) {
				if (this.getOwnerPetId() != -1) // required since getInteger will always return 0 if no key is found
				{
					MoCreatures.instance.mapData.removeOwnerPet(this, this.getOwnerPetId());//this.getOwnerPetId());
				}
				this.setOwnerId(null);

			}
			return true;
		}
		//if the player interacting is not the owner, do nothing!
		if (MoCreatures.proxy.enableOwnership && getOwnerId() != null
				&& !player.getUniqueID().equals(this.getOwnerId()) && !MoCTools.isThisPlayerAnOP(player)) {
			return true;
		}

		//changes name
		if (!this.world.isRemote && !stack.isEmpty() && getIsTamed()
				&& (stack.getItem() == MoCItems.medallion || stack.getItem() == Items.BOOK || stack.getItem() == Items.NAME_TAG)) {
			if (MoCTools.tameWithName(player, this)) {
				return true;
			}
			return false;
		}

		//sets it free, untamed
		if (!stack.isEmpty() && getIsTamed() && ((stack.getItem() == MoCItems.scrollFreedom))) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			if (!this.world.isRemote) {
				if (this.getOwnerPetId() != -1) // required since getInteger will always return 0 if no key is found
				{
					MoCreatures.instance.mapData.removeOwnerPet(this, this.getOwnerPetId());//this.getOwnerPetId());
				}
				this.setOwnerId(null);
				this.setPetName("");
				this.dropMyStuff();
				this.setTamed(false);
			}

			return true;
		}

		//removes owner, any other player can claim it by renaming it
		if (!stack.isEmpty() && getIsTamed() && ((stack.getItem() == MoCItems.scrollOfSale))) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			if (!this.world.isRemote) {
				if (this.getOwnerPetId() != -1) // required since getInteger will always return 0 if no key is found
				{
					MoCreatures.instance.mapData.removeOwnerPet(this, this.getOwnerPetId());//this.getOwnerPetId());
				}
				this.setOwnerId(null);
			}
			return true;
		}

		if (!stack.isEmpty() && getIsTamed() && isMyHealFood(stack)) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
			MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_EATING);
			if (!this.world.isRemote) {
				this.setHealth(getMaxHealth());
			}
			return true;
		}

		//stores in fishnet
		if (!stack.isEmpty() && stack.getItem() == MoCItems.fishnet && stack.getItemDamage() == 0 && this.canBeTrappedInNet()) {
			if (!this.world.isRemote) {
				MoCPetData petData = MoCreatures.instance.mapData.getPetData(this.getOwnerId());
				if (petData != null) {
					petData.setInAmulet(this.getOwnerPetId(), true);
				}
			}
			player.setHeldItem(hand, ItemStack.EMPTY);
			if (!this.world.isRemote) {
				MoCTools.dropAmulet(this, 1, player);
				this.isDead = true;
			}

			return true;
		}

		return null;
	}

	// Fixes despawn issue when chunks unload and duplicated mounts when disconnecting on servers
	@Override
	public void setDead() {
		if (!this.world.isRemote && getIsTamed() && getHealth() > 0 && !this.riderIsDisconnecting) {
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
		if (this.getOwnerId() != null) {
			nbttagcompound.setString("OwnerUUID", this.getOwnerId().toString());
		}
		if (getOwnerPetId() != -1) {
			nbttagcompound.setInteger("PetId", this.getOwnerPetId());
		}
		if (this instanceof IMoCTameable && getIsTamed() && MoCreatures.instance.mapData != null) {
			MoCreatures.instance.mapData.updateOwnerPet(this);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setTamed(nbttagcompound.getBoolean("Tamed"));
		String s = "";
		if (nbttagcompound.hasKey("OwnerUUID", 8))
		{
			s = nbttagcompound.getString("OwnerUUID");
		}
		if (!s.isEmpty())
		{
			this.setOwnerId(UUID.fromString(s));
		}
		if (nbttagcompound.hasKey("PetId")) {
			setOwnerPetId(nbttagcompound.getInteger("PetId"));
		}
		if (this.getIsTamed() && nbttagcompound.hasKey("PetId")) {
			MoCPetData petData = MoCreatures.instance.mapData.getPetData(this.getOwnerId());
			if (petData != null) {
				NBTTagList tag = petData.getOwnerRootNBT().getTagList("TamedList", 10);
				for (int i = 0; i < tag.tagCount(); i++) {
					NBTTagCompound nbt = tag.getCompoundTagAt(i);
					if (nbt.getInteger("PetId") == nbttagcompound.getInteger("PetId")) {
						// update amulet flag
						nbt.setBoolean("InAmulet", false);
						// check if cloned and if so kill
						if (nbt.hasKey("Cloned")) {
							// entity was cloned
							nbt.removeTag("Cloned"); // clear flag
							this.setTamed(false);
							this.setDead();
						}
					}
				}
			} else // no pet data was found, mocreatures.dat could of been deleted so reset petId to -1
			{
				this.setOwnerPetId(-1);
			}
		}
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
		return !this.getIsTamed();
	}

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
	}

	/**
	 * fixes despawning tamed creatures
	 */
	@Override
	public boolean isEntityInsideOpaqueBlock() {
		if (this.getIsTamed()) {
			return false;
		}

		return super.isEntityInsideOpaqueBlock();
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
					baby.setEdad(35);
					baby.setTamed(true);
					baby.setOwnerId(this.getOwnerId());
					baby.setType(getOffspringTypeInt((IMoCTameable) mate));

					EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.getOwnerId());
					if (entityplayer != null) {
						MoCTools.tameWithName(entityplayer, baby);
					}
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

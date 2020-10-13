package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.ITame;
import com.google.common.base.Optional;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.Constants.NBT;

public class ComponentTame<T extends EntityAnimalComp> extends Component<T> implements ITame {
	
	private static class PerClassValues {
		public final DataParameter<Boolean> TAMED;
		public final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID;
		
		public PerClassValues(Class clazz) {
			TAMED = EntityDataManager.<Boolean>createKey(clazz, DataSerializers.BOOLEAN);
			OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(clazz, DataSerializers.OPTIONAL_UNIQUE_ID);
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz) {
		return classMap.computeIfAbsent(clazz, PerClassValues::new);
	}
	
	private final PerClassValues values;
	
	public ComponentTame(Class clazz, T animal) {
		super(animal);
		this.values = getValues(clazz);
	}
	
	protected void setTamed(boolean tamed) {
		dataManager.set(values.TAMED, tamed);
	}
	
	@Override
	public void setTamedBy(EntityPlayer player) {
		setTamed(true);
		setOwnerId(player.getUniqueID());
		
		if (player instanceof EntityPlayerMP) {
			CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP)player, animal);
		}
	}
	
	@Override
	public UUID getOwnerId() {
		return (UUID)((Optional)dataManager.get(values.OWNER_UNIQUE_ID)).orNull();
	}
	
	public void setOwnerId(@Nullable UUID uuid) {
		dataManager.set(values.OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}
	
	public boolean isTamed() {
		return getOwnerId() != null;
	}
	
	@Override
	public Entity getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : animal.world.getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException v) {
			return null;
		}
	}
	
	public boolean isOwner(EntityPlayer player) {
		return player.getUniqueID() == getOwnerId();
	}
	
	@Override
	public void register() {
		dataManager.register(values.TAMED, false);
		dataManager.register(values.OWNER_UNIQUE_ID, Optional.absent());
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setString("OwnerUUID", getOwnerId() == null ? "" : getOwnerId().toString());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("OwnerUUID", NBT.TAG_STRING)) {
			String s = compound.getString("OwnerUUID");
			if (!s.isEmpty()) {
				try {
					setOwnerId(UUID.fromString(s));
					setTamed(true);
				}
				catch (Throwable v) {
					setTamed(false);
				}
			}
		}
	}
	
	@Override
	public boolean handleStatusUpdate(byte id) {
		switch(id) {
			case 6: playTameEffect(false); return true;
			case 7: playTameEffect(true); return true;
			default: super.handleStatusUpdate(id); return false;
		}
	}
	
	protected void tameSuccess() {
		animal.getNavigator().clearPath(); //Forget wherever you were going
		animal.setAttackTarget((EntityLivingBase)null); //Forget whatever you were attacking
		//animal.aiSit.setSitting(true);
		playTameEffect(true);
		animal.world.setEntityState(animal, (byte)7);
		postTame();
	}
	
	protected void tameFail() {
		playTameEffect(false);
		animal.world.setEntityState(animal, (byte)6);
	}
	
	protected void postTame() {
		animal.setHealth(animal.getMaxHealth());
	}
	
	protected boolean tameChance() {
		return animal.world.rand.nextInt(3) == 0;
	}
	
	protected void playTameEffect(boolean play) {
		EnumParticleTypes enumparticletypes = play ? EnumParticleTypes.HEART : EnumParticleTypes.SMOKE_NORMAL;
		
		Random rand = animal.world.rand;
		
		for (int i = 0; i < 7; ++i) {
			double x = rand.nextGaussian() * 0.02;
			double y = rand.nextGaussian() * 0.02;
			double z = rand.nextGaussian() * 0.02;
			animal.world.spawnParticle(enumparticletypes,
					animal.posX + rand.nextFloat() * animal.width * 2.0f - animal.width,
					animal.posY + 0.5 + rand.nextFloat() * animal.height,
					animal.posZ + rand.nextFloat() * animal.width * 2.0f - animal.width,
					x, y, z);
		}
	}
	
}

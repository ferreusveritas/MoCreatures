package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ComponentRide<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	private static class PerClassValues {
		public final DataParameter<Boolean> RIDEABLE;
		
		public PerClassValues(Class clazz) {
			RIDEABLE = EntityDataManager.<Boolean>createKey(clazz, DataSerializers.BOOLEAN);
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz) {
		return classMap.computeIfAbsent(clazz, PerClassValues::new);
	}
	
	private final PerClassValues values;
	
	public ComponentRide(Class clazz, T animal) {
		super(animal);
		this.values = getValues(clazz);
	}
	
	private ComponentTame tame;
	
	@Override
	public void link() {
		tame = animal.getComponent(ComponentTame.class);
	}
	
	@Override
	public void register() {
		dataManager.register(values.RIDEABLE, false);
	}
	
	public boolean isRideable() {
		return dataManager.get(values.RIDEABLE);
	}
	
	public void setRideable(boolean rideable) {
		dataManager.set(values.RIDEABLE, rideable );
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setBoolean("Rideable", isRideable());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setRideable(compound.getBoolean("Rideable"));
	}
	
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if(animal.isAdult()) {
			if (!isRideable() && isTame() && (itemStack.getItem() == Items.SADDLE)) {
				consumeItemFromStack(player, itemStack);
				setRideable(true);
				return true;
			} else {
				if(!player.isSneaking()) {
					animal.atAttention();
					player.rotationYaw = animal.rotationYaw;
					player.rotationPitch = animal.rotationPitch;
					player.startRiding(animal);
				}
			}
		}
		return false;
	}
	
	@Override
	public void dropStuff() {
		//MoCTools.dropSaddle(animal, animal.world); TODO
		super.dropStuff();
	}
	
	/** Moves the entity based on the specified heading.  Args: strafe, forward */
	public boolean travel(float strafe, float vertical, float forward) {
		
		if (animal.isBeingRidden()) {
			EntityLivingBase passenger = (EntityLivingBase)animal.getControllingPassenger();
			if (passenger != null) {
				return moveWithRider(strafe, vertical, forward, passenger); //riding movement
			}
		}
		/*if ((animal.isAmphibian() && animal.isInWater()) || (animal.isFlyer() && getIsFlying()) { //amphibian in water movement
				animal.moveRelative(strafe, vertical, forward, 0.1F);
				animal.move(MoverType.SELF, animal.motionX, animal.motionY, animal.motionZ);
				animal.motionX *= 0.9;
				animal.motionY *= 0.9;
				animal.motionZ *= 0.9;
				if (animal.getAttackTarget() == null) {
					animal.motionY -= 0.005D;
				}
				return true;
			}*/
		return false; // regular movement
	}
	
	private boolean isTame() {
		return tame != null && tame.isTamed();
	}
	
	/**
	 ** Riding Code
	 * @param strafe
	 * @param forward
	 * @param passenger 
	 */
	public boolean moveWithRider(float strafe, float vertical, float forward, @Nonnull EntityLivingBase passenger) {
		
		return isTame() ? 
				moveEntityWithRiderTamed(strafe, vertical, forward, passenger) : 
					moveEntityWithRiderUntamed(strafe, vertical, forward, passenger);
	}
	
	protected void moveJump() {
		
		if (animal.isJumpPending() && !animal.isJumping() && animal.onGround) {
			animal.motionY += animal.getCustomJump() * (animal.isBeingRidden() ? 2.0 : 1.0);
			animal.velocityChanged = true;
			animal.setJumping(true);
			animal.setJumpPending(false);
		}
	}
	
	public boolean moveEntityWithRiderTamed(float strafe, float vertical, float forward, @Nonnull EntityLivingBase passenger) {
		
		if(isRideable()) {
			animal.rotationYaw = passenger.rotationYaw;
			animal.prevRotationYaw = animal.rotationYaw;
			animal.rotationPitch = passenger.rotationPitch * 0.5F;
			animal.setRotation(animal.rotationYaw, animal.rotationPitch);
			animal.renderYawOffset = animal.rotationYaw;
			animal.rotationYawHead = animal.renderYawOffset;
			
			strafe = (float) (passenger.moveStrafing * 0.5F * animal.getCustomSpeed());
			forward = (float) (passenger.moveForward * animal.getCustomSpeed());
			
			moveJump();
			
			if (animal.isDivePending()) {
				animal.setDivePending(false);
				animal.motionY -= 0.3D;
			}
			
			animal.setAIMoveSpeed((float) animal.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			
		}
		
		animal.superTravel(strafe, vertical, forward);
		
		animal.setJumping(false);
		animal.setDivePending(false);
		animal.setJumpPending(false);
		
		return true;
	}
	
	public boolean moveEntityWithRiderUntamed(float strafe, float vertical, float forward, EntityLivingBase passenger) {
		
		//Riding behavior if untamed
		
		World world = animal.world;
		Random rand = animal.world.rand;
		
		if (!world.isRemote) {
			
			//moveJump();
			
			if (rand.nextInt(5) == 0) {
				animal.motionX += (rand.nextDouble() - 0.5) / 3.0;
				animal.motionZ += (rand.nextDouble() - 0.5) / 3.0;
				animal.velocityChanged = true;
			}
			if(rand.nextInt(10) == 0) {
				if (rand.nextInt(5) == 0) {
					animal.removePassengers();
					//animal.motionY += 0.25;
					animal.velocityChanged = true;
					passenger.addVelocity((rand.nextDouble() - 0.5), 0.6, (rand.nextDouble() - 0.5));
					passenger.velocityChanged = true;
					return true;
				}
				animal.setJumpPending(true);
			}
			
			animal.move(MoverType.SELF, animal.motionX, animal.motionY, animal.motionZ);
			
		}
		
		moveJump();
		
		
		//animal.setJumping(false);
		
		/*
		 * TODO: Implement temper tame component
				if (!world.isRemote && this instanceof IMoCTameable && passenger instanceof EntityPlayer) {
					int chance = (getMaxTemper() - getTemper());
					if (chance <= 0) {
						chance = 1;
					}
					if (rand.nextInt(chance * 8) == 0) {
						MoCTools.tameWithName((EntityPlayer) passenger, (IMoCTameable) this);
					}
				}
		 */
		
		return false;
	}
	
}

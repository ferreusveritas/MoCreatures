package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

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
	
	protected ComponentTame tame;
	
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
	
	private boolean checkRidingPermission(EntityPlayer player) {
		return MoCTools.isThisPlayerAnOP(player) || isOwner(player);
	}
	
	protected boolean canBeRiddenByPlayer(EntityPlayer player) {
		return isTamed() ? checkRidingPermission(player) : true;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if(animal.isAdult()) {
			if (!isRideable() && isTamed() && (itemStack.getItem() == Items.SADDLE)) {
				consumeItemFromStack(player, itemStack);
				setRideable(true);
				return true;
			} else {
				if(!player.isSneaking() && canBeRiddenByPlayer(player)) {
					animal.atAttention();
					player.rotationYaw = animal.rotationYaw;
					player.rotationPitch = animal.rotationPitch;
					player.startRiding(animal);
					return true;
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
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if (animal.getRidingEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) animal.getRidingEntity();
			List<Entity> list = animal.world.getEntitiesWithinAABBExcludingEntity(animal, animal.getEntityBoundingBox().expand(1.0, 0.0, 1.0));
			for (Entity entity : list) {
				if (!entity.isDead) {
					entity.onCollideWithPlayer(entityplayer);
					if (entity instanceof EntityMob) {
						if ((animal.getDistance(entity) < 2.0F) && entity instanceof EntityMob && (animal.world.rand.nextInt(10) == 0)) {
							animal.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity),
									(float) ((EntityMob) entity).getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
						}
					}
				}
			}
		}
		
	}
	
	/** Moves the entity based on the specified heading.  Args: strafe, forward */
	public boolean travel(float strafe, float vertical, float forward) {
		
		if (animal.isBeingRidden()) {
			EntityLivingBase passenger = (EntityLivingBase)animal.getControllingPassenger();
			if (passenger instanceof EntityLivingBase) {
				return isTamed() ? 
						moveEntityWithRiderTamed(strafe, vertical, forward, passenger) : 
							moveEntityWithRiderUntamed(strafe, vertical, forward, passenger);
			}
		}
		
		return false; // regular movement
	}
	
	protected boolean isTamed() {
		return tame != null && tame.isTamed();
	}
	
	protected boolean isOwner(EntityPlayer player) {
		return isTamed() && tame.isOwner(player);
	}
	
	public void performJump() {
		if (!animal.isJumping() && animal.isJumpPending()) {
			animal.motionY += animal.getCustomJump();
			animal.setJumping(true);
			animal.setJumpPending(false);
		}
	}
	
	protected void moveJump() {
		
		if (animal.onGround) {
			animal.setJumping(false);
		}
		
		if (animal.isJumpPending() && !animal.isJumping() && animal.onGround) {
			animal.motionY += animal.getCustomJump() * (animal.isBeingRidden() ? 2.0 : 1.0);
			animal.velocityChanged = true;
			animal.setJumping(true);
			animal.setJumpPending(false);
		}
	}
	
	protected void moveDive() {
		//So it doesn't sink on its own
		/*if (animal.motionY < 0D && isSwimming()) {
			animal.motionY = 0D;
		}*/
		
		if (animal.isDivePending()) {
			animal.setDivePending(false);
			animal.motionY -= 0.3D;
		}
	}
	
	protected float strafePenalty() {
		return 0.5f;
	}
	
	public boolean moveEntityWithRiderTamed(float strafe, float vertical, float forward, @Nonnull EntityLivingBase passenger) {
		
		if(isRideable()) {
			animal.rotationYaw = passenger.rotationYaw;
			animal.prevRotationYaw = animal.rotationYaw;
			animal.rotationPitch = passenger.rotationPitch * 0.5F;
			animal.setRotation(animal.rotationYaw, animal.rotationPitch);
			animal.renderYawOffset = animal.rotationYaw;
			animal.rotationYawHead = animal.renderYawOffset;
			
			strafe = passenger.moveStrafing * strafePenalty() * (float)animal.getCustomSpeed();
			forward = (float) (passenger.moveForward * animal.getCustomSpeed());
			
			moveJump();
			moveDive();
			
			animal.setAIMoveSpeed((float) animal.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
		}
		
		animal.superTravel(strafe, vertical, forward);
		
		/*
		animal.setJumping(false);
		animal.setDivePending(false);
		animal.setJumpPending(false);
		*/
		
		return true;
	}
	
	public boolean moveEntityWithRiderUntamed(float strafe, float vertical, float forward, EntityLivingBase passenger) {
		
		if (animal.isServer()) {
			Random rand = animal.world.rand;
			
			//Thrash around randomly
			if (rand.nextInt(5) == 0) {
				animal.motionX += (rand.nextDouble() - 0.5) / 3.0;
				animal.motionZ += (rand.nextDouble() - 0.5) / 3.0;
				animal.velocityChanged = true;
			}
			
			//Jump about randomly
			if(rand.nextInt(10) == 0) {
				//Randomly jettison the passenger during a random jump 
				if (rand.nextInt(5) == 0) {
					animal.removePassengers();
					animal.velocityChanged = true;
					passenger.addVelocity((rand.nextDouble() - 0.5), 0.6, (rand.nextDouble() - 0.5));
					passenger.velocityChanged = true;
					return true;
				}
				performJump();
			}
			
			animal.move(MoverType.SELF, animal.motionX, animal.motionY, animal.motionZ);
			
			if (passenger instanceof EntityPlayer) {
				if(tame instanceof ComponentTameTemper) {
					ComponentTameTemper temperTame = (ComponentTameTemper) tame;
					temperTame.attemptTaming((EntityPlayer) passenger);
				}
			}
		}
		
		moveJump();
		
		return false;
	}
	
}

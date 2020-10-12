package com.ferreusveritas.mocreatures.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.entity.components.Component;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityAnimalComp extends EntityAnimal {
	
	public EntityAnimalComp(World world) {
		super(world);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		componentMap = new HashMap();
		componentList = new ArrayList<>();
		setupComponents();
		componentList.trimToSize();
		componentList.forEach(c -> c.register());
		componentList.forEach(c -> c.link());
	}
	
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		
		if(super.processInteract(player, hand)) {
			return true;
		}
		
		ItemStack itemStack = player.getHeldItem(hand);
		
		for(Component component : componentList) {
			if(component instanceof IProcessInteract) {
				if(((IProcessInteract)component).processInteract(player, hand, itemStack)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		updateHealing();
		
		componentList.forEach(c -> c.onLivingUpdate());
	}
	
	//TODO: Move to either amulet item or create component
	//** Interaction method for storing a tame animal in an amulet */
	/*public boolean processInteractAmulet(EntityPlayer player, EnumHand hand, ItemStack stack) {
		//stores in petAmulet
		if (stack.getItem() == MoCItems.petamulet && stack.getItemDamage() == 0) {
			player.setHeldItem(hand, ItemStack.EMPTY);
			if (!world.isRemote) {
				//petData.setInAmulet(this.getOwnerPetId(), true);
				//dropMyStuff();
				//MoCTools.dropAmulet(this, 2, player);
				isDead = true;
			}
			return true;
		}
		
		return false;
	}*/
	
	
	
	////////////////////////////////////////////////////////////////
	// Attributes
	////////////////////////////////////////////////////////////////
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		setupAttributes();
		return super.onInitialSpawn(difficulty, livingdata);
	}
	
	public void setupAttributes() { }
	
	
	////////////////////////////////////////////////////////////////
	// AI
	////////////////////////////////////////////////////////////////
	
	/** Causes an animal in a sitting or resting position to stand up and be ready for action, such as hunting or riding */
	public void atAttention() { }
	
	public void atEase() { }
	
	
	////////////////////////////////////////////////////////////////
	// Movement special
	////////////////////////////////////////////////////////////////
	
	protected boolean jumping;
	protected boolean jumpPending;
	protected boolean divePending;
	
	public boolean isJumping() {
		return jumping;
	}
	
	@Override
	public void setJumping(boolean flag) {
		jumping = flag;
	}
	
	public void setJumpPending(boolean val) {
		jumpPending = val;
	}
	
	public boolean isJumpPending() {
		return jumpPending;
	}
	
	public void setDivePending(boolean val) {
		divePending = val;
	}
	
	public boolean isDivePending() {
		return divePending;
	}
	
	public double getCustomJump() {
		return 0.25f;
	}
	
	/**
	 * @return The speed multiplier used when mounted
	 */
	public double getCustomSpeed() {
		return 1.0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Healing
	////////////////////////////////////////////////////////////////
	
	public void updateHealing() {
		if (!world.isRemote && (rand.nextInt(300) == 0) && (getHealth() <= getMaxHealth()) && !isCorpse()) {
			setHealth(getHealth() + 1);
		}
	}
	
	////////////////////////////////////////////////////////////////
	// Food
	////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Death
	////////////////////////////////////////////////////////////////
	
	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		getComponents().forEach(c -> c.dropStuff());
	}
	
	public boolean isCorpse() {
		return deathTime != 0;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Spawning
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Swimming
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Hunting/Attacking
	////////////////////////////////////////////////////////////////
	
	public boolean canAttackTarget(EntityLivingBase entity) {
		return height >= entity.height && width >= entity.width;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Data
	////////////////////////////////////////////////////////////////
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		componentList.forEach(c -> c.writeComponentToNBT(compound));
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		componentList.forEach(c -> c.readComponentFromNBT(compound));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		for(Component component : componentList) {
			if(component.handleStatusUpdate(id)) {
				return;
			}
		}
		
		super.handleStatusUpdate(id);
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	// --COMPONENTS--
	//
	////////////////////////////////////////////////////////////////
	
	private Map<Class<?>, Component> componentMap;
	private ArrayList<Component> componentList;
	
	abstract protected void setupComponents();
	
	public void addComponents(Component ... components) {
		for(Component component : components) {
			componentMap.put(component.getClass(), component);
			componentList.add(component);
		}
	}
	
	public <T> T getComponent(Class<? extends T> clazz) {
		for(Component component : componentList) {
			if(clazz.isAssignableFrom(component.getClass())) {
				return (T)component;
			}
		}
		return null;
	}
	
	public Collection<Component> getComponents() {
		return componentMap.values();
	}
	
	
	////////////////////////////////////////////////////////////////
	// Used by various components
	////////////////////////////////////////////////////////////////
	
	public boolean isAdult() {
		return getGrowingAge() >= 0;
	}
	
	@Override
	protected void onGrowingAdult() {
		setGrowingAge(0);
		setupAttributes();
	}
	
	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return getPassengers().isEmpty() ? null : getPassengers().get(0);
	}
	
	public boolean isSitting() {
		return false;
	}
	
	
	////////////////////////////////////////////////////////////////
	// Expose protected functions for components
	////////////////////////////////////////////////////////////////
	
	@Override
	public void setRotation(float yaw, float pitch) {
		super.setRotation(yaw, pitch);
	}
	
	public void superTravel(float strafe, float vertical, float forward) {
		super.travel(strafe, vertical, forward);
	}
	
	
	
	////////////////////////////////////////////////////////////////
	//
	// --CLIENT SIDE--
	//
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Sounds
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Animations
	////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////
	// Rendering
	////////////////////////////////////////////////////////////////
	
}





//Organization Template Follows:

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// AI
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Movement Special
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Healing
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Food
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Death
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Spawning
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Swimming
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Hunting/Attacking
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Data
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
//
// --COMPONENTS--
//
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Taming
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Age and Gender
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Heal Food
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Riding
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Sitting
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Chest
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
//
// --CLIENT SIDE--
//
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Sounds
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Animations
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
// Rendering
////////////////////////////////////////////////////////////////

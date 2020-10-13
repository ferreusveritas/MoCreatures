package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.components.ComponentStandSit.Posture;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityPandaBear extends EntityBear {
	
	public EntityPandaBear(World world) {
		super(world);
	}
	
	@Override
	public ResourceLocation getTexture() {
		return MoCreatures.proxy.getTexture("bearpanda.png");
	}
	
	@Override
	public float getSpeciesSize() {
		return 1.2F;
	}
	
	@Override
	public float calculateMaxHealth() {
		return 20;
	}
	
	@Override
	public boolean isReadyToHunt() {
		return false;
	}
	
	@Override
	public double calculateAttackDmg() {
		return 1;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean shouldAttackPlayers() {
		return false;
	}
	
	@Override
	protected boolean isEdible(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.REEDS;
	}
	
	@Override
	public boolean isMyFavoriteFood(ItemStack stack) {
		return isEdible(stack);
	}
	
	@Override
	protected int foodNourishment(ItemStack stack) {
		return isEdible(stack) ?  2 : 0;
	}
	
	public boolean processInteractFeed(EntityPlayer player, ItemStack stack, EnumHand hand) {
		if (!stack.isEmpty() && (stack.getItem() == Items.SUGAR || stack.getItem() == Items.REEDS)) {
			
			consumeItemFromStack(player, stack);
			
			if (!world.isRemote) {
				setTamedBy(player);
			}
			
			setHealth(getMaxHealth());
			eatingAnimal();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void updateSitting() {
		
		//panda bears and cubs will sit down every now and then
		if (isAdult() && !isTamed() && (rand.nextInt(300) == 0)) {
			setPosture(Posture.Sitting);
		}
		
		super.updateSitting();
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		EntityPandaBear bearcub = new EntityPandaBear(world);
		bearcub.tame.setOwnerId(getOwnerId());
		return bearcub;
	}
	
}

package com.ferreusveritas.mocreatures.entity.components;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Component<T extends EntityAnimalComp> {
	
	public final T animal;
	protected final EntityDataManager dataManager;
	
	public Component(T animal) {
		this.animal = animal;
		this.dataManager = animal.getDataManager();
	}
	
	public void register() { }
	
	public void link() { }
	
	public void writeComponentToNBT(NBTTagCompound compound) { }
	
	public void readComponentFromNBT(NBTTagCompound compound) { }
	
	public void onLivingUpdate() { }
	
	public void dropStuff() { }
	
	protected void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public boolean handleStatusUpdate(byte id) {
		return false;
	}
	
}

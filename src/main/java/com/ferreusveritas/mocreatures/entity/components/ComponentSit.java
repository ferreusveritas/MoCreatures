package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;
import com.ferreusveritas.mocreatures.init.MoCItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;

public class ComponentSit<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	private static class PerClassValues {
		public final DataParameter<Boolean> SITTING;
		
		public PerClassValues(Class clazz) {
			SITTING = EntityDataManager.<Boolean>createKey(clazz, DataSerializers.BOOLEAN);
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz) {
		return classMap.computeIfAbsent(clazz, PerClassValues::new);
	}
	
	private final PerClassValues values;
	private ComponentTame tame;

	public ComponentSit(Class clazz, T animal) {
		super(animal);
		this.values = getValues(clazz);
	}
	
	@Override
	public void link() {
		super.link();
		tame = animal.getComponent(ComponentTame.class);
	}
	
	@Override
	public void register() {
		dataManager.register(values.SITTING, false);
	}
	
	public boolean isSitting() {
		return dataManager.get(values.SITTING);
	}
	
	public void setSitting(boolean sit) {
		dataManager.set(values.SITTING, sit );
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setBoolean("Sitting", isSitting());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setSitting(compound.getBoolean("Sitting"));
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		if(player.isSneaking() && tame != null && tame.getOwner() == player && itemStack.getItem() == MoCItems.whip) {
			setSitting(!isSitting());
			return true;
		}
		
		return false;
	}
	
}

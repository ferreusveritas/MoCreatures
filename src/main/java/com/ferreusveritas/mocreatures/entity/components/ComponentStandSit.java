package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;

public class ComponentStandSit<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	public enum Posture {
		AllFours,
		Standing,
		Sitting
	}
	
	private static class PerClassValues {
		public final DataParameter<Integer> POSTURE;
		
		public PerClassValues(Class clazz) {
			POSTURE = EntityDataManager.<Integer>createKey(clazz, DataSerializers.VARINT);
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz) {
		return classMap.computeIfAbsent(clazz, PerClassValues::new);
	}
	
	private final PerClassValues values;
	private ComponentTame tame;
	
	public ComponentStandSit(Class clazz, T animal) {
		super(animal);
		this.values = getValues(clazz);
	}
	
	@Override
	public void link() {
		super.link();
		tame = animal.getComponent(ComponentTame.class);
	}
	
	
	public Posture getPosture() {
		return Posture.values()[dataManager.get(values.POSTURE)];
	}
	
	public void setPosture(Posture posture) {
		dataManager.set(values.POSTURE, posture.ordinal());
	}
	
	@Override
	public void register() {
		dataManager.register(values.POSTURE, Posture.AllFours.ordinal());
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setByte("StandSit", (byte) getPosture().ordinal());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setPosture(Posture.values()[compound.getByte("StandSit")]);
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if(player.isSneaking() && tame != null && tame.getOwner() == player) {
			setPosture(getPosture() == Posture.Sitting ? Posture.AllFours : Posture.Sitting);
			return true;
		}
		
		return false;
	}
	
}

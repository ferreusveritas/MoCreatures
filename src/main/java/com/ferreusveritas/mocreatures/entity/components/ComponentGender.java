package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

public class ComponentGender<T extends EntityAnimalComp> extends Component<T> implements IGender {
	
	private static class PerClassValues {
		public final DataParameter<Byte> GENDER;
		
		public PerClassValues(Class clazz) {
			GENDER = EntityDataManager.<Byte>createKey(clazz, DataSerializers.BYTE);
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz) {
		return classMap.computeIfAbsent(clazz, PerClassValues::new);
	}
	
	private final PerClassValues values;
	
	public ComponentGender(Class clazz, T animal) {
		super(animal);
		this.values = getValues(clazz);
	}
	
	@Override
	public void register() {
		dataManager.register(values.GENDER, Gender.None.toByte());
	}
	
	@Override
	public Gender getGender() {
		return Gender.fromByte(dataManager.get(values.GENDER));
	}
	
	@Override
	public void setGender(Gender gender) {
		dataManager.set(values.GENDER, gender.toByte());
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setByte("Gender", getGender().toByte());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setGender(Gender.fromByte(compound.getByte("Gender")));
	}
	
}

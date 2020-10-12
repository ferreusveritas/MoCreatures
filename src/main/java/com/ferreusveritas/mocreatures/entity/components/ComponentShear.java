package com.ferreusveritas.mocreatures.entity.components;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ComponentShear<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	final boolean requiresTame;
	
	public ComponentShear(Class clazz, T animal, boolean requiresTame) {
		super(animal);
		this.requiresTame = requiresTame;
	}
	
	private ComponentTame tameable;
	
	@Override
	public void link() {
		super.link();
		tameable = animal.getComponent(ComponentTame.class);
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if( requiresTame || (tameable != null && tameable.isTamed() && tameable.getOwner() == player) ) {
			if (itemStack.getItem() == Items.SHEARS) {
				if (!animal.world.isRemote) {
					animal.getComponents().forEach(c -> c.dropStuff());
				}
				return true;
			}
		}
		
		return false;
	}
	
}

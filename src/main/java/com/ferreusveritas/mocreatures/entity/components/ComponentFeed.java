package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ComponentFeed<T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	private static class PerClassValues<T> {
		public final boolean requiresTame;
		
		public PerClassValues(Class clazz, boolean requiresTame) {
			this.requiresTame = requiresTame;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static <T> PerClassValues getValues(Class clazz, boolean requiresTame) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, requiresTame));
	}
	
	private final PerClassValues<T> values;
	private ComponentTame tame;
	
	public ComponentFeed(Class clazz, T animal, boolean requiresTame) {
		super(animal);
		values = getValues(clazz, requiresTame);
	}
	
	@Override
	public void link() {
		super.link();
		tame = animal.getComponent(ComponentTame.class);
	}
	
	protected boolean isTamed() {
		return tame != null && tame.isTamed();
	}
	
	protected boolean canPlayerFeed(EntityPlayer player) {
		return values.requiresTame ? isTamed() : true;
	}
	
	public boolean feed(EntityPlayer player, ItemStack itemStack) {
		boolean consume = false;
		if(player == null || (player != null && canPlayerFeed(player))) {
			for(Component component : animal.getComponents()) {
				if(component instanceof IFeedable) {
					consume |= ((IFeedable)component).feed(player, itemStack);
				}
			}
			if(consume) {
				MoCTools.playCustomSound(animal, MoCSoundEvents.ENTITY_GENERIC_EATING);//TODO: Customize
				return true;
			}
		}
		return consume;
	}
	
	public boolean feed(EntityItem entityItem) {
		ItemStack stack = entityItem.getItem();
		if(feed(null, stack)) {
			stack.shrink(1);
			if(stack.isEmpty()) {
				entityItem.setDead();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if(feed(player, itemStack)) {
			consumeItemFromStack(player, itemStack);//Ensure only a single item is consumed
			return true;
		}
		
		return false;
	}
	
}

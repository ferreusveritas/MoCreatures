package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.IProcessInteract;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.inventory.MoCAnimalChest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;

public class ComponentChest <T extends EntityAnimalComp> extends Component<T> implements IProcessInteract {
	
	private static class PerClassValues {
		public final DataParameter<Boolean> CHESTED;
		public final String chestName;
		
		public PerClassValues(Class clazz, String chestName) {
			CHESTED = EntityDataManager.<Boolean>createKey(clazz, DataSerializers.BOOLEAN);
			this.chestName = chestName;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz, String chestName) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, chestName));
	}
	
	private final PerClassValues values;
	private MoCAnimalChest localchest;
	
	public ComponentChest(Class clazz, T animal, String chestName) {
		super(animal);
		this.values = getValues(clazz, chestName);
	}
	
	@Override
	public void register() {
		dataManager.register(values.CHESTED, false);
	}
	
	public boolean isChested() {
		return dataManager.get(values.CHESTED);
	}
	
	public void setChested(boolean chest) {
		dataManager.set(values.CHESTED, chest );
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setBoolean("Chested", isChested());
		
		if (isChested() && localchest != null) {
			NBTTagList nbttaglist = new NBTTagList();
			for (int i = 0; i < localchest.getSizeInventory(); i++) {
				ItemStack stack = localchest.getStackInSlot(i);
				if (stack != null && !stack.isEmpty()) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					nbttaglist.appendTag(tag);
				}
			}
			compound.setTag("Items", nbttaglist);
		}
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setChested(compound.getBoolean("Chested"));
		
		if (isChested()) {
			NBTTagList itemlist = compound.getTagList("Items", 10);
			localchest = new MoCAnimalChest(values.chestName, 18);
			for (int i = 0; i < itemlist.tagCount(); i++) {
				NBTTagCompound tag = itemlist.getCompoundTagAt(i);
				int j = tag.getByte("Slot") & 0xff;
				if ((j >= 0) && j < localchest.getSizeInventory()) {
					localchest.setInventorySlotContents(j, new ItemStack(tag));
				}
			}
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
		
		if (isChested() && player.isSneaking() && itemStack.getItem() != MoCItems.whip) {
			// if first time opening a chest, we must initialize it
			if (localchest == null) {
				localchest = new MoCAnimalChest(values.chestName, 18);
			}
			if (!animal.world.isRemote) {
				player.displayGUIChest(localchest);
			}
			return true;
		}
		
		if (!isChested() && itemStack.getItem() == Item.getItemFromBlock(Blocks.CHEST)) {
			setChested(true);
			consumeItemFromStack(player, itemStack);
			return true;
		}
		
		return false;
	}
	
	@Override
	public void dropStuff() {
		super.dropStuff();
		
		if (isChested()) {
			MoCTools.dropInventory(animal, localchest);
			MoCTools.dropCustomItem(animal, animal.world, new ItemStack(Blocks.CHEST, 1));
			setChested(false);
		}
		
	}
	
	
}

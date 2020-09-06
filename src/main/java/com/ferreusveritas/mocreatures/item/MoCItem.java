package com.ferreusveritas.mocreatures.item;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCreatures;
import net.minecraft.item.Item;

public class MoCItem extends Item {

	public MoCItem() {
	}

	public MoCItem(String name) {
		this(name, 0);
	}

	public MoCItem(String name, int meta) {
		this.setCreativeTab(MoCreatures.tabMoC);
		this.setRegistryName(MoCConstants.MOD_ID, name);
		this.setUnlocalizedName(name);
	}
}

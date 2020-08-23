package com.ferreusveritas.mocreatures.item;

import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoCItemEgg extends MoCItem {

	public MoCItemEgg(String name) {
		super(name);
		this.maxStackSize = 16;
		setHasSubtypes(true);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		stack.shrink(1);
		if (!world.isRemote && player.onGround) {
			int i = stack.getItemDamage();
			if (i == 30) {
				i = 31; //for ostrich eggs. placed eggs become stolen eggs.
			}
			MoCEntityEgg entityegg = new MoCEntityEgg(world, i);
			entityegg.setPosition(player.posX, player.posY, player.posZ);
			player.world.spawnEntity(entityegg);
			entityegg.motionY += world.rand.nextFloat() * 0.05F;
			entityegg.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
			entityegg.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) {
			return;
		}

		for(EnumEgg egg : EnumEgg.values()) {
			if(egg != EnumEgg.None) {
				items.add(new ItemStack(this, 1, egg.eggNum));
			}
		}
				
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return (new StringBuilder()).append(getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
	}
}

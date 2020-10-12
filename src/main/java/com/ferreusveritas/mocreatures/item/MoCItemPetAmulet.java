package com.ferreusveritas.mocreatures.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.ferreusveritas.mocreatures.util.MoCLog;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MoCItemPetAmulet extends MoCItem {
	
	private String name;
	private float health;
	private int edad;
	private int creatureType;
	private String spawnClass;
	private String ownerName;
	private UUID ownerUniqueId;
	private int amuletType;
	private boolean adult;
	private int PetId;
	
	public MoCItemPetAmulet(String name) {
		super(name);
		this.maxStackSize = 1;
		setHasSubtypes(true);
	}
	
	public MoCItemPetAmulet(String name, int type) {
		this(name);
		this.amuletType = type;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		final ItemStack stack = player.getHeldItem(hand);
		double dist = 1D;
		double newPosY = player.posY;
		double newPosX = player.posX - (dist * Math.cos((MoCTools.realAngle(player.rotationYaw - 90F)) / 57.29578F));
		double newPosZ = player.posZ - (dist * Math.sin((MoCTools.realAngle(player.rotationYaw - 90F)) / 57.29578F));
		
		ItemStack emptyAmulet = new ItemStack(MoCItems.fishnet, 1, 0);
		if (this.amuletType == 1) {
			emptyAmulet = new ItemStack(MoCItems.petamulet, 1, 0);
		}
		
		if (!world.isRemote) {
			initAndReadNBT(stack);
			if (this.spawnClass.isEmpty()) {
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
			try {
				this.spawnClass = this.spawnClass.replace(MoCConstants.MOD_PREFIX, "").toLowerCase();
				if (this.spawnClass.equalsIgnoreCase("MoCHorse")) {
					this.spawnClass = "WildHorse";
				} else if (this.spawnClass.equalsIgnoreCase("PolarBear")) {
					this.spawnClass = "WildPolarBear";
				}
				
				if (this.spawnClass.equalsIgnoreCase("Ray")) {
					switch (this.creatureType) {
						case 1:
							this.spawnClass = "MantaRay";
							break;
						case 2:
							this.spawnClass = "StingRay";
							break;
					}
				}
				EntityLiving tempLiving = (EntityLiving) EntityList.createEntityByIDFromName(new ResourceLocation(MoCConstants.MOD_PREFIX + this.spawnClass.toLowerCase()), world);
				if (tempLiving != null && tempLiving instanceof IMoCEntity) {
					IMoCTameable storedCreature = (IMoCTameable) tempLiving;
					((EntityLiving) storedCreature).setPosition(newPosX, newPosY, newPosZ);
					storedCreature.setType(this.creatureType);
					storedCreature.setTamed(true);
					this.ownerName = player.getName();
					((EntityLiving) storedCreature).setHealth(this.health);
					//storedCreature.setEdad(this.edad);
					storedCreature.setAdult(this.adult);
					
					if (this.ownerUniqueId == null) {
						this.ownerUniqueId = player.getUniqueID();
					}
					
					/*if (player.getEntityWorld().spawnEntity((EntityLiving) storedCreature)) {
						MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAppear(((EntityLiving) storedCreature).getEntityId()),
								new TargetPoint(player.getEntityWorld().provider.getDimensionType().getId(), player.posX, player.posY,
										player.posZ, 64));
						if (this.ownerUniqueId == null || this.name.isEmpty()) {
							MoCTools.tameWithName(player, storedCreature);
						}
						
						player.setHeldItem(hand, emptyAmulet);
						MoCPetData petData = MoCreatures.instance.mapData.getPetData(storedCreature.getOwnerId());
						if (petData != null) {
							petData.setInAmulet(storedCreature.getOwnerPetId(), false);
						}
					}*/
				}
			} catch (Exception ex) {
				if (MoCreatures.proxy.debug) {
					MoCLog.logger.warn("Error spawning creature from fishnet/amulet " + ex);
				}
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
	}
	
	public void readFromNBT(NBTTagCompound nbt) {
		this.PetId = nbt.getInteger("PetId");
		this.creatureType = nbt.getInteger("CreatureType");
		this.health = nbt.getFloat("Health");
		this.edad = nbt.getInteger("Edad");
		this.name = nbt.getString("Name");
		this.spawnClass = nbt.getString("SpawnClass");
		this.adult = nbt.getBoolean("Adult");
		this.ownerName = nbt.getString("OwnerName");
		if (nbt.hasUniqueId("OwnerUUID")) {
			this.ownerUniqueId = nbt.getUniqueId("OwnerUUID");
		}
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("PetID", this.PetId);
		nbt.setInteger("CreatureType", this.creatureType);
		nbt.setFloat("Health", this.health);
		nbt.setInteger("Edad", this.edad);
		nbt.setString("Name", this.name);
		nbt.setString("SpawnClass", this.spawnClass);
		nbt.setBoolean("Adult", this.adult);
		nbt.setString("OwnerName", this.ownerName);
		if (this.ownerUniqueId != null) {
			nbt.setUniqueId("OwnerUUID", ownerUniqueId);
		}
	}
	
	@SideOnly(Side.CLIENT)
	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		initAndReadNBT(stack);
		if (this.spawnClass != "") {
			tooltip.add(TextFormatting.AQUA + this.spawnClass);
		}
		if (this.name != "") {
			tooltip.add(TextFormatting.BLUE + this.name);
		}
		if (this.ownerName != "") {
			tooltip.add(TextFormatting.DARK_BLUE + "Owned by " + this.ownerName);
		}
	}
	
	private void initAndReadNBT(ItemStack itemstack) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbtcompound = itemstack.getTagCompound();
		readFromNBT(nbtcompound);
	}
}

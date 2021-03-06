package com.ferreusveritas.mocreatures.item;

import java.util.List;

import com.ferreusveritas.mocreatures.entity.passive.EntityBigCat;
import com.ferreusveritas.mocreatures.entity.passive.EntityElephant;
import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich;
import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntityWyvern;
import com.ferreusveritas.mocreatures.init.MoCSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MoCItemWhip extends MoCItem {
	
	public MoCItemWhip(String name) {
		super(name);
		this.maxStackSize = 1;
		setMaxDamage(24);
	}
	
	@Override
	public boolean isFull3D() {
		return true;
	}
	
	public ItemStack onItemRightClick2(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		return itemstack;
	}
	
	@Override
	public EnumActionResult
	onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		int i1 = 0;
		Block block = worldIn.getBlockState(pos).getBlock();
		Block block1 = worldIn.getBlockState(pos.up()).getBlock();
		if (side != EnumFacing.DOWN && (block1 == Blocks.AIR) && (block != Blocks.AIR) && (block != Blocks.STANDING_SIGN)) {
			whipFX(worldIn, pos);
			worldIn.playSound(player, pos, MoCSoundEvents.ENTITY_GENERIC_WHIP, SoundCategory.PLAYERS, 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));
			stack.damageItem(1, player);
			List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(12D, 12D, 12D));
			for (int l1 = 0; l1 < list.size(); l1++) {
				Entity entity = list.get(l1);
				
				/*if (entity instanceof MoCEntityAnimal) {
					MoCEntityAnimal animal = (MoCEntityAnimal) entity;
					if (MoCreatures.proxy.enableOwnership && animal.getOwnerId() != null
							&& !player.getName().equals(animal.getOwnerId()) && !MoCTools.isThisPlayerAnOP(player)) {
						continue;
					}
				}*/
				
				if (entity instanceof EntityBigCat) {
					EntityBigCat entitybigcat = (EntityBigCat) entity;
					if (entitybigcat.isTamed()) {
						entitybigcat.setSitting(!entitybigcat.isSitting());
						i1++;
					} else if ((worldIn.getDifficulty().getDifficultyId() > 0) && entitybigcat.isAdult()) {
						entitybigcat.setAttackTarget(player);
					}
				}
				
				if ((entity instanceof EntityWyvern)) {
					EntityWyvern entitywyvern = (EntityWyvern) entity;
					if (entitywyvern.getIsTamed() && entitywyvern.getRidingEntity() == null && !entitywyvern.isOnAir()) {
						entitywyvern.setSitting(!entitywyvern.getIsSitting());
					}
				}
				
				if ((entity instanceof EntityPetScorpion)) {
					EntityPetScorpion petscorpion = (EntityPetScorpion) entity;
					if (petscorpion.getIsTamed() && petscorpion.getRidingEntity() == null) {
						petscorpion.setSitting(!petscorpion.getIsSitting());
					}
				}
				
				if (entity instanceof EntityOstrich) {
					EntityOstrich entityostrich = (EntityOstrich) entity;
					
					//makes ridden ostrich sprint
					if (entityostrich.isBeingRidden() && entityostrich.sprintCounter == 0) {
						entityostrich.sprintCounter = 1;
					}
					
				}
				if (entity instanceof EntityElephant) {
					EntityElephant entityelephant = (EntityElephant) entity;
					
					//makes elephants charge
					if (entityelephant.isBeingRidden() && entityelephant.sprintCounter == 0) {
						entityelephant.sprintCounter = 1;
					}
				}
				
			}
			
			if (i1 > 6) {
				//entityplayer.triggerAchievement(MoCreatures.Indiana);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}
	
	public void whipFX(World world, BlockPos pos) {
		double d = pos.getX() + 0.5F;
		double d1 = pos.getY() + 1.0F;
		double d2 = pos.getZ() + 0.5F;
		double d3 = 0.2199999988079071D;
		double d4 = 0.27000001072883606D;
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, d - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, d + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, d, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, d, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d1, d2, 0.0D, 0.0D, 0.0D);
		world.spawnParticle(EnumParticleTypes.FLAME, d, d1, d2, 0.0D, 0.0D, 0.0D);
	}
}

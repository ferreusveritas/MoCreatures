package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class EntityLeopard extends EntityBigCat {
	
	public enum LeopardType {
		None,
		Common,
		Snow
	}
	
	LeopardType type = LeopardType.None;
	
	public EntityLeopard(World world) {
		super(world);
	}
	
	@Override
	public void setupAttributes() {
		if (getLeopardType() == LeopardType.None) {
			setLeopardType(checkSpawningBiome());
		}
		
		super.setupAttributes();
	}
	
	public void setLeopardType(LeopardType lType) {
		type = lType;
	}
	
	public LeopardType getLeopardType() {
		return type;
	}
	
	public LeopardType checkSpawningBiome() {
		int x = MathHelper.floor(posX);
		int y = MathHelper.floor(getEntityBoundingBox().minY);
		int z = MathHelper.floor(posZ);
		BlockPos pos = new BlockPos(x, y, z);
		
		Biome currentbiome = MoCTools.Biomekind(world, pos);
		try {
			if (BiomeDictionary.hasType(currentbiome, Type.SNOWY)) {
				return LeopardType.Snow;
			}
		} catch (Exception e) { }
		return LeopardType.Common;
	}
	
	public ResourceLocation getTexture() {
		switch (getLeopardType()) {
			case Snow:
				return MoCreatures.proxy.getTexture("bcsnowleopard.png");
			case Common:
			default:
				return MoCreatures.proxy.getTexture("bcleopard.png");
		}
	}
	
	@Override
	public float calculateMaxHealth() {
		return 25.0f;
	}
	
	@Override
	public double calculateAttackDmg() {
		return 5.0;
	}
	
	@Override
	public double getFollowRange() {
		return 6.0;
	}
	
	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		if (!isAdult() && isAdult() || (entity instanceof EntityLeopard)) {
			return false;
		}
		return entity.height < 1.3f && entity.width < 1.3f;
	}
	
	@Override
	public float getMoveSpeed() {
		return 1.6f;
	}
	
}

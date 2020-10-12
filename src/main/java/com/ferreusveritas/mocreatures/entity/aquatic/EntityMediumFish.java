package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIMoveHelperFish;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMediumFish extends MoCEntityAquatic {

	private static Map<Integer, MediumFishType> map = new HashMap<>();
	
	public static enum MediumFishType {
		None(0, EnumEgg.None),
		Salmon(1, EnumEgg.Salmon),
		Cod(2, EnumEgg.Cod),
		Bass(3, EnumEgg.Bass);
		
		public final int type;
		public final EnumEgg egg;
		
		private MediumFishType(int type, EnumEgg egg) {
			this.type = type;
			this.egg = egg;
			map.put(type, this);
		}
	}
	
	public static MediumFishType getFish(int type) {
		return map.getOrDefault(type, MediumFishType.None);
	}
	
	public EntityMediumFish(World world) {
		super(world);
		setSize(0.6F, 0.3F);
		//setEdad(30 + this.rand.nextInt(70));
		moveHelper = new EntityAIMoveHelperFish(this);
	}

	protected void setType(MediumFishType type) {
		setType(type.type);
	}
	
	public static EntityMediumFish createEntity(World world, EnumEgg egg) {
		switch(egg) {
			case Salmon: return new EntitySalmon(world);
			case Cod: return new EntityCod(world);
			case Bass: return new EntityBass(world);
			default: return new EntitySalmon(world); 
		}
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(3, new EntityAIFleeFromEntityMoC(this, new Predicate<Entity>() {

			public boolean apply(Entity entity) {
				return (entity.height > 0.6F && entity.width > 0.3F);
			}
		}, 2.0F, 0.6D, 1.5D));
		
		tasks.addTask(5, new EntityAIWander(this, 1.0, 50));

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}

	@Override
	public void selectType() {
		if (getType() == 0) {
			setType(this.rand.nextInt(MediumFishType.values().length - 1) + 1);
		}
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		int i = this.rand.nextInt(100);
		if (i < 70) {
			entityDropItem(new ItemStack(Items.FISH, 1, 0), 0.0F);
		} else {
			int j = this.rand.nextInt(2);
			for (int k = 0; k < j; k++) {
				entityDropItem(new ItemStack(MoCItems.mocegg, 1, getEggNumber()), 0.0F);
			}
		}
	}

	protected int getEggNumber() {
		return 70;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.world.isRemote) {
			if (getIsTamed() && this.rand.nextInt(100) == 0 && getHealth() < getMaxHealth()) {
				this.setHealth(getMaxHealth());
			}
		}
		if (!this.isInsideOfMaterial(Material.WATER)) {
			this.prevRenderYawOffset = this.renderYawOffset = this.rotationYaw = this.prevRotationYaw;
			this.rotationPitch = this.prevRotationPitch;
		}
	}

	@Override
	public float getSizeFactor() {
		return 1.0f;
		//return getEdad() * 0.01F;
	}

	@Override
	public float getAdjustedYOffset() {
		if (!this.isInsideOfMaterial(Material.WATER)) {
			return 1F;
		}
		return 0.5F;
	}

	@Override
	protected boolean isFisheable() {
		return !getIsTamed();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float yawRotationOffset() {
		if (!this.isInsideOfMaterial(Material.WATER)) {
			return 90F;
		}
		return 90F + super.yawRotationOffset();
	}

	@Override
	public float rollRotationOffset() {
		if (!isInWater() && this.onGround) {
			return -90F;
		}
		return 0F;
	}

	@Override
	public float getAdjustedZOffset() {
		if (!isInWater()) {
			return 0.2F;
		}
		return 0F;
	}

	@Override
	public float getAdjustedXOffset() {
		return 0F;
	}

	@Override
	protected boolean canBeTrappedInNet() {
		return true;
	}

	@Override
	protected boolean usesNewAI() {
		return true;
	}

	@Override
	public float getAIMoveSpeed() {
		return 0.15F;
	}

	@Override
	public boolean isMovementCeased() {
		return !isInWater();
	}

	@Override
	protected double minDivingDepth() {
		return 0.5D;
	}

	@Override
	protected double maxDivingDepth() {
		return 4.0D;
	}

	@Override
	public int getMaxEdad() {
		return 120;
	}

	@Override
	public boolean isNotScared() {
		return getIsTamed();
	}
}

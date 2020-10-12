package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIMoveHelperFish;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.init.MoCItems;
import com.google.common.base.Predicate;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntitySmallFish extends MoCEntityAquatic {

	private static Map<Integer, SmallFishType> map = new HashMap<>();
	
	public static enum SmallFishType {
		None(0, EnumEgg.None),
		Anchovy(1, EnumEgg.Anchovy),
		AngelFish(2, EnumEgg.AngelFish),
		Angler(3, EnumEgg.Angler),
		ClownFish(4, EnumEgg.ClownFish),
		GoldFish(5, EnumEgg.GoldFish),
		HippoTang(6, EnumEgg.HippoTang),
		Manderin(7, EnumEgg.Manderin);
		
		public final int type;
		public final EnumEgg egg;
		
		private SmallFishType(int type, EnumEgg egg) {
			this.type = type;
			this.egg = egg;
			map.put(type, this);
		}
	}
	
	public static SmallFishType getFish(int type) {
		return map.getOrDefault(type, SmallFishType.None);
	}
	
	////////////////////////////////////////////////////////////////
	
	public EntitySmallFish(World world) {
		super(world);
		setSize(0.3F, 0.3F);
		//setEdad(70 + rand.nextInt(30));
		moveHelper = new EntityAIMoveHelperFish(this);
	}
	
	protected void setType(SmallFishType type) {
		setType(type.type);
	}
	
	protected SmallFishType getFish() {
		return getFish(getType());
	}
	
	public static EntitySmallFish createEntity(World world, EnumEgg egg) {
		
		switch(egg) {
			case Anchovy: return new EntityAnchovy(world);
			case AngelFish: return new EntityAngelFish(world);
			case Angler: return new EntityAngler(world);
			case ClownFish: return new EntityClownFish(world);
			case GoldFish: return new EntityGoldFish(world);
			case HippoTang: return new EntityHippoTang(world);
			case Manderin: return new EntityManderin(world);
			default: return new EntityClownFish(world);
		}
		
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAIPanicMoC(this, 1.3D));
		tasks.addTask(2, new EntityAIFleeFromEntityMoC(this, new Predicate<Entity>() {

			public boolean apply(Entity entity) {
				return (entity.height > 0.3F || entity.width > 0.3F);
			}
		}, 2.0F, 0.6D, 1.5D));
		//this.tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0D, 80));

		tasks.addTask(5, new EntityAIWander(this, 0.5D, 20));
	}

	@Override
	public float getMoveSpeed() {
		return 0.4F;
	}
	
	@Override
	public float getBlockPathWeight(BlockPos pos) {
		
		IBlockState state = world.getBlockState(pos);
		
		if(state.getMaterial() == Material.WATER) {
			return 2.0f;
		}

		return super.getBlockPathWeight(pos);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
	}

	@Override
	public void selectType() {
		if (getFish() == SmallFishType.None) {
			setType(rand.nextInt(SmallFishType.values().length - 1) + 1);
		}

	}

	@Override
	protected boolean canBeTrappedInNet() {
		return true;
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		int i = rand.nextInt(100);
		if (i < 70) {
			entityDropItem(new ItemStack(Items.FISH, 1, 0), 0.0F);
		} else {
			int j = rand.nextInt(2);
			for (int k = 0; k < j; k++) {
				entityDropItem(new ItemStack(MoCItems.mocegg, 1, getEgg().eggNum), 0.0F);
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!world.isRemote) {
			if (getIsTamed() && rand.nextInt(100) == 0 && getHealth() < getMaxHealth()) {
				setHealth(getMaxHealth());
			}
		}
		if (!isInWater()) {
			prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
			rotationPitch = prevRotationPitch;
		}
	}

	@Override
	public float getSizeFactor() {
		return 1.0f;
		//return getEdad() * 0.01F;
	}

	@Override
	public float getAdjustedYOffset() {
		return isInWater() ? 0.3f : 0.5f;
	}

	@Override
	protected boolean isFisheable() {
		return !getIsTamed();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float yawRotationOffset() {
		if (!isInWater()) {
			return 90F;
		}
		return 90F + super.yawRotationOffset();
	}

	@Override
	public float rollRotationOffset() {
		return isInWater() ? 0.0f : -90.0f;
	}

	@Override
	public float getAdjustedXOffset() {
		return 0F;
	}

	@Override
	protected boolean usesNewAI() {
		return true;
	}

	@Override
	public float getAIMoveSpeed() {
		return 0.10F;
	}

	@Override
	public boolean isMovementCeased() {
		return !isInWater();
	}

	@Override
	protected double minDivingDepth() {
		return 0.2D;
	}

	@Override
	protected double maxDivingDepth() {
		return 2.0D;
	}

	@Override
	public int getMaxEdad() {
		return 120;
	}

	@Override
	public boolean isNotScared() {
		return getIsTamed();
	}

	@Override
	public float getAdjustedZOffset() {
		return isInWater() ? 0.0f : 0.1f;
	}

	protected abstract EnumEgg getEgg();
	
}

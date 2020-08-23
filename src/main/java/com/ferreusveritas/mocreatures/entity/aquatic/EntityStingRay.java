package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityStingRay extends EntityRay {

	private int poisoncounter;
	private int tailCounter;
	
	public EntityStingRay(World world) {
		super(world);
		setSize(1.2F, 0.5F);
		setEdad(50 + (rand.nextInt(40)));
		setTexture("stingray");
	}

	public void setStingRayType(StingRayType stingRayType) {
		setType(stingRayType.typeNum);
	}
	
	private StingRayType getStingRayType(int typeNum) {
		return speciesMap.getOrDefault(typeNum, StingRayType.Common);
	}
	
	private StingRayType getStingRayType() {
		return getStingRayType(getType());
	}
	
	protected boolean isBiomeDuvotican() {
		return world.getBiome(new BlockPos(posX, posY, posZ)) == com.ferreusveritas.stargarden.features.Worlds.duvoticaBiome;
	}
	
	public static Map<Integer, StingRayType> speciesMap = new HashMap<>();  
	
	enum StingRayType {
		None(0, 1.0f),
		Common(1, 1.0f),
		Duvotican(2, 2.25f);
		
		public final int typeNum;
		public final float sizeScale;
				
		private StingRayType(int typeNum, float sizeScale) {
			this.typeNum = typeNum;
			speciesMap.put(typeNum, this);
			this.sizeScale = sizeScale;
		}
		
	}
	
	@Override
	public void selectType() {
		setStingRayType(isBiomeDuvotican() ? StingRayType.Duvotican : StingRayType.Common);
	}
	
	@Override
	public float getSizeFactor() {
		float f = super.getSizeFactor();
		return f *= getStingRayType().sizeScale;
	}


	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10D);
	}

	@Override
	public boolean isPoisoning() {
		return this.tailCounter != 0;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			if (!getIsTamed() && ++this.poisoncounter > 250 && (this.world.getDifficulty().getDifficultyId() > 0) && this.rand.nextInt(30) == 0) {
				if (MoCTools.findNearPlayerAndPoison(this, true)) {
					MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1),
							new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
					this.poisoncounter = 0;
				}
			}
		} else //client stuff
		{
			if (this.tailCounter > 0 && ++this.tailCounter > 50) {
				this.tailCounter = 0;
			}
		}
	}

	@Override
	public void performAnimation(int animationType) {
		if (animationType == 1) //attacking with tail
		{
			this.tailCounter = 1;
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i)) {
			if ((this.world.getDifficulty().getDifficultyId() == 0)) {
				return true;
			}
			Entity entity = damagesource.getTrueSource();
			if (entity instanceof EntityLivingBase) {
				if (entity != this) {
					setAttackTarget((EntityLivingBase) entity);
				}
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	protected Item getDropItem() {
		System.out.println("Type: " + getStingRayType());
		return getStingRayType() == StingRayType.Duvotican ? Items.FISH : super.getDropItem();
	}
	
}

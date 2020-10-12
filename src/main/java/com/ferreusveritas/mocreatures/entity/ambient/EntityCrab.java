package com.ferreusveritas.mocreatures.entity.ambient;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityAmbient;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIPanicMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCrab extends MoCEntityAmbient {
	
	public EntityCrab(World world) {
		super(world);
		setSize(0.3F, 0.3F);
		//setEdad(50 + this.rand.nextInt(50));
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIPanicMoC(this, 1.0D));
		this.tasks.addTask(1, new EntityAIFleeFromEntityMoC(this, new Predicate<Entity>() {
			
			public boolean apply(Entity entity) {
				return !(entity instanceof EntityCrab) && (entity.height > 0.3F || entity.width > 0.3F);
			}
		}, 6.0F, 0.6D, 1D));
		this.tasks.addTask(6, new EntityAIWanderMoC2(this, 1.0D));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}
	
	@Override
	public void selectType() {
		if (getType() == 0) {
			setType(this.rand.nextInt(5) + 1);
		}
		
	}
	
	@Override
	public ResourceLocation getTexture() {
		switch (getType()) {
			case 1:
				return MoCreatures.proxy.getTexture("craba.png");
			case 2:
				return MoCreatures.proxy.getTexture("crabb.png");
			case 3:
				return MoCreatures.proxy.getTexture("crabc.png");
			case 4:
				return MoCreatures.proxy.getTexture("crabd.png");
			case 5:
				return MoCreatures.proxy.getTexture("crabe.png");
				
			default:
				return MoCreatures.proxy.getTexture("craba.png");
		}
	}
	
	@Override
	public void fall(float f, float f1) {
	}
	
	@Override
	public boolean isOnLadder() {
		return this.collidedHorizontally;
	}
	
	public boolean climbing() {
		return !this.onGround && isOnLadder();
	}
	
	@Override
	public void jump() {
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public float getSizeFactor() {
		return 1.0f;
		//return 0.7F * getEdad() * 0.01F;
	}
	
	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean isFleeing() {
		return MoCTools.getMyMovementSpeed(this) > 0.09F;
	}
	
	/**
	 * Get this Entity's EnumCreatureAttribute
	 */
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}
	
	@Override
	protected boolean canBeTrappedInNet() {
		return true;
	}
	
	@Override
	public boolean isNotScared() {
		return this.getIsTamed();
	}
	
}

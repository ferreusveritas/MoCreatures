package com.ferreusveritas.mocreatures.entity.aquatic;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityJellyFish extends MoCEntityAquatic {
	
	private int poisoncounter;
	
	public EntityJellyFish(World world) {
		super(world);
		setSize(0.3F, 0.5F);
		//setEdad(50 + (rand.nextInt(50)));
	}
	
	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new AIMoveRandom(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
	}
	
	@Override
	public void selectType() {
		if (getType() == 0) {
			setType(rand.nextInt(5) + 1);
		}
	}
	
	@Override
	public float getAIMoveSpeed() {
		return 0.02F;
	}
	
	@Override
	public ResourceLocation getTexture() {
		switch (getType()) {
			case 1: return MoCreatures.proxy.getTexture("jellyfisha.png");
			case 2: return MoCreatures.proxy.getTexture("jellyfishb.png");
			case 3: return MoCreatures.proxy.getTexture("jellyfishc.png");
			case 4: return MoCreatures.proxy.getTexture("jellyfishd.png");
			case 5: return MoCreatures.proxy.getTexture("jellyfishe.png");
			case 6: return MoCreatures.proxy.getTexture("jellyfishf.png");
			case 7: return MoCreatures.proxy.getTexture("jellyfishg.png");
			case 8: return MoCreatures.proxy.getTexture("jellyfishh.png");
			case 9: return MoCreatures.proxy.getTexture("jellyfishi.png");
			case 10: return MoCreatures.proxy.getTexture("jellyfishj.png");
			case 11: return MoCreatures.proxy.getTexture("jellyfishk.png");
			case 12: return MoCreatures.proxy.getTexture("jellyfishl.png");
			default: return MoCreatures.proxy.getTexture("jellyfisha.png");
		}
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!world.isRemote) {
			if (!getIsTamed() && ++poisoncounter > 250 && shouldAttackPlayers() && rand.nextInt(30) == 0) {
				if (MoCTools.findNearPlayerAndPoison(this, true)) {
					poisoncounter = 0;
				}
			}
		}
	}
	
	@Override
	protected Item getDropItem() {
		boolean flag = rand.nextInt(2) == 0;
		if (flag) {
			return Items.SLIME_BALL;
		}
		return null;
	}
	
	@Override
	public float pitchRotationOffset() {
		if (!isInWater()) {
			return 90F;
		}
		return 0F;
	}
	
	@Override
	public float getSizeFactor() {
		float myMoveSpeed = MoCTools.getMyMovementSpeed(this);
		float pulseSpeed = 0.08F;
		if (myMoveSpeed > 0F)
			pulseSpeed = 0.5F;
		float pulseSize = MathHelper.cos(ticksExisted * pulseSpeed) * 0.2F;
		return 100 * 0.01F + (pulseSize / 5);
		//return getEdad() * 0.01F + (pulseSize / 5);
	}
	
	@Override
	protected boolean canBeTrappedInNet() {
		return true;
	}
	
	@Override
	public int getMaxEdad() {
		return 100;
	}
	
	static class AIMoveRandom extends EntityAIBase {
		
		private final EntityJellyFish jellyFish;
		
		public AIMoveRandom(EntityJellyFish jellyFish) {
			this.jellyFish = jellyFish;
		}
		
		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return true;
		}
		
		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void updateTask() {
			int idle = jellyFish.getIdleTime();
			
			if (idle > 300) {
				jellyFish.motionX *= 0.5f;
				jellyFish.motionY *= 0.5f;
				jellyFish.motionZ *= 0.5f;
			} else if (jellyFish.getRNG().nextInt(30) == 0 || !jellyFish.inWater) {
				float speed = jellyFish.getAIMoveSpeed();
				float rndAngle = jellyFish.getRNG().nextFloat() * ((float)Math.PI * 2F);
				float x = MathHelper.cos(rndAngle) * 0.2F;
				float y = -0.1F + jellyFish.getRNG().nextFloat() * 0.2F;
				float z = MathHelper.sin(rndAngle) * 0.2F;
				jellyFish.motionX += x * speed * 10.0f;
				jellyFish.motionY += y * speed * 10.0f;
				jellyFish.motionZ += z * speed * 10.0f;
			}
		}
	}
	
}

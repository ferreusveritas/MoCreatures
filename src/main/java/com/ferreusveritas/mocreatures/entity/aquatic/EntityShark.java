package com.ferreusveritas.mocreatures.entity.aquatic;

import com.ferreusveritas.mocreatures.entity.MoCEntityAquatic;
import com.ferreusveritas.mocreatures.entity.ai.EntityAINearestAttackableTargetMoC;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIWanderMoC2;
import com.ferreusveritas.mocreatures.init.MoCItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityShark extends MoCEntityAquatic {

	public EntityShark(World world) {
		super(world);
		setSize(1.7F, 0.8F);
		//setEdad(60 + this.rand.nextInt(100));
		setTexture("shark");
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0D, 30));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTargetMoC(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float i) {
		if (super.attackEntityFrom(damagesource, i) && (this.world.getDifficulty().getDifficultyId() > 0)) {
			Entity entity = damagesource.getTrueSource();
			if (this.isRidingOrBeingRiddenBy(entity)) {
				return true;
			}
			if (entity != this && entity instanceof EntityLivingBase) {
				setAttackTarget((EntityLivingBase) entity);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void dropFewItems(boolean flag, int x) {
		int i = this.rand.nextInt(100);
		if (i < 90) {
			int j = this.rand.nextInt(3) + 1;
			for (int l = 0; l < j; l++) {
				entityDropItem(new ItemStack(MoCItems.sharkteeth, 1, 0), 0.0F);
			}
		} else if ((this.world.getDifficulty().getDifficultyId() > 0) /*&& (getEdad() > 150)*/) {
			int k = this.rand.nextInt(3);
			for (int i1 = 0; i1 < k; i1++) {
				entityDropItem(new ItemStack(MoCItems.mocegg, 1, 11), 0.0F);
			}
		}
	}

	/*protected Entity findPlayerToAttack() {
        if ((this.world.getDifficulty().getDifficultyId() > 0) && (getEdad() >= 100)) {
            EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 16D);
            if ((entityplayer != null) && entityplayer.isInWater() && !getIsTamed()) {
                return entityplayer;
            }
        }
        return null;
    }*/

	/* public EntityLivingBase FindTarget(Entity entity, double d) {
         double d1 = -1D;
         EntityLivingBase entityliving = null;
         List list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(d, d, d));
         for (int i = 0; i < list.size(); i++) {
             Entity entity1 = (Entity) list.get(i);
             if (!(entity1 instanceof EntityLivingBase) || (entity1 instanceof MoCEntityAquatic) || (entity1 instanceof MoCEntityEgg)
                     || (entity1 instanceof EntityPlayer) || ((entity1 instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
                     || ((entity1 instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses))
                     || ((entity1 instanceof MoCEntityDolphin) && (getIsTamed() || !(MoCreatures.proxy.attackDolphins)))) {
                 continue;
             }
             double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
             if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)) && ((EntityLivingBase) entity1).canEntityBeSeen(entity)) {
                 d1 = d2;
                 entityliving = (EntityLivingBase) entity1;
             }
         }
         return entityliving;
     }*/

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			if (!getIsAdult() && (this.rand.nextInt(50) == 0)) {
				/*setEdad(getEdad() + 1);
				if (getEdad() >= 200) {
					setAdult(true);
				}*/
			}
		}
	}

	@Override
	public void setDead() {
		if (!this.world.isRemote && getIsTamed() && (getHealth() > 0)) {
			return;
		} else {
			super.setDead();
			return;
		}
	}

	public boolean isMyHealFood(Item item1) {
		return false;
	}

	@Override
	protected boolean usesNewAI() {
		return true;
	}

	@Override
	public float getAIMoveSpeed() {
		return 0.12F;
	}

	@Override
	public boolean isMovementCeased() {
		return !isInWater();
	}

	@Override
	protected double minDivingDepth() {
		return 1D;
	}

	@Override
	protected double maxDivingDepth() {
		return 6.0D;
	}

	@Override
	public int getMaxEdad() {
		return 200;
	}

	@Override
	public boolean isNotScared() {
		return true;
	}

}

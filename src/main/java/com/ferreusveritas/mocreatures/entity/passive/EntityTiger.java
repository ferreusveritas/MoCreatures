package com.ferreusveritas.mocreatures.entity.passive;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityTiger extends EntityBigCat {

	public EntityTiger(World world) {
		super(world);
	}

	@Override
	public void selectType() {

		if (getType() == 0) {
			if (this.rand.nextInt(20) == 0) {
				setType(2);
			} else {
				setType(1);
			}
		}
		super.selectType();
	}

	@Override
	public ResourceLocation getTexture() {
		switch (getType()) {
			case 1:
				return MoCreatures.proxy.getTexture("bctiger.png");
			case 2:
				return MoCreatures.proxy.getTexture("bcwhitetiger.png");
			case 3:
				return MoCreatures.proxy.getTexture("bcwhitetiger.png"); //winged tiger
				/*case 4:
                return MoCreatures.proxy.getTexture("bcleoger.png"); // Tiger x Leopard
				 */default:
					 return MoCreatures.proxy.getTexture("bctiger.png");
		}
	}

	@Override
	public boolean isFlyer() {
		return this.getType() == 3;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		final Boolean tameResult = this.processTameInteract(player, hand);
		if (tameResult != null) {
			return tameResult;
		}

		if (this.getIsRideable() && this.getIsAdult() && (!this.getIsChested() || !player.isSneaking()) && !this.isBeingRidden()) {
			if (!this.world.isRemote && player.startRiding(this)) {
				player.rotationYaw = this.rotationYaw;
				player.rotationPitch = this.rotationPitch;
				setSitting(false);
			}

			return true;
		}

		return super.processInteract(player, hand);
	}

	@Override
	public String getOffspringClazz(IMoCTameable mate) {
		return "Tiger";
	}

	@Override
	public int getOffspringTypeInt(IMoCTameable mate) {
		return this.getType();
	}

	@Override
	public boolean compatibleMate(Entity mate) {
		return (mate instanceof EntityTiger && ((EntityTiger) mate).getType() < 3);
	}

	@Override
	public boolean readytoBreed() {
		return this.getType() < 3 && super.readytoBreed();
	}

	@Override
	public float calculateMaxHealth() {
		if (this.getType() == 2) {
			return 40F;
		}
		return 35F;
	}

	@Override
	public double calculateAttackDmg() {
		if (this.getType() == 2) {
			return 8D;
		}
		return 7D;
	}

	@Override
	public double getAttackRange() {
		return 8D;
	}

	@Override
	public int getMaxEdad() {
		if (getType() > 2) {
			return 130;
		}
		return 120;
	}

	@Override
	public boolean canAttackTarget(EntityLivingBase entity) {
		if (!this.getIsAdult() && (this.getEdad() < this.getMaxEdad() * 0.8)) {
			return false;
		}
		if (entity instanceof EntityTiger) {
			return false;
		}
		return entity.height < 2F && entity.width < 2F;
	}

	@Override
	public float getMoveSpeed() {
		return 1.5F;
	}
}

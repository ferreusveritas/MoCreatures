package com.ferreusveritas.mocreatures.entity.passive;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import com.ferreusveritas.mocreatures.entity.aquatic.EnumEgg;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityManticorePet extends EntityBigCat {

	private static Map<Integer, ManticoreType> map = new HashMap<>();
	private static Map<EnumEgg, ManticoreType> eggmap = new HashMap<>();

	public static enum ManticoreType {
		None(0, EnumEgg.None),
		Common(1, EnumEgg.DirtScorpion),
		Dark(2, EnumEgg.CaveScorpion),
		Blue(3, EnumEgg.NetherScorpion),
		Green(4, EnumEgg.FrostScorpion);

		public final int type;
		public final EnumEgg egg;

		private ManticoreType(int type, EnumEgg egg) {
			this.type = type;
			this.egg = egg;
			map.put(type, this);
			eggmap.put(egg, this);
		}
	}

	public void setType(ManticoreType type) {
		setType(type.type);
	}

	public static ManticoreType getManticore(int type) {
		return map.getOrDefault(type, ManticoreType.None);
	}

	public ManticoreType getManticore() {
		return getManticore(getType());
	}

	public static ManticoreType getManticore(EnumEgg egg) {
		return eggmap.getOrDefault(egg, ManticoreType.None);
	}

	public EntityManticorePet(World world) {
		super(world);
		this.chestName = "ManticoreChest";
	}

	@Override
	public void selectType() {
		if (getManticore() == ManticoreType.None) {
			int sel = rand.nextInt(ManticoreType.values().length - 1) + 1;
			setType(ManticoreType.values()[sel]);
		}
		super.selectType();
	}

	@Override
	public ResourceLocation getTexture() {
		switch (getManticore()) {
			case Common: return MoCreatures.proxy.getTexture("bcmanticore.png");
			case Dark: return MoCreatures.proxy.getTexture("bcmanticoredark.png");
			case Blue: return MoCreatures.proxy.getTexture("bcmanticoreblue.png");
			case Green: return MoCreatures.proxy.getTexture("bcmanticoregreen.png");
			default: return MoCreatures.proxy.getTexture("bcmanticore.png");
		}
	}

	@Override
	public boolean hasMane() {
		return true;
	}

	@Override
	public boolean isFlyer() {
		return true;
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
		return "";
	}

	@Override
	public int getOffspringTypeInt(IMoCTameable mate) {
		return 0;
	}

	@Override
	public boolean compatibleMate(Entity mate) {
		return false;
	}

	@Override
	public boolean readytoBreed() {
		return false;
	}

	@Override
	public float calculateMaxHealth() {
		return 40f;
	}

	@Override
	public double calculateAttackDmg() {
		return 7.0;
	}

	@Override
	public double getAttackRange() {
		return 8.0;
	}

	@Override
	public int getMaxEdad() {
		return 130;
	}

	@Override
	public boolean getHasStinger() {
		return true;
	}

	@Override
	public boolean hasSaberTeeth() {
		return true;
	}

}

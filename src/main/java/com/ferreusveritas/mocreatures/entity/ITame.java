package com.ferreusveritas.mocreatures.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;

public interface ITame extends IEntityOwnable {
	
	public void setTamedBy(EntityPlayer player);
	
	@Nullable
	abstract UUID getOwnerId();
	
	@Nullable
	abstract Entity getOwner();
	
}

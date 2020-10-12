package com.ferreusveritas.mocreatures.entity;

import net.minecraft.util.ResourceLocation;

public interface IModelRenderInfo {

	public ResourceLocation getTexture();
	
	float rollRotationOffset();
	
	float pitchRotationOffset();
	
	float yawRotationOffset();
	
	float getSizeFactor();

}

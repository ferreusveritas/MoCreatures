package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.aquatic.EntityShark;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderShark extends RenderMoC<EntityShark> {
	
	public RenderShark(ModelBase modelbase, float f) {
		super(modelbase, f);
	}
	
	@Override
	protected float handleRotationFloat(EntityShark entityshark, float f) {
		stretch(entityshark);
		return entityshark.ticksExisted + f;
	}
	
	protected void stretch(EntityShark entityshark) {
		//GlStateManager.scale(entityshark.getEdad() * 0.01F, entityshark.getEdad() * 0.01F, entityshark.getEdad() * 0.01F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityShark entityshark) {
		return entityshark.getTexture();
	}
}

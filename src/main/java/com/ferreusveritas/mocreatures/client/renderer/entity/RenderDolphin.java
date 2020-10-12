package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.aquatic.EntityDolphin;

import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDolphin extends RenderMoCNew<EntityDolphin> {
	
	public RenderDolphin(ModelBase modelbase, float f) {
		super(modelbase, f);
	}
	
	@Override
	protected float handleRotationFloat(EntityDolphin entitydolphin, float f) {
		stretch(entitydolphin);
		return entitydolphin.ticksExisted + f;
	}
	
	protected void stretch(EntityDolphin entitydolphin) {
		//GlStateManager.scale(entitydolphin.getEdad() * 0.01F, entitydolphin.getEdad() * 0.01F, entitydolphin.getEdad() * 0.01F);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityDolphin entitydolphin) {
		return entitydolphin.getTexture();
	}
	
}

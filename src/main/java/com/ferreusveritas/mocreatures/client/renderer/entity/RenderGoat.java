package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelGoat;
import com.ferreusveritas.mocreatures.entity.passive.EntityGoat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGoat extends RenderMoCNew<EntityGoat> {
	
	@Override
	protected ResourceLocation getEntityTexture(EntityGoat entitygoat) {
		return entitygoat.getTexture();
	}
	
	public RenderGoat(ModelBase modelbase, float f) {
		super(modelbase, f);
		tempGoat = (ModelGoat) modelbase;
	}
	
	@Override
	protected void preRenderCallback(EntityGoat entitygoat, float f) {
		GlStateManager.translate(0.0F, depth, 0.0F);
		stretch(entitygoat);
	}
	
	@Override
	public void doRender(EntityGoat entitygoat, double d, double d1, double d2, float f, float f1) {
		tempGoat.bleat = entitygoat.getBleating();
		tempGoat.attacking = entitygoat.getAttacking();
		tempGoat.legMov = entitygoat.legMovement();
		tempGoat.earMov = entitygoat.earMovement();
		tempGoat.tailMov = entitygoat.tailMovement();
		tempGoat.eatMov = entitygoat.mouthMovement();
		
		super.doRender(entitygoat, d, d1, d2, f, f1);
	}
	
	private final ModelGoat tempGoat;
	float depth = 0F;
}

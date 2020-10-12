package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMoC<T extends EntityLiving> extends RenderLiving<T> {

	public RenderMoC(ModelBase modelbase, float f) {
		super(MoCClientProxy.mc.getRenderManager(), modelbase, f);
	}

	@Override
	public void doRender(T entity, double d, double d1, double d2, float f, float f1) {
		doRenderMoC(entity, d, d1, d2, f, f1);
	}

	public void doRenderMoC(T entity, double d, double d1, double d2, float f, float f1) {
		super.doRender(entity, d, d1, d2, f, f1);
	}

	protected void stretch(IMoCEntity mocreature) {
		float f = mocreature.getSizeFactor();
		if (f != 0) {
			GlStateManager.scale(f, f, f);
		}
	}

	@Override
	protected void preRenderCallback(T entityliving, float f) {
		IMoCEntity mocreature = (IMoCEntity) entityliving;
		super.preRenderCallback(entityliving, f);
		//adjustOffsets is not working well
		//adjustOffsets(mocreature.getAdjustedXOffset(), mocreature.getAdjustedYOffset(), mocreature.getAdjustedZOffset());
		adjustPitch(mocreature);
		adjustRoll(mocreature);
		adjustYaw(mocreature);
		stretch(mocreature);
		//super.preRenderCallback(entityliving, f);
	}

	/**
	 * Tilts the creature to the front / back
	 *
	 * @param mocreature
	 */
	protected void adjustPitch(IMoCEntity mocreature) {
		float f = mocreature.pitchRotationOffset();

		if (f != 0) {
			GlStateManager.rotate(f, -1F, 0.0F, 0.0F);
		}
	}

	/**
	 * Rolls creature
	 *
	 * @param mocreature
	 */
	protected void adjustRoll(IMoCEntity mocreature) {
		float f = mocreature.rollRotationOffset();

		if (f != 0) {
			GlStateManager.rotate(f, 0F, 0F, -1F);
		}
	}

	protected void adjustYaw(IMoCEntity mocreature) {
		float f = mocreature.yawRotationOffset();
		if (f != 0) {
			GlStateManager.rotate(f, 0.0F, -1.0F, 0.0F);
		}
	}

	/**
	 * translates the model
	 *
	 */
	protected void adjustOffsets(float xOffset, float yOffset, float zOffset) {
		GlStateManager.translate(xOffset, yOffset, zOffset);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityLiving entity) {
		return ((IMoCEntity) entity).getTexture();
	}

}

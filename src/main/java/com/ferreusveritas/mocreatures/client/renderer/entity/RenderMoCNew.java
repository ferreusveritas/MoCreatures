package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.IModelRenderInfo;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMoCNew<T extends EntityLiving> extends RenderLiving<T> {

	public RenderMoCNew(ModelBase modelbase, float shadowsize) {
		super(MoCClientProxy.mc.getRenderManager(), modelbase, shadowsize);
	}

	@Override
	public void doRender(T entity, double d, double d1, double d2, float f, float f1) {
		doRenderMoC(entity, d, d1, d2, f, f1);
	}

	public void doRenderMoC(T entity, double d, double d1, double d2, float f, float f1) {
		super.doRender(entity, d, d1, d2, f, f1);
	}

	@Override
	protected void preRenderCallback(T entityliving, float f) {
		EntityLiving mocreature = (EntityLiving) entityliving;
		super.preRenderCallback(entityliving, f);
		//adjustOffsets is not working well
		//adjustOffsets(mocreature.getAdjustedXOffset(), mocreature.getAdjustedYOffset(), mocreature.getAdjustedZOffset());
		
		if(mocreature instanceof IModelRenderInfo) {
			IModelRenderInfo renderInfo = (IModelRenderInfo) mocreature;
			adjustPitch(renderInfo);
			adjustRoll(renderInfo);
			adjustYaw(renderInfo);
			stretch(renderInfo);
		}
		
		//super.preRenderCallback(entityliving, f);
	}
	
	/**
	 * Tilts the creature to the front / back
	 */
	protected void adjustPitch(IModelRenderInfo renderInfo) {
		float f = renderInfo.pitchRotationOffset();
		if (f != 0) {
			GlStateManager.rotate(f, -1.0f, 0.0f, 0.0f);
		}
	}
	
	/**
	 * Rolls creature
	 */
	protected void adjustRoll(IModelRenderInfo renderInfo) {
		float f = renderInfo.rollRotationOffset();
		if (f != 0) {
			GlStateManager.rotate(f, 0.0f, 0.0f, -1.0f);
		}
	}

	protected void adjustYaw(IModelRenderInfo renderInfo) {
		float f = renderInfo.yawRotationOffset();
		if (f != 0) {
			GlStateManager.rotate(f, 0.0f, -1.0f, 0.0f);
		}
	}

	protected void stretch(IModelRenderInfo renderInfo) {
		float f = renderInfo.getSizeFactor();
		if (f != 0) {
			GlStateManager.scale(f, f, f);
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
		
		if(entity instanceof IModelRenderInfo) {
			return ((IModelRenderInfo) entity).getTexture();
		}
		
		return null;
	}

}

package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
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
		boolean debug = false;
		IMoCEntity entityMoC = (IMoCEntity) entity;
		boolean displayName = entityMoC.renderName() || debug;
		if (displayName) {
			float scale = 0.01666667f * 1.6f;
			float distance = ((Entity) entityMoC).getDistance(this.renderManager.renderViewEntity);
			if (distance < 8.0f) {
				FontRenderer fontrenderer = getFontRendererFromRenderManager();
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) d + 0.0F, (float) d1 + 0.1f, (float) d2);
				GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(-scale, -scale, scale);
				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				int yOff = entityMoC.nameYOffset();
				boolean displayHealth = MoCreatures.proxy.getDisplayPetHealth();

				if (displayHealth) {
					GlStateManager.disableTexture2D();
					if (!displayName) {
						yOff += 8;
					}
					tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
					// might break SSP
					float health = entity.getHealth();
					float maxHealth = entity.getMaxHealth();
					float healthRatio = health / maxHealth;
					float barWidth = 40.0f * healthRatio;
					tessellator.getBuffer().pos(-20F + barWidth, -10 + yOff, 0.0D).color(0.7F, 0.0F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(-20F + barWidth, -6 + yOff, 0.0D).color(0.7F, 0.0F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(20D, -6 + yOff, 0.0D).color(0.7F, 0.0F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(20D, -10 + yOff, 0.0D).color(0.7F, 0.0F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(-20D, -10 + yOff, 0.0D).color(0.0F, 0.7F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(-20D, -6 + yOff, 0.0D).color(0.0F, 0.7F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(barWidth - 20F, -6 + yOff, 0.0D).color(0.0F, 0.7F, 0.0F, 1.0F).endVertex();
					tessellator.getBuffer().pos(barWidth - 20F, -10 + yOff, 0.0D).color(0.0F, 0.7F, 0.0F, 1.0F).endVertex();
					tessellator.draw();
					GlStateManager.enableTexture2D();
				}

				String name = debug ? "test" : entityMoC.getPetName();

				//Draw a background box for the text
				//GlStateManager.depthMask(false);
				//GlStateManager.disableDepth();
				//GlStateManager.enableBlend();
				//GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.disableTexture2D();
				tessellator.getBuffer().begin(7, DefaultVertexFormats.POSITION_COLOR);
				int w = fontrenderer.getStringWidth(name) / 2;
				tessellator.getBuffer().pos(-w - 1, -1 + yOff, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.getBuffer().pos(-w - 1, 8 + yOff, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.getBuffer().pos(w + 1, 8 + yOff, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.getBuffer().pos(w + 1, -1 + yOff, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
				tessellator.draw();
				GlStateManager.enableTexture2D();

				//Draw the actual text
				GlStateManager.translate(0.0f, 0.0f, -0.01f); //Move the text slightly in front of the black box so it doesn't z fight
				//fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, yOff, 0x20ffffff);
				//GlStateManager.enableDepth();
				//GlStateManager.depthMask(true);
				fontrenderer.drawString(name, -fontrenderer.getStringWidth(name) / 2, yOff, -1);
				//GlStateManager.disableBlend();
				//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
			}
		}

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

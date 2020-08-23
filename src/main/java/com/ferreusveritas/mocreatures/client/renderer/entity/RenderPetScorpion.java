package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPetScorpion extends RenderMoC<EntityPetScorpion> {

    public RenderPetScorpion(ModelScorpion modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    public void doRender(EntityPetScorpion entityscorpion, double d, double d1, double d2, float f, float f1) {
        super.doRender(entityscorpion, d, d1, d2, f, f1);
    }

    @Override
    protected void preRenderCallback(EntityPetScorpion entityscorpion, float f) {
        boolean sitting = entityscorpion.getIsSitting();
        if (entityscorpion.climbing()) {
            rotateAnimal(entityscorpion);
        }
        if (sitting) {
            float factorY = 0.4F * (float) (entityscorpion.getEdad() / 100F);
            GlStateManager.translate(0F, factorY, 0F);
        }
        if (!entityscorpion.getIsAdult()) {
            stretch(entityscorpion);
            if (entityscorpion.getRidingEntity() != null) {
                upsideDown(entityscorpion);
            }
        } else {
            adjustHeight(entityscorpion);
        }
    }

    protected void upsideDown(EntityPetScorpion entityscorpion) {
    	GlStateManager.rotate(-90F, -1F, 0.0F, 0.0F);
    	GlStateManager.translate(-1.5F, -0.5F, -2.5F);
    }

    protected void adjustHeight(EntityPetScorpion entityscorpion) {
    	GlStateManager.translate(0.0F, -0.1F, 0.0F);
    }

    protected void rotateAnimal(EntityPetScorpion entityscorpion) {
    	GlStateManager.rotate(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(EntityPetScorpion entityscorpion) {

        float f = 1.1F;
        if (!entityscorpion.getIsAdult()) {
            f = entityscorpion.getEdad() * 0.01F;
        }
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPetScorpion entityscorpion) {
        return entityscorpion.getTexture();
    }
}

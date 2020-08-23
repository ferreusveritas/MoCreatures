package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelScorpion;
import com.ferreusveritas.mocreatures.entity.monster.EntityScorpion;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderScorpion extends RenderMoC<EntityScorpion> {

    public RenderScorpion(ModelScorpion modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    public void doRender(EntityScorpion entityscorpion, double d, double d1, double d2, float f, float f1) {
        super.doRender(entityscorpion, d, d1, d2, f, f1);
    }

    @Override
    protected void preRenderCallback(EntityScorpion entityscorpion, float f) {
        if (entityscorpion.climbing()) {
            rotateAnimal(entityscorpion);
        }

        if (!entityscorpion.getIsAdult()) {
            stretch(entityscorpion);
            if (entityscorpion.getIsPicked()) {
                upsideDown(entityscorpion);
            }
        } else {
            adjustHeight(entityscorpion);
        }
    }

    protected void upsideDown(EntityScorpion entityscorpion) {
    	GlStateManager.rotate(-90F, -1F, 0.0F, 0.0F);
    	GlStateManager.translate(-1.5F, -0.5F, -2.5F);
    }

    protected void adjustHeight(EntityScorpion entityscorpion) {
    	GlStateManager.translate(0.0F, -0.1F, 0.0F);
    }

    protected void rotateAnimal(EntityScorpion entityscorpion) {
    	GlStateManager.rotate(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(EntityScorpion entityscorpion) {

        float f = 1.1F;
        if (!entityscorpion.getIsAdult()) {
            f = entityscorpion.getEdad() * 0.01F;
        }
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityScorpion entityscorpion) {
        return entityscorpion.getTexture();
    }
}

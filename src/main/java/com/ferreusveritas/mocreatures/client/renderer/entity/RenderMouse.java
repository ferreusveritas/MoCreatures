package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.passive.EntityMouse;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMouse extends RenderMoC<EntityMouse> {

    public RenderMouse(ModelBase modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    public void doRender(EntityMouse entitymouse, double d, double d1, double d2, float f, float f1) {
        super.doRender(entitymouse, d, d1, d2, f, f1);
    }

    @Override
    protected float handleRotationFloat(EntityMouse entitymouse, float f) {
        stretch(entitymouse);
        return entitymouse.ticksExisted + f;
    }

    @Override
    protected void preRenderCallback(EntityMouse entitymouse, float f) {
        if (entitymouse.upsideDown()) {
            upsideDown(entitymouse);

        }
        if (entitymouse.climbing()) {
            rotateAnimal(entitymouse);
        }
    }

    protected void rotateAnimal(EntityMouse entitymouse) {
        GlStateManager.rotate(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(EntityMouse entitymouse) {
        float f = 0.6F;
        GlStateManager.scale(f, f, f);
    }

    protected void upsideDown(EntityMouse entitymouse) {
    	GlStateManager.rotate(-90F, -1F, 0.0F, 0.0F);
        //GL11.glTranslatef(-0.55F, 0F, -0.7F);
    	GlStateManager.translate(-0.55F, 0F, 0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMouse entitymouse) {
        return entitymouse.getTexture();
    }
}

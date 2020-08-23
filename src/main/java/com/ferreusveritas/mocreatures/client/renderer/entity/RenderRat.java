package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.monster.EntityRat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderRat<T extends EntityRat> extends RenderLiving<T> {

    public RenderRat(ModelBase modelbase, float f) {
        super(MoCClientProxy.mc.getRenderManager(), modelbase, f);
    }

    @Override
    public void doRender(T entityrat, double d, double d1, double d2, float f, float f1) {
        super.doRender(entityrat, d, d1, d2, f, f1);
    }

    @Override
    protected float handleRotationFloat(T entityrat, float f) {
        stretch(entityrat);
        return entityrat.ticksExisted + f;
    }

    @Override
    protected void preRenderCallback(T entityrat, float f) {
        if (entityrat.climbing()) {
            rotateAnimal(entityrat);
        }
    }

    protected void rotateAnimal(T entityrat) {
    	GlStateManager.rotate(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(T entityrat) {
        float f = 0.8F;
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entityrat) {
        return entityrat.getTexture();
    }
}

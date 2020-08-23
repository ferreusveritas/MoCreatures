package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.MoCEntityInsect;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderInsect<T extends MoCEntityInsect> extends RenderMoC<T> {

    public RenderInsect(ModelBase modelbase) {
        super(modelbase, 0.0F);

    }

    @Override
    protected void preRenderCallback(T entityinsect, float par2) {
        if (entityinsect.climbing()) {
            rotateAnimal(entityinsect);
        }

        stretch(entityinsect);
    }

    protected void rotateAnimal(T entityinsect) {
    	GlStateManager.rotate(90F, -1F, 0.0F, 0.0F);
    }

    protected void stretch(T entityinsect) {
        float sizeFactor = entityinsect.getSizeFactor();
        GlStateManager.scale(sizeFactor, sizeFactor, sizeFactor);
    }
}

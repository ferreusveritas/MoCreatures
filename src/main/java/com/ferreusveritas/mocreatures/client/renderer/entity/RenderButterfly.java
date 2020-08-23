package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.ambient.EntityButterfly;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderButterfly extends RenderInsect<EntityButterfly> {

    public RenderButterfly(ModelBase modelbase) {
        super(modelbase);

    }

    @Override
    protected void preRenderCallback(EntityButterfly entitybutterfly, float par2) {
        if (entitybutterfly.isOnAir() || !entitybutterfly.onGround) {
            adjustHeight(entitybutterfly, entitybutterfly.tFloat());
        }
        if (entitybutterfly.climbing()) {
            rotateAnimal(entitybutterfly);
        }
        stretch(entitybutterfly);
    }

    protected void adjustHeight(EntityButterfly entitybutterfly, float FHeight) {
        GlStateManager.translate(0.0F, FHeight, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityButterfly entitybutterfly) {
        return entitybutterfly.getTexture();
    }
}

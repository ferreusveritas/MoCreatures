package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderOstrich extends RenderMoC<EntityOstrich> {

    public RenderOstrich(ModelBase modelbase, float f) {
        super(modelbase, 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityOstrich entityostrich) {
        return entityostrich.getTexture();
    }

    protected void adjustHeight(EntityOstrich entityliving, float FHeight) {
        GlStateManager.translate(0.0F, FHeight, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityOstrich entityliving, float f) {
        EntityOstrich entityostrich = (EntityOstrich) entityliving;
        if (entityostrich.getType() == 1) {
            stretch(entityostrich);
        }

        super.preRenderCallback(entityliving, f);

    }

    protected void stretch(EntityOstrich entityostrich) {

        float f = entityostrich.getEdad() * 0.01F;
        GlStateManager.scale(f, f, f);
    }

}

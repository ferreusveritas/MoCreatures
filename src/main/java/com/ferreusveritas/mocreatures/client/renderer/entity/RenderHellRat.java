package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.monster.EntityHellRat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderHellRat extends RenderRat<EntityHellRat> {

    public RenderHellRat(ModelBase modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    protected void stretch(EntityHellRat entityhellrat) {
        float f = 1.3F;
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHellRat entityhellrat) {
        return entityhellrat.getTexture();
    }
}

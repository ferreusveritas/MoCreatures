package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEgg extends RenderLiving<MoCEntityEgg> {

    public RenderEgg(ModelBase modelbase, float f) {
        super(MoCClientProxy.mc.getRenderManager(), modelbase, f);
    }

    @Override
    protected void preRenderCallback(MoCEntityEgg entityegg, float f) {
        stretch(entityegg);
        super.preRenderCallback(entityegg, f);

    }

    protected void stretch(MoCEntityEgg entityegg) {
        float f = entityegg.getSize() * 0.01F;
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(MoCEntityEgg entityegg) {
        return entityegg.getTexture();
    }
}

package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityBear;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBear extends RenderMoC<EntityBear> {

    public RenderBear(ModelBear modelbase, float f) {
        super(modelbase, f);
    }

    @Override
    protected void preRenderCallback(EntityBear entitybear, float f) {
        stretch(entitybear);
        super.preRenderCallback(entitybear, f);

    }

    protected void stretch(EntityBear entitybear) {
        float sizeFactor = entitybear.getEdad() * 0.01F;
        if (entitybear.getIsAdult()) {
            sizeFactor = 1.0F;
        }
        sizeFactor *= entitybear.getBearSize();
        GlStateManager.scale(sizeFactor, sizeFactor, sizeFactor);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBear entitybear) {
        return entitybear.getTexture();
    }
}

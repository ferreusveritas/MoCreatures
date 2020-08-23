package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelGoat;
import com.ferreusveritas.mocreatures.entity.passive.EntityGoat;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGoat extends RenderMoC<EntityGoat> {

    @Override
    protected ResourceLocation getEntityTexture(EntityGoat entitygoat) {
        return entitygoat.getTexture();
    }

    public RenderGoat(ModelBase modelbase, float f) {
        super(modelbase, f);
        this.tempGoat = (ModelGoat) modelbase;
    }

    @Override
    protected void preRenderCallback(EntityGoat entitygoat, float f) {
        GlStateManager.translate(0.0F, this.depth, 0.0F);
        stretch(entitygoat);

    }

    @Override
    public void doRender(EntityGoat entitygoat, double d, double d1, double d2, float f, float f1) {
        this.tempGoat.typeInt = entitygoat.getType();
        this.tempGoat.edad = entitygoat.getEdad() * 0.01F;
        this.tempGoat.bleat = entitygoat.getBleating();
        this.tempGoat.attacking = entitygoat.getAttacking();
        this.tempGoat.legMov = entitygoat.legMovement();
        this.tempGoat.earMov = entitygoat.earMovement();
        this.tempGoat.tailMov = entitygoat.tailMovement();
        this.tempGoat.eatMov = entitygoat.mouthMovement();

        super.doRender(entitygoat, d, d1, d2, f, f1);
    }

    protected void stretch(EntityGoat entitygoat) {
        GlStateManager.scale(entitygoat.getEdad() * 0.01F, entitygoat.getEdad() * 0.01F, entitygoat.getEdad() * 0.01F);
    }

    private final ModelGoat tempGoat;
    float depth = 0F;
}

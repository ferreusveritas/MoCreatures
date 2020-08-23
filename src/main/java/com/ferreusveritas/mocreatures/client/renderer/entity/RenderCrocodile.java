package com.ferreusveritas.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.client.model.ModelCrocodile;
import com.ferreusveritas.mocreatures.entity.passive.EntityCrocodile;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrocodile extends RenderLiving<EntityCrocodile> {

    public RenderCrocodile(ModelCrocodile modelbase, float f) {
        super(MoCClientProxy.mc.getRenderManager(), modelbase, f);
        this.croc = modelbase;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCrocodile entitycrocodile) {
        return ((EntityCrocodile) entitycrocodile).getTexture();
    }

    @Override
    public void doRender(EntityCrocodile entitycrocodile, double d, double d1, double d2, float f, float f1) {
        super.doRender(entitycrocodile, d, d1, d2, f, f1);
    }

    @Override
    protected void preRenderCallback(EntityCrocodile entitycrocodile, float f) {
        this.croc.biteProgress = entitycrocodile.biteProgress;
        this.croc.swimming = entitycrocodile.isSwimming();
        this.croc.resting = entitycrocodile.getIsSitting();
        if (entitycrocodile.isSpinning()) {
            spinCroc(entitycrocodile, (EntityLiving) entitycrocodile.getRidingEntity());
        }
        stretch(entitycrocodile);
        if (entitycrocodile.getIsSitting()) {
            if (!entitycrocodile.isInsideOfMaterial(Material.WATER)) {
                adjustHeight(entitycrocodile, 0.2F);
            } else {
                //adjustHeight(entitycrocodile, 0.1F);
            }

        }
        // if(!entitycrocodile.getIsAdult()) { }
    }

    protected void rotateAnimal(EntityCrocodile entitycrocodile) {

        //float f = entitycrocodile.swingProgress *10F *entitycrocodile.getFlipDirection();
        //float f2 = entitycrocodile.swingProgress /30 *entitycrocodile.getFlipDirection();
        //GL11.glRotatef(180F + f, 0.0F, 0.0F, -1.0F);
        //GL11.glTranslatef(0.0F-f2, 0.5F, 0.0F);
    }

    protected void adjustHeight(EntityCrocodile entitycrocodile, float FHeight) {
    	GlStateManager.translate(0.0F, FHeight, 0.0F);
    }

    protected void spinCroc(EntityCrocodile entitycrocodile, EntityLiving prey) {
        int intSpin = entitycrocodile.spinInt;
        int direction = 1;
        if (intSpin > 40) {
            intSpin -= 40;
            direction = -1;
        }
        int intEndSpin = intSpin;
        if (intSpin >= 20) {
            intEndSpin = (20 - (intSpin - 20));
        }
        if (intEndSpin == 0) {
            intEndSpin = 1;
        }
        float f3 = (((intEndSpin) - 1.0F) / 20F) * 1.6F;
        f3 = MathHelper.sqrt(f3);
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }
        f3 *= direction;
        GL11.glRotatef(f3 * 90F, 0.0F, 0.0F, 1.0F);

        if (prey != null) {
            prey.deathTime = intEndSpin;
        }
    }

    protected void stretch(EntityCrocodile entitycrocodile) {
        // float f = 1.3F;
        float f = entitycrocodile.getEdad() * 0.01F;
        // if(!entitycrocodile.getIsAdult()) { f = entitycrocodile.edad; }
        GL11.glScalef(f, f, f);
    }

    public ModelCrocodile croc;

}

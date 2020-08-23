package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.passive.EntitySnake;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSnake extends RenderMoC<EntitySnake> {

    public RenderSnake(ModelBase modelbase, float f) {
        super(modelbase, 0.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySnake par1Entity) {
        return ((EntitySnake) par1Entity).getTexture();
    }

    protected void adjustHeight(EntitySnake entitysnake, float FHeight) {
        GlStateManager.translate(0.0F, FHeight, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntitySnake entitysnake, float f) {
        stretch(entitysnake);

        /*
         * if(mod_mocreatures.mc.isMultiplayerWorld() &&
         * (entitysnake.pickedUp())) { GL11.glTranslatef(0.0F, 1.4F, 0.0F); }
         */

        if (entitysnake.pickedUp())// && entitysnake.getSizeF() < 0.6F)
        {
            float xOff = (entitysnake.getSizeF() - 1.0F);
            if (xOff > 0.0F) {
                xOff = 0.0F;
            }
            if (entitysnake.world.isRemote) {
            	GlStateManager.translate(xOff, 0.0F, 0F);
            } else {
            	GlStateManager.translate(xOff, 0F, 0.0F);
                //-0.5 puts it in the right shoulder
            }
            /*
             * //if(small) //works for small snakes GL11.glRotatef(20F, 1F, 0F,
             * 0F); if(mod_mocreatures.mc.isMultiplayerWorld()) {
             * GL11.glTranslatef(-0.5F, 1.4F, 0F); } else {
             * GL11.glTranslatef(0.7F, 0F, 1.2F); }
             */
        }

        if (entitysnake.isInsideOfMaterial(Material.WATER)) {
            adjustHeight(entitysnake, -0.25F);
        }

        super.preRenderCallback(entitysnake, f);
    }

    protected void stretch(EntitySnake entitysnake) {
        float f = entitysnake.getSizeF();
        GlStateManager.scale(f, f, f);
    }

    /*
     * @Override protected void preRenderCallback(EntityLiving entityliving,
     * float f) { MoCEntitySnake entitysnake = (MoCEntitySnake) entityliving;
     * //tempSnake.textPos = entitysnake.type - 1; if (entitysnake.type <4) {
     * tempSnake.textPos = 0; }else { tempSnake.textPos = 1; }
     * super.preRenderCallback(entityliving, f); } private MoCModelSnake
     * tempSnake;
     */
}

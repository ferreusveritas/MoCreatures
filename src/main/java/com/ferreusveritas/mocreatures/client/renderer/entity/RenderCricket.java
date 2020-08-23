package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.entity.ambient.EntityCricket;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCricket extends RenderMoC<EntityCricket> {

    public RenderCricket(ModelBase modelbase) {
        super(modelbase, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityCricket entitycricket, float par2) {
        rotateCricket((EntityCricket) entitycricket);
    }

    protected void rotateCricket(EntityCricket entitycricket) {
        if (!entitycricket.onGround) {
            if (entitycricket.motionY > 0.5D) {
                GlStateManager.rotate(35F, -1F, 0.0F, 0.0F);
            } else if (entitycricket.motionY < -0.5D) {
            	GlStateManager.rotate(-35F, -1F, 0.0F, 0.0F);
            } else {
            	GlStateManager.rotate((float) (entitycricket.motionY * 70D), -1F, 0.0F, 0.0F);
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCricket par1Entity) {
        return ((EntityCricket) par1Entity).getTexture();
    }
}

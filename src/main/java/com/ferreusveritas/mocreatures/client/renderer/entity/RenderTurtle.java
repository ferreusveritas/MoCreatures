package com.ferreusveritas.mocreatures.client.renderer.entity;

import com.ferreusveritas.mocreatures.client.model.ModelTurtle;
import com.ferreusveritas.mocreatures.entity.passive.EntityTurtle;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTurtle extends RenderMoC<EntityTurtle> {

    public ModelTurtle turtly;

    public RenderTurtle(ModelTurtle modelbase, float f) {
        super(modelbase, f);
        this.turtly = modelbase;
    }

    @Override
    protected void preRenderCallback(EntityTurtle entityturtle, float f) {
        this.turtly.upsidedown = entityturtle.getIsUpsideDown();
        this.turtly.swingProgress = entityturtle.swingProgress;
        this.turtly.isHiding = entityturtle.getIsHiding();

        if (!entityturtle.world.isRemote && (entityturtle.getRidingEntity() != null)) {

            GlStateManager.translate(0.0F, 1.3F, 0.0F);

        }
        if (entityturtle.getIsHiding()) {
            adjustHeight(entityturtle, 0.15F * entityturtle.getAge() * 0.01F);
        } else if (!entityturtle.getIsHiding() && !entityturtle.getIsUpsideDown() && !entityturtle.isInsideOfMaterial(Material.WATER)) {
            adjustHeight(entityturtle, 0.05F * entityturtle.getAge() * 0.01F);
        }
        if (entityturtle.getIsUpsideDown()) {
            rotateAnimal(entityturtle);
        }

        stretch(entityturtle);

    }

    protected void rotateAnimal(EntityTurtle entityturtle) {
        //GL11.glRotatef(180F, -1F, 0.0F, 0.0F); //head up 180
        //GL11.glRotatef(180F, 0.0F, -1.0F, 0.0F); //head around 180

        float f = entityturtle.swingProgress * 10F * entityturtle.getFlipDirection();
        float f2 = entityturtle.swingProgress / 30 * entityturtle.getFlipDirection();
        GlStateManager.rotate(180F + f, 0.0F, 0.0F, -1.0F);
        GlStateManager.translate(0.0F - f2, 0.5F * entityturtle.getAge() * 0.01F, 0.0F);
    }

    protected void adjustHeight(EntityTurtle entityturtle, float height) {
    	GlStateManager.translate(0.0F, height, 0.0F);
    }

    protected void stretch(EntityTurtle entityturtle) {
        float f = entityturtle.getAge() * 0.01F;
        GlStateManager.scale(f, f, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTurtle entityturtle) {
        return entityturtle.getTexture();
    }
}

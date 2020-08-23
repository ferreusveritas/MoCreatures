package com.ferreusveritas.mocreatures.client.model;

import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelPetScorpion extends ModelScorpion {

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        EntityPetScorpion scorpy = (EntityPetScorpion) entity;
        poisoning = scorpy.swingingTail();
        isTalking = scorpy.mouthCounter != 0;
        babies = scorpy.getHasBabies();
        attacking = scorpy.armCounter;
        sitting = scorpy.getIsSitting();
        setRotationAngles(f, f1, f2, f3, f4, f5);
        renderParts(f5);
    }
}

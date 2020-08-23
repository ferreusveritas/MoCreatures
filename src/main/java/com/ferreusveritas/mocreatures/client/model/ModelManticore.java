package com.ferreusveritas.mocreatures.client.model;

import com.ferreusveritas.mocreatures.entity.monster.EntityManticore;
import net.minecraft.entity.Entity;

public class ModelManticore extends ModelNewBigCat {

    @Override
    public void updateAnimationModifiers(Entity entity) {
        EntityManticore bigcat = (EntityManticore) entity;
        this.isFlyer = true;
        this.isSaddled = bigcat.getIsRideable();
        this.flapwings = (bigcat.wingFlapCounter != 0);
        this.floating = (this.isFlyer && bigcat.isOnAir());
        this.poisoning = bigcat.swingingTail();
        this.isRidden = (bigcat.isBeingRidden());
        this.hasMane = true;
        this.hasSaberTeeth = true;
        this.onAir = (bigcat.isOnAir());
        this.hasStinger = true;
        this.isMovingVertically = bigcat.motionY != 0;
        this.hasChest = false;
        this.isTamed = false;
        this.hasChest = false;

    }
}

package com.ferreusveritas.mocreatures.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public interface IMoCTameable extends IMoCEntity {

    boolean isRiderDisconnecting();

    void setRiderDisconnecting(boolean flag);

    void playTameEffect(boolean par1);

    void setTamed(boolean par1);

    void setDead();

    void writeEntityToNBT(NBTTagCompound nbttagcompound);

    void readEntityFromNBT(NBTTagCompound nbttagcompound);

    float getPetHealth();

    void spawnHeart();

    boolean readytoBreed();
    
    String getOffspringClazz(IMoCTameable mate);

    int getOffspringTypeInt(IMoCTameable mate); 

    boolean compatibleMate(Entity mate);
    
    void setHasEaten(boolean flag);
    
    boolean getHasEaten();
    
    void setGestationTime(int time);
    
    int getGestationTime();
}

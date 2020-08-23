package com.ferreusveritas.mocreatures.entity.monster;

import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.init.MoCItems;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFireOgre extends EntityOgre{

    public EntityFireOgre(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    @Override
    public ResourceLocation getTexture() {
        return MoCreatures.proxy.getTexture("ogrered.png");
    }
    
    @Override
    public boolean isFireStarter() {
        return true;
    }
    
    @Override
    public float getDestroyForce() {
            return MoCreatures.proxy.fireOgreStrength;
    }
    
    @Override
    protected boolean isHarmedByDaylight() {
        return true;
    }
    
    @Override
    protected Item getDropItem() {
        return MoCItems.heartfire;
    } 
}

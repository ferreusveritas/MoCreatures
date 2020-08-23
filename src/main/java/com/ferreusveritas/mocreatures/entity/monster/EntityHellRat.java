package com.ferreusveritas.mocreatures.entity.monster;

import com.ferreusveritas.mocreatures.MoCreatures;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityHellRat extends EntityRat {

    private int textCounter;

    public EntityHellRat(World world) {
        super(world);
        setSize(0.7F, 0.7F);
        this.isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    public void selectType() {
        setType(4);
    }

    @Override
    public ResourceLocation getTexture() {
        if (this.rand.nextInt(2) == 0) {
            this.textCounter++;
        }
        if (this.textCounter < 10) {
            this.textCounter = 10;
        }
        if (this.textCounter > 29) {
            this.textCounter = 10;
        }
        String textNumber = "" + this.textCounter;
        textNumber = textNumber.substring(0, 1);
        return MoCreatures.proxy.getTexture("hellrat" + textNumber + ".png");
    }

    @Override
    protected Item getDropItem() {
        boolean flag = (this.rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
        if (flag) {
            return Item.getItemFromBlock(Blocks.FIRE);
        }
        return Items.REDSTONE;
    }
}

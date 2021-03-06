package com.ferreusveritas.mocreatures.entity.ambient;

import com.google.common.base.Predicate;
import com.ferreusveritas.mocreatures.entity.MoCEntityInsect;
import com.ferreusveritas.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityRoach extends MoCEntityInsect

{

    public EntityRoach(World world) {
        super(world);
        this.texture = "roach.png";
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(3, new EntityAIFleeFromEntityMoC(this, new Predicate<Entity>() {

            public boolean apply(Entity entity) {
                return !(entity instanceof EntityCrab) && (entity.height > 0.3F || entity.width > 0.3F);
            }
        }, 6.0F, 0.8D, 1.3D));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.world.isRemote) {

            if (getIsFlying() && this.rand.nextInt(50) == 0) {
                setIsFlying(false);
            }

            /*if (!getIsFlying() && this.rand.nextInt(10) == 0) {
                EntityLivingBase entityliving = getBoogey(3D);
                if (entityliving != null) {
                    MoCTools.runLikeHell(this, entityliving);
                }
            }*/
        }
    }

    @Override
    public boolean entitiesToInclude(Entity entity) {
        return !(entity instanceof MoCEntityInsect) && super.entitiesToInclude(entity);
    }

    @Override
    public boolean isFlyer() {
        return true;
    }

    @Override
    public boolean isMyFavoriteFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == Items.ROTTEN_FLESH;
    }

    @Override
    protected int getFlyingFreq() {
        return 500;
    }

    @Override
    public float getAIMoveSpeed() {
        if (getIsFlying()) {
            return 0.1F;
        }
        return 0.25F;
    }

    @Override
    public boolean isNotScared() {
        return getIsFlying();
    }
}

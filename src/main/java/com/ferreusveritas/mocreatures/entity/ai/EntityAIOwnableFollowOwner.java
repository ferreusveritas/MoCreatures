package com.ferreusveritas.mocreatures.entity.ai;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIOwnableFollowOwner extends EntityAIBase {
	private final EntityAnimal animal;
	private Entity owner;
	World world;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;
	
	public EntityAIOwnableFollowOwner(EntityAnimal tameableIn, double followSpeedIn, float minDistIn, float maxDistIn) {
		animal = tameableIn;
		world = tameableIn.world;
		followSpeed = followSpeedIn;
		petPathfinder = tameableIn.getNavigator();
		minDist = minDistIn;
		maxDist = maxDistIn;
		setMutexBits(3);
		
		if (!(animal instanceof IEntityOwnable)) {
			throw new IllegalArgumentException("Only ownable Entity supported for EntityAIOwnableFollowOwner");
		}
		
		if (!(tameableIn.getNavigator() instanceof PathNavigateGround) && !(tameableIn.getNavigator() instanceof PathNavigateFlying)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}
	
	@Override
	public boolean shouldExecute() {
		if(animal instanceof IEntityOwnable) {
			Entity entityOwner = ((IEntityOwnable)animal).getOwner();
			
			if (
					(entityOwner == null) || // There is no owner
					(entityOwner instanceof EntityPlayer && ((EntityPlayer)entityOwner).isSpectator()) || // Player is a spectator
					(isSitting(animal)) || // Sitting animals don't follow their owner
					(animal.getDistanceSq(entityOwner) < minDist * minDist) // Close enough to player
					) {
				return false;
			}
			
			owner = entityOwner;
			return true;
		}
		return false;
	}
	
	public boolean isSitting(Entity entity) {
		if(entity instanceof EntityTameable) {
			return ((EntityTameable)entity).isSitting();
		}
		if(entity instanceof EntityAnimalComp) {
			return ((EntityAnimalComp)entity).isSitting();
		}
		return false;
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return !petPathfinder.noPath() && animal.getDistanceSq(owner) > maxDist * maxDist && !isSitting(animal);
	}
	
	@Override
	public void startExecuting() {
		timeToRecalcPath = 0;
		oldWaterCost = animal.getPathPriority(PathNodeType.WATER);
		animal.setPathPriority(PathNodeType.WATER, 0.0F);
	}
	
	@Override
	public void resetTask() {
		owner = null;
		petPathfinder.clearPath();
		animal.setPathPriority(PathNodeType.WATER, oldWaterCost);
	}
	
	@Override
	public void updateTask() {
		animal.getLookHelper().setLookPositionWithEntity(owner, 10.0F, animal.getVerticalFaceSpeed());
		
		if (!isSitting(animal)) {
			if (--timeToRecalcPath <= 0) {
				timeToRecalcPath = 10;
				
				if (!petPathfinder.tryMoveToEntityLiving(owner, followSpeed)) {
					if (!animal.getLeashed() && !animal.isRiding()) {
						if (animal.getDistanceSq(owner) >= 144.0D) {
							int x = MathHelper.floor(owner.posX) - 2;
							int y = MathHelper.floor(owner.getEntityBoundingBox().minY);
							int z = MathHelper.floor(owner.posZ) - 2;
							
							for (int xoff = 0; xoff <= 4; ++xoff) {
								for (int zoff = 0; zoff <= 4; ++zoff) {
									if ((xoff < 1 || zoff < 1 || xoff > 3 || zoff > 3) && isTeleportFriendlyBlock(x, y, z, xoff, zoff)) {
										animal.setLocationAndAngles(x + xoff + 0.5F, y, z + zoff + 0.5F, animal.rotationYaw, animal.rotationPitch);
										petPathfinder.clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	protected boolean isTeleportFriendlyBlock(int x, int y, int z, int xoff, int zoff) {
		BlockPos blockpos = new BlockPos(x + xoff, y - 1, z + zoff);
		IBlockState iblockstate = world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(animal) && world.isAirBlock(blockpos.up()) && world.isAirBlock(blockpos.up(2));
	}
	
}

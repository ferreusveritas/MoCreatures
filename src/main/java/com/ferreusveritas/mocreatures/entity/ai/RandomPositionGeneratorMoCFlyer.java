package com.ferreusveritas.mocreatures.entity.ai;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGeneratorMoCFlyer
{
	/**
	 * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
	 * MULTIPLE findTowards and findAway calls, will share this var
	 */
	private static Vec3d staticVector = Vec3d.ZERO;

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks
	 */
	@Nullable
	public static Vec3d findRandomTarget(EntityCreature entitycreatureIn, int xz, int y)
	{
		/**
		 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
		 * of par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
		 */
		return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3d)null);
	}

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
	 */
	@Nullable
	public static Vec3d findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3d targetVec3)
	{
		staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
		/**
		 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
		 * of par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
		 */
		return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
	}

	/**
	 * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
	 */
	@Nullable
	public static Vec3d findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3d targetVec3)
	{
		staticVector = (new Vec3d(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3);
		/**
		 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction
		 * of par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
		 */
		return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
	}

	/**
	 * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
	 * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
	 */
	@Nullable
	private static Vec3d findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, @Nullable Vec3d targetVec3) {
		Random random = entitycreatureIn.getRNG();
		boolean targetFound = false;
		int delX = 0;
		int delY = 0;
		int delZ = 0;
		float largestWeight = -99999.0F;
		boolean nearHome;

		if (entitycreatureIn.hasHome()) {
			double homeDistSq = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor(entitycreatureIn.posX), (double)MathHelper.floor(entitycreatureIn.posY), (double)MathHelper.floor(entitycreatureIn.posZ)) + 4.0D;
			double horDist = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
			nearHome = homeDistSq < horDist * horDist;
		}
		else {
			nearHome = false;
		}

		for (int iter = 0; iter < 10; ++iter)	{
			int rndX = random.nextInt(2 * xz + 1) - xz;
			int rndY = random.nextInt(2 * y + 1) - y;
			int rndZ = random.nextInt(2 * xz + 1) - xz;

			//rndX = (int)(rndX * (10 - iter) / 10f);
			//rndY = (int)(rndY * (10 - iter) / 10f);
			//rndZ = (int)(rndZ * (10 - iter) / 10f);
			
			if (targetVec3 == null || (double)rndX * targetVec3.x + (double)rndZ * targetVec3.z >= 0.0) {
				if (entitycreatureIn.hasHome() && xz > 1) {
					BlockPos blockpos = entitycreatureIn.getHomePosition();

					if (entitycreatureIn.posX > (double)blockpos.getX()) {
						rndX -= random.nextInt(xz / 2);
					}
					else {
						rndX += random.nextInt(xz / 2);
					}

					if (entitycreatureIn.posZ > (double)blockpos.getZ()) {
						rndZ -= random.nextInt(xz / 2);
					}
					else {
						rndZ += random.nextInt(xz / 2);
					}
				}

				BlockPos checkPos = new BlockPos((double)rndX + entitycreatureIn.posX, (double)rndY + entitycreatureIn.posY, (double)rndZ + entitycreatureIn.posZ);

				if ((!nearHome || entitycreatureIn.isWithinHomeDistanceFromPosition(checkPos))) { // && pathnavigate.canEntityStandOnPos(blockpos1))
					float pathWeight = entitycreatureIn.getBlockPathWeight(checkPos);
					//System.out.println("Weight: " + pathWeight + " @ " + checkPos);
										
					if (pathWeight > largestWeight) {
						largestWeight = pathWeight;
						delX = rndX;
						delY = rndY;
						delZ = rndZ;
						targetFound = true;
					}
				}
			}
		}
		
		return targetFound ? new Vec3d((double)delX + entitycreatureIn.posX, (double)delY + entitycreatureIn.posY, (double)delZ + entitycreatureIn.posZ) : null;
	}
}
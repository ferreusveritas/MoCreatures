package com.ferreusveritas.mocreatures;

import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MoCEventHooks {

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		// if overworld has been deleted or unloaded, reset our flag
		if (event.getWorld().provider.getDimensionType().getId() == 0) {
			MoCreatures.proxy.worldInitDone = false;
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (DimensionManager.getWorld(0) != null && !MoCreatures.proxy.worldInitDone) // if overworld has loaded, use its mapstorage
		{
			MoCPetMapData data = (MoCPetMapData) DimensionManager.getWorld(0).getMapStorage().getOrLoadData(MoCPetMapData.class, "mocreatures");
			if (data == null) {
				data = new MoCPetMapData("mocreatures");
			}

			DimensionManager.getWorld(0).getMapStorage().setData("mocreatures", data);
			DimensionManager.getWorld(0).getMapStorage().saveAllData();
			MoCreatures.instance.mapData = data;
			MoCreatures.proxy.worldInitDone = true;
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		if (!event.getEntity().world.isRemote) {
			if (IMoCTameable.class.isAssignableFrom(event.getEntityLiving().getClass())) {
				IMoCTameable mocEntity = (IMoCTameable) event.getEntityLiving();
				if (mocEntity.getIsTamed() && mocEntity.getPetHealth() > 0 && !mocEntity.isRiderDisconnecting()) {
					return;
				}

				if (mocEntity.getOwnerPetId() != -1) // required since getInteger will always return 0 if no key is found
				{
					MoCreatures.instance.mapData.removeOwnerPet(mocEntity, mocEntity.getOwnerPetId());
				}
			}
		}
	}

	/*
	// used for Despawner
	@SubscribeEvent
	public void onLivingDespawn(LivingSpawnEvent.AllowDespawn event) {
		if (MoCreatures.proxy.forceDespawns && !MoCreatures.isCustomSpawnerLoaded) {
			// try to guess what we should ignore
			// Monsters
			if ((IMob.class.isAssignableFrom(event.getEntityLiving().getClass()) || IRangedAttackMob.class.isAssignableFrom(event.getEntityLiving().getClass()))
					|| event.getEntityLiving().isCreatureType(EnumCreatureType.MONSTER, false)) {
				return;
			}
			// Tameable
			if (event.getEntityLiving() instanceof EntityTameable) {
				if (((EntityTameable) event.getEntityLiving()).isTamed()) {
					return;
				}
			}
			// Farm animals
			if (   event.getEntityLiving() instanceof EntitySheep
				|| event.getEntityLiving() instanceof EntityPig
				|| event.getEntityLiving() instanceof EntityCow
				|| event.getEntityLiving() instanceof EntityChicken) {
				// check lightlevel
				if (isValidDespawnLightLevel(event.getEntity(), event.getWorld(), MoCreatures.proxy.minDespawnLightLevel,
						MoCreatures.proxy.maxDespawnLightLevel)) {
					return;
				}
			}
			// Others
			NBTTagCompound nbt = new NBTTagCompound();
			event.getEntityLiving().writeToNBT(nbt);
			if (nbt != null) {
				if (nbt.hasKey("Owner") && !nbt.getString("Owner").equals("")) {
					return; // ignore
				}
				if (nbt.hasKey("Tamed") && nbt.getBoolean("Tamed") == true) {
					return; // ignore
				}
			}
			// Deny Rest
			if (event.getEntityLiving().getIdleTime() > 600) {
				event.setResult(Result.ALLOW);
			}

			if (MoCreatures.proxy.debug) {
				int x = MathHelper.floor(event.getEntity().posX);
				int y = MathHelper.floor(event.getEntity().getEntityBoundingBox().minY);
				int z = MathHelper.floor(event.getEntity().posZ);
				MoCLog.logger.info("Forced Despawn of entity " + event.getEntityLiving() + " at " + x + ", " + y + ", " + z
						+ ". To prevent forced despawns, use /moc forceDespawns false.");
			}
		}
	}
	 

	private boolean isValidDespawnLightLevel(Entity entity, World world, int minDespawnLightLevel, int maxDespawnLightLevel) {
		return false;
	}
	*/
}

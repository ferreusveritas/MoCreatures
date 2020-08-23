package com.ferreusveritas.mocreatures;

import java.util.function.Predicate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class MoCSpawnData {
	
	private SpawnListEntry spawnListEntry;
	private Predicate<Biome> predicate;
	
	public MoCSpawnData(Class<? extends EntityLiving> cls, int weight, int minGroup, int maxGroup, Predicate<Biome> predicate) {
		spawnListEntry = new SpawnListEntry(cls, weight, minGroup, maxGroup);
		this.predicate = predicate;
	}

	int getWeight() {
		return spawnListEntry.itemWeight;
	}
	
	int getMinGroup() {
		return spawnListEntry.minGroupCount;
	}

	int getMaxGroup() {
		return spawnListEntry.maxGroupCount;
	}

	public SpawnListEntry getSpawnListEntry() {
		return spawnListEntry;
	}
	
	public boolean shouldSpawnInBiome(Biome biome) {
		return predicate.test(biome);
	}
	
}

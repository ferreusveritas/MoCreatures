package com.ferreusveritas.mocreatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class MoCEntityData implements Iterable<MoCSpawnData> {

	private String entityName;
	private Class<? extends EntityLiving> cls;
	private EnumCreatureType typeOfCreature;
	private List<MoCSpawnData> spawnDataList = new ArrayList<>();
	
	private boolean canSpawn = true;
	
	private int frequency = 8;//At the moment this only has to be greater than zero

	public MoCEntityData(String name, Class<? extends EntityLiving> cls, EnumCreatureType type) {
		this.entityName = name;
		this.cls = cls;
		this.typeOfCreature = type;
		MoCreatures.entityMap.put(cls, this);
	}

	public MoCEntityData AddSpawn(int weight, int minGroup, int maxGroup, Biome ... biomes) {
		return AddSpawn(weight, minGroup, maxGroup, new BiomeListPredicate(biomes));
	}
	
	public MoCEntityData AddSpawn(int weight, int minGroup, int maxGroup, BiomeDictionary.Type ... biomesTypes) {
		return AddSpawn(weight, minGroup, maxGroup, new BiomeTypeListPredicate(biomesTypes));
	}
	
	public MoCEntityData AddSpawn(int weight, int minGroup, int maxGroup, Predicate<Biome> predicate) {
		MoCSpawnData spawnData = new MoCSpawnData(cls, weight, minGroup, maxGroup, predicate);
		return AddSpawn(spawnData);
	}
	
	public MoCEntityData AddSpawn(MoCSpawnData spawnData) {
		spawnDataList.add(spawnData);
		return this;
	}
	
	public Class<? extends EntityLiving> getEntityClass() {
		return cls;
	}

	public EnumCreatureType getType() {
		if (this.typeOfCreature != null) {
			return this.typeOfCreature;
		}
		return null;
	}

	public void setType(EnumCreatureType type) {
		this.typeOfCreature = type;
	}

	public int getFrequency() {
		return this.frequency;
	}

	public void setFrequency(int freq) {
		this.frequency = Math.max(0, freq);
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(String name) {
		this.entityName = name;
	}

	public void setCanSpawn(boolean flag) {
		this.canSpawn = flag;
	}

	public boolean getCanSpawn() {
		return this.canSpawn;
	}

	@Override
	public Iterator<MoCSpawnData> iterator() {
		return spawnDataList.iterator();
	}

}

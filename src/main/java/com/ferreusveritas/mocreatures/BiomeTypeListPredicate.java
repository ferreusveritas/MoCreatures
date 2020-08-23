package com.ferreusveritas.mocreatures;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeTypeListPredicate implements Predicate<Biome> {

	private List<BiomeDictionary.Type> typeList;
	
	public BiomeTypeListPredicate(BiomeDictionary.Type ... types) {
		typeList = Collections.unmodifiableList(Arrays.asList(types));
	}
	
	@Override
	public boolean test(Biome biome) {
		for(BiomeDictionary.Type type: typeList) {
			if(BiomeDictionary.hasType(biome, type)) {
				return true;
			}
		}
		return false;
	}

}

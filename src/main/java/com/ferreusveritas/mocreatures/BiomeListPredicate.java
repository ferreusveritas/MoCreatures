package com.ferreusveritas.mocreatures;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.biome.Biome;
import scala.actors.threadpool.Arrays;

public class BiomeListPredicate implements Predicate<Biome> {

	private List<Biome> biomeList;
	
	public BiomeListPredicate(Biome ... biomes) {
		biomeList = Collections.unmodifiableList(Arrays.asList(biomes));
	}
	
	@Override
	public boolean test(Biome biome) {
		return biomeList.contains(biome);
	}

}
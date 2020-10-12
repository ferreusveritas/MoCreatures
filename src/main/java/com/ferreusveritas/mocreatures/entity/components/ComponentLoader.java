package com.ferreusveritas.mocreatures.entity.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;

public class ComponentLoader<T extends EntityAnimalComp> {
	
	private final List<Function<T, Component>> list;
	
	public ComponentLoader(Function<T, Component> ... create) {
		list = Arrays.asList(create);
	}
	
	public ComponentLoader(ComponentLoader loader, Function<T, Component> ... create) {
		list = new ArrayList(loader.list);
		list.addAll(Arrays.asList(create));
	}
	
	public void assemble(T animal) {
		for(Function<T, Component> c : list) {
			animal.addComponents(c.apply(animal));
		}
	}
	
}

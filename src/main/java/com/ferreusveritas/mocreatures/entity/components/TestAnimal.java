package com.ferreusveritas.mocreatures.entity.components;

import java.util.UUID;

import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.IGender;
import com.ferreusveritas.mocreatures.entity.ITame;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class TestAnimal extends EntityAnimalComp implements ITame, IGender {
	
	public static ComponentLoader<TestAnimal> loader = new ComponentLoader<>(
			animal -> new ComponentGender<>(TestAnimal.class, animal),
			animal -> new ComponentTameFood<>(TestAnimal.class, animal, (a, i) -> i.getItem() == Items.APPLE ),
			animal -> new ComponentSit<>(TestAnimal.class, animal),
			animal -> new ComponentChest<>(TestAnimal.class, animal, "testChest"),
			animal -> new ComponentHealFood<>(TestAnimal.class, animal, true, (a, t) -> 2 )
			);
	
	@Override
	protected void setupComponents() {
		loader.assemble(this);
	}
	
	public TestAnimal(World world) {
		super(world);
		gender = getComponent(ComponentGender.class);
		tame = getComponent(ComponentTame.class);
		sit = getComponent(ComponentSit.class);
		chest = getComponent(ComponentChest.class);
		healfood = getComponent(ComponentHealFood.class);
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}
	
	private final ComponentTame tame;
	@Override public void setTamedBy(EntityPlayer player) { tame.setTamedBy(player); }
	@Override public UUID getOwnerId() { return tame.getOwnerId(); }
	@Override public Entity getOwner() { return tame.getOwner(); }
	
	private final ComponentSit sit;
	@Override public boolean isSitting() { return sit.isSitting(); }
	
	private final ComponentGender gender;
	@Override public Gender getGender() { return gender.getGender(); }
	@Override public void setGender(Gender g) { gender.setGender(g);}
	
	private final ComponentChest chest;
	private final ComponentHealFood healfood;
}

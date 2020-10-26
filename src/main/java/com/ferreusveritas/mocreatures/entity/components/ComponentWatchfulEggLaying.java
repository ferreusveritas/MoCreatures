package com.ferreusveritas.mocreatures.entity.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.Animation;
import com.ferreusveritas.mocreatures.entity.EntityAnimalComp;
import com.ferreusveritas.mocreatures.entity.Gender;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;
import com.ferreusveritas.mocreatures.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class ComponentWatchfulEggLaying<T extends EntityAnimalComp> extends Component<T> implements IFeedable {
	
	private static class PerClassValues {
		public final DataParameter<Boolean> EGG_WATCH;
		public final int eggNum;
		
		public PerClassValues(Class clazz, int eggNum) {
			EGG_WATCH = EntityDataManager.<Boolean>createKey(clazz, DataSerializers.BOOLEAN);
			this.eggNum = eggNum;
		}
	}
	
	private static Map<Class, PerClassValues> classMap = new HashMap<>();
	
	private static PerClassValues getValues(Class clazz, int eggNum) {
		return classMap.computeIfAbsent(clazz, c -> new PerClassValues(c, eggNum));
	}
	
	private final PerClassValues values;
	private ComponentTame tame;
	private ComponentGender gender;
	
	private int eggCounter;
	private boolean canLayEggs;
	
	public ComponentWatchfulEggLaying(Class clazz, T animal, int eggNum) {
		super(animal);
		this.values = getValues(clazz, eggNum);
		eggCounter = animal.getRand().nextInt(1000) + 1000;
		canLayEggs = false;
	}
	
	@Override
	public void link() {
		super.link();
		tame = animal.getComponent(ComponentTame.class);
		gender = animal.getComponent(ComponentGender.class);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		updateEggWatching();
	}
	
	private boolean isTamed() {
		return tame != null && tame.isTamed();
	}
	
	private Gender getGender() {
		return gender != null ? gender.getGender() : Gender.None;
	}
	
	@Override
	public void register() {
		dataManager.register(values.EGG_WATCH, false);
	}
	
	public boolean isEggWatching() {
		return dataManager.get(values.EGG_WATCH);
	}
	
	public void setEggWatching(boolean eggwatch) {
		dataManager.set(values.EGG_WATCH, eggwatch);
	}
	
	@Override
	public void writeComponentToNBT(NBTTagCompound compound) {
		compound.setBoolean("EggWatch", isEggWatching());
	}
	
	@Override
	public void readComponentFromNBT(NBTTagCompound compound) {
		setEggWatching(compound.getBoolean("EggWatch"));
	}
	
	public void updateEggWatching() {
		World world = animal.world;
		Random rand = animal.getRand();
		
		//egg laying..  Only females lay eggs.
		if (animal.isServer() && canLayEggs && animal.isAdult() && (getGender() == Gender.Female) && !isEggWatching()) { 
			if ((--eggCounter <= 0) && ( rand.nextInt(500) < MoCreatures.proxy.ostrichEggDropChance)) {
				EntityPlayer player = world.getClosestPlayerToEntity(animal, 12.0);
				if (player != null) {
					EntityAnimalComp male = (EntityAnimalComp) Util.getClosestLivingEntity(animal, 8.0, e -> adultMaleFilter(animal, e));
					if (male != null) {
						MoCEntityEgg entityegg = new MoCEntityEgg(world, values.eggNum);
						entityegg.setPosition(animal.posX, animal.posY, animal.posZ);
						world.spawnEntity(entityegg);
						
						if (!isTamed()) {
							setEggWatching(true);
							//Make the male eggWatch as well
							if (male != null) {
								ComponentWatchfulEggLaying watchComp = male.getComponent(ComponentWatchfulEggLaying.class);
								if(watchComp != null) {
									watchComp.setEggWatching(true);
								}
							}
							animal.doAnimation(Animation.openMouth); //This will never work since this is server side only
						}
						
						MoCTools.playCustomSound(animal, SoundEvents.ENTITY_CHICKEN_EGG);
						eggCounter = rand.nextInt(2000) + 2000;
						canLayEggs = false;
					}
					
				}
			}
		}
		
		//egg protection
		if (isEggWatching()) {
			//look for and protect eggs and move close
			MoCEntityEgg myEgg = (MoCEntityEgg) Util.getAnyLivingEntity(animal, 8.0, e -> eggFilter(e));
			if (myEgg != null) {// There's an egg
				if (MoCTools.getSqDistanceTo(myEgg, animal.posX, animal.posY, animal.posZ) > 4.0) { // But it's too far away to protect it
					animal.getNavigator().setPath(animal.getNavigator().getPathToPos(myEgg.getPosition()), 16.0f); //So move closer to it
				}
			} else { // didn't find egg.  It was there before so it must have been taken
				setEggWatching(false);
				
				EntityPlayer eggStealer = world.getClosestPlayerToEntity(animal, 10.0);
				if (eggStealer != null) {
					if (!isTamed() && world.getDifficulty() != EnumDifficulty.PEACEFUL) {
						animal.setAttackTarget(eggStealer);
						animal.doAnimation(Animation.flapWings);
					}
				}
			}
		}
	}
	
	
	@Override
	public boolean feed(EntityPlayer player, ItemStack itemStack) {
		if (!isTamed() && !itemStack.isEmpty() && animal.isAdult() && getGender() == Gender.Female && itemStack.getItem() == Items.MELON_SEEDS) {
			animal.doAnimation(Animation.openMouth);
			canLayEggs = true;
			return true;
		}
		return false;
	}
	
	private boolean adultMaleFilter(T animal, Entity entity) {
		if(entity.getClass().equals(animal.getClass()) && (entity instanceof EntityAnimalComp) && ((EntityAnimalComp) entity).isAdult()) {
			ComponentGender genderComp = ((EntityAnimalComp)entity).getComponent(ComponentGender.class);
			if(genderComp != null) {
				return genderComp.getGender() == Gender.Male;
			}
		}
		return false;
	}
	
	private boolean eggFilter(Entity entity) {
		return ((entity instanceof MoCEntityEgg) && (((MoCEntityEgg) entity).eggType == values.eggNum));
	}
	
}

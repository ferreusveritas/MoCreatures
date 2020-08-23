package com.ferreusveritas.mocreatures.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.ferreusveritas.mocreatures.BiomeTypeListPredicate;
import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCEntityData;
import com.ferreusveritas.mocreatures.MoCSpawnData;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.ambient.EntityAnt;
import com.ferreusveritas.mocreatures.entity.ambient.EntityBee;
import com.ferreusveritas.mocreatures.entity.ambient.EntityButterfly;
import com.ferreusveritas.mocreatures.entity.ambient.EntityCrab;
import com.ferreusveritas.mocreatures.entity.ambient.EntityCricket;
import com.ferreusveritas.mocreatures.entity.ambient.EntityDragonfly;
import com.ferreusveritas.mocreatures.entity.ambient.EntityFirefly;
import com.ferreusveritas.mocreatures.entity.ambient.EntityFly;
import com.ferreusveritas.mocreatures.entity.ambient.EntityMaggot;
import com.ferreusveritas.mocreatures.entity.ambient.EntityRoach;
import com.ferreusveritas.mocreatures.entity.ambient.EntitySnail;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityAnchovy;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityAngelFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityAngler;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityBass;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityClownFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityCod;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityDolphin;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityGoldFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityHippoTang;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityJellyFish;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityManderin;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityMantaRay;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityPiranha;
import com.ferreusveritas.mocreatures.entity.aquatic.EntitySalmon;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityShark;
import com.ferreusveritas.mocreatures.entity.aquatic.EntityStingRay;
import com.ferreusveritas.mocreatures.entity.item.MoCEntityEgg;
import com.ferreusveritas.mocreatures.entity.monster.EntityCaveOgre;
import com.ferreusveritas.mocreatures.entity.monster.EntityFireOgre;
import com.ferreusveritas.mocreatures.entity.monster.EntityGreenOgre;
import com.ferreusveritas.mocreatures.entity.monster.EntityHellRat;
import com.ferreusveritas.mocreatures.entity.monster.EntityManticore;
import com.ferreusveritas.mocreatures.entity.monster.EntityRat;
import com.ferreusveritas.mocreatures.entity.monster.EntityScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntityBlackBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityBoar;
import com.ferreusveritas.mocreatures.entity.passive.EntityCrocodile;
import com.ferreusveritas.mocreatures.entity.passive.EntityDeer;
import com.ferreusveritas.mocreatures.entity.passive.EntityDuck;
import com.ferreusveritas.mocreatures.entity.passive.EntityElephant;
import com.ferreusveritas.mocreatures.entity.passive.EntityEnt;
import com.ferreusveritas.mocreatures.entity.passive.EntityFox;
import com.ferreusveritas.mocreatures.entity.passive.EntityGoat;
import com.ferreusveritas.mocreatures.entity.passive.EntityGrizzlyBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityKomodo;
import com.ferreusveritas.mocreatures.entity.passive.EntityLeopard;
import com.ferreusveritas.mocreatures.entity.passive.EntityLion;
import com.ferreusveritas.mocreatures.entity.passive.EntityManticorePet;
import com.ferreusveritas.mocreatures.entity.passive.EntityMole;
import com.ferreusveritas.mocreatures.entity.passive.EntityMouse;
import com.ferreusveritas.mocreatures.entity.passive.EntityOstrich;
import com.ferreusveritas.mocreatures.entity.passive.EntityPandaBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityPanther;
import com.ferreusveritas.mocreatures.entity.passive.EntityPetScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntityRaccoon;
import com.ferreusveritas.mocreatures.entity.passive.EntitySnake;
import com.ferreusveritas.mocreatures.entity.passive.EntityTiger;
import com.ferreusveritas.mocreatures.entity.passive.EntityTurkey;
import com.ferreusveritas.mocreatures.entity.passive.EntityTurtle;
import com.ferreusveritas.mocreatures.entity.passive.EntityWyvern;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MoCEntities {

	public static List<EntityEntry> ENTITIES = new ArrayList<>();
	public static List<EntityEntry> SPAWN_ENTITIES = new ArrayList<>();
	static int MoCEntityID = 0;

	private static EntityEntry createEntityEntry(Class<? extends Entity> cls, String name) {
		EntityEntry entityEntry = new EntityEntry(cls, name);
		entityEntry.setRegistryName(new ResourceLocation(MoCConstants.MOD_PREFIX + name.toLowerCase()));
		ENTITIES.add(entityEntry);
		return entityEntry;
	}

	private static EntityEntry createEntityEntry(Class<? extends Entity> cls, String name, int primaryColorIn, int secondaryColorIn) {
		EntityEntry entityEntry = new EntityEntry(cls, name);
		entityEntry.setRegistryName(new ResourceLocation(MoCConstants.MOD_PREFIX + name.toLowerCase()));
		entityEntry.setEgg(new EntityEggInfo(new ResourceLocation(MoCConstants.MOD_PREFIX + name.toLowerCase()), primaryColorIn, secondaryColorIn));
		SPAWN_ENTITIES.add(entityEntry);
		return entityEntry;
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName) {
		final ResourceLocation resourceLocation = new ResourceLocation(MoCConstants.MOD_PREFIX + entityName.toLowerCase());
		EntityRegistry.registerModEntity(resourceLocation, entityClass, resourceLocation.toString(), MoCEntityID, MoCreatures.instance, 80, 1, true);
		MoCEntityID += 1;
	}

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int eggColor, int eggDotsColor) {
		final ResourceLocation resourceLocation = new ResourceLocation(MoCConstants.MOD_PREFIX + entityName.toLowerCase());
		EntityRegistry.registerModEntity(resourceLocation, entityClass, resourceLocation.toString(), MoCEntityID, MoCreatures.instance, 80, 1, true, eggColor, eggDotsColor);
		MoCEntityID += 1;
	}

	public static EntityEntry BEAR = createEntityEntry(EntityBlackBear.class, "BlackBear", 10, 1);//, 0x2600, 0x052500);
	public static EntityEntry BOAR = createEntityEntry(EntityBoar.class, "Boar", 14772545, 9141102);//, 0x2600, 0x052500);
	public static EntityEntry CROCODILE = createEntityEntry(EntityCrocodile.class, "Crocodile", 16711680, 65407);//, 0x2600, 0x052500);
	public static EntityEntry DUCK = createEntityEntry(EntityDuck.class, "Duck", 14021607, 15656192);//, 0x2600, 0x052500);
	public static EntityEntry DEER = createEntityEntry(EntityDeer.class, "Deer", 14021607, 33023);//, 0x2600, 0x052500);
	public static EntityEntry ELEPHANT = createEntityEntry(EntityElephant.class, "Elephant", 14772545, 23423);
	public static EntityEntry ENT = createEntityEntry(EntityEnt.class, "Ent", 12623485, 16711680);
	public static EntityEntry FOX = createEntityEntry(EntityFox.class, "Fox", 14772545, 5253242);//, 0x2600, 0x052500);
	public static EntityEntry GOAT = createEntityEntry(EntityGoat.class, "Goat", 7434694, 6053069);//, 0x2600, 0x052500);
	public static EntityEntry GRIZZLY_BEAR = createEntityEntry(EntityGrizzlyBear.class, "GrizzlyBear", 14772545, 1);//, 0x2600, 0x052500);
	public static EntityEntry KOMODO_DRAGON = createEntityEntry(EntityKomodo.class, "KomodoDragon", 16711680, 23423);
	public static EntityEntry LEOPARD = createEntityEntry(EntityLeopard.class, "Leopard", 13749760, 10);
	public static EntityEntry LION = createEntityEntry(EntityLion.class, "Lion", 15313474, 13749760);
	public static EntityEntry MANTICORE_PET = createEntityEntry(EntityManticorePet.class, "ManticorePet");
	public static EntityEntry MOLE = createEntityEntry(EntityMole.class, "Mole", 14020607, 16711680);
	public static EntityEntry MOUSE = createEntityEntry(EntityMouse.class, "Mouse", 14772545, 0);//, 0x02600, 0x002500);
	public static EntityEntry OSTRICH = createEntityEntry(EntityOstrich.class, "Ostrich", 14020607, 9639167);//, 0x2600, 0x052500);
	public static EntityEntry PANDA_BEAR = createEntityEntry(EntityPandaBear.class, "PandaBear", 10, 9141102);//, 0x2600, 0x052500);
	public static EntityEntry PANTHER = createEntityEntry(EntityPanther.class, "Panther", 10, 205);
	public static EntityEntry PET_SCORPION = createEntityEntry(EntityPetScorpion.class, "PetScorpion");
	public static EntityEntry RACCOON = createEntityEntry(EntityRaccoon.class, "Raccoon", 14772545, 13749760);
	public static EntityEntry SNAKE = createEntityEntry(EntitySnake.class, "Snake", 14020607, 13749760);//, 0x05800, 0x006800);
	public static EntityEntry TIGER = createEntityEntry(EntityTiger.class, "Tiger", 14772545, 0);
	public static EntityEntry TURTLE = createEntityEntry(EntityTurtle.class, "Turtle", 14772545, 9320590);//, 0x04800, 0x004500);
	public static EntityEntry TURKEY = createEntityEntry(EntityTurkey.class, "Turkey", 14020607, 16711680);//, 0x2600, 0x052500);
	public static EntityEntry WYVERN = createEntityEntry(EntityWyvern.class, "Wyvern", 14772545, 65407);

	/**
	 * Mobs
	 */
	public static EntityEntry CAVE_OGRE = createEntityEntry(EntityCaveOgre.class, "CaveOgre", 16711680, 33023);//, 0x2600, 0x052500);
	public static EntityEntry FIRE_OGRE = createEntityEntry(EntityFireOgre.class, "FireOgre", 16711680, 9320595);//, 0x2600, 0x052500);
	public static EntityEntry GREEN_OGRE = createEntityEntry(EntityGreenOgre.class, "GreenOgre", 16711680, 65407);//, 0x2600, 0x052500);
	public static EntityEntry HELLRAT = createEntityEntry(EntityHellRat.class, "HellRat", 16711680, 14772545);//, 0x2600, 0x052500);
	public static EntityEntry MANTICORE = createEntityEntry(EntityManticore.class, "Manticore", 16711680, 0);
	public static EntityEntry RAT = createEntityEntry(EntityRat.class, "Rat", 16711680, 9141102);//, 0x2600, 0x052500);
	public static EntityEntry SCORPION = createEntityEntry(EntityScorpion.class, "Scorpion", 16711680, 6053069);//, 0x2600, 0x052500);

	/**
	 * Aquatic
	 */
	public static EntityEntry ANCHOVY = createEntityEntry(EntityAnchovy.class, "Anchovy", 5665535, 205);
	public static EntityEntry ANGELFISH = createEntityEntry(EntityAngelFish.class, "AngelFish", 5665535, 7434694);
	public static EntityEntry ANGLER = createEntityEntry(EntityAngler.class, "Angler", 5665535, 10);
	public static EntityEntry BASS = createEntityEntry(EntityBass.class, "Bass", 33023, 2372490);
	public static EntityEntry CLOWNFISH = createEntityEntry(EntityClownFish.class, "ClownFish", 5665535, 14772545);
	public static EntityEntry COD = createEntityEntry(EntityCod.class, "Cod", 33023, 16622);
	public static EntityEntry DOLPHIN = createEntityEntry(EntityDolphin.class, "Dolphin", 33023, 15631086);//, 0x2600, 0x052500);
	public static EntityEntry GOLDFISH = createEntityEntry(EntityGoldFish.class, "GoldFish", 5665535, 15656192);
	public static EntityEntry HIPPOTANG = createEntityEntry(EntityHippoTang.class, "HippoTang", 5665535, 2037680);
	public static EntityEntry JELLYFISH = createEntityEntry(EntityJellyFish.class, "JellyFish", 33023, 14772545);//, 0x2600, 0x052500);
	public static EntityEntry MANDERIN = createEntityEntry(EntityManderin.class, "Manderin", 5665535, 12623485);
	public static EntityEntry PIRANHA = createEntityEntry(EntityPiranha.class, "Piranha", 33023, 16711680);
	public static EntityEntry SALMON = createEntityEntry(EntitySalmon.class, "Salmon", 33023, 12623485);
	public static EntityEntry MANTARAY = createEntityEntry(EntityMantaRay.class, "MantaRay", 33023, 9141102);//14772545, 9141102);
	public static EntityEntry SHARK = createEntityEntry(EntityShark.class, "Shark", 33023, 9013643);//, 0x2600, 0x052500);
	public static EntityEntry STINGRAY = createEntityEntry(EntityStingRay.class, "StingRay", 33023, 6053069);//14772545, 9141102);

	/**
	 * Ambients
	 */
	public static EntityEntry ANT = createEntityEntry(EntityAnt.class, "Ant", 65407, 12623485);
	public static EntityEntry BEE = createEntityEntry(EntityBee.class, "Bee", 65407, 15656192);//, 0x2600, 0x052500);
	public static EntityEntry BUTTERFLY = createEntityEntry(EntityButterfly.class, "ButterFly", 65407, 7434694);//, 0x22600, 0x012500);
	public static EntityEntry CRAB = createEntityEntry(EntityCrab.class, "Crab", 65407, 13749760);
	public static EntityEntry CRICKET = createEntityEntry(EntityCricket.class, "Cricket", 65407, 16622);//, 0x2600, 0x052500);
	public static EntityEntry DRAGONFLY = createEntityEntry(EntityDragonfly.class, "DragonFly", 65407, 14020607);//, 0x2600, 0x052500);
	public static EntityEntry FIREFLY = createEntityEntry(EntityFirefly.class, "Firefly", 65407, 9320590);//, 0x2600, 0x052500);
	public static EntityEntry FLY = createEntityEntry(EntityFly.class, "Fly", 65407, 1);//, 0x2600, 0x052500);
	public static EntityEntry MAGGOT = createEntityEntry(EntityMaggot.class, "Maggot", 65407, 9141102);
	public static EntityEntry SNAIL = createEntityEntry(EntitySnail.class, "Snail", 65407, 14772545);//, 0x2600, 0x052500);
	public static EntityEntry ROACH = createEntityEntry(EntityRoach.class, "Roach", 65407, 13749760);

	/**
	 * Others
	 */
	public static EntityEntry EGG = createEntityEntry(MoCEntityEgg.class, "Egg");//, 0x2600, 0x052500);

	@Mod.EventBusSubscriber(modid = MoCConstants.MOD_ID)
	public static class RegistrationHandler {

		@SubscribeEvent
		public static void registerEntities(final RegistryEvent.Register<EntityEntry> event) {
			MoCreatures.LOGGER.info("Registering entities...");
			for (EntityEntry entry : ENTITIES) {
				registerEntity(entry.getEntityClass(), entry.getName());
			}
			for (EntityEntry entry : SPAWN_ENTITIES) {
				registerEntity(entry.getEntityClass(), entry.getName(), entry.getEgg().primaryColor, entry.getEgg().secondaryColor);
			}

			registerAmbients();
			registerCreatures();
			registerWaterCreatures();
			registerMonsters();

			Collection<Biome> allBiomes = ForgeRegistries.BIOMES.getValuesCollection();

			for (MoCEntityData entityData : MoCreatures.mocEntityMap.values()) {
				if(entityData.getCanSpawn()) {
					for(MoCSpawnData spawnData : entityData) {
						for (Biome biome : allBiomes) {
							if(spawnData.shouldSpawnInBiome(biome)) {
								SpawnListEntry spawnListEntry = spawnData.getSpawnListEntry();
								List<SpawnListEntry> spawnableList = biome.getSpawnableList(entityData.getType());
								if (!spawnableList.contains(spawnListEntry)) {
									spawnableList.add(spawnListEntry);
								}
							}
						}
					}
				}
			}

			MoCreatures.LOGGER.info("Entity registration complete.");


		}
		
		//////////////////////////////////////////////////////////////
		// ENTITIY ADDERS
		//////////////////////////////////////////////////////////////
		
		private static MoCEntityData Add(String name, Class<? extends EntityLiving> cls, EnumCreatureType type) {
			MoCEntityData entityData = new MoCEntityData(name, cls, type);
			MoCreatures.mocEntityMap.put(name, entityData);
			return entityData;
		}

		private static MoCEntityData AddAmbient(String name, Class<? extends EntityLiving> cls) {
			return Add(name, cls, EnumCreatureType.AMBIENT);
		}
		
		private static MoCEntityData AddWater(String name, Class<? extends EntityLiving> cls) {
			EntitySpawnPlacementRegistry.setPlacementType(cls, EntityLiving.SpawnPlacementType.IN_WATER);
			return Add(name, cls, EnumCreatureType.WATER_CREATURE);
		}

		private static MoCEntityData AddCreature(String name, Class<? extends EntityLiving> cls) {
			return Add(name, cls, EnumCreatureType.CREATURE);
		}

		private static MoCEntityData AddMonster(String name, Class<? extends EntityLiving> cls) {
			return Add(name, cls, EnumCreatureType.MONSTER);
		}
		
		
		//////////////////////////////////////////////////////////////
		// BIOME PREDICATES
		//////////////////////////////////////////////////////////////
		
		public static Predicate<Biome> vanilla() {
			return p -> p.getRegistryName().getResourceDomain().equals("minecraft");
		}

		public static Predicate<Biome> bop() {
			return p -> p.getRegistryName().getResourceDomain().equals("biomesoplenty");
		}
		
		public static Predicate<Biome> flowery() {
			return p ->
				p == Biomes.MUTATED_FOREST ||
				p instanceof biomesoplenty.common.biome.overworld.BiomeGenFlowerField ||
				p instanceof biomesoplenty.common.biome.overworld.BiomeGenFlowerIsland ||
				p instanceof biomesoplenty.common.biome.overworld.BiomeGenCherryBlossomGrove ||
				p instanceof biomesoplenty.common.biome.overworld.BiomeGenLavenderFields;
		}
		
		public static Predicate<Biome> humid() {
			return new BiomeTypeListPredicate(Type.HOT).and(new BiomeTypeListPredicate(Type.WET)).or(new BiomeTypeListPredicate(Type.SWAMP));
		}
		
		private static Predicate<Biome> bamboo() {
			return b -> (b instanceof biomesoplenty.common.biome.overworld.BiomeGenBambooForest);
		}

		private static Predicate<Biome> desert() {
			return new BiomeTypeListPredicate(Type.HOT)
			.and(new BiomeTypeListPredicate(Type.DRY))
			.and(new BiomeTypeListPredicate(Type.SANDY));
		}
		
		private static Predicate<Biome> redwoods() {
			return b -> (b instanceof biomesoplenty.common.biome.overworld.BiomeGenRedwoodForest || 
					b == Biomes.MUTATED_REDWOOD_TAIGA ||
					b == Biomes.REDWOOD_TAIGA ||
					b == Biomes.REDWOOD_TAIGA_HILLS ||
					b == Biomes.MUTATED_REDWOOD_TAIGA_HILLS);
		}

		private static Predicate<Biome> boreal() {
			return b -> b instanceof biomesoplenty.common.biome.overworld.BiomeGenBorealForest;
		}

		@SuppressWarnings("unused")
		private static Predicate<Biome> taiga() {
			return b -> b instanceof BiomeTaiga;
		}
		
		private static Predicate<Biome> forest() {
			return new BiomeTypeListPredicate(Type.FOREST);
		}
		
		private static Predicate<Biome> wetlands() {
			return b -> (b instanceof biomesoplenty.common.biome.overworld.BiomeGenLandOfLakes || b instanceof biomesoplenty.common.biome.overworld.BiomeGenWetland);
		}
		
		private static Predicate<Biome> ocean() {
			return new BiomeTypeListPredicate(Type.OCEAN).and(maridia().negate());
		}

		private static Predicate<Biome> deepOceanLiteral() {
			return b -> b == Biomes.DEEP_OCEAN;
		}
		
		private static Predicate<Biome> deepOcean() {
			return deepOceanLiteral().and(maridia().negate());
		}

		private static Predicate<Biome> swamp() {
			return new BiomeTypeListPredicate(Type.SWAMP);
		}

		private static Predicate<Biome> dead() {
			return new BiomeTypeListPredicate(Type.DEAD, Type.WASTELAND);
		}
		
		private static Predicate<Biome> water() {
			return new BiomeTypeListPredicate(Type.BEACH, Type.SWAMP, Type.WATER, Type.OCEAN, Type.RIVER).and(maridia().negate());
		}
		
		private static Predicate<Biome> coralReef() {
			return b -> b instanceof biomesoplenty.common.biome.overworld.BiomeGenCoralReef;
		}
		
		private static Predicate<Biome> tropicalIsland() {
			return b -> b instanceof biomesoplenty.common.biome.overworld.BiomeGenTropicalIsland;
		}
		
		private static Predicate<Biome> beach() {
			return new BiomeTypeListPredicate(Type.BEACH);
		}
		
		private static Predicate<Biome> warmOcean() {
			return ocean().or(beach()).and(new BiomeTypeListPredicate(Type.COLD).negate());
		}

		private static Predicate<Biome> duvotica() {
			return b -> b == com.ferreusveritas.stargarden.features.Worlds.duvoticaBiome;
		}
		
		private static Predicate<Biome> maridia() {
			return b -> b == com.ferreusveritas.stargarden.features.Worlds.maridiaBiome;
		}
		
		private static Predicate<Biome> ogre() {
			return new BiomeTypeListPredicate(
				Type.SANDY,
				Type.FOREST,
				Type.JUNGLE,
				Type.HILLS,
				Type.MESA,
				Type.MOUNTAIN,
				Type.PLAINS,
				Type.SWAMP,
				Type.WASTELAND,
				Type.DEAD,
				Type.SPOOKY)
					.and(tropicalIsland().negate()//We don't want them on Tropical Islands as it's against the spirit of that Biome
					.and(vanilla().or(bop()))//Make sure they don't show up in custom dimensions unexpectedly such as twilight forest
				);
		}

		
		//////////////////////////////////////////////////////////////
		// AMBIENTS
		//////////////////////////////////////////////////////////////
		
		public static void registerAmbients() {

			AddAmbient("Ant", EntityAnt.class);
				/*.AddSpawn(2, 1, 4, //Ants seem kinda pointless
					Type.FOREST,
					Type.HILLS,
					Type.JUNGLE,
					Type.MESA,
					Type.MOUNTAIN,
					Type.PLAINS,
					Type.SWAMP,
					Type.WASTELAND
				);*/
			
			AddAmbient("Bee",EntityBee.class)
				.AddSpawn(4, 4, 6, flowery());

			AddAmbient("ButterFly", EntityButterfly.class)
				.AddSpawn(8, 1, 5, flowery())
				.AddSpawn(3, 1, 2,
					Type.FOREST,
					Type.HILLS,
					Type.JUNGLE,
					Type.MESA,
					Type.MOUNTAIN,
					Type.PLAINS
				)
				.AddSpawn(5, 1, 3, duvotica());

			AddAmbient("Crab", EntityCrab.class)
				.AddSpawn(5, 1, 2, Type.BEACH, Type.OCEAN);

			AddAmbient("Cricket", EntityCricket.class)
				.AddSpawn(3, 1, 2,
					Type.FOREST,
					Type.HILLS,
					Type.JUNGLE,
					Type.MESA,
					Type.MOUNTAIN,
					Type.PLAINS,
					Type.SWAMP
				);

			AddAmbient("DragonFly", EntityDragonfly.class)
				.AddSpawn(5, 1, 2, swamp().or(tropicalIsland().or(duvotica()).and(dead().negate())));

			AddAmbient("Firefly", EntityFirefly.class);
				//.AddSpawn(8, 1, 2, Type.FOREST,	Type.PLAINS, Type.SWAMP);

			AddAmbient("Fly", EntityFly.class)
				.AddSpawn(8, 1, 2, dead())
				.AddSpawn(2, 1, 1, humid());

			AddAmbient("Maggot", EntityMaggot.class)
				.AddSpawn(8, 1, 2, dead());

			AddAmbient("Snail", EntitySnail.class)
				.AddSpawn(5, 1, 2, humid());

			AddAmbient("Roach", EntityRoach.class)
				.AddSpawn(5, 1, 2, humid());
			 
		}

		
		//////////////////////////////////////////////////////////////
		// CREATURES
		//////////////////////////////////////////////////////////////
		
		public static void registerCreatures() {

			AddCreature("BlackBear", EntityBlackBear.class)
				.AddSpawn(2, 1, 3, forest().and(bamboo().negate()).and(redwoods().negate()));

			AddCreature("GrizzlyBear", EntityGrizzlyBear.class)
				.AddSpawn(2, 1, 2, redwoods().or(boreal()));

			AddCreature("PandaBear", EntityPandaBear.class)
				.AddSpawn(5, 1, 3, bamboo());

			AddCreature("Boar", EntityBoar.class)
				.AddSpawn(4, 2, 2, Type.FOREST, Type.JUNGLE, Type.PLAINS);

			AddCreature("Crocodile", EntityCrocodile.class)
				.AddSpawn(3, 1, 2, Type.SWAMP);

			AddCreature("Deer", EntityDeer.class)
				.AddSpawn(12, 1, 4, Type.FOREST, Type.PLAINS);

			AddCreature("Duck", EntityDuck.class)
				.AddSpawn(12, 1, 4, wetlands())
				.AddSpawn(4, 1, 2, swamp());

			AddCreature("Elephant", EntityElephant.class)
				.AddSpawn(2, 1, 1, Type.JUNGLE, Type.SNOWY)
				.AddSpawn(3, 1, 1, Type.SAVANNA);

			AddCreature("Ent", EntityEnt.class);
				//.AddSpawn(4, 1, 2, Type.FOREST);

			AddCreature("Fox", EntityFox.class)
				.AddSpawn(4, 1, 1, Type.FOREST, Type.PLAINS, Type.SNOWY);

			AddCreature("Goat", EntityGoat.class)
				.AddSpawn(4, 1, 3,
					Type.FOREST,
					Type.HILLS,
					Type.JUNGLE,
					Type.MESA,
					Type.MOUNTAIN,
					Type.PLAINS
				);

			AddCreature("KomodoDragon", EntityKomodo.class)
				.AddSpawn(1, 1, 2, Type.SWAMP);

			AddCreature("Leopard", EntityLeopard.class)
				.AddSpawn(1, 1, 2,
					Type.HILLS,
					Type.JUNGLE,
					Type.MOUNTAIN,
					Type.SNOWY
				);
			
			AddCreature("Lion", EntityLion.class)
				.AddSpawn(1, 1, 2, Type.SAVANNA);

			AddCreature("Mole", EntityMole.class)
				.AddSpawn(3, 1, 2, Type.PLAINS);

			AddCreature("Mouse", EntityMouse.class)
				.AddSpawn(3, 1, 2,
					Type.FOREST,
					Type.HILLS,
					Type.PLAINS
				);

			AddCreature("Ostrich", EntityOstrich.class)
				.AddSpawn(3, 1, 1, Type.SAVANNA);

			AddCreature("Panther", EntityPanther.class)
				.AddSpawn(1, 1, 2, Type.JUNGLE);

			AddCreature("Raccoon", EntityRaccoon.class)
				.AddSpawn(6, 1, 2, Type.FOREST,	Type.PLAINS);

			AddCreature("Snake", EntitySnake.class)
				.AddSpawn(3, 1, 2,
					new BiomeTypeListPredicate(Type.SANDY, Type.FOREST,	Type.HILLS,	Type.JUNGLE, Type.MESA,	Type.MOUNTAIN, Type.PLAINS,	Type.SWAMP)
						.and(new BiomeTypeListPredicate(Type.COLD).negate())
				);

			AddCreature("Tiger", EntityTiger.class)
				.AddSpawn(1, 1, 2, Type.JUNGLE);

			AddCreature("Turkey", EntityTurkey.class)
				.AddSpawn(6, 1, 2, new BiomeTypeListPredicate(Type.PLAINS)
						.and(new BiomeTypeListPredicate(Type.SAVANNA).negate()));

			AddCreature("Turtle", EntityTurtle.class)
				.AddSpawn(6, 1, 2, Type.JUNGLE,	Type.SWAMP);

			AddCreature("Wyvern", EntityWyvern.class);
			//.AddSpawn(8, 1, 3, ...);

		}


		//////////////////////////////////////////////////////////////
		// WATER CREATURES
		//////////////////////////////////////////////////////////////

		public static void registerWaterCreatures() {

			AddWater("Bass", EntityBass.class)
				.AddSpawn(6, 1, 4, water());

			AddWater("Cod", EntityCod.class)
				.AddSpawn(6, 1, 4, water());
			
			AddWater("Dolphin", EntityDolphin.class)
				.AddSpawn(2, 1, 4, warmOcean());
			
			AddWater("JellyFish", EntityJellyFish.class)
				.AddSpawn(4, 1, 6, ocean())
				.AddSpawn(2, 1, 2, maridia());
			
			AddWater("Salmon", EntitySalmon.class)
				.AddSpawn(5, 1, 4, water());
			
			AddWater("Piranha", EntityPiranha.class)
				.AddSpawn(1, 4, 8, new BiomeTypeListPredicate(Type.RIVER).and(new BiomeTypeListPredicate(Type.COLD).negate()));
			
			AddWater("MantaRay", EntityMantaRay.class)
				.AddSpawn(1, 1, 2,	ocean())
				.AddSpawn(1, 1, 1, maridia());
			
			AddWater("StingRay", EntityStingRay.class)
				.AddSpawn(2, 1, 3, coralReef().or(tropicalIsland()))
				.AddSpawn(4, 1, 2, duvotica());
			
			AddWater("Shark", EntityShark.class)
				.AddSpawn(1, 1, 2, warmOcean())
				.AddSpawn(2, 1, 2, coralReef());
			
			AddWater("Anchovy", EntityAnchovy.class)
				.AddSpawn(14, 4, 8, ocean());
			
			AddWater("AngelFish", EntityAngelFish.class)
				.AddSpawn(12, 4, 8, coralReef());
			
			AddWater("Angler", EntityAngler.class)
				.AddSpawn(4, 1, 2, deepOcean());
						
			AddWater("ClownFish", EntityClownFish.class)
				.AddSpawn(12, 1, 4,	coralReef());
			
			AddWater("GoldFish", EntityGoldFish.class)
				.AddSpawn(3, 1, 2,	new BiomeTypeListPredicate(Type.SWAMP).and(new BiomeTypeListPredicate(Type.DEAD, Type.SPOOKY).negate()));
			
			AddWater("HippoTang", EntityHippoTang.class)
				.AddSpawn(12, 4, 6, coralReef());
			
			AddWater("Manderin", EntityManderin.class)
				.AddSpawn(12, 4, 6, coralReef());
			
		}

		
		//////////////////////////////////////////////////////////////
		// MONSTERS
		//////////////////////////////////////////////////////////////
		
		public static void registerMonsters() {
			
			AddMonster("HellRat", EntityHellRat.class)
				.AddSpawn(2, 1, 4, Type.NETHER,	Type.DEAD);
			
			AddMonster("GreenOgre", EntityGreenOgre.class)
				.AddSpawn(1, 1, 2, ogre());
			
			AddMonster("FireOgre", EntityFireOgre.class)
				.AddSpawn(1, 1, 2, ogre());
			
			AddMonster("CaveOgre", EntityCaveOgre.class)
				.AddSpawn(1, 1, 2, ogre());
			
			AddMonster("Rat", EntityRat.class)
				.AddSpawn(2, 1, 2, 
					new BiomeTypeListPredicate(
					Type.SANDY,
					Type.FOREST,
					Type.SNOWY,
					Type.JUNGLE,
					Type.HILLS,
					Type.MESA,
					Type.MOUNTAIN,
					Type.PLAINS,
					Type.SWAMP,
					Type.WASTELAND)//Any of these types
					.and(duvotica().negate()) //But not Duvotica
					.and(maridia().negate()) //But not Maridia
					.and(tropicalIsland().negate()) //But not Tropical Islands
					.and(vanilla().or(bop()))//Make sure they don't show up in custom dimensions unexpectedly such as twilight forest
				);
			
			AddMonster("Scorpion", EntityScorpion.class)
				.AddSpawn(2, 1, 2, desert() //Desert
					.or(new BiomeTypeListPredicate(Type.SNOWY, Type.JUNGLE, Type.NETHER, Type.WASTELAND)) //Or any of these types
					.and(duvotica().negate()) //But not Duvotica
					.and(maridia().negate()) //But not Maridia
					.and(tropicalIsland().negate()) //But not Tropical Islands
					.and(vanilla().or(bop())) //Make sure they don't show up in custom dimensions unexpectedly such as twilight forest
				); //But not Tropical Islands
			
			AddMonster("Manticore", EntityManticore.class);
			 
		}

	}
}

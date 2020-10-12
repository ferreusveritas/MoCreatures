package com.ferreusveritas.mocreatures;

import java.util.ArrayList;
import java.util.List;

import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.util.MoCLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MoCProxy implements IGuiHandler {

	public static String ARMOR_TEXTURE = "textures/armor/";
	public static String BLOCK_TEXTURE = "textures/blocks/";
	public static String ITEM_TEXTURE = "textures/items/";
	public static String MODEL_TEXTURE = "textures/models/";
	public static String GUI_TEXTURE = "textures/gui/";
	public static String MISC_TEXTURE = "textures/misc/";

	//CONFIG VARIABLES
	// Client Only
	public boolean animateTextures;

	public boolean attackDolphins;
	public boolean attackWolves;

	public boolean destroyDrops;
	public boolean elephantBulldozer;
	public boolean killallVillagers;

	public int maxTamed;
	public int maxOPTamed;
	public int ostrichEggDropChance;
	public int rareItemDropChance;
	public int wyvernEggDropChance;
	public int motherWyvernEggDropChance;
	public int particleFX;
	// defaults
	public int frequency = 6;
	public int minGroup = 1;
	public int maxGroup = 2;
	public int maxSpawnInChunk = 3;
	public float strength = 1;
	public int minDespawnLightLevel = 2;
	public int maxDespawnLightLevel = 7;

	// ogre settings
	public float ogreStrength;
	public float caveOgreStrength;
	public float fireOgreStrength;
	public short ogreAttackRange;
	public short fireOgreChance;
	public short caveOgreChance;

	public boolean forceDespawns;
	public boolean enableHunters;
	public boolean debug = false;
	public boolean allowInstaSpawn;
	public boolean needsUpdate = false;
	public int activeScreen = -1;

	protected static final String CATEGORY_MOC_GENERAL_SETTINGS = "global-settings";
	protected static final String CATEGORY_MOC_CREATURE_GENERAL_SETTINGS = "creature-general-settings";
	protected static final String CATEGORY_MOC_MONSTER_GENERAL_SETTINGS = "monster-general-settings";
	protected static final String CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS = "water-mob-general-settings";
	protected static final String CATEGORY_MOC_AMBIENT_GENERAL_SETTINGS = "ambient-general-settings";
	protected static final String CATEGORY_MOC_ID_SETTINGS = "custom-id-settings";

	public void resetAllData() {
		//registerEntities();
		this.readGlobalConfigValues();
	}

	//----------------CONFIG INITIALIZATION
	public void ConfigInit(FMLPreInitializationEvent event) {
		//registerEntities();
		this.readGlobalConfigValues();
		if (this.debug) {
			MoCLog.logger.info("Initializing MoCreatures Config File at " + event.getSuggestedConfigurationFile().getParent() + "MoCSettings.cfg");
		}
	}

	//-----------------THE FOLLOWING ARE CLIENT SIDE ONLY, NOT TO BE USED IN SERVER AS THEY AFFECT ONLY DISPLAY / SOUNDS

	public boolean getAnimateTextures() {
		return false;
	}

	public int getParticleFX() {
		return 0;
	}

	public ResourceLocation getTexture(String texture) {
		return null;
	}

	public EntityPlayer getPlayer() {
		return null;
	}

	public void printMessageToPlayer(String msg) {
	}

	public List<String> parseName(String biomeConfigEntry) {
		String tag = biomeConfigEntry.substring(0, biomeConfigEntry.indexOf('|'));
		String biomeName = biomeConfigEntry.substring(biomeConfigEntry.indexOf('|') + 1, biomeConfigEntry.length());
		List<String> biomeParts = new ArrayList<String>();
		biomeParts.add(tag);
		biomeParts.add(biomeName);
		return biomeParts;
	}

	/**
	 * Reads values from file
	 */
	public void readGlobalConfigValues() {
		// client-side only
		this.animateTextures = true;

		// general
		this.allowInstaSpawn = false;//Allows you to instantly spawn MoCreatures from GUI.
		this.debug = false;//"Turns on verbose logging"
		this.minDespawnLightLevel = 2;//The minimum light level threshold used to determine whether or not to despawn a farm animal.
		this.maxDespawnLightLevel = 7;//The maximum light level threshold used to determine whether or not to despawn a farm animal. Note: Configure this value in CMS if it is installed.
		this.forceDespawns = true;//If true, it will force despawns on all creatures including vanilla for a more dynamic experience while exploring world. If false, all passive mocreatures will not despawn to prevent other creatures from taking over. Note: if you experience issues with farm animals despawning, adjust despawnLightLevel. If CMS is installed, this setting must remain true if you want MoCreatures to despawn.
		this.maxTamed = 10;//Max tamed creatures a player can have. Requires enableOwnership to be set to true.
		this.maxOPTamed = 20;//Max tamed creatures an op can have. Requires enableOwnership to be set to true.
		this.elephantBulldozer = true;
		this.ostrichEggDropChance = 3;//A value of 3 means ostriches have a 3% chance to drop an egg.
		this.particleFX = 3;
		this.attackDolphins = false;//Allows water creatures to attack dolphins.
		this.attackWolves = false;//Allows creatures to attack wolves.
		this.enableHunters = true;//Allows creatures to attack other creatures. Not recommended if despawning is off.
		this.destroyDrops = false;
		this.killallVillagers = false;
		this.rareItemDropChance = 25;//A value of 25 means Horses/Ostriches/Scorpions/etc. have a 25% chance to drop a rare item such as a heart of darkness, unicorn, bone when killed. Raise the value if you want higher drop rates.
		this.wyvernEggDropChance = 10;//A value of 10 means wyverns have a 10% chance to drop an egg.
		this.motherWyvernEggDropChance = 33;//A value of 33 means mother wyverns have a 33% chance to drop an egg.

		this.ogreStrength = 2.5f;//The block destruction radius of green Ogres
		this.caveOgreStrength = 3.0f;//The block destruction radius of Cave Ogres
		this.fireOgreStrength = 2.0f;//The block destruction radius of Fire Ogres
		this.ogreAttackRange = 12;//The block radius where ogres 'smell' players
		this.fireOgreChance = 25;//The chance percentage of spawning Fire ogres in the Overworld
		this.caveOgreChance = 75;//The chance percentage of spawning Cave ogres at depth of 50 in the Overworld

	}

	// Client stuff
	public void registerRenderers() {
		// Nothing here as this is the server side proxy
	}

	public void registerRenderInformation() {
		//client
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	/***
	 * Dummy to know if is dedicated server or not
	 *
	 * @return
	 */
	public int getProxyMode() {
		return 1;
	}

	/**
	 * Sets the name client side. Name is synchronized with datawatchers
	 *
	 * @param player
	 * @param mocanimal
	 */
	public void setName(EntityPlayer player, IMoCEntity mocanimal) {
		//client side only
	}

}

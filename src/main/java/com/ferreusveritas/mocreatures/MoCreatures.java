package com.ferreusveritas.mocreatures;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ferreusveritas.mocreatures.client.MoCCreativeTabs;
import com.ferreusveritas.mocreatures.client.handlers.MoCKeyHandler;
import com.ferreusveritas.mocreatures.command.CommandMoCPets;
import com.ferreusveritas.mocreatures.command.CommandMoCSpawn;
import com.ferreusveritas.mocreatures.command.CommandMoCTP;
import com.ferreusveritas.mocreatures.command.CommandMoCreatures;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.mojang.authlib.GameProfile;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MoCConstants.MOD_ID, name = MoCConstants.MOD_NAME, version = MoCConstants.MOD_VERSION, dependencies=MoCConstants.DEPENDENCIES)
public class MoCreatures {

	@Instance(MoCConstants.MOD_ID)
	public static MoCreatures instance;

	@SidedProxy(clientSide = "com.ferreusveritas.mocreatures.client.MoCClientProxy", serverSide = "com.ferreusveritas.mocreatures.MoCProxy")
	public static MoCProxy proxy;
	public static final Logger LOGGER = LogManager.getLogger(MoCConstants.MOD_ID);
	public static final CreativeTabs tabMoC = new MoCCreativeTabs(CreativeTabs.CREATIVE_TAB_ARRAY.length, "MoCreaturesTab");
	public MoCPetMapData mapData;
	public static GameProfile MOCFAKEPLAYER = new GameProfile(UUID.fromString("6E379B45-1111-2222-3333-2FE1A88BCD66"), "[MoCreatures]");

	public static Map<String, MoCEntityData> mocEntityMap = new TreeMap<String, MoCEntityData>(String.CASE_INSENSITIVE_ORDER);
	public static Map<Class<? extends EntityLiving>, MoCEntityData> entityMap = new HashMap<Class<? extends EntityLiving>, MoCEntityData>();
	public static Map<Integer, Class<? extends EntityLiving>> instaSpawnerMap = new HashMap<Integer, Class<? extends EntityLiving>>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MoCMessageHandler.init();
		MinecraftForge.EVENT_BUS.register(new MoCEventHooks());
		proxy.ConfigInit(event);
		if (!isServer()) {
			MinecraftForge.EVENT_BUS.register(new MoCKeyHandler());
		}
	}

	//how to check for client: if(FMLCommonHandler.instance().getSide().isRemote())

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerRenderers();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandMoCreatures());
		event.registerServerCommand(new CommandMoCTP());
		event.registerServerCommand(new CommandMoCPets());
		if (isServer()) {
			if (FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
				event.registerServerCommand(new CommandMoCSpawn());
			}
		}
	}

	public static void updateSettings() {
		proxy.readGlobalConfigValues();
	}

	public static boolean isServer() {
		return (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER);
	}
}

package com.ferreusveritas.mocreatures.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCEntityData;
import com.ferreusveritas.mocreatures.MoCProxy;
import com.ferreusveritas.mocreatures.client.model.ModelAnt;
import com.ferreusveritas.mocreatures.client.model.ModelBear;
import com.ferreusveritas.mocreatures.client.model.ModelBee;
import com.ferreusveritas.mocreatures.client.model.ModelBoar;
import com.ferreusveritas.mocreatures.client.model.ModelButterfly;
import com.ferreusveritas.mocreatures.client.model.ModelCrab;
import com.ferreusveritas.mocreatures.client.model.ModelCricket;
import com.ferreusveritas.mocreatures.client.model.ModelCrocodile;
import com.ferreusveritas.mocreatures.client.model.ModelDeer;
import com.ferreusveritas.mocreatures.client.model.ModelDolphin;
import com.ferreusveritas.mocreatures.client.model.ModelDragonfly;
import com.ferreusveritas.mocreatures.client.model.ModelDuck;
import com.ferreusveritas.mocreatures.client.model.ModelEgg;
import com.ferreusveritas.mocreatures.client.model.ModelElephant;
import com.ferreusveritas.mocreatures.client.model.ModelFirefly;
import com.ferreusveritas.mocreatures.client.model.ModelFly;
import com.ferreusveritas.mocreatures.client.model.ModelFox;
import com.ferreusveritas.mocreatures.client.model.ModelGoat;
import com.ferreusveritas.mocreatures.client.model.ModelJellyFish;
import com.ferreusveritas.mocreatures.client.model.ModelKomodo;
import com.ferreusveritas.mocreatures.client.model.ModelMaggot;
import com.ferreusveritas.mocreatures.client.model.ModelMediumFish;
import com.ferreusveritas.mocreatures.client.model.ModelMole;
import com.ferreusveritas.mocreatures.client.model.ModelMouse;
import com.ferreusveritas.mocreatures.client.model.ModelNewBigCat;
import com.ferreusveritas.mocreatures.client.model.ModelOgre;
import com.ferreusveritas.mocreatures.client.model.ModelOstrich;
import com.ferreusveritas.mocreatures.client.model.ModelPetScorpion;
import com.ferreusveritas.mocreatures.client.model.ModelRaccoon;
import com.ferreusveritas.mocreatures.client.model.ModelRat;
import com.ferreusveritas.mocreatures.client.model.ModelRay;
import com.ferreusveritas.mocreatures.client.model.ModelRoach;
import com.ferreusveritas.mocreatures.client.model.ModelScorpion;
import com.ferreusveritas.mocreatures.client.model.ModelShark;
import com.ferreusveritas.mocreatures.client.model.ModelSmallFish;
import com.ferreusveritas.mocreatures.client.model.ModelSnail;
import com.ferreusveritas.mocreatures.client.model.ModelSnake;
import com.ferreusveritas.mocreatures.client.model.ModelTurkey;
import com.ferreusveritas.mocreatures.client.model.ModelTurtle;
import com.ferreusveritas.mocreatures.client.model.ModelWyvern;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderButterfly;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderCricket;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderCrocodile;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderDolphin;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderEgg;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderFirefly;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderGoat;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderHellRat;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderInsect;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderMoC;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderMouse;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderOstrich;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderPetScorpion;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderRat;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderScorpion;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderShark;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderSnake;
import com.ferreusveritas.mocreatures.client.renderer.entity.RenderTurtle;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
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
import com.ferreusveritas.mocreatures.entity.monster.EntityRat;
import com.ferreusveritas.mocreatures.entity.monster.EntityScorpion;
import com.ferreusveritas.mocreatures.entity.passive.EntityBlackBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityBoar;
import com.ferreusveritas.mocreatures.entity.passive.EntityCrocodile;
import com.ferreusveritas.mocreatures.entity.passive.EntityDeer;
import com.ferreusveritas.mocreatures.entity.passive.EntityDuck;
import com.ferreusveritas.mocreatures.entity.passive.EntityElephant;
import com.ferreusveritas.mocreatures.entity.passive.EntityFox;
import com.ferreusveritas.mocreatures.entity.passive.EntityGoat;
import com.ferreusveritas.mocreatures.entity.passive.EntityGrizzlyBear;
import com.ferreusveritas.mocreatures.entity.passive.EntityKomodo;
import com.ferreusveritas.mocreatures.entity.passive.EntityLeopard;
import com.ferreusveritas.mocreatures.entity.passive.EntityLion;
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

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MoCClientProxy extends MoCProxy {

	public static Minecraft mc = Minecraft.getMinecraft();
	public static MoCClientProxy instance;

	public MoCClientProxy() {
		instance = this;
	}

	@Override
	public void registerRenderers() {
	}

	@Override
	public ResourceLocation getTexture(String texture) {
		return new ResourceLocation(MoCConstants.MOD_ID, "textures/models/" + texture);
	}

	@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
	@Override
	public void registerRenderInformation() {
		// Register your custom renderers
		RenderingRegistry.registerEntityRenderingHandler(EntityMouse.class, new RenderMouse(new ModelMouse(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntitySnake.class, new RenderSnake(new ModelSnake(), 0.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTurkey.class, new RenderMoC(new ModelTurkey(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, new RenderButterfly(new ModelButterfly()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBoar.class, new RenderMoC(new ModelBoar(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityBlackBear.class, new RenderMoC(new ModelBear(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGrizzlyBear.class, new RenderMoC(new ModelBear(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityPandaBear.class, new RenderMoC(new ModelBear(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDuck.class, new RenderMoC(new ModelDuck(), 0.3F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDeer.class, new RenderMoC(new ModelDeer(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityFox.class, new RenderMoC(new ModelFox(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityShark.class, new RenderShark(new ModelShark(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityDolphin.class, new RenderDolphin(new ModelDolphin(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(MoCEntityEgg.class, new RenderEgg(new ModelEgg(), 0.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityRat.class, new RenderRat(new ModelRat(), 0.2F));
		RenderingRegistry.registerEntityRenderingHandler(EntityHellRat.class, new RenderHellRat(new ModelRat(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityScorpion.class, new RenderScorpion(new ModelScorpion(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityCrocodile.class, new RenderCrocodile(new ModelCrocodile(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMantaRay.class, new RenderMoC(new ModelRay(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityStingRay.class, new RenderMoC(new ModelRay(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityJellyFish.class, new RenderMoC(new ModelJellyFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGoat.class, new RenderGoat(new ModelGoat(), 0.3F));
		RenderingRegistry.registerEntityRenderingHandler(EntityOstrich.class, new RenderOstrich(new ModelOstrich(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityBee.class, new RenderInsect(new ModelBee()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFly.class, new RenderInsect(new ModelFly()));
		RenderingRegistry.registerEntityRenderingHandler(EntityDragonfly.class, new RenderInsect(new ModelDragonfly()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFirefly.class, new RenderFirefly(new ModelFirefly()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCricket.class, new RenderCricket(new ModelCricket()));
		RenderingRegistry.registerEntityRenderingHandler(EntitySnail.class, new RenderMoC(new ModelSnail(), 0.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityPetScorpion.class, new RenderPetScorpion(new ModelPetScorpion(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityElephant.class, new RenderMoC(new ModelElephant(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityKomodo.class, new RenderMoC(new ModelKomodo(), 0.3F));
		RenderingRegistry.registerEntityRenderingHandler(EntityWyvern.class, new RenderMoC(new ModelWyvern(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGreenOgre.class, new RenderMoC(new ModelOgre(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityCaveOgre.class, new RenderMoC(new ModelOgre(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityFireOgre.class, new RenderMoC(new ModelOgre(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityRoach.class, new RenderInsect(new ModelRoach()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMaggot.class, new RenderMoC(new ModelMaggot(), 0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityCrab.class, new RenderMoC(new ModelCrab(), 0.2F));
		RenderingRegistry.registerEntityRenderingHandler(EntityRaccoon.class, new RenderMoC(new ModelRaccoon(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityAnt.class, new RenderInsect(new ModelAnt()));
		RenderingRegistry.registerEntityRenderingHandler(EntityCod.class, new RenderMoC(new ModelMediumFish(), 0.2F));
		RenderingRegistry.registerEntityRenderingHandler(EntitySalmon.class, new RenderMoC(new ModelMediumFish(), 0.2F));
		RenderingRegistry.registerEntityRenderingHandler(EntityBass.class, new RenderMoC(new ModelMediumFish(), 0.2F));
		RenderingRegistry.registerEntityRenderingHandler(EntityAnchovy.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityAngelFish.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityAngler.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityClownFish.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityGoldFish.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityHippoTang.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityManderin.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityPiranha.class, new RenderMoC(new ModelSmallFish(), 0.1F));
		RenderingRegistry.registerEntityRenderingHandler(EntityMole.class, new RenderMoC(new ModelMole(), 0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityLion.class, new RenderMoC(new ModelNewBigCat(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTiger.class, new RenderMoC(new ModelNewBigCat(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTurtle.class, new RenderTurtle(new ModelTurtle(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityPanther.class, new RenderMoC(new ModelNewBigCat(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityLeopard.class, new RenderMoC(new ModelNewBigCat(), 0.5F));
	}

	@Override
	public EntityPlayer getPlayer() {
		return MoCClientProxy.mc.player;
	}

	/**
	 * Sets the name client side. Name is synchronized with datawatchers
	 *
	 * @param player
	 * @param mocanimal
	 */
	@Override
	public void setName(EntityPlayer player, IMoCEntity mocanimal) { }

	@Override
	public int getProxyMode() {
		return 2;
	}

	public static final List<String> entityTypes = Arrays.asList("CREATURE", "MONSTER", "WATERCREATURE", "AMBIENT");

	public MoCEntityData currentSelectedEntity;

	@Override
	public void ConfigInit(FMLPreInitializationEvent event) {
		super.ConfigInit(event);
	}

	public void resetToDefaults() {
		resetAllData();
	}

	@Override
	public int getParticleFX() {
		return this.particleFX;
	}

	@Override
	public boolean getDisplayPetName() {
		return this.displayPetName;
	}

	@Override
	public boolean getDisplayPetIcons() {
		return this.displayPetIcons;
	}

	@Override
	public boolean getDisplayPetHealth() {
		return this.displayPetHealth;
	}

	@Override
	public boolean getAnimateTextures() {
		return this.animateTextures;
	}

	@Override
	public void printMessageToPlayer(String msg) {
		Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(msg));
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
}

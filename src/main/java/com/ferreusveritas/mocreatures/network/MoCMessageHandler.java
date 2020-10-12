package com.ferreusveritas.mocreatures.network;

import java.util.List;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import com.ferreusveritas.mocreatures.entity.monster.EntityOgre;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAnimation;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAppear;
import com.ferreusveritas.mocreatures.network.message.MoCMessageAttachedEntity;
import com.ferreusveritas.mocreatures.network.message.MoCMessageEntityDive;
import com.ferreusveritas.mocreatures.network.message.MoCMessageEntityJump;
import com.ferreusveritas.mocreatures.network.message.MoCMessageExplode;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHealth;
import com.ferreusveritas.mocreatures.network.message.MoCMessageHeart;
import com.ferreusveritas.mocreatures.network.message.MoCMessageInstaSpawn;
import com.ferreusveritas.mocreatures.network.message.MoCMessageNameGUI;
import com.ferreusveritas.mocreatures.network.message.MoCMessageTwoBytes;
import com.ferreusveritas.mocreatures.network.message.MoCMessageVanish;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MoCMessageHandler {
	
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MoCreatures");
	
	public static void init() {
		INSTANCE.registerMessage(MoCMessageAnimation.class, MoCMessageAnimation.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageAppear.class, MoCMessageAppear.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageAttachedEntity.class, MoCMessageAttachedEntity.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageEntityDive.class, MoCMessageEntityDive.class, 3, Side.SERVER);
		INSTANCE.registerMessage(MoCMessageEntityJump.class, MoCMessageEntityJump.class, 4, Side.SERVER);
		INSTANCE.registerMessage(MoCMessageExplode.class, MoCMessageExplode.class, 5, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageHealth.class, MoCMessageHealth.class, 6, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageHeart.class, MoCMessageHeart.class, 7, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageInstaSpawn.class, MoCMessageInstaSpawn.class, 8, Side.SERVER);
		INSTANCE.registerMessage(MoCMessageNameGUI.class, MoCMessageNameGUI.class, 9, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageTwoBytes.class, MoCMessageTwoBytes.class, 12, Side.CLIENT);
		INSTANCE.registerMessage(MoCMessageVanish.class, MoCMessageVanish.class, 13, Side.CLIENT);
	}
	
	// Wrap client message handling due to 1.8 clients receiving packets on Netty thread
	// This solves random NPE issues when attempting to access world data on client
	@SuppressWarnings("rawtypes")
	public static void handleMessage(IMessageHandler message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			FMLCommonHandler.instance().getWorldThread(FMLCommonHandler.instance().getClientToServerNetworkManager().getNetHandler()).addScheduledTask(new ClientPacketTask(message, ctx));
		}
	}
	
	// redirects client received packets to main thread to avoid NPEs
	public static class ClientPacketTask implements Runnable {
		
		@SuppressWarnings("rawtypes")
		private IMessageHandler message;
		@SuppressWarnings("unused")
		private MessageContext ctx;
		
		@SuppressWarnings("rawtypes")
		public ClientPacketTask(IMessageHandler message, MessageContext ctx) {
			this.message = message;
			this.ctx = ctx;
		}
		
		@Override
		public void run() {
			if (this.message instanceof MoCMessageAnimation) {
				MoCMessageAnimation message = (MoCMessageAnimation) this.message;
				List<Entity> entList = MoCClientProxy.mc.player.world.loadedEntityList;
				for (Entity ent : entList) {
					if (ent.getEntityId() == message.entityId && ent instanceof IMoCEntity) {
						((IMoCEntity) ent).performAnimation(message.animationType);
						break;
					}
				}
				return;
			} else if (this.message instanceof MoCMessageAppear) {//TODO: Remove Me
				return;
			} else if (this.message instanceof MoCMessageAttachedEntity) {
				MoCMessageAttachedEntity message = (MoCMessageAttachedEntity) this.message;
				Object var2 = MoCClientProxy.mc.player.world.getEntityByID(message.sourceEntityId);
				Entity var3 = MoCClientProxy.mc.player.world.getEntityByID(message.targetEntityId);
				
				if (var2 != null) {
					((Entity) var2).startRiding(var3);
				}
				return;
			} else if (this.message instanceof MoCMessageExplode) {
				MoCMessageExplode message = (MoCMessageExplode) this.message;
				List<Entity> entList = MoCClientProxy.mc.player.world.loadedEntityList;
				for (Entity ent : entList) {
					if (ent.getEntityId() == message.entityId && ent instanceof EntityOgre) {
						((EntityOgre) ent).performDestroyBlastAttack();
						break;
					}
				}
				return;
			} else if (this.message instanceof MoCMessageHealth) {
				MoCMessageHealth message = (MoCMessageHealth) this.message;
				List<Entity> entList = MoCClientProxy.mc.player.world.loadedEntityList;
				for (Entity ent : entList) {
					if (ent.getEntityId() == message.entityId && ent instanceof EntityLiving) {
						((EntityLiving) ent).setHealth(message.health);
						break;
					}
				}
				return;
			} else if (this.message instanceof MoCMessageHeart) {
				MoCMessageHeart message = (MoCMessageHeart) this.message;
				Entity entity = null;
				while (entity == null) {
					entity = MoCClientProxy.mc.player.world.getEntityByID(message.entityId);
					if (entity != null) {
						if (entity instanceof IMoCTameable) {
							((IMoCTameable)entity).spawnHeart();
						}
					}
				}
				return;
			} else if (this.message instanceof MoCMessageTwoBytes) { //TODO: Remove Me
				return;
			} else if (this.message instanceof MoCMessageVanish) {//TODO: Remove Me
				return;
			}
		}
	}
}

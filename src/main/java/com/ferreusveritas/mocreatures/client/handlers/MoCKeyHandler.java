package com.ferreusveritas.mocreatures.client.handlers;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.ferreusveritas.mocreatures.client.MoCClientProxy;
import com.ferreusveritas.mocreatures.entity.IMoCEntity;
import com.ferreusveritas.mocreatures.network.MoCMessageHandler;
import com.ferreusveritas.mocreatures.network.message.MoCMessageEntityDive;
import com.ferreusveritas.mocreatures.network.message.MoCMessageEntityJump;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class MoCKeyHandler {

	int keyCount;
	//static KeyBinding jumpBinding = new KeyBinding("jumpBind", Keyboard.KEY_F);
	//static KeyBinding jumpBinding = new KeyBinding("MoCreatures Jump", MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode(), "key.categories.movement");
	static KeyBinding diveBinding = new KeyBinding("MoCreatures Dive", Keyboard.KEY_Z, "key.categories.movement");
	static KeyBinding guiBinding = new KeyBinding("MoCreatures GUI", Keyboard.KEY_F13, "key.categories.misc"); //TODO fix bug that crashes game when invoking GUI

	//static KeyBinding dismountBinding = new KeyBinding("MoCreatures Dismount", Keyboard.KEY_F);

	public MoCKeyHandler() {
		//the first value is an array of KeyBindings, the second is whether or not the call
		//keyDown should repeat as long as the key is down
		//net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(jumpBinding);
		net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(diveBinding);
		net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(guiBinding);
	}

	@SubscribeEvent
	public void onInput(InputEvent event) {
		int keyPressed = (Mouse.getEventButton() + -100);
		if (keyPressed == -101) {
			keyPressed = Keyboard.getEventKey();
		}

		EntityPlayer ep = MoCClientProxy.mc.player;
		if (ep == null) {
			return;
		}
		if (FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().getChatOpen()) {
			return; // if chatting return
		}
		if (Keyboard.getEventKeyState() && ep.getRidingEntity() != null) {
			Keyboard.enableRepeatEvents(true); // allow holding down key. Fixes flying
		}

		// isKeyDown must be called with valid keys only. Mouse binds always use negative id's so we avoid them here.
		boolean kbJump = MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode() >= 0 ? Keyboard.isKeyDown(MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode()) : keyPressed == MoCClientProxy.mc.gameSettings.keyBindJump.getKeyCode();
		boolean kbDive = diveBinding.getKeyCode() >= 0 ? Keyboard.isKeyDown(diveBinding.getKeyCode()) : keyPressed == diveBinding.getKeyCode();
		//boolean kbDismount = kb.keyDescription.equals("MoCreatures Dismount");

		/**
		 * this avoids double jumping
		 */
		if (kbJump && ep != null && ep.getRidingEntity() != null && ep.getRidingEntity() instanceof IMoCEntity) {
			// keyCount = 0;
			// jump code needs to be executed client/server simultaneously to take
			((IMoCEntity) ep.getRidingEntity()).makeEntityJump();
			MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityJump());
		}

		if (kbDive && ep != null && ep.getRidingEntity() != null && ep.getRidingEntity() instanceof IMoCEntity) {
			//  keyCount = 0;
			// jump code needs to be executed client/server simultaneously to take
			((IMoCEntity) ep.getRidingEntity()).makeEntityDive();
			MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageEntityDive());
		}
	}
}

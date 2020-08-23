package com.ferreusveritas.mocreatures.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ferreusveritas.mocreatures.MoCPetData;
import com.ferreusveritas.mocreatures.MoCTools;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.entity.IMoCTameable;
import com.mojang.authlib.GameProfile;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandMoCreatures extends CommandBase {

	private static List<String> commands = new ArrayList<String>();
	private static List<String> aliases = new ArrayList<String>();
	private static List<String> tabCompletionStrings = new ArrayList<String>();

	static {
		commands.add("/moc attackdolphins <boolean>");
		commands.add("/moc attackhorses <boolean>");
		commands.add("/moc attackwolves <boolean>");
		commands.add("/moc canspawn <boolean>");
		commands.add("/moc caveogrechance <float>");
		commands.add("/moc caveogrestrength <float>");
		commands.add("/moc debug <boolean>");
		// TODO commands.add("/moc deletepets <playername>");
		commands.add("/moc destroydrops <boolean>");
		commands.add("/moc enablehunters <boolean>");
		commands.add("/moc easybreeding <boolean>");
		commands.add("/moc elephantbulldozer <boolean>");
		commands.add("/moc enableownership <boolean>");
		commands.add("/moc enableresetownerscroll <boolean>");
		commands.add("/moc fireogrechance <int>");
		commands.add("/moc fireogrestrength <float>");
		commands.add("/moc frequency <entity> <int>");
		commands.add("/moc golemdestroyblocks <boolean>");
		commands.add("/moc tamed");
		commands.add("/moc tamed <playername>");
		commands.add("/moc maxchunk <entity> <int>");
		commands.add("/moc maxspawn <entity> <int>");
		commands.add("/moc maxtamedperop <int>");
		commands.add("/moc maxtamedperplayer <int>");
		commands.add("/moc minspawn <entity> <int>");
		commands.add("/moc motherwyverneggdropchance <int>");
		commands.add("/moc ogreattackrange <int>");
		commands.add("/moc ogrestrength <float>");
		commands.add("/moc ostricheggdropchance <int>");
		commands.add("/moc rareitemdropchance <int>");
		commands.add("/moc spawnhorse <int>");
		commands.add("/moc spawnwyvern <int>");
		commands.add("/moc tamedcount <playername>");
		commands.add("/moc tp <petid> <playername>");
		commands.add("/moc <command> value");
		commands.add("/moc wyverneggdropchance <int>");
		commands.add("/moc zebrachance <int>");
		aliases.add("moc");
		tabCompletionStrings.add("attackdolphins");
		tabCompletionStrings.add("attackhorses");
		tabCompletionStrings.add("attackwolves");
		tabCompletionStrings.add("canspawn");
		tabCompletionStrings.add("caveogrechance");
		tabCompletionStrings.add("caveogrestrength");
		tabCompletionStrings.add("debug");
		// TODO tabCompletionStrings.add("deletepets");
		tabCompletionStrings.add("destroydrops");
		tabCompletionStrings.add("easybreeding");
		tabCompletionStrings.add("elephantbulldozer");
		tabCompletionStrings.add("enableownership");
		tabCompletionStrings.add("enableresetownerscroll");
		tabCompletionStrings.add("fireogrechance");
		tabCompletionStrings.add("fireogrestrength");
		tabCompletionStrings.add("forcedespawns");
		tabCompletionStrings.add("frequency");
		tabCompletionStrings.add("golemdestroyblocks");
		tabCompletionStrings.add("tamed");
		tabCompletionStrings.add("maxchunk");
		tabCompletionStrings.add("maxspawn");
		tabCompletionStrings.add("maxtamedperop");
		tabCompletionStrings.add("maxtamedperplayer");
		tabCompletionStrings.add("minspawn");
		tabCompletionStrings.add("motherwyverneggdropchance");
		tabCompletionStrings.add("ogreattackrange");
		tabCompletionStrings.add("ogreattackstrength");
		tabCompletionStrings.add("ostricheggdropchance");
		tabCompletionStrings.add("rareitemdropchance");
		tabCompletionStrings.add("spawnhorse");
		tabCompletionStrings.add("spawnwyvern");
		tabCompletionStrings.add("tamedcount");
		tabCompletionStrings.add("tp");
		tabCompletionStrings.add("wyverneggdropchance");
		tabCompletionStrings.add("zebrachance");
	}

	@Override
	public String getName() {
		return "mocreatures";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getUsage(ICommandSender par1ICommandSender) {
		return "commands.mocreatures.usage";
	}

	/**
	 * Adds the strings available in this command to the given list of tab
	 * completion options.
	 */
	public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return getListOfStringsMatchingLastWord(par2ArrayOfStr, (String[]) tabCompletionStrings.toArray(new String[tabCompletionStrings.size()]));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		String command = "";
		if (args.length == 0) {
			command = "help";
		} else {
			command = args[0];
		}
		String par2 = "";
		if (args.length > 1) {
			par2 = args[1];
		}
		/*String par3 = "";
		if (args.length == 3) {
			par3 = args[2];
		}*/

		if (command.equalsIgnoreCase("tamed") || command.equalsIgnoreCase("tame")) {
			if (args.length == 2 && !Character.isDigit(args[1].charAt(0))) {
				int unloadedCount = 0;
				int loadedCount = 0;
				ArrayList<Integer> foundIds = new ArrayList<Integer>();
				ArrayList<String> tamedlist = new ArrayList<String>();
				String playername = par2;
				GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playername);
				if (profile == null) {
					return;
				}
				// search for tamed entity
				for (int dimension : DimensionManager.getIDs()) {
					WorldServer world = DimensionManager.getWorld(dimension);
					for (int j = 0; j < world.loadedEntityList.size(); j++) {
						Entity entity = (Entity) world.loadedEntityList.get(j);
						if (IMoCTameable.class.isAssignableFrom(entity.getClass())) {
							IMoCTameable mocreature = (IMoCTameable) entity;
							if (mocreature.getOwnerId().equals(profile.getId())) {
								loadedCount++;
								foundIds.add(mocreature.getOwnerPetId());
								tamedlist.add(TextFormatting.WHITE + "Found pet with " + TextFormatting.DARK_AQUA + "Type"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN
										+ ((EntityLiving) mocreature).getName() + TextFormatting.DARK_AQUA + ", Name"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + mocreature.getPetName()
										+ TextFormatting.DARK_AQUA + ", Owner" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
										+ profile.getName() + TextFormatting.DARK_AQUA + ", PetId" + TextFormatting.WHITE + ":"
										+ TextFormatting.GREEN + mocreature.getOwnerPetId() + TextFormatting.DARK_AQUA + ", Dimension"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + entity.dimension + TextFormatting.DARK_AQUA
										+ ", Pos" + TextFormatting.WHITE + ":" + TextFormatting.LIGHT_PURPLE + Math.round(entity.posX)
										+ TextFormatting.WHITE + ", " + TextFormatting.LIGHT_PURPLE + Math.round(entity.posY)
										+ TextFormatting.WHITE + ", " + TextFormatting.LIGHT_PURPLE + Math.round(entity.posZ));
							}
						}
					}
				}
				MoCPetData ownerPetData = MoCreatures.instance.mapData.getPetData(profile.getId());
				if (ownerPetData != null) {
					for (int i = 0; i < ownerPetData.getTamedList().tagCount(); i++) {
						NBTTagCompound nbt = ownerPetData.getTamedList().getCompoundTagAt(i);
						if (nbt.hasKey("PetId") && !foundIds.contains(nbt.getInteger("PetId"))) {
							unloadedCount++;
							double posX = nbt.getTagList("Pos", 6).getDoubleAt(0);
							double posY = nbt.getTagList("Pos", 6).getDoubleAt(1);
							double posZ = nbt.getTagList("Pos", 6).getDoubleAt(2);
							tamedlist.add(TextFormatting.WHITE + "Found unloaded pet with " + TextFormatting.DARK_AQUA + "Type"
									+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + nbt.getString("EntityName")
									+ TextFormatting.DARK_AQUA + ", Name" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
									+ nbt.getString("Name") + TextFormatting.DARK_AQUA + ", Owner" + TextFormatting.WHITE + ":"
									+ TextFormatting.GREEN + nbt.getString("Owner") + TextFormatting.DARK_AQUA + ", PetId"
									+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + nbt.getInteger("PetId")
									+ TextFormatting.DARK_AQUA + ", Dimension" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
									+ nbt.getInteger("Dimension") + TextFormatting.DARK_AQUA + ", Pos" + TextFormatting.WHITE + ":"
									+ TextFormatting.LIGHT_PURPLE + Math.round(posX) + TextFormatting.WHITE + ", "
									+ TextFormatting.LIGHT_PURPLE + Math.round(posY) + TextFormatting.WHITE + ", "
									+ TextFormatting.LIGHT_PURPLE + Math.round(posZ));
						}
					}
				}
				if (tamedlist.size() > 0) {
					sendPageHelp(sender, (byte) 10, tamedlist, args, "Listing tamed pets");
					sender.sendMessage(new TextComponentTranslation("Loaded tamed count : " + TextFormatting.AQUA + loadedCount
							+ TextFormatting.WHITE + ", Unloaded count : " + TextFormatting.AQUA + unloadedCount + TextFormatting.WHITE
							+ ", Total count : " + TextFormatting.AQUA + (ownerPetData != null ? ownerPetData.getTamedList().tagCount() : 0)));
				} else {
					sender.sendMessage(new TextComponentTranslation("Player " + TextFormatting.GREEN + playername
							+ TextFormatting.WHITE + " does not have any tamed animals."));
				}
			} else if (command.equalsIgnoreCase("tamed") || command.equalsIgnoreCase("tame") && !par2.equals("")) {
				int unloadedCount = 0;
				int loadedCount = 0;
				ArrayList<Integer> foundIds = new ArrayList<Integer>();
				ArrayList<String> tamedlist = new ArrayList<String>();
				// search for mocreature tamed entities
				for (int dimension : DimensionManager.getIDs()) {
					WorldServer world = DimensionManager.getWorld(dimension);
					for (int j = 0; j < world.loadedEntityList.size(); j++) {
						Entity entity = (Entity) world.loadedEntityList.get(j);
						if (IMoCTameable.class.isAssignableFrom(entity.getClass())) {
							IMoCTameable mocreature = (IMoCTameable) entity;
							if (mocreature.getOwnerPetId() != -1) {
								loadedCount++;
								foundIds.add(mocreature.getOwnerPetId());
								tamedlist.add(TextFormatting.WHITE + "Found pet with " + TextFormatting.DARK_AQUA + "Type"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN
										+ ((EntityLiving) mocreature).getName() + TextFormatting.DARK_AQUA + ", Name"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + mocreature.getPetName()
										+ TextFormatting.DARK_AQUA + ", Owner" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
										+ mocreature.getOwnerId() + TextFormatting.DARK_AQUA + ", PetId" + TextFormatting.WHITE + ":"
										+ TextFormatting.GREEN + mocreature.getOwnerPetId() + TextFormatting.DARK_AQUA + ", Dimension"
										+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + entity.dimension + TextFormatting.DARK_AQUA
										+ ", Pos" + TextFormatting.WHITE + ":" + TextFormatting.LIGHT_PURPLE + Math.round(entity.posX)
										+ TextFormatting.WHITE + ", " + TextFormatting.LIGHT_PURPLE + Math.round(entity.posY)
										+ TextFormatting.WHITE + ", " + TextFormatting.LIGHT_PURPLE + Math.round(entity.posZ));
							}
						}
					}
				}
				// if (!MoCreatures.isServer())
				// {
				for (MoCPetData ownerPetData : MoCreatures.instance.mapData.getPetMap().values()) {
					for (int i = 0; i < ownerPetData.getTamedList().tagCount(); i++) {
						NBTTagCompound nbt = ownerPetData.getTamedList().getCompoundTagAt(i);
						if (nbt.hasKey("PetId") && !foundIds.contains(nbt.getInteger("PetId"))) {
							unloadedCount++;
							double posX = nbt.getTagList("Pos", 10).getDoubleAt(0);
							double posY = nbt.getTagList("Pos", 10).getDoubleAt(1);
							double posZ = nbt.getTagList("Pos", 10).getDoubleAt(2);
							tamedlist.add(TextFormatting.WHITE + "Found unloaded pet with " + TextFormatting.DARK_AQUA + "Type"
									+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + nbt.getString("EntityName")
									+ TextFormatting.DARK_AQUA + ", Name" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
									+ nbt.getString("Name") + TextFormatting.DARK_AQUA + ", Owner" + TextFormatting.WHITE + ":"
									+ TextFormatting.GREEN + nbt.getString("Owner") + TextFormatting.DARK_AQUA + ", PetId"
									+ TextFormatting.WHITE + ":" + TextFormatting.GREEN + nbt.getInteger("PetId")
									+ TextFormatting.DARK_AQUA + ", Dimension" + TextFormatting.WHITE + ":" + TextFormatting.GREEN
									+ nbt.getInteger("Dimension") + TextFormatting.DARK_AQUA + ", Pos" + TextFormatting.WHITE + ":"
									+ TextFormatting.LIGHT_PURPLE + Math.round(posX) + TextFormatting.WHITE + ", "
									+ TextFormatting.LIGHT_PURPLE + Math.round(posY) + TextFormatting.WHITE + ", "
									+ TextFormatting.LIGHT_PURPLE + Math.round(posZ));
						}
					}
				}
				//}
				sendPageHelp(sender, (byte) 10, tamedlist, args, "Listing tamed pets");
				sender.sendMessage(new TextComponentTranslation("Loaded tamed count : "
						+ TextFormatting.AQUA
						+ loadedCount
						+ TextFormatting.WHITE
						+ (!MoCreatures.isServer() ? ", Unloaded Count : " + TextFormatting.AQUA + unloadedCount + TextFormatting.WHITE
								+ ", Total count : " + TextFormatting.AQUA + (loadedCount + unloadedCount) : "")));
			}
		} else if (command.equalsIgnoreCase("tamedcount")) {
			String playername = par2;
			List<EntityPlayerMP> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP player = (EntityPlayerMP) players.get(i);
				if (player.getName().equalsIgnoreCase(playername)) {
					int tamedCount = MoCTools.numberTamedByPlayer(player);
					sender.sendMessage(new TextComponentTranslation(TextFormatting.GREEN + playername
							+ "'s recorded tamed count is " + TextFormatting.AQUA + tamedCount));
				}
			}
			sender.sendMessage(new TextComponentTranslation(TextFormatting.RED + "Could not find player "
					+ TextFormatting.GREEN + playername + TextFormatting.RED
					+ ". Please verify the player is online and/or name was entered correctly."));
		}

		// START HELP COMMAND
		if (command.equalsIgnoreCase("help")) {
			List<String> list = this.getSortedPossibleCommands(sender);
			byte b0 = 10;
			int i = (list.size() - 1) / b0;
			int j = 0;

			if (args.length > 1) {
				try {
					j = args.length == 0 ? 0 : parseInt(args[1], 1, i + 1) - 1;
				} catch (NumberInvalidException numberinvalidexception) {
					numberinvalidexception.printStackTrace();
				}
			}

			int k = Math.min((j + 1) * b0, list.size());
			sender.sendMessage(new TextComponentTranslation(TextFormatting.DARK_GREEN + "--- Showing MoCreatures help page "
					+ Integer.valueOf(j + 1) + " of " + Integer.valueOf(i + 1) + "(/moc help <page>)---"));

			for (int l = j * b0; l < k; ++l) {
				String commandToSend = list.get(l);
				sender.sendMessage(new TextComponentTranslation(commandToSend));
			}
		}
		// END HELP COMMAND
	}

	/**
	 * Returns a sorted list of all possible commands for the given
	 * ICommandSender.
	 */
	protected List<String> getSortedPossibleCommands(ICommandSender par1ICommandSender) {
		Collections.sort(CommandMoCreatures.commands);
		return CommandMoCreatures.commands;
	}

	public boolean teleportLoadedPet(WorldServer world, EntityPlayerMP player, int petId, String petName, ICommandSender par1ICommandSender) {
		for (int j = 0; j < world.loadedEntityList.size(); j++) {
			Entity entity = (Entity) world.loadedEntityList.get(j);
			// search for entities that are MoCEntityAnimal's
			if (IMoCTameable.class.isAssignableFrom(entity.getClass()) && !((IMoCTameable) entity).getPetName().equals("")
					&& ((IMoCTameable) entity).getOwnerPetId() == petId) {
				// grab the entity data
				NBTTagCompound compound = new NBTTagCompound();
				entity.writeToNBT(compound);
				if (compound != null && compound.getString("Owner") != null) {
					String owner = compound.getString("Owner");
					String name = compound.getString("Name");
					if (owner != null && owner.equalsIgnoreCase(player.getName())) {
						// check if in same dimension
						if (entity.dimension == player.dimension) {
							entity.setPosition(player.posX, player.posY, player.posZ);
						} else if (!player.world.isRemote)// transfer entity to player dimension
						{
							Entity newEntity = EntityList.newEntity(entity.getClass(), player.world);
							if (newEntity != null) {
								MoCTools.copyDataFromOld(newEntity, entity); // transfer all existing data to our new entity
								newEntity.setPosition(player.posX, player.posY, player.posZ);
								DimensionManager.getWorld(player.dimension).spawnEntity(newEntity);
							}
							if (entity.getRidingEntity() == null) {
								entity.isDead = true;
							} else // dismount players
							{
								entity.getRidingEntity().dismountRidingEntity();
								entity.isDead = true;
							}
							world.resetUpdateEntityTick();
							DimensionManager.getWorld(player.dimension).resetUpdateEntityTick();
						}
						par1ICommandSender.sendMessage(new TextComponentTranslation(TextFormatting.GREEN + name + TextFormatting.WHITE
								+ " has been tp'd to location " + Math.round(player.posX) + ", " + Math.round(player.posY) + ", "
								+ Math.round(player.posZ) + " in dimension " + player.dimension));
						return true;
					}
				}
			}
		}
		return false;
	}

	public void sendCommandHelp(ICommandSender sender) {
		sender.sendMessage(new TextComponentTranslation("\u00a72Listing MoCreatures commands"));
		for (int i = 0; i < commands.size(); i++) {
			sender.sendMessage(new TextComponentTranslation(commands.get(i)));
		}
	}

	public void sendPageHelp(ICommandSender sender, byte pagelimit, ArrayList<String> list, String[] par2ArrayOfStr, String title) {
		int x = (list.size() - 1) / pagelimit;
		int j = 0;

		if (Character.isDigit(par2ArrayOfStr[par2ArrayOfStr.length - 1].charAt(0))) {
			try {
				j = par2ArrayOfStr.length == 0 ? 0 : parseInt(par2ArrayOfStr[par2ArrayOfStr.length - 1], 1, x + 1) - 1;
			} catch (NumberInvalidException numberinvalidexception) {
				numberinvalidexception.printStackTrace();
			}
		}
		int k = Math.min((j + 1) * pagelimit, list.size());

		sender.sendMessage(new TextComponentTranslation(TextFormatting.WHITE + title + " (pg " + TextFormatting.WHITE
				+ Integer.valueOf(j + 1) + TextFormatting.DARK_GREEN + "/" + TextFormatting.WHITE + Integer.valueOf(x + 1) + ")"));

		for (int l = j * pagelimit; l < k; ++l) {
			String tamedInfo = list.get(l);
			sender.sendMessage(new TextComponentTranslation(tamedInfo));
		}
	}
}

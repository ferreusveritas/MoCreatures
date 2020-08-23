package com.ferreusveritas.mocreatures.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ferreusveritas.mocreatures.MoCConstants;
import com.ferreusveritas.mocreatures.MoCreatures;
import com.ferreusveritas.mocreatures.item.MoCItem;
import com.ferreusveritas.mocreatures.item.MoCItemArmor;
import com.ferreusveritas.mocreatures.item.MoCItemEgg;
import com.ferreusveritas.mocreatures.item.MoCItemPetAmulet;
import com.ferreusveritas.mocreatures.item.MoCItemWeapon;
import com.ferreusveritas.mocreatures.item.MoCItemWhip;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class MoCItems {

	public static final Set<Item> ITEMS = new HashSet<>();

	static ArmorMaterial scorpARMOR = EnumHelper.addArmorMaterial("crocARMOR", "crocARMOR", 15, new int[] {2, 6, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial furARMOR = EnumHelper.addArmorMaterial("furARMOR", "furARMOR", 15, new int[] {2, 6, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial hideARMOR = EnumHelper.addArmorMaterial("hideARMOR", "hideARMOR", 15, new int[] {2, 6, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial scorpdARMOR = EnumHelper.addArmorMaterial("scorpdARMOR", "scorpdARMOR", 15, new int[] {2, 6, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial scorpfARMOR = EnumHelper.addArmorMaterial("scorpfARMOR", "scorpdARMOR", 18, new int[] {2, 7, 6, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial scorpnARMOR = EnumHelper.addArmorMaterial("scorpnARMOR", "scorpdARMOR", 20, new int[] {3, 7, 6, 3}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial scorpcARMOR = EnumHelper.addArmorMaterial("scorpcARMOR", "scorpdARMOR", 15, new int[] {2, 6, 5, 2}, 12, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	static ArmorMaterial silverARMOR = EnumHelper.addArmorMaterial("silverARMOR", "scorpdARMOR", 15, new int[] {2, 6, 5, 2}, 15, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);

	public static final MoCItem sharkteeth = new MoCItem("sharkteeth");
	public static final MoCItem mocegg = new MoCItemEgg("mocegg");
	public static final MoCItem bigcatclaw = new MoCItem("bigcatclaw");
	public static final MoCItem whip = new MoCItemWhip("whip");

	public static final MoCItem medallion = new MoCItem("medallion");

	public static final MoCItem hideCroc = new MoCItem("reptilehide");
	public static final MoCItem fur = new MoCItem("fur");

	public static final MoCItem essencedarkness = new MoCItem("essencedarkness");
	public static final MoCItem essencefire = new MoCItem("essencefire");
	public static final MoCItem essenceundead = new MoCItem("essenceundead");
	public static final MoCItem essencelight = new MoCItem("essencelight");

	public static final MoCItem fishnet = new MoCItemPetAmulet("fishnet");
	public static final MoCItem fishnetfull = new MoCItemPetAmulet("fishnetfull");
	public static final MoCItem petamulet = new MoCItemPetAmulet("petamulet", 1);
	public static final MoCItem petamuletfull = new MoCItemPetAmulet("petamuletfull", 1);

	public static final MoCItem heartdarkness = new MoCItem("heartdarkness");
	public static final MoCItem heartfire = new MoCItem("heartfire");
	public static final MoCItem heartundead = new MoCItem("heartundead");

	public static final MoCItem animalHide = new MoCItem("hide");
	public static final MoCItem chitinCave = new MoCItem("chitinblack");
	public static final MoCItem chitinFrost = new MoCItem("chitinfrost");
	public static final MoCItem chitinNether = new MoCItem("chitinnether");
	public static final MoCItem chitin = new MoCItem("chitin");

	// Weapons
	public static final MoCItemWeapon scorpStingCave = new MoCItemWeapon("scorpstingcave", ToolMaterial.GOLD, 4, true);
	public static final MoCItemWeapon scorpStingFrost = new MoCItemWeapon("scorpstingfrost", ToolMaterial.GOLD, 2, true);
	public static final MoCItemWeapon scorpStingNether = new MoCItemWeapon("scorpstingnether", ToolMaterial.GOLD, 3, true);
	public static final MoCItemWeapon scorpStingDirt = new MoCItemWeapon("scorpstingdirt", ToolMaterial.GOLD, 1, true);

	public static final MoCItemWeapon tusksWood = new MoCItemWeapon("tuskswood", ToolMaterial.WOOD);
	public static final MoCItemWeapon tusksIron = new MoCItemWeapon("tusksiron", ToolMaterial.IRON);
	public static final MoCItemWeapon tusksDiamond = new MoCItemWeapon("tusksdiamond", ToolMaterial.DIAMOND);

	// Armors
	public static final MoCItemArmor plateCroc = new MoCItemArmor("reptileplate", scorpARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor helmetCroc = new MoCItemArmor("reptilehelmet", scorpARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor legsCroc = new MoCItemArmor("reptilelegs", scorpARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor bootsCroc = new MoCItemArmor("reptileboots", scorpARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor chestFur = new MoCItemArmor("furchest", furARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor helmetFur = new MoCItemArmor("furhelmet", furARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor legsFur = new MoCItemArmor("furlegs", furARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor bootsFur = new MoCItemArmor("furboots", furARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor chestHide = new MoCItemArmor("hidechest", hideARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor helmetHide = new MoCItemArmor("hidehelmet", hideARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor legsHide = new MoCItemArmor("hidelegs", hideARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor bootsHide = new MoCItemArmor("hideboots", hideARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor scorpPlateDirt = new MoCItemArmor("scorpplatedirt", scorpARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor scorpHelmetDirt = new MoCItemArmor("scorphelmetdirt", scorpARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor scorpLegsDirt = new MoCItemArmor("scorplegsdirt", scorpARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor scorpBootsDirt = new MoCItemArmor("scorpbootsdirt", scorpARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor scorpPlateFrost = new MoCItemArmor("scorpplatefrost", scorpARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor scorpHelmetFrost = new MoCItemArmor("scorphelmetfrost", scorpARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor scorpLegsFrost = new MoCItemArmor("scorplegsfrost", scorpARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor scorpBootsFrost = new MoCItemArmor("scorpbootsfrost", scorpARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor scorpPlateCave = new MoCItemArmor("scorpplatecave", scorpARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor scorpHelmetCave = new MoCItemArmor("scorphelmetcave", scorpARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor scorpLegsCave = new MoCItemArmor("scorplegscave", scorpARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor scorpBootsCave = new MoCItemArmor("scorpbootscave", scorpARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItemArmor scorpPlateNether = new MoCItemArmor("scorpplatenether", scorpARMOR, 4, EntityEquipmentSlot.CHEST);
	public static final MoCItemArmor scorpHelmetNether = new MoCItemArmor("scorphelmetnether", scorpARMOR, 4, EntityEquipmentSlot.HEAD);
	public static final MoCItemArmor scorpLegsNether = new MoCItemArmor("scorplegsnether", scorpARMOR, 4, EntityEquipmentSlot.LEGS);
	public static final MoCItemArmor scorpBootsNether = new MoCItemArmor("scorpbootsnether", scorpARMOR, 4, EntityEquipmentSlot.FEET);

	public static final MoCItem elephantHarness = new MoCItem("elephantharness");
	public static final MoCItem elephantChest = new MoCItem("elephantchest");
	public static final MoCItem elephantGarment = new MoCItem("elephantgarment");
	public static final MoCItem elephantHowdah = new MoCItem("elephanthowdah");
	public static final MoCItem mammothPlatform = new MoCItem("mammothplatform");

	public static final MoCItem scrollFreedom = new MoCItem("scrolloffreedom");
	public static final MoCItem scrollOfSale = new MoCItem("scrollofsale");
	public static final MoCItem scrollOfOwner = new MoCItem("scrollofowner");

	@Mod.EventBusSubscriber(modid = MoCConstants.MOD_ID)
	public static class RegistrationHandler {
		/**
		 * Register this mod's {@link Item}s.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event) {


			List<Item> items = new ArrayList<Item>(Arrays.asList(new Item[] {
					sharkteeth,
					mocegg,
					bigcatclaw,
					whip,
					medallion,
					hideCroc,
					plateCroc,
					helmetCroc,
					legsCroc,
					bootsCroc,
					fur,

					essencedarkness,
					essencefire,
					essenceundead,
					essencelight,

					fishnet,
					fishnetfull,
					petamulet,
					petamuletfull,

					chestFur,
					helmetFur,
					legsFur,
					bootsFur,

					heartdarkness,
					heartfire,
					heartundead,

					animalHide,
					chestHide,
					helmetHide,
					legsHide,
					bootsHide,

					chitinCave,
					chitinFrost,
					chitinNether,
					chitin,

					scorpPlateDirt,
					scorpHelmetDirt,
					scorpLegsDirt,
					scorpBootsDirt,
					scorpPlateFrost,
					scorpHelmetFrost,
					scorpLegsFrost,
					scorpBootsFrost,
					scorpPlateNether,
					scorpHelmetNether,
					scorpLegsNether,
					scorpBootsNether,
					scorpHelmetCave,
					scorpPlateCave,
					scorpLegsCave,
					scorpBootsCave,

					scorpStingDirt,
					scorpStingFrost,
					scorpStingCave,
					scorpStingNether,

					tusksWood,
					tusksIron,
					tusksDiamond,
					elephantChest,
					elephantGarment,
					elephantHarness,
					elephantHowdah,
					mammothPlatform,

					scrollFreedom,
					scrollOfSale,
					scrollOfOwner
			}));

			final IForgeRegistry<Item> registry = event.getRegistry();

			for (final Item item : items) {
				registry.register(item);
				ITEMS.add(item);
				if (!MoCreatures.isServer()) {
					ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MoCConstants.MOD_PREFIX + item.getUnlocalizedName().replace("item.",  ""), "inventory"));
				}
				if (item instanceof MoCItemEgg) {
					for (int i = 0; i < 91; i++) {
						if (!MoCreatures.isServer()) {
							ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(MoCConstants.MOD_PREFIX + "mocegg", "inventory"));
						}
					}
				}
			}
		}
	}
}

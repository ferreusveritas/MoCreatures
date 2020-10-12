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

	public static final MoCItem hideCroc = new MoCItem("reptilehide");
	public static final MoCItem fur = new MoCItem("fur");

	public static final MoCItem fishnet = new MoCItemPetAmulet("fishnet");
	public static final MoCItem fishnetfull = new MoCItemPetAmulet("fishnetfull");
	public static final MoCItem petamulet = new MoCItemPetAmulet("petamulet", 1);
	public static final MoCItem petamuletfull = new MoCItemPetAmulet("petamuletfull", 1);

	public static final MoCItem animalHide = new MoCItem("hide");

	// Weapons

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

	public static final MoCItem elephantHarness = new MoCItem("elephantharness");
	public static final MoCItem elephantChest = new MoCItem("elephantchest");
	public static final MoCItem elephantGarment = new MoCItem("elephantgarment");
	public static final MoCItem elephantHowdah = new MoCItem("elephanthowdah");
	public static final MoCItem mammothPlatform = new MoCItem("mammothplatform");
	
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
					hideCroc,
					plateCroc,
					helmetCroc,
					legsCroc,
					bootsCroc,
					fur,

					fishnet,
					fishnetfull,
					petamulet,
					petamuletfull,

					chestFur,
					helmetFur,
					legsFur,
					bootsFur,

					animalHide,
					chestHide,
					helmetHide,
					legsHide,
					bootsHide,

					tusksWood,
					tusksIron,
					tusksDiamond,
					elephantChest,
					elephantGarment,
					elephantHarness,
					elephantHowdah,
					mammothPlatform

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

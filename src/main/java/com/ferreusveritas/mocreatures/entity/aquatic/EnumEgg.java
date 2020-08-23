package com.ferreusveritas.mocreatures.entity.aquatic;

import java.util.HashMap;
import java.util.Map;


public enum EnumEgg {

	None(0),
	
	Shark(11),
	
	//Snakes
	DarkSnake(21),
	SpottedSnake(22),
	OrangeSnake(23),
	GreenSnake(24),
	CoralSnake(25),
	Cobra(26),
	RattleSnake(27),
	Python(28),
	
	OstrichWild(30),
	OstrichStolen(31),
	OstrichNether(32),
	
	Komodo(33),
	
	DirtScorpion(41),
	CaveScorpion(42),
	NetherScorpion(43),
	FrostScorpion(44),
	UndeadScorpion(45),
	
	WyvernJungle(50),
	WyvernSwamp(51),
	WyvernSavanna(52),
	WyvernSand(53),
	WyvernMother(54),
	WyvernUndead(55),
	WyvernLight(56),
	WyvernDark(57),
	WyvernArctic(58),
	WyvernCave(59),
	WyvernMountain(60),
	WyvernSea(61),
	
	ManticoreCommon(62),
	ManticoreDark(63),
	ManticoreBlue(64),
	ManticoreGreen(65),
	
	//Medium Fish
	Salmon(70),
	Cod(71),
	Bass(72),
	
	//Small Fish
	Anchovy(80),
	AngelFish(81),
	Angler(82),
	ClownFish(83),
	GoldFish(84),
	HippoTang(85),
	Manderin(86),
	
	Piranha(90);
	
	public final int eggNum;
	
	private EnumEgg(int eggNum) {
		this.eggNum = eggNum;
	}
	
	private static Map<Integer, EnumEgg> map = null;
	
	public static EnumEgg eggNumToEggType(int eggNum) {
		if(map == null) {
			map = new HashMap<>();
			for(EnumEgg e : EnumEgg.values()) {
				map.put(e.eggNum, e);
			}
		}
		return map.getOrDefault(eggNum, None);
	}
	
}

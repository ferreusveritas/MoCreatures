package com.ferreusveritas.mocreatures.entity;

public enum Gender {
	None,
	Male,
	Female;
	
	public static Gender fromByte(int byt) {
		switch(byt) {
			default: return None;
			case 1: return Male;
			case 2: return Female;
		}
	}
	
	public byte toByte() {
		return (byte) ordinal();
	}
	
}
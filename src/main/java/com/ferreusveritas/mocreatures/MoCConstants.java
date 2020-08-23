package com.ferreusveritas.mocreatures;

public class MoCConstants {
    public static final String MOD_ID = "mocreatures";
    public static final String MOD_PREFIX = MOD_ID + ":";
    public static final String MOD_NAME = "FerreusVeritas's Mo'Creatures";
    public static final String MOD_VERSION = "1.0.0";
    
	public static final String OPTAFTER = "after:";
	public static final String OPTBEFORE = "before:";
	public static final String REQAFTER = "required-after:";
	public static final String REQBEFORE = "required-before:";
	public static final String NEXT = ";";
	public static final String AT = "@[";
	public static final String GREATERTHAN = "@(";
	public static final String ORGREATER = ",)";
    
	public static final String DEPENDENCIES
	= REQAFTER + com.ferreusveritas.stargarden.ModConstants.MODID
	+ NEXT
	+ REQAFTER + biomesoplenty.core.BiomesOPlenty.MOD_ID
	;
}

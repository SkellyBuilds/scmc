package com.skellybuilds.SCMC.config;

import com.skellybuilds.SCMC.config.option.*;
import java.util.HashSet;


public class ModMenuConfig {
	// Move default comments to langs?
	//public static final BooleanConfigOption ALLOWSERVERMODMENUCONNECTION = new BooleanConfigOption("allowservermodmenudownloads", true);
	public static final BooleanConfigOption USEONLYCLIENTMODS = new BooleanConfigOption("useonlyuniversalmods", true, "Universal mods basically are both client side & server side mods. So one for both! \n  If you leave this enabled, server mods will not be used unless you specified otherwise");
	public static final BooleanConfigOption CANKICKPLAYERSWITHNOMODS = new BooleanConfigOption("cankickplayerswithnomods", true, "This gives you the choice to allow the mod to kick players if they are missing a mod or otherwise \n  If you choose false, it won't kick the player but notify the player about the missing mods so they can be aware!");
	public static final StringSetConfigOption USETHISMODSONLY = new StringSetConfigOption("usethismodsonly", new HashSet<>(), "This is an array that will only use the mod ids provided for the mod, nothing else. \n  If you leave it empty, All mods will be used by SCMC unless you configurated it otherwise");
	public static final StringSetConfigOption DONTUSETHISMODS = new StringSetConfigOption("dontusethismods", new HashSet<>(), "This will basically blacklist a mod from being registered in SCMC, if you have it usethismods, the blacklist won't be checked since the whitelist is always checked first. ");
	public static final StringSetConfigOption USETHISMODS = new StringSetConfigOption("usethismods", new HashSet<>(), "This is basically an exception, which ignores everything and registers anyways, sort of like a whitelist");
	public  static final StringSetConfigOption OPTIONALMODS = new StringSetConfigOption("optionalmods", new HashSet<>(), "This list of mods won't be required to download, meaning they won't show up in the missing mod screen and there will be a GUI difference for the mod to clarify via servermodmenu");
}

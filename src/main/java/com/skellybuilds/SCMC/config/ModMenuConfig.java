package com.skellybuilds.SCMC.config;

import com.skellybuilds.SCMC.config.option.*;
import java.util.HashSet;


public class ModMenuConfig {
	//public static final BooleanConfigOption ALLOWSERVERMODMENUCONNECTION = new BooleanConfigOption("allowservermodmenudownloads", true);
	public static final BooleanConfigOption USEONLYCLIENTMODS = new BooleanConfigOption("useonlyuniversalmods", true);
	public static final BooleanConfigOption CANKICKPLAYERSWITHNOMODS = new BooleanConfigOption("cankickplayerswithnomods", true);
	public static final StringSetConfigOption USETHISMODS = new StringSetConfigOption("usethismods", new HashSet<>());
	public static final StringSetConfigOption DONTUSETHISMODS = new StringSetConfigOption("dontusethismods", new HashSet<>());
}

package com.skellybuilds.SCMC.config;

import com.skellybuilds.SCMC.config.option.*;
import net.minecraft.text.Text;
import java.util.HashSet;


public class ModMenuConfig {
	public static final BooleanConfigOption USEONLYCLIENTMODS = new BooleanConfigOption("useonlyuniversalmods", true, Text.translatable("scmc.config.desc.useonlyuniversalmods").getString());
	public static final BooleanConfigOption CANKICKPLAYERSWITHNOMODS = new BooleanConfigOption("cankickplayerswithnomods", true, Text.translatable("scmc.config.desc.cankickplayerswithnomods").getString());
	public static final BooleanConfigOption ENABLESCMCCRASHES = new BooleanConfigOption("enablescmccrashes", true, Text.translatable("scmc.config.desc.enablescmccrashes").getString());
	public static final StringSetConfigOption USETHISMODSONLY = new StringSetConfigOption("usethismodsonly", new HashSet<>(), Text.translatable("scmc.config.desc.usethismodsonly").getString());
	public static final StringSetConfigOption DONTUSETHISMODS = new StringSetConfigOption("dontusethismods", new HashSet<>(), Text.translatable("scmc.config.desc.dontusethismods").getString());
	public static final StringSetConfigOption USETHISMODS = new StringSetConfigOption("usethismods", new HashSet<>(), Text.translatable("scmc.config.desc.useonlyuniversalmods").getString());
	public static final StringSetConfigOption OPTIONALMODS = new StringSetConfigOption("optionalmods", new HashSet<>(), Text.translatable("scmc.config.desc.optionalmods").getString());
	public static final IntConfigOption CPORT = new IntConfigOption("port", 27752, Text.translatable("scmc.config.desc.port").getString());
	public static final BooleanConfigOption CANTRANSLATEMTEXTS = new BooleanConfigOption("canreadmodtranslations", true, Text.translatable("scmc.config.desc.canreadmodtranslations").getString());
	public static final BooleanConfigOption TRANSLATEANYWAYS = new BooleanConfigOption("translatebydefault", false, Text.translatable("scmc.config.desc.translatebydefault").getString());
}

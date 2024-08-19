package com.skellybuilds.SCMC.config.option;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigOptionStorage {
	private static final Map<String, Boolean> BOOLEAN_OPTIONS = new HashMap<>();
	private static final Map<String, Set<String>> STRING_SET_OPTIONS = new HashMap<>();
	private static final Map<String, String> COMMENT_DATA = new HashMap<>(); // Key, Comment

	public static void setStringSet(String key, Set<String> value) {
		STRING_SET_OPTIONS.put(key, value);
	}

	public static void setComment(String key, String comment) { COMMENT_DATA.put(key, comment); }

	public static String getComment(String key){return COMMENT_DATA.get(key); }

	public static Set<String> getStringSet(String key) {
		return STRING_SET_OPTIONS.get(key);
	}

	public static void setBoolean(String key, boolean value) {
		BOOLEAN_OPTIONS.put(key, value);
	}

	public static void toggleBoolean(String key) {
		setBoolean(key, !getBoolean(key));
	}

	public static boolean getBoolean(String key) {
		return BOOLEAN_OPTIONS.get(key);
	}
}

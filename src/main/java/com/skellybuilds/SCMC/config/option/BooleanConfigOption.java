package com.skellybuilds.SCMC.config.option;

public class BooleanConfigOption {
	private final String key;
	private final boolean defaultValue;
	private final String defaultComment;

	public BooleanConfigOption(String key, boolean defaultValue, String enabledKey, String disabledKey, String defaultComment) {
		ConfigOptionStorage.setBoolean(key, defaultValue);
		this.key = key;
		this.defaultValue = defaultValue;
		this.defaultComment = defaultComment;
		ConfigOptionStorage.setComment(key, defaultComment);
	}

	public BooleanConfigOption(String key, boolean defaultValue) {
		this(key, defaultValue, "true", "false", "");
	}
	public BooleanConfigOption(String key, boolean defaultValue, String defaultComment) {
		this(key, defaultValue, "true", "false", defaultComment);
	}

	public String getKey() {
		return key;
	}

	public boolean getValue() {
		return ConfigOptionStorage.getBoolean(key);
	}

	public String getComment() {return ConfigOptionStorage.getComment(key); } // I don't know when i'll will use this but maybe some day it will be useful for experimenting

	public void setComment(String comment){ConfigOptionStorage.setComment(key, comment);}

	public void setValue(boolean value) {
		ConfigOptionStorage.setBoolean(key, value);
	}

	public void toggleValue() {
		ConfigOptionStorage.toggleBoolean(key);
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public String getDefaultComment(){ return defaultComment; }
}

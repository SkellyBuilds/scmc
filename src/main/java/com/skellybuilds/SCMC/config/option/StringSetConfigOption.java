package com.skellybuilds.SCMC.config.option;
import java.util.Set;

public class StringSetConfigOption {
	private final String key;
	private final Set<String> defaultValue;
	private final String defaultComment;

	public StringSetConfigOption(String key, Set<String> defaultValue, String defaultComment) {
		super();
		ConfigOptionStorage.setStringSet(key, defaultValue);
		this.key = key;
		this.defaultValue = defaultValue;
		this.defaultComment = defaultComment;
		ConfigOptionStorage.setComment(key, defaultComment);
	}

	public StringSetConfigOption(String key, Set<String> defaultValue) {
		super();
		ConfigOptionStorage.setStringSet(key, defaultValue);
		this.key = key;
		this.defaultValue = defaultValue;
		this.defaultComment = "";
		ConfigOptionStorage.setComment(key, defaultComment);
	}

	public String getKey() {
		return key;
	}

	public Set<String> getValue() {
		return ConfigOptionStorage.getStringSet(key);
	}

	public void setValue(Set<String> value) {
		ConfigOptionStorage.setStringSet(key, value);
	}

	public Set<String> getDefaultValue() {
		return defaultValue;
	}

	public String getDefaultComment(){return defaultComment;}

	public String getComment() {return ConfigOptionStorage.getComment(key); } // I don't know when i'll will use this but maybe some day it will be useful for experimenting

	public void setComment(String comment){ConfigOptionStorage.setComment(key, comment);}
}

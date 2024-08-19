package com.skellybuilds.SCMC.config.option;


public class IntConfigOption {
    private final String key;
    private final Integer defaultValue;
    private final String defaultComment;

    public IntConfigOption(String key, Integer defaultValue, String defaultComment) {
        ConfigOptionStorage.setInt(key, defaultValue);
        this.key = key;
        this.defaultValue = defaultValue;
        this.defaultComment = defaultComment;
        ConfigOptionStorage.setComment(key, defaultComment);
    }

    public IntConfigOption(String key, Integer defaultValue){this(key, defaultValue, "");}

    public String getKey() {
        return key;
    }

    public Integer getValue() {
        return ConfigOptionStorage.getInt(key);
    }

    public String getComment() {return ConfigOptionStorage.getComment(key); } // I don't know when i'll will use this but maybe some day it will be useful for experimenting

    public void setComment(String comment){ConfigOptionStorage.setComment(key, comment);}

    public void setValue(Integer value) {
        ConfigOptionStorage.setInt(key, value);
    }


    public Integer getDefaultValue() {
        return defaultValue;
    }

    public String getDefaultComment(){ return defaultComment; }





}

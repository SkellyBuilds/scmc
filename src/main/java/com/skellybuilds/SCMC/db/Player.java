package com.skellybuilds.SCMC.db;

import java.util.ArrayList;
import java.util.List;

public class Player {
    protected final String username;
    public List<String> mods;
    protected String localeC;

    public String getLocale(){
        return localeC;
    }

    public String getUsername(){
        return username;
    }

    public void setLocale(String locale){
        localeC = locale;
    }

    public List<String> getMods() {
        return mods;
    }

    public Player(String username){
        this.username = username;
        this.mods = new ArrayList<>();
        this.localeC = "en_us";
    }

    public Player(String username, List<String> mods){
        this.username = username;
        this.mods = mods;
        this.localeC = "en_us";
    }

    public Player(String username, List<String> mods, String locale){
        this.username = username;
        this.mods = mods;
        this.localeC = locale;
    }
}

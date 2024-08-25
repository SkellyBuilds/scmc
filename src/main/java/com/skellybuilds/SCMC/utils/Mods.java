package com.skellybuilds.SCMC.utils;

import com.skellybuilds.SCMC.SCMC;
import com.skellybuilds.SCMC.config.ModMenuConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.api.metadata.Person;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mods {
    List<Mod> ModL = new ArrayList<>();

    public List<Mod> getMods() {
        return ModL;
    }

    public void loadMods(){
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            // might be more efficient ways to do this :/
            if(Objects.equals(mod.getMetadata().getId().toUpperCase(), SCMC.MOD_ID) || mod.getMetadata().getId().contains("fabric-") || mod.getMetadata().getId().contains("fabric") ||  mod.getMetadata().getId().contains("mixin") || mod.getMetadata().getCustomValue("fabric-loom:generated") != null || mod.getMetadata().getName().toUpperCase().contains("JAVA") || Objects.equals(mod.getMetadata().getId(), "minecraft")){
               continue;
            }


            if(!ModMenuConfig.USETHISMODS.getValue().isEmpty()){
                AtomicBoolean isMSEA = new AtomicBoolean(false);
                ModMenuConfig.USETHISMODS.getValue().forEach((string) -> {
                    if (Objects.equals(mod.getMetadata().getId(), string)) isMSEA.set(true);
                });

                if(!isMSEA.get()){
                    String modenv = mod.getMetadata().getEnvironment().toString().toUpperCase();
                    if (ModMenuConfig.USEONLYCLIENTMODS.getValue() && Objects.equals(modenv, "SERVER")) {
                        continue;
                    }


                    if (!ModMenuConfig.DONTUSETHISMODS.getValue().isEmpty()) {
                        AtomicBoolean isMSE = new AtomicBoolean(false);
                        ModMenuConfig.DONTUSETHISMODS.getValue().forEach((string) -> {
                            if (Objects.equals(mod.getMetadata().getId(), string)) {
                                isMSE.set(true);
                            }
                        });
                        if (isMSE.get()) continue;
                    }


                    if (!ModMenuConfig.USETHISMODSONLY.getValue().isEmpty()) {
                        AtomicBoolean isMS = new AtomicBoolean(false);
                        ModMenuConfig.USETHISMODSONLY.getValue().forEach((string) -> {
                            if (Objects.equals(mod.getMetadata().getId(), string)) {
                                isMS.set(true);
                            }
                        });
                        if (!isMS.get()) continue;
                    }
                }
            } else {

                String modenv = mod.getMetadata().getEnvironment().toString().toUpperCase();
                if (ModMenuConfig.USEONLYCLIENTMODS.getValue() && Objects.equals(modenv, "SERVER")) {
                    continue;
                }


                if (!ModMenuConfig.DONTUSETHISMODS.getValue().isEmpty()) {
                    AtomicBoolean isMSE = new AtomicBoolean(false);
                    ModMenuConfig.DONTUSETHISMODS.getValue().forEach((string) -> {
                        if (Objects.equals(mod.getMetadata().getId(), string)) {
                            isMSE.set(true);
                        }
                    });
                    if (isMSE.get()) continue;
                }


                if (!ModMenuConfig.USETHISMODSONLY.getValue().isEmpty()) {
                    AtomicBoolean isMS = new AtomicBoolean(false);
                    ModMenuConfig.USETHISMODSONLY.getValue().forEach((string) -> {
                        if (Objects.equals(mod.getMetadata().getId(), string)) {
                            isMS.set(true);
                        }
                    });
                    if (!isMS.get()) continue;
                }
            }


            Mod newMod = new Mod();
            if(mod.getOrigin().getKind() == ModOrigin.Kind.NESTED) newMod.isComponent = true;
            ModMeta meta = new ModMeta();
            newMod.id = mod.getMetadata().getId();
            newMod.version = mod.getMetadata().getVersion().toString();
            meta.baseDesc = mod.getMetadata().getDescription();
            meta.name = mod.getMetadata().getName();
            List<String> cNames = new ArrayList<>();
            List<String> aNames = new ArrayList<>();
            for (Person wah :  mod.getMetadata().getContributors()) {
                cNames.add(wah.getName());
            }
            for (Person wah :  mod.getMetadata().getAuthors()) {
                cNames.add(wah.getName());
            }
            meta.contributers = cNames.toArray(new String[0]);
            meta.authors = aNames.toArray(new String[0]);

            ModMenuConfig.OPTIONALMODS.getValue().forEach((string) -> {
                if(Objects.equals(string, mod.getMetadata().getId())){
                    newMod.isOptional = true;
                }
            });

            Contact was = new Contact();
            if(mod.getMetadata().getContact().get("homepage").isPresent()) {
            was.setHomepage(mod.getMetadata().getContact().get("homepage").get());
            }
            if(mod.getMetadata().getContact().get("issues").isPresent()) {
                was.setIssues(mod.getMetadata().getContact().get("issues").get());
            }
            if(mod.getMetadata().getContact().get("sources").isPresent()) {
                was.setSources(mod.getMetadata().getContact().get("sources").get());
            }

            meta.contact = was;

            Links meme = new Links();

            CustomValue modMenuValue = mod.getMetadata().getCustomValue("modmenu");
            if (modMenuValue != null && modMenuValue.getType() == CustomValue.CvType.OBJECT) {
                CustomValue.CvObject modMenuObject = modMenuValue.getAsObject();

                if(com.skellybuilds.SCMC.utils.CustomValues.getStringMap("links", modMenuObject).isEmpty()) {
                meme.setLinks(new HashMap<>());
                }
                meme.setLinks(com.skellybuilds.SCMC.utils.CustomValues.getStringMap("links", modMenuObject).orElse(new HashMap<>()));
            }

            meta.links = meme;
            if(mod.getOrigin().getKind() != ModOrigin.Kind.NESTED) {
                meta.icon = com.skellybuilds.SCMC.utils.Encoders.getModLogoAsBase64(mod.getMetadata().getId());
            }
            newMod.meta = meta;
            ModL.add(newMod);
        }
    }


    public static class Mod {
        public String version;
        public String id;
        public ModMeta meta;
        public boolean isOptional = false;
        public boolean isComponent = false;
    }

    public static class ModMeta {
        public String icon;
        public String name;
        public String baseDesc;
        public String[] contributers;
        public String[] authors;
        public Contact contact;
        public Links links;
    }

    public class Contact {
        private String homepage;
        private String sources;
        private String issues;

        public Contact(){

        }

        public Contact(String home, String source, String issue){
            homepage = home;
            sources = source;
            issues = issue;
        }

        // Getters and Setters
        public String getHomepage() {
            return homepage;
        }

        public void setHomepage(String homepage) {
            this.homepage = homepage;
        }

        public String getSources() {
            return sources;
        }

        public void setSources(String sources) {
            this.sources = sources;
        }

        public String getIssues() {
            return issues;
        }

        public void setIssues(String issues) {
            this.issues = issues;
        }



    }
    public class Links {
        private Map<String, String> links = new HashMap<>();

        // Getter
        public Map<String, String> getLinks() {
            return links;
        }

        // Setter for the whole map
        public void setLinks(Map<String, String> links) {
            this.links = links;
        }

        // Method to set individual link
        public void setLink(String key, String value) {
            this.links.put("modmenu."+key, value);
        }

        // Method to get individual link
        public String getLink(String key) {
            return this.links.get(key);
        }
    }
}

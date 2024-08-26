package com.skellybuilds.SCMC.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skellybuilds.SCMC.SCMC;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Locales {
    private static final Map<String, Map<String, JsonObject>> lMap = new HashMap<>(); // <ModId, <Language Code, Language JSON>>

    public static void loadLocale(String modId, String targetLanguageCode){
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isEmpty()) {
            System.out.println("Mod not found: " + modId);
            return;
        }

        ModContainer modContainer = modContainerOptional.get();
        Path modJarPath = modContainer.getOrigin().getPaths().get(0);

        try (ZipFile zipFile = new ZipFile(modJarPath.toFile())) {
            // Load the language file from the mod's assets
            String languagePath = String.format("assets/%s/lang/%s.json", modId, targetLanguageCode);

            ZipEntry entry = zipFile.getEntry(languagePath);
            if (entry == null) {
                SCMC.LOGGER.error("Unable to locate language {} for {}", targetLanguageCode, modId);
                return;
            }

            try (InputStream inputStream = zipFile.getInputStream(entry);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, JsonObject> curLocaleM = new HashMap<>();
                curLocaleM.put(targetLanguageCode, jsonObject);
                lMap.put(modId, curLocaleM);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Text fetchTranslatedText(String modId, String key, String targetLanguageCode) {
       if(!lMap.isEmpty()){
           if(lMap.get(modId) != null) {
               if (lMap.get(modId).get(targetLanguageCode) != null) {
                   JsonObject jsonObject = lMap.get(modId).get(targetLanguageCode);

                   String translatedString = jsonObject.has(key) ? jsonObject.get(key).getAsString() : key;

                   // Return the translated text
                   return Text.literal(translatedString);
               }
           }
       }

        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isEmpty()) {
            System.out.println("Mod not found: " + modId);
            return Text.literal("NOT A FILE");
        }

        ModContainer modContainer = modContainerOptional.get();
        Path modJarPath = modContainer.getOrigin().getPaths().get(0);

        try (ZipFile zipFile = new ZipFile(modJarPath.toFile())) {
            // Load the language file from the mod's assets
            String languagePath = String.format("assets/%s/lang/%s.json", modId, targetLanguageCode);

            ZipEntry entry = zipFile.getEntry(languagePath);
            if (entry == null) {
                SCMC.LOGGER.error("Unable to locate language {} for {}", targetLanguageCode, modId);
                return Text.literal("UNSUPPORTED_LOCALE");
            }

            try (InputStream inputStream = zipFile.getInputStream(entry);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, JsonObject> curLocaleM = new HashMap<>();
                curLocaleM.put(targetLanguageCode, jsonObject);
                lMap.put(modId, curLocaleM);
                String translatedString = jsonObject.has(key) ? jsonObject.get(key).getAsString() : key;

                // Return the translated text
                return Text.literal(translatedString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Text getTranslatedText(String modId, String key, String targetLanguageCode) {
         // Load the language file from the mod's assets
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (modContainerOptional.isEmpty()) {
            System.out.println("Mod not found: " + modId);
            return Text.literal("NOT A FILE");
        }

        ModContainer modContainer = modContainerOptional.get();
        Path modJarPath = modContainer.getOrigin().getPaths().get(0);

        try (ZipFile zipFile = new ZipFile(modJarPath.toFile())) {
            // Load the language file from the mod's assets
            String languagePath = String.format("assets/%s/lang/%s.json", modId, targetLanguageCode);

            ZipEntry entry = zipFile.getEntry(languagePath);
            if (entry == null) {
                SCMC.LOGGER.error("Unable to locate language {} for {}", targetLanguageCode, modId);
                return Text.literal("UNSUPPORTED_LOCALE");
            }

            try (InputStream inputStream = zipFile.getInputStream(entry);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                String translatedString = jsonObject.has(key) ? jsonObject.get(key).getAsString() : key;

                // Return the translated text
                return Text.literal(translatedString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

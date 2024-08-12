package com.skellybuilds.SCMC.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Encoders {

    public static String getModLogoAsBase64(String modId) {
        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(modId);
        if (!modContainerOptional.isPresent()) {
            System.out.println("Mod not found: " + modId);
            return null;
        }

        ModContainer modContainer = modContainerOptional.get();
        Path modJarPath = modContainer.getOrigin().getPaths().get(0);

        try (ZipFile zipFile = new ZipFile(modJarPath.toFile())) {
            Optional<String> url = modContainer.getMetadata().getIconPath(64);
            if(url.isEmpty()){
                return null;
            }
            ZipEntry logoEntry = zipFile.getEntry(url.get());
            if (logoEntry == null) {
               // System.out.println("Mod logo not found for: " + modId);
                return null;
            }

            try (InputStream inputStream = zipFile.getInputStream(logoEntry);
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                byte[] logoBytes = byteArrayOutputStream.toByteArray();
                return Base64.getEncoder().encodeToString(logoBytes);
            }
        } catch (Exception e) {
          //  e.printStackTrace();
            return null;
        }
    }



}

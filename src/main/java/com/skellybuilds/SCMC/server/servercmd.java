package com.skellybuilds.SCMC.server;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skellybuilds.SCMC.config.ModMenuConfig;
import com.skellybuilds.SCMC.utils.Mods;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.skellybuilds.SCMC.SCMC.PlayerN;

public class servercmd {
    public static final Logger LOGGER = LoggerFactory.getLogger("SCMC [Server Client Mods Checker]");

    public static void helloworld(String[] argument, Socket socket){
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        out.println("ok");
    }

    public static void getModName(String[] argument, Socket socket) {
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(argument[0]);
        if (modContainerOptional.isEmpty()) {
            return;
        }

        ModContainer modContainer = modContainerOptional.get();
        Path modJarPath = modContainer.getOrigin().getPaths().get(0);

        if(ModMenuConfig.USEONLYCLIENTMODS.getValue() && Objects.equals(modContainer.getMetadata().getEnvironment().toString().toLowerCase(), "SERVER")){
            return;
        }

        if(!ModMenuConfig.DONTUSETHISMODS.getValue().isEmpty()) {
            AtomicBoolean isMSE = new AtomicBoolean(false);
            ModMenuConfig.DONTUSETHISMODS.getValue().forEach((string) -> {
                if (Objects.equals(modContainer.getMetadata().getId(), string)) {
                    isMSE.set(true);
                }
            });
            if(isMSE.get()) return;
        }


        if(!ModMenuConfig.USETHISMODS.getValue().isEmpty()) {
            AtomicBoolean isMS = new AtomicBoolean(false);
            ModMenuConfig.USETHISMODS.getValue().forEach((string) -> {
                if (Objects.equals(modContainer.getMetadata().getId(), string)) {
                    isMS.set(true);
                }
            });
            if(!isMS.get()) return;
        }

        try {
            Path modsDirectory = Paths.get("./mods").toRealPath(); // Get the canonical (real) path of the mods folder
            Path requestedFile = modsDirectory.resolve(modJarPath).normalize(); // Resolve and normalize the requested file path

            // Check if the requested file is within the mods directory
            if (!requestedFile.startsWith(modsDirectory)) {
                return;
            }

            File file = new File("./mods", modJarPath.toString());

            out.println(file.getName());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
    public static void downloadFile(String[] argument, Socket socket) {
        OutputStream out = null;
        try {
        out = socket.getOutputStream();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }

        if(argument[0] == null){
            try {
                out.write(444);
                out.flush();
            } catch (IOException e) {
               LOGGER.error(e.toString());
            }

    }

    try {
        Path modsDirectory = Paths.get("./mods").toRealPath(); // Get the canonical (real) path of the mods folder
        Path requestedFile = modsDirectory.resolve(argument[0]).normalize(); // Resolve and normalize the requested file path

        // Check if the requested file is within the mods directory
        if (!requestedFile.startsWith(modsDirectory)) {
            System.err.println("Security warning: Attempted to access a file outside the mods folder.");
            return;
        }

//        Optional<ModContainer> modContainerOptional = FabricLoader.getInstance().getModContainer(argument[0]);
//        if (modContainerOptional.isEmpty()) {
//            return;
//        }
//
//        ModContainer modContainer = modContainerOptional.get();
//
//        if(ModMenuConfig.USEONLYCLIENTMODS.getValue() && Objects.equals(modContainer.getMetadata().getEnvironment().toString().toLowerCase(), "SERVER")){
//            return;
//        }
//
//        if(!ModMenuConfig.DONTUSETHISMODS.getValue().isEmpty()) {
//            AtomicBoolean isMSE = new AtomicBoolean(false);
//            ModMenuConfig.DONTUSETHISMODS.getValue().forEach((string) -> {
//                if (Objects.equals(modContainer.getMetadata().getId(), string)) {
//                    isMSE.set(true);
//                }
//            });
//            if(isMSE.get()) return;
//        }
//
//
//        if(!ModMenuConfig.USETHISMODS.getValue().isEmpty()) {
//            AtomicBoolean isMS = new AtomicBoolean(false);
//            ModMenuConfig.USETHISMODS.getValue().forEach((string) -> {
//                if (Objects.equals(modContainer.getMetadata().getId(), string)) {
//                    isMS.set(true);
//                }
//            });
//            if(!isMS.get()) return;
//        }



        File file = new File("./mods", argument[0]);
        if (file.exists() && file.isFile()) {
            try (FileInputStream fileInput = new FileInputStream(file);
                 BufferedInputStream fileBufferedInput = new BufferedInputStream(fileInput)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileBufferedInput.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();

            } catch (IOException e) {
             LOGGER.error(e.getMessage());
            }
        } else {
            try {
                out.write(420);
                out.flush();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
    catch (IOException e) {
        System.err.println("Error handling file request: " + e.getMessage());
    }
    }

    public static void getsvrmds(String[] argument, Socket socket){

        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mods md = new Mods();
        md.loadMods();
        StringBuilder data = new StringBuilder();
        Gson gson = new Gson();
        data.append("[");
        for(Mods.Mod mod : md.getMods()) {
            if(argument.length > 0) {
                if (mod.isComponent && !Objects.equals(argument[0], "withcomponents")) continue; // no components
            } else {
                if(mod.isComponent) continue;
            }
            if(data.isEmpty() || data.toString().equals("[")) {
                data.append(gson.toJson(mod));
            } else {
                data.append(",").append(gson.toJson(mod));
            }
        }
       data.append("]");

        if(data.isEmpty()){
            data.append("EMPTY");
        }

        out.println(data);
    }

    public static void getsvrcmdname(String[] argument, Socket socket){
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Mods md = new Mods();
        md.loadMods();
        StringBuilder data = new StringBuilder();

        for(Mods.Mod mod : md.getMods()) {
            if(argument.length > 0) {
                if (mod.isComponent && !Objects.equals(argument[0], "withcomponents")) continue; // no components
            } else {
                if(mod.isComponent) continue;
            }
            if(data.isEmpty()) {
                data.append(mod.meta.name);
            } else {
                data.append(",").append(mod.meta.name);
            }
        }

        if(data.isEmpty()){
            data.append("EMPTY");
        }

        out.println(data);
    }

    // adds players mods to the map in the future
    public static void verifyPlayerMods(String[] argument, Socket socket){
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // verify if the player has the necesscary mods

        // {data: []}
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(argument[0], JsonObject.class);
        JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();

        String[] stringArray = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            stringArray[i] = jsonArray.get(i).getAsString();
        }
        PlayerN.put(jsonObject.get("playerN").getAsString(), stringArray);
        out.println("OK");
    }


}

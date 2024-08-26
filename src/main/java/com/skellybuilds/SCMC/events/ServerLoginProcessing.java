package com.skellybuilds.SCMC.events;

import com.mojang.authlib.GameProfile;
import com.skellybuilds.SCMC.mixin.ServerLoginNetworkHandlerAccessor;
import com.skellybuilds.SCMC.utils.Mods;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static com.skellybuilds.SCMC.SCMC.*;

public class ServerLoginProcessing {
    public static void onEventRegister(ServerLoginNetworkHandler handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer loginSynchronizer){
        String username = "NULL";
        GameProfile profile = ((ServerLoginNetworkHandlerAccessor) handler).getGameProfile();
        if(profile != null)
            username = Objects.requireNonNull(profile).getName();

        LOGGER.info("Player with username {} is attempting to join the server.", username);
        if(PLAYERS.get(username) != null){
            Mods md = new Mods();
            md.loadMods("nul");
            List<String> allIDs = PLAYERS.get(username).getMods();
            List<String> ModL = md.getMods().stream().filter(ra -> !ra.isOptional).map((ra) -> ra.id).toList();


            if(!new HashSet<>(allIDs).containsAll(ModL)){
                List<String> missingElements = new ArrayList<>();


                // Check for missing elements
                for (String element : ModL) {
                    if (!allIDs.contains(element)) {
                        missingElements.add(element);
                    }
                }
                boolean isMore = false;
                int moreS = 0;
                if(missingElements.size() > 7){
                    isMore = true;
                    moreS = missingElements.size()-7;
                    missingElements = missingElements.subList(0, 7);
                }


                MutableText fullStuffA = Text.literal("You don't have the required mods! Download them using server mod menu in order to join!").formatted(Formatting.RED);
                fullStuffA.append("\n");
                Text listOfT = Text.literal("\n List of missing mods:")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                fullStuffA.append(listOfT);
                for (String missingElement : missingElements) {
                    fullStuffA.append("\n");
                    Text misEle = Text.literal(missingElement)
                            .setStyle(Style.EMPTY.withBold(true).withUnderline(true).withColor(Formatting.YELLOW));
                    fullStuffA.append(misEle);
                }

                if(isMore) {
                    Text nMore = Text.literal(" & " + moreS + " more...")
                            .setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
                    fullStuffA.append(nMore);
                }
                Text misWA = Text.literal("\nPlease install this mods & restart the game!")
                        .setStyle(Style.EMPTY.withColor(Formatting.WHITE));
                fullStuffA.append(misWA);

                handler.disconnect(fullStuffA);
            }
        } else {
            if(PLAYERS.isEmpty()) handler.disconnect(Text.literal("Something might be up with server mod menu."));
            else handler.disconnect(Text.literal("You require servermodmenu to use this server!"));
        }
    }
}

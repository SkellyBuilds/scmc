package com.skellybuilds.SCMC.events;

import com.skellybuilds.SCMC.utils.Mods;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static com.skellybuilds.SCMC.SCMC.PlayerN;

public class ServerJoinProcessing {
public static void onEventRegister(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
    Mods md = new Mods();
    md.loadMods();

    ServerPlayerEntity player = handler.getPlayer();

    if(PlayerN.get(player.getName().getString()) == null){
        player.sendMessage(Text.literal("Unable to fetch your player data! Is your server mod menu working or do you have it installed?"));
        return;
    }

    List<String> allIDs = Arrays.asList(PlayerN.get(player.getName().getString()));
    List<String> ModL = md.getMods().stream().filter(data -> !data.isOptional).map((ra) -> ra.id).toList();

    if(!new HashSet<>(allIDs).containsAll(ModL)){
        List<String> missingElements = new ArrayList<>();


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


        MutableText fullStuffA = Text.literal("Hello "+handler.player.getName().getString() + "! \n You can play in this server but you don't have the recommanded mods! It's suggested to download them using server mod menu in order to prevent any issues or bugs from missing mods!").formatted(Formatting.RED);
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
        Text misWA = Text.literal("\nPlease install this mods & restart your game then join back :)")
                .setStyle(Style.EMPTY.withColor(Formatting.WHITE));
        fullStuffA.append(misWA);

        player.sendMessage(fullStuffA);
    }
}
}

package com.skellybuilds.SCMC;

import com.mojang.authlib.GameProfile;
import com.skellybuilds.SCMC.config.ModMenuConfig;
import com.skellybuilds.SCMC.utils.Mods;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import com.skellybuilds.SCMC.mixin.ServerLoginNetworkHandlerAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import com.skellybuilds.SCMC.config.ModMenuConfigManager;
import com.skellybuilds.SCMC.server.servercmd;
import com.skellybuilds.SCMC.utils.Network;
import net.fabricmc.api.ModInitializer;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.*;

public class SCMC implements ModInitializer {
	public static final String MOD_ID = "SCMC";
    public static final Logger LOGGER = LoggerFactory.getLogger("SCMC [Server Client Mods Checker]");
	public static Network svr = new Network(27752);
	public Network.ServerCommands cmds = new Network.ServerCommands();
	public static final Map<String, String[]> PlayerN = new HashMap<>();
	private boolean pluh = false;

	private void bla(MinecraftServer minecraftServer){
		if(!pluh) {
			ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			if(ModMenuConfig.CANKICKPLAYERSWITHNOMODS.getValue()){
				// Get the player's username
                LOGGER.info("Someone is joining "+minecraftServer.getName());
				String username = "NULL";
				GameProfile profile = ((ServerLoginNetworkHandlerAccessor) handler).getGameProfile();
                if(profile != null)
				username = Objects.requireNonNull(profile).getName();

//				try {
//					username = handler.getClass().getField("profile").get("name").toString();
//				} catch (NoSuchFieldException | IllegalAccessException e) {
//					LOGGER.error("No such field or access :(");
//				}

				LOGGER.info("Player with username {} is attempting to join the server.", username);
				if(PlayerN.get(username) != null){
					Mods md = new Mods();
					md.loadMods();
					List<String> allIDs = Arrays.asList(PlayerN.get(username));
					List<String> ModL = md.getMods().stream().map((ra) -> ra.id).toList();


					if(new HashSet<>(allIDs).containsAll(ModL)){

					} else {
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
					if(PlayerN.isEmpty()) handler.disconnect(Text.literal("Please join again, servermodmenu went wrong!?"));
					else handler.disconnect(Text.literal("You require servermodmenu to use this server!"));
				}
			}
				});
			if(!ModMenuConfig.CANKICKPLAYERSWITHNOMODS.getValue()){
				ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
					Mods md = new Mods();
					md.loadMods();

					ServerPlayerEntity player = handler.getPlayer();

							List<String> allIDs = Arrays.asList(PlayerN.get(player.getName().getString()));
							List<String> ModL = md.getMods().stream().map((ra) -> ra.id).toList();


							if(new HashSet<>(allIDs).containsAll(ModL)){

							} else {
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
				});
			}
			pluh = true;
		}
	}
	@Override
	public void onInitialize() {
	Thread main = new Thread(() -> {
		ModMenuConfigManager.initializeConfig();


		cmds.addCommand("getall", servercmd::getsvrmds);
		cmds.addCommand("hello", servercmd::helloworld);
		cmds.addCommand("download", servercmd::downloadFile);
		cmds.addCommand("getmod", servercmd::getModName);
		cmds.addCommand("addpmods", servercmd::verifyPlayerMods);

		LOGGER.info("SCMC to Server Mod Menu getting ready");

		svr.init(cmds);

		LOGGER.info("SCMC to Server Mod Menu is ready");


	});

	main.setName("Main Connection Thread - SCMC");
	main.start();

	ServerTickEvents.END_SERVER_TICK.register(this::bla);

	}
}
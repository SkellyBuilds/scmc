package com.skellybuilds.SCMC;

import com.mojang.authlib.exceptions.MinecraftClientException;
import com.skellybuilds.SCMC.config.ModMenuConfig;
import com.skellybuilds.SCMC.events.ServerJoinProcessing;
import com.skellybuilds.SCMC.events.ServerLoginProcessing;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import com.skellybuilds.SCMC.config.ModMenuConfigManager;
import com.skellybuilds.SCMC.server.servercmd;
import com.skellybuilds.SCMC.utils.Network;
import net.fabricmc.api.ModInitializer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SCMC implements ModInitializer {
	public static final String MOD_ID = "SCMC";
    public static final Logger LOGGER = LoggerFactory.getLogger("SCMC [Server Client Mods Checker]");
	public static Network svr = new Network(25572);
	public Network.ServerCommands cmds = new Network.ServerCommands();
	public static final Map<String, String[]> PlayerN = new HashMap<>();
	public static final ModContainer MainModContainer = FabricLoader.getInstance().getModContainer("scmc").orElse(null);

	private boolean isCrashed = false;
	private CrashReport crashRP;

	@Override
	public void onInitialize() {


			ModMenuConfigManager.prepareConfigFile();
			if (!ModMenuConfigManager.fileExistant()) {
				LOGGER.info(Text.translatable("scmc.console.welcome", Text.translatable("scmc.welcome.header"), MainModContainer.getMetadata().getVersion()).getString());
				ModMenuConfigManager.initializeConfig();
			} else {
				ModMenuConfigManager.initializeConfig();
				LOGGER.info(Text.translatable("scmc.console.initalize", Text.translatable("scmc.header"), MainModContainer.getMetadata().getVersion()).getString());
			}

			Thread main = new Thread(() -> {
				try {
					Thread connection = new Thread(() -> {
						try {
							LOGGER.info("Initalizing - Connection");
							cmds.addCommand("getall", servercmd::getsvrmds);
							cmds.addCommand("hello", servercmd::helloworld);
							cmds.addCommand("download", servercmd::downloadFile);
							cmds.addCommand("getmod", servercmd::getModName);
							cmds.addCommand("addpmods", servercmd::verifyPlayerMods);
							svr.init(cmds);
							LOGGER.info("Connection - Ready");
						} catch (Exception error) {
							LOGGER.error("Exception occured while initalizing connection thread \n {}", error.getMessage());
							try {
								throw new Exception("Critical exception while running connection thread");
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});

					connection.setName("Main Thread - Connection - SCMC");
					connection.setPriority(1);


					Thread events = new Thread(() -> {
						LOGGER.info("Initalizing - Events");
						if(!ModMenuConfig.CANKICKPLAYERSWITHNOMODS.getValue()){
							ServerPlayConnectionEvents.JOIN.register(ServerJoinProcessing::onEventRegister);
						} else ServerLoginConnectionEvents.QUERY_START.register(ServerLoginProcessing::onEventRegister);
						LOGGER.info("Events - Ready");
					});

					events.setName("Main Thread - Events - SCMC");
					connection.setPriority(2);

					connection.start();
					events.start();

					boolean isCOMP = false;
					do {
						if (connection.getState() != Thread.State.RUNNABLE && events.getState() != Thread.State.RUNNABLE) {
							LOGGER.info("Everything is OK!");
							isCOMP = true;
						}
					} while (!isCOMP);

					while (true) {
						if (svr.crashed) {
							throw new Exception("Networking has crashed! This is very fatal - " + svr.howC);
						}
					}
				} catch (Exception error) {
					LOGGER.error("Exception occured while running the main thread \n {}", error.getMessage());
					CrashReport crashReport = new CrashReport("Welp, something went wrong in SCMC :(", new RuntimeException("SCMC has CRAAAASHED *add dramatic car crash sounds*"));

					CrashReportSection section = crashReport.addElement("Main suspect");
					section.add("Error message", error.getMessage());
					section.add("Cause", error.getCause());
					section.add("Stack trace", error.getStackTrace());
					crashReport.getSystemDetailsSection();

					crashRP = crashReport;
					throw new CrashException(crashRP);
				}
			});

			main.setName("Main Thread - index - SCMC");
			main.start();

	}
}
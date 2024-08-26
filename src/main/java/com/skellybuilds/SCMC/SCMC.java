package com.skellybuilds.SCMC;


import com.skellybuilds.SCMC.config.ModMenuConfig;
import com.skellybuilds.SCMC.db.Player;
import com.skellybuilds.SCMC.events.ServerJoinProcessing;
import com.skellybuilds.SCMC.events.ServerLoginProcessing;
import com.skellybuilds.SCMC.utils.Locales;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Environment(EnvType.SERVER)
public class SCMC implements ModInitializer {
	public static final String MOD_ID = "SCMC";
    public static final Logger LOGGER = LoggerFactory.getLogger("SCMC [Server Client Mods Checker]");
	public static Network svr = new Network(27752);
	public Network.ServerCommands cmds = new Network.ServerCommands();
	public static final Map<String, Player> PLAYERS = new HashMap<>();
	// Next update, i'll make this a class.
	public static final ModContainer MainModContainer = FabricLoader.getInstance().getModContainer("scmc").orElse(null);
	private boolean registerd = false;
	public static MinecraftServer serverD = null; // may be useful for additional crashes
	private String prevLoc = "";

	// I moved to getCrash data into a new one which is used in setCrash to prevent outdated crash report code
	public static String getCrash(Exception error, boolean writeCR){
		CrashReport crashReport = new CrashReport("SCMC has well crashed :( *add dramatic car crash sounds* \n\n", new RuntimeException(error.getMessage()));

		CrashReportSection section = crashReport.addElement("Main suspect");
		section.add("Stack trace", error.fillInStackTrace());

		if(writeCR){
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss.SSS");

			File dafile = new File("./crash-reports/SCMC-CRASH-"+ now.format(formatter) +".txt");
			crashReport.writeToFile(dafile);
			String fullpath = "UNABLE TO GET PATH! SEARCH IN CRASH REPORTS FOLDER";
			try {
				fullpath = dafile.getCanonicalPath();
			} catch (IOException e) {
				LOGGER.error(e.toString());
			}
			LOGGER.info("Crash report has been saved at .{}! \n If you can't manage to solve it, please report it at https://github.com/SkellyBuilds/scmc/issues", fullpath);
		}

		return "\n" + crashReport.asString();
	}

	public static void setCrash(Exception error, MinecraftServer server){

		server.sendMessage(Text.literal("A crash occured from SCMC! Show this to any admins"));

		LOGGER.error(getCrash(error, true));
		LOGGER.error("Shutting down the server! Read the crash report for more information!");
		server.stop(true);
	}




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

			svr.setPort(ModMenuConfig.CPORT.getValue());

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			if(!registerd) {
				registerd = true;
				serverD = server;
				Thread main = new Thread(() -> {
					Thread connection = new Thread(() -> {
						try {
							LOGGER.info("Initalizing - Connection");
							cmds.addCommand("getall", servercmd::getsvrmds);
							cmds.addCommand("hello", servercmd::helloworld);
							cmds.addCommand("download", servercmd::downloadFile);
							cmds.addCommand("getmod", servercmd::getModName);
							cmds.addCommand("addpmods", servercmd::verifyPlayerMods);
							cmds.addCommand("addploc", servercmd::setPlayerLocale);
							cmds.addCommand("getversion", servercmd::getVersion);
							svr.init(cmds);

							while (true) {
								if (!svr.isOnline && svr.crashed) {
									LOGGER.error("NETWORkING HAS CRASHED!");
									Exception test = new Exception("Critical exception while running connection thread - " + svr.howC + "\n \nIf this crash is related to your port, fix your permissions or configuration to a proper setting!");
									if(ModMenuConfig.ENABLESCMCCRASHES.getValue()){
										setCrash(test, server);
									} else {
										LOGGER.error(getCrash(test, false));
										LOGGER.info("SCMC will not stop the server but it's recommanded to take note!");
									}
									break;
								} else if (svr.isOnline && !svr.crashed) {
									break;
								}
							}

							if(svr.isOnline)LOGGER.info("Connection - Ready");
						} catch (Exception error) {
							LOGGER.error("Exception occured while initalizing connection thread \n {}", error.getMessage());
							try {
								throw new Exception();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					});

					connection.setUncaughtExceptionHandler((thread, error) -> {
						if(!svr.isOnline && ModMenuConfig.ENABLESCMCCRASHES.getValue())
						setCrash((Exception) error, server);
						else {
							if(!ModMenuConfig.ENABLESCMCCRASHES.getValue() && !svr.isOnline){
								LOGGER.error("SCMC connection thread has errored & it could not recover! \n This is the error report \n");
								LOGGER.error(getCrash((Exception) error, false));
							}
						}
					});

					connection.setName("Main Thread - Connection - SCMC");
					connection.setPriority(1);


					Thread events = new Thread(() -> {
						LOGGER.info("Initalizing - Events");
						if (!ModMenuConfig.CANKICKPLAYERSWITHNOMODS.getValue()) {
							ServerPlayConnectionEvents.JOIN.register(ServerJoinProcessing::onEventRegister);
						} else ServerLoginConnectionEvents.QUERY_START.register(ServerLoginProcessing::onEventRegister);
						LOGGER.info("Events - Ready");
					});

					events.setUncaughtExceptionHandler((thread, error) -> {
						if(ModMenuConfig.ENABLESCMCCRASHES.getValue()){
							setCrash((Exception) error, server);
						} else {
							LOGGER.error("An error occured to the events register! This is the following error report: \n");
							LOGGER.error(getCrash((Exception) error, false));
						}
					});

					events.setName("Main Thread - Events - SCMC");
					connection.setPriority(2);

					connection.start();
					events.start();

					boolean isCOMP = false;
					do {
						if (connection.getState() != Thread.State.RUNNABLE && events.getState() != Thread.State.RUNNABLE) {
							LOGGER.info("All sub threads have been completed");
							isCOMP = true;
						}
					} while (!isCOMP);

				});

				main.setName("Main Thread - index - SCMC");


				main.start();


			}
		});

		LOGGER.info("Testing locale from bclib: \"{}\"", Locales.fetchTranslatedText("bclib", "title.link.bclib.discord", "en_us").getString());
		LOGGER.info("Testing locale 2 from bclib: \"{}\"", Locales.fetchTranslatedText("bclib", "title.bclib.modmenu.main", "ru_ru").getString());


    }
}
package com.skellybuilds.SCMC.config;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.skellybuilds.SCMC.SCMC;
import com.skellybuilds.SCMC.config.option.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;



public class ModMenuConfigManager {
	private static File file;
	//private static  engine;


	public static void prepareConfigFile() {
		if (file != null) {
			return;
		}

		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), SCMC.MOD_ID + ".js");
	}

	public static boolean fileExistant(){
		return file.exists();
	}

	public static void convertToJS(){
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
		File fileS;

        try {
			fileS = new File(FabricLoader.getInstance().getConfigDir().toFile(), SCMC.MOD_ID + ".json");
            BufferedReader br = new BufferedReader(new FileReader(fileS));
			JsonObject json = new JsonParser().parse(br).getAsJsonObject();
			String jsConfig = "module.exports = " + gson.toJson(json);

			try (FileWriter writer = new FileWriter(file)) {
				writer.write(jsConfig);
			}

			boolean deleted = fileS.delete();
			if(!deleted) {
				SCMC.LOGGER.error("Unable to delete the previous config file");
			}

			load();

			save();

		} catch (IOException e) {
            SCMC.LOGGER.error("Unable to read previous json");
        }

	}

	public static void initializeConfig() {
		load();
	}

	@SuppressWarnings("unchecked")
	private static void load() {
		prepareConfigFile();

		try {
			if (!file.exists()) {
				if(new File(FabricLoader.getInstance().getConfigDir().toFile(), SCMC.MOD_ID + ".json").exists()){
					convertToJS();
					return;
				} else {
					save();
				}
			}
			if (file.exists()) {
				Gson gson = new Gson();
				JsonObject json = null;
				StringBuilder cJson = new StringBuilder();
				Map<String, String> cJK = new HashMap<>();
				String cVersion = "";
				boolean canUpdateConfig = true;

				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					boolean inMultiLineComment = false;
					boolean isCommentButNoKey = false;
					StringBuilder multiLineComment = new StringBuilder();
					String mtlC = "";
					boolean startStuff = false;


					while ((line = br.readLine()) != null ) {

						if(line.contains("DISABLE-CONFIG-UPDATES")){
							canUpdateConfig = false;
						}

						if(line.contains("Config Version:")){
							line = line.substring(0, line.length()-2);
							int lD = line.indexOf(": ");
							cVersion = line.substring(lD+2);
						}

						int comment1LIndex = line.indexOf("//");

						if(!startStuff) {
							if (line.contains("module.exports = ")) {
								line = line.replace("module.exports = ", "");
								startStuff = true;
							} else {
								continue; // example headers or extra code
							}
						}

						if (comment1LIndex != -1 && !inMultiLineComment) {
							int beforeKS = line.indexOf(":");
							if (beforeKS == -1){
								line = line.substring(0, comment1LIndex);
								cJson.append(line);
								continue;
							}; // No key found

							cJK.put(line.substring(0, beforeKS).replace(" ", "").replaceAll("\"", ""), line.substring(comment1LIndex + 2).trim());
							line = line.substring(0, comment1LIndex);
						}

						int multiLineStartIndex = line.indexOf("/*");
						int multiLineEndIndex = line.indexOf("*/");

						if (multiLineStartIndex != -1 && !inMultiLineComment) {
							int beforeKS = line.indexOf(":");
							if (beforeKS == -1){
								isCommentButNoKey = true;
								line = line.substring(0, multiLineStartIndex);
								cJson.append(line);
								continue;
							}; // No key found
							inMultiLineComment = true;
							mtlC = line.substring(0, beforeKS);
							multiLineComment.append(line.substring(multiLineStartIndex + 2).trim()).append("\n");
							line = line.substring(0, multiLineStartIndex);
							cJson.append(line);
						} else if (inMultiLineComment || isCommentButNoKey) {
							if (multiLineEndIndex != -1) {
								if(isCommentButNoKey){
									isCommentButNoKey = false;
									if(line.replaceAll(" ", "").length() > 2) { // if */ only, no use
										line = line.substring(multiLineEndIndex + 2);
										cJson.append(line);
									}
									continue;
								}
								inMultiLineComment = false;
								multiLineComment.append(line.substring(0, multiLineEndIndex).trim());
								// Its better to leave comments as it is, any contributors or myself will have to remove the breaks themselves
								String fixedMLC = multiLineComment.toString(); //.substring(1);
								//fixedMLC = fixedMLC.substring(0, fixedMLC.length() -1);
								multiLineComment = new StringBuilder();
								cJK.put(mtlC.replace(" ", "").replaceAll("\"", ""), fixedMLC);
								if(line.replaceAll(" ", "").length() > 2) { // if */ only, no use
									line = line.substring(multiLineEndIndex + 2);
									cJson.append(line);
								}
							} else {
								if(!isCommentButNoKey) multiLineComment.append(line.trim()).append("\n");
							}
						} else {
								cJson.append(line);
						}



					}
				}

				if(cJson.isEmpty()){
					save();
					load();
					return;
				}

				try {
					json = gson.fromJson(cJson.toString(), JsonObject.class);
				} catch (JsonSyntaxException e) {
					SCMC.LOGGER.error("Failed to load Config: Parsed JS to JSON file has failed \n More Details: \n {}\n\n^\n| - Error information related to config \n Don't understand, need help or you believe this is a bug?\n Report to https://github.com/SkellyBuilds/scmc/issues", e.toString());
					throw new RuntimeException("Config parsing failed! Scroll to the error");
				}
				for (Field field : ModMenuConfig.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
							StringSetConfigOption option = (StringSetConfigOption) field.get(null);
							JsonArray jsonArray = json.getAsJsonArray(option.getKey());
							if (jsonArray != null) {
								ConfigOptionStorage.setStringSet(option.getKey(), Sets.newHashSet(jsonArray).stream().map(JsonElement::getAsString).collect(Collectors.toSet()));
								if(cJK.get(option.getKey()) != null){
									ConfigOptionStorage.setComment(option.getKey(), cJK.get(option.getKey()));
								}
							}
						} else if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
							BooleanConfigOption option = (BooleanConfigOption) field.get(null);
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(option.getKey());
							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								ConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
								if(cJK.get(option.getKey()) != null) ConfigOptionStorage.setComment(option.getKey(), cJK.get(option.getKey()));
							}
						} else if(IntConfigOption.class.isAssignableFrom(field.getType())){
							IntConfigOption option = (IntConfigOption) field.get(null);
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(option.getKey());
							if (jsonPrimitive != null && jsonPrimitive.isNumber()) {
								ConfigOptionStorage.setInt(option.getKey(), jsonPrimitive.getAsInt());
								if(cJK.get(option.getKey()) != null) ConfigOptionStorage.setComment(option.getKey(), cJK.get(option.getKey()));
							}
						}
					}
				}

				if(!cVersion.isEmpty() && canUpdateConfig){
					if(!cVersion.equals(SCMC.MainModContainer.getMetadata().getCustomValue("configV").getAsString())){
						save(); // changes the config version to latest header & version so basically an auto update
					}
				}

			}
		} catch (IllegalAccessException | IOException e) {
			System.err.println("Couldn't load Mod Menu configuration file; reverting to defaults");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void save() {
		prepareConfigFile();

		JsonObject config = new JsonObject();
		Map<String, String> cJK = new HashMap<>();

		try {
			for (Field field : ModMenuConfig.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
						BooleanConfigOption option = (BooleanConfigOption) field.get(null);
						// i have no clue why prospector put the variable name instead of the key
						config.addProperty(option.getKey(), ConfigOptionStorage.getBoolean(option.getKey()));
						if(!option.getComment().isEmpty()){
							cJK.put(option.getKey(), option.getComment());
						}
					} else if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
						StringSetConfigOption option = (StringSetConfigOption) field.get(null);
						JsonArray array = new JsonArray();
						ConfigOptionStorage.getStringSet(option.getKey()).forEach(array::add);
						config.add(option.getKey(), array);
						if(!option.getComment().isEmpty()){
							cJK.put(option.getKey(), option.getComment());
						}
					} else if(IntConfigOption.class.isAssignableFrom(field.getType())){
						IntConfigOption option = (IntConfigOption) field.get(null);
						config.addProperty(option.getKey(), ConfigOptionStorage.getInt(option.getKey()));
						if(!option.getComment().isEmpty()){
							cJK.put(option.getKey(), option.getComment());
						}
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}


		String cVersion = "";
		if(SCMC.MainModContainer == null) cVersion = "Unable to fetch config version. Report this as an issue at https://github.com/SkellyBuilds/scmc/issues";
		else {
			cVersion = SCMC.MainModContainer.getMetadata().getCustomValue("configV").getAsString();
		}

		String headerSt = Text.translatable("scmc.config.intro", Text.translatable("scmc.header"), cVersion).getString();

		Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
		String jsonString = "/*"+headerSt+"*/\n\n module.exports = " + GSON.toJson(config);

		if(!cJK.isEmpty()) {
			StringBuilder jsonStringC = new StringBuilder();
			jsonStringC.append(jsonString);
			final String[] finalJsonString = {jsonString};
			cJK.forEach((Key, Comment) -> {
				if(Comment.contains("\n")){
					String fKey = "\"" + Key + "\":";
					int keyI = finalJsonString[0].indexOf(fKey);
					if (keyI != -1) {
						int beforeNL = finalJsonString[0].indexOf('\n', keyI);
						jsonStringC.insert(beforeNL, " /*" + Comment + "*/\n");
						finalJsonString[0] = jsonStringC.toString();
					}
				} else {
					String fKey = "\"" + Key + "\":";
					int keyI = finalJsonString[0].indexOf(fKey);
					if (keyI != -1) {
						int beforeNL = finalJsonString[0].indexOf('\n', keyI);
						jsonStringC.insert(beforeNL, " //" + Comment);
						finalJsonString[0] = jsonStringC.toString();
					}
				}
			});
			jsonString = jsonStringC.toString();
		}

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("Couldn't save Mod Menu configuration file");
			e.printStackTrace();
		}
	}
	public static void saveAsJSON() {
		prepareConfigFile();

		JsonObject config = new JsonObject();

		try {
			for (Field field : ModMenuConfig.class.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
					if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
						BooleanConfigOption option = (BooleanConfigOption) field.get(null);
						config.addProperty(field.getName().toLowerCase(Locale.ROOT), ConfigOptionStorage.getBoolean(option.getKey()));
					} else if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
						StringSetConfigOption option = (StringSetConfigOption) field.get(null);
						JsonArray array = new JsonArray();
						ConfigOptionStorage.getStringSet(option.getKey()).forEach(array::add);
						config.add(field.getName().toLowerCase(Locale.ROOT), array);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
		String jsonString = GSON.toJson(config);

		try (FileWriter fileWriter = new FileWriter(file)) {
			fileWriter.write(jsonString);
		} catch (IOException e) {
			System.err.println("Couldn't save Mod Menu configuration file");
			e.printStackTrace();
		}
	}
}

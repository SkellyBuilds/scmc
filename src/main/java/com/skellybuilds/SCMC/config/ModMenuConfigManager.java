package com.skellybuilds.SCMC.config;

import com.google.common.collect.Sets;
import com.google.gson.*;
import com.skellybuilds.SCMC.SCMC;
import com.skellybuilds.SCMC.config.option.*;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Collectors;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class ModMenuConfigManager {
	private static File file;
	//private static  engine;

	private static JsonObject jsObjtoJSON(Value value, Gson gson) {
		JsonObject jsonObject = new JsonObject();

		for (String key : value.getMemberKeys()) {
			Value memberValue = value.getMember(key);
			JsonElement jsonElement = gson.toJsonTree(memberValue.as(Object.class));
			jsonObject.add(key, jsonElement);
		}

		return jsonObject;
	}


	private static void prepareConfigFile() {
		if (file != null) {
			return;
		}

		file = new File(FabricLoader.getInstance().getConfigDir().toFile(), SCMC.MOD_ID + ".js");
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
				//BufferedReader br = new BufferedReader(new FileReader(file));
				Gson gson = new Gson();
				JsonObject json;
				Path filePath = Paths.get(file.getAbsolutePath());
				String scriptContent = Files.readString(filePath);

				try (Context context = Context.create("js")) {
					context.eval("js",scriptContent);
					json = jsObjtoJSON(context.getBindings("js").getMember("module").getMember("exports"), gson);
				}

				for (Field field : ModMenuConfig.class.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
						if (StringSetConfigOption.class.isAssignableFrom(field.getType())) {
							JsonArray jsonArray = json.getAsJsonArray(field.getName().toLowerCase(Locale.ROOT));
							if (jsonArray != null) {
								StringSetConfigOption option = (StringSetConfigOption) field.get(null);
								ConfigOptionStorage.setStringSet(option.getKey(), Sets.newHashSet(jsonArray).stream().map(JsonElement::getAsString).collect(Collectors.toSet()));
							}
						} else if (BooleanConfigOption.class.isAssignableFrom(field.getType())) {
							JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive(field.getName().toLowerCase(Locale.ROOT));
							if (jsonPrimitive != null && jsonPrimitive.isBoolean()) {
								BooleanConfigOption option = (BooleanConfigOption) field.get(null);
								ConfigOptionStorage.setBoolean(option.getKey(), jsonPrimitive.getAsBoolean());
							}
						}
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
		String jsonString = "module.exports = " + GSON.toJson(config);



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

package elucent.albedo;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigManager {

	public static Configuration config;

	// LIGHTING
	public static int maxLights;
	public static boolean enableLights;

	public static void init(File configFile) {
		if (config == null) {
			config = new Configuration(configFile);
			load();
		}
	}

	public static void load() {
		config.addCustomCategoryComment("light",
				"Settings related to lighting.");

		maxLights = config.getInt("maxLights", "light", 10, 0, 100,
				"The maximum number of lights allowed to render in a scene. Lights are sorted nearest-first, so further-away lights will be culled after nearer lights.");
		enableLights = config.getBoolean("enableLights", "light", true,
				"Enables lighting in general.");

		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(Albedo.MODID)) {
			load();
		}
	}
}

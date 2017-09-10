/*
 * The MIT License
 *
 * Copyright (c) 2017 Elucent, Una Thompson (unascribed), and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.mirage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigManager {

	public static Configuration config;

	// LIGHTING
	public static int maxLights;
	public static int frameSkip;
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
		frameSkip = config.getInt("frameSkip", "light", 10, 0, 160, "Skips sending light updates to the card some frames. This can speed up fps greatly when bandwidth is a problem. 0 always sends data.");
		
		if (config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equalsIgnoreCase(Mirage.MODID)) {
			load();
		}
	}
}

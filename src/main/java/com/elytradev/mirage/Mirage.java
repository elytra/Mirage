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

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GLContext;

import com.elytradev.mirage.shader.Shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=Mirage.MODID, version=Mirage.VERSION, name=Mirage.NAME, guiFactory="com.elytradev.mirage.gui.MirageGuiFactory", clientSideOnly=true)
public class Mirage {
	public static final String MODID = "mirage";
	public static final String VERSION = "2.0";
	public static final String NAME = "Mirage";
	
	public static boolean initialized = false;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		ConfigManager.init(event.getSuggestedConfigurationFile());
		
		if (!GLContext.getCapabilities().OpenGL20) {
			LogManager.getLogger("Mirage").error("OpenGL 2.0 or later is required for Mirage. Disabling.");
			Loader.instance().activeModContainer().setEnabledState(false);
			return;
		}

		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(Shaders::reload);
		
		MinecraftForge.EVENT_BUS.register(new EventManager());
		MinecraftForge.EVENT_BUS.register(new ConfigManager());
	}
}

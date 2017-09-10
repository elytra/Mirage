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

package com.elytradev.mirage.lighting;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.elytradev.mirage.ConfigManager;
import com.elytradev.mirage.event.GatherLightsEvent;
import com.elytradev.mirage.lighting.Light;
import com.elytradev.mirage.shader.Shaders;

public class LightManager {
	public static final ArrayList<Light> lights = Lists.newArrayList();
	private static long frameId = 0;
	
	public static void addLight(Light l) {
		if (l != null) {
			lights.add(l);
		}
	}
	
	public static void uploadLights() {
		Shaders.currentProgram.getUniform("lightCount").setInt(lights.size());
		
		frameId++;
		if (frameId<ConfigManager.frameSkip+1) return;
		frameId = 0;

		for (int i = 0; i < Math.min(ConfigManager.maxLights, lights.size()); i++) {
				Light l = lights.get(i);
				Shaders.currentProgram.getUniform("lights["+i+"].position").setFloat(l.x, l.y, l.z);
				Shaders.currentProgram.getUniform("lights["+i+"].color").setFloat(l.r, l.g, l.b, l.a);
				Shaders.currentProgram.getUniform("lights["+i+"].coneDirection").setFloat(l.sx, l.sy, l.sz);
				Shaders.currentProgram.getUniform("lights["+i+"].coneFalloff").setFloat(l.sf);
				Shaders.currentProgram.getUniform("lights["+i+"].intensity").setFloat(l.l);
		}
	}
	
	public static void update(World world) {
		GatherLightsEvent event = new GatherLightsEvent(lights);
		MinecraftForge.EVENT_BUS.post(event);
		
		for (Entity e : world.getLoadedEntityList()) {
			if (e instanceof IColoredLight){
				addLight(((IColoredLight)e).getColoredLight());
			}
		}
		for (TileEntity t : world.loadedTileEntityList) {
			if (t instanceof IColoredLight) {
				addLight(((IColoredLight)t).getColoredLight());
			}
		}
		
		lights.sort(distComparator);
	}
	
	public static void clear() {
		lights.clear();
	}
	
	public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
		return (Math.pow((x1-x2),2.0) + Math.pow((y1-y2),2.0) + Math.pow((z1-z2),2.0));
	}
	
	public static DistComparator distComparator = new DistComparator();
	
	public static class DistComparator implements Comparator<Light> {
		@Override
		public int compare(Light a, Light b) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			
			double dist1 = distanceSquared(a.x, a.y, a.z, p.posX, p.posY, p.posZ);
			double dist2 = distanceSquared(b.x, b.y, b.z, p.posX, p.posY, p.posZ);
			return Double.compare(dist1, dist2);
		}
	}
}

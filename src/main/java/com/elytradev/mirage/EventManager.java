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

import org.lwjgl.opengl.GL11;
import com.elytradev.mirage.lighting.Light;
import com.elytradev.mirage.lighting.LightManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventManager {
	public static int ticks = 0;
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			ticks++;
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		EntityPlayer p = Minecraft.getMinecraft().player;
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			double interpX = p.lastTickPosX + ((p.posX - p.lastTickPosX) * event.getPartialTicks());
			double interpY = p.lastTickPosY + ((p.posY - p.lastTickPosY) * event.getPartialTicks());
			double interpZ = p.lastTickPosZ + ((p.posZ - p.lastTickPosZ) * event.getPartialTicks());
			GlStateManager.disableDepth();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder vb = tess.getBuffer();
			GlStateManager.color(1, 1, 1);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GL11.glLineWidth(4f);
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			drawLightCaltrops(tess, vb, interpX, interpY, interpZ, 1);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.enableDepth();
		}
	}

	private void drawLightCaltrops(Tessellator tess, BufferBuilder vb, double interpX, double interpY, double interpZ, float a) {
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		int steps = 6;
		for (Light l : LightManager.lights) {
			for (int i = 0; i < steps; i++) {
				for (int j = 0; j < steps; j++) {
					float theta = (i/(float)steps) * (float)(Math.PI*2);
					float phi = (j/(float)steps) * (float)(Math.PI*2);
					
					double x = MathHelper.cos(theta) * MathHelper.sin(phi) * (l.radius/2);
					double y = MathHelper.sin(theta) * MathHelper.sin(phi) * (l.radius/2);
					double z = -MathHelper.cos(phi) * (l.radius/2);
					
					vb.pos(l.x-interpX, l.y-interpY, l.z-interpZ).color(l.r, l.g, l.b, l.a*a).endVertex();
					vb.pos((l.x+x)-interpX, (l.y+y)-interpY, (l.z+z)-interpZ).color(l.r, l.g, l.b, 0).endVertex();
				}
			}
		}
		tess.draw();
	}
}

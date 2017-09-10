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

package com.elytradev.mirage.asm;

import com.elytradev.mirage.ConfigManager;
import com.elytradev.mirage.EventManager;
import com.elytradev.mirage.lighting.LightManager;
import com.elytradev.mirage.shader.ShaderProgram;
import com.elytradev.mirage.shader.Shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class Hooks {

	public static void enableLightShader() {
		if (ConfigManager.enableLights) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			Shaders.fastLightProgram.use();
			
			Shaders.fastLightProgram.getUniform("ticks").setFloat(EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks());
			Shaders.fastLightProgram.getUniform("sampler").setInt(0);
			Shaders.fastLightProgram.getUniform("lightmap").setInt(1);
			Shaders.fastLightProgram.getUniform("brightlayer").setInt(2);
			Shaders.fastLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
			
			LightManager.clear();
			LightManager.update(Minecraft.getMinecraft().world);
			LightManager.uploadLights();
			/*
			Shaders.entityLightProgram.use();
			
			Shaders.entityLightProgram.getUniform("sampler").setInt(0);
			Shaders.entityLightProgram.getUniform("lightmap").setInt(1);
			Shaders.entityLightProgram.getUniform("brightlayer").setInt(2);
			
			LightManager.uploadLights();
			
			Shaders.entityLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
			Shaders.entityLightProgram.getUniform("lightingEnabled").setInt(GL11.glIsEnabled(GL11.GL_LIGHTING) ? 1 : 0);
			*/
			//Shaders.fastLightProgram.use();
		}
	}
	
	public static void disableLightShader() {
		if (ConfigManager.enableLights) {
			ShaderProgram.NONE.use();
		}
	}
	
	public static void preRenderChunk(RenderChunk c) {
		if (ConfigManager.enableLights) {
			BlockPos pos = c.getPosition();
			
			Shaders.fastLightProgram.getUniform("chunkX").setInt(pos.getX());
			Shaders.fastLightProgram.getUniform("chunkY").setInt(pos.getY());
			Shaders.fastLightProgram.getUniform("chunkZ").setInt(pos.getZ());
		}
	}
	
}

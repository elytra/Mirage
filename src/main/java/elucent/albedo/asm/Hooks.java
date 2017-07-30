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

package elucent.albedo.asm;

import org.lwjgl.opengl.GL11;

import elucent.albedo.ConfigManager;
import elucent.albedo.EventManager;
import elucent.albedo.lighting.LightManager;
import elucent.albedo.util.ShaderProgram;
import elucent.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class Hooks {

	public static void enableLightShader() {
		if (ConfigManager.enableLights) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			ShaderUtil.fastLightProgram.use();
			
			ShaderUtil.fastLightProgram.getUniform("ticks").setFloat(EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks());
			ShaderUtil.fastLightProgram.getUniform("sampler").setInt(0);
			ShaderUtil.fastLightProgram.getUniform("lightmap").setInt(1);
			ShaderUtil.fastLightProgram.getUniform("brightlayer").setInt(2);
			ShaderUtil.fastLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
			
			LightManager.clear();
			LightManager.update(Minecraft.getMinecraft().world);
			LightManager.uploadLights();
			
			ShaderUtil.entityLightProgram.use();
			
			ShaderUtil.entityLightProgram.getUniform("sampler").setInt(0);
			ShaderUtil.entityLightProgram.getUniform("lightmap").setInt(1);
			ShaderUtil.entityLightProgram.getUniform("brightlayer").setInt(2);
			
			LightManager.uploadLights();
			
			ShaderUtil.entityLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
			ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(GL11.glIsEnabled(GL11.GL_LIGHTING) ? 1 : 0);
			
			ShaderUtil.fastLightProgram.use();
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
			
			ShaderUtil.fastLightProgram.getUniform("chunkX").setInt(pos.getX());
			ShaderUtil.fastLightProgram.getUniform("chunkY").setInt(pos.getY());
			ShaderUtil.fastLightProgram.getUniform("chunkZ").setInt(pos.getZ());
		}
	}
	
}

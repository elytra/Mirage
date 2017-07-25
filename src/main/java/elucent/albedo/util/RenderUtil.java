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

package elucent.albedo.util;

import elucent.albedo.EventManager;
import elucent.albedo.event.RenderChunkUniformsEvent;
import elucent.albedo.item.ItemRenderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;

public class RenderUtil {
	public static void renderChunkUniforms(RenderChunk c) {
		MinecraftForge.EVENT_BUS.post(new RenderChunkUniformsEvent(c));
	}
	
	public static boolean lightingEnabled = false;
	
	public static ShaderProgram previousShader = ShaderProgram.NONE;
	public static boolean enabledLast = false;
	
	public static void enableLightingUniforms() {
		if (!EventManager.isGui) {
			if (enabledLast) {
				previousShader.use();
				enabledLast = false;
			}
			if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
				ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(1);
			}
		}
	}
	
	public static void disableLightingUniforms() {
		if (!EventManager.isGui) {
			if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
				ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(0);
			}
			if (!enabledLast) {
				previousShader = ShaderUtil.currentProgram;
				enabledLast = true;
				ShaderProgram.NONE.use();
			}
		}
	}
	
	public static void enableFogUniforms() {
		if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
			ShaderUtil.entityLightProgram.getUniform("fogIntensity").setFloat(Minecraft.getMinecraft().world.provider.getDimensionType() == DimensionType.NETHER ? 0.015625f : 1.0f);
		}
	}
	
	public static void disableFogUniforms() {
		if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
			ShaderUtil.entityLightProgram.getUniform("fogIntensity").setFloat(0);
		}
	}
	
	public static TransformType itemTransformType = TransformType.NONE;
	
	public static void setTransform(TransformType t) {
		itemTransformType = t;
	}

	public static void setTransformGUI() {
		itemTransformType = TransformType.GUI;
	}
	
	public static void renderItem(ItemStack stack) {
		if (ItemRenderRegistry.itemRenderMap.containsKey(stack.getItem())) {
			ItemRenderRegistry.itemRenderMap.get(stack.getItem()).render(stack, itemTransformType);
		}
	}
}

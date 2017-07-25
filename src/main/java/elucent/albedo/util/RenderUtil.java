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

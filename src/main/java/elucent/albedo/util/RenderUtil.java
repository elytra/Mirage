package elucent.albedo.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import elucent.albedo.event.RenderChunkUniformsEvent;
import elucent.albedo.item.ItemRenderRegistry;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

public class RenderUtil {
	public static void renderChunkUniforms(RenderChunk c){
		MinecraftForge.EVENT_BUS.post(new RenderChunkUniformsEvent(c));
	}
	
	public static boolean lightingEnabled = false;
	
	public static int previousShader = 0;
	
	public static void enableLightingUniforms(){
		if (ShaderUtil.currentProgram == ShaderUtil.entityLightProgram){
			int lightPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightingEnabled");
			GL20.glUniform1i(lightPos, 1);
		}
	}
	
	public static void disableLightingUniforms(){
		if (ShaderUtil.currentProgram == ShaderUtil.entityLightProgram){
			int lightPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightingEnabled");
			GL20.glUniform1i(lightPos, 0);
		}
	}
	
	public static void enableFogUniforms(){
		if (ShaderUtil.currentProgram == ShaderUtil.entityLightProgram){
			int lightPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "fogEnabled");
			GL20.glUniform1i(lightPos, 1);
		}
	}
	
	public static void disableFogUniforms(){
		if (ShaderUtil.currentProgram == ShaderUtil.entityLightProgram){
			int lightPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "fogEnabled");
			GL20.glUniform1i(lightPos, 0);
		}
	}
	
	public static TransformType itemTransformType = TransformType.NONE;
	
	public static void setTransform(TransformType t){
		itemTransformType = t;
	}

	public static void setTransformGUI(){
		itemTransformType = TransformType.GUI;
	}
	
	public static void renderItem(ItemStack stack){
		if (ItemRenderRegistry.itemRenderMap.containsKey(stack.getItem())){
			ItemRenderRegistry.itemRenderMap.get(stack.getItem()).render(stack, itemTransformType);
		}
	}
}

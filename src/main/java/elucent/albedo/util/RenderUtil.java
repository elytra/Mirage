package elucent.albedo.util;

import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;

public class RenderUtil {
	public static void renderChunkUniforms(RenderChunk c){
		if (ShaderUtil.currentProgram == ShaderUtil.lightProgram
				|| ShaderUtil.currentProgram == ShaderUtil.fastLightProgram){
			BlockPos pos = c.getPosition();
			int chunkX = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "chunkX");
			int chunkY = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "chunkY");
			int chunkZ = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "chunkZ");
			GL20.glUniform1i(chunkX, pos.getX());
			GL20.glUniform1i(chunkY, pos.getY());
			GL20.glUniform1i(chunkZ, pos.getZ());
		}
	}
}

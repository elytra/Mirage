package elucent.albedo.util;

import org.lwjgl.opengl.GL20;

import elucent.albedo.event.RenderChunkUniformsEvent;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;

public class RenderUtil {
	public static void renderChunkUniforms(RenderChunk c){
		MinecraftForge.EVENT_BUS.post(new RenderChunkUniformsEvent(c));
	}
}

package elucent.albedo.event;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderChunkUniformsEvent extends Event{
	RenderChunk renderChunk;
	public RenderChunkUniformsEvent(RenderChunk r){
		super();
		this.renderChunk = r;
	}
	
	public RenderChunk getChunk(){
		return renderChunk;
	}
	
	@Override
	public boolean isCancelable(){
		return false;
	}
}

package elucent.albedo.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ShaderSelectEvent extends Event{
	int shader = 0;
	public ShaderSelectEvent(int shader){
		super();
		this.shader = shader;
		this.setCanceled(false);
	}
	
	public int getShader(){
		return shader;
	}
	
	public void setShader(int shader){
		this.shader = shader;
	}
	
	@Override
	public boolean isCancelable(){
		return true;
	}
}

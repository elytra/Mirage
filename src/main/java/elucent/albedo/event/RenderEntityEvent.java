package elucent.albedo.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEntityEvent extends Event{
	Entity e = null;
	public RenderEntityEvent(Entity e){
		super();
		this.e = e;
	}
	
	public Entity getEntity(){
		return e;
	}
	
	@Override
	public boolean isCancelable(){
		return false;
	}
	
	public static void postNewEvent(Entity e){
		MinecraftForge.EVENT_BUS.post(new RenderEntityEvent(e));
	}
}

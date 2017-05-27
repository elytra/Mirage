package elucent.albedo.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderTileEntityEvent extends Event{
	TileEntity e = null;
	public RenderTileEntityEvent(TileEntity e){
		super();
		this.e = e;
	}
	
	public TileEntity getEntity(){
		return e;
	}
	
	@Override
	public boolean isCancelable(){
		return false;
	}
	
	public static void postNewEvent(TileEntity e){
		MinecraftForge.EVENT_BUS.post(new RenderTileEntityEvent(e));
	}
}

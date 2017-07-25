package elucent.albedo.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderTileEntityEvent extends Event {
	private final TileEntity tileEntity;

	public RenderTileEntityEvent(TileEntity tileEntity) {
		super();
		this.tileEntity = tileEntity;
	}

	public TileEntity getTileEntity() {
		return tileEntity;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	public static void postNewEvent(TileEntity e) {
		MinecraftForge.EVENT_BUS.post(new RenderTileEntityEvent(e));
	}
}

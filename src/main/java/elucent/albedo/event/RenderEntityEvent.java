package elucent.albedo.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEntityEvent extends Event {
	private final Entity entity;

	public RenderEntityEvent(Entity entity) {
		super();
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	public static void postNewEvent(Entity entity) {
		MinecraftForge.EVENT_BUS.post(new RenderEntityEvent(entity));
	}
}

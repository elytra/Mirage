package elucent.albedo.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ProfilerStartEvent extends Event {
	private final String section;

	public ProfilerStartEvent(String section) {
		super();
		this.section = section;
	}

	public String getSection() {
		return section;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	public static void postNewEvent(String section) {
		MinecraftForge.EVENT_BUS.post(new ProfilerStartEvent(section));
	}
}

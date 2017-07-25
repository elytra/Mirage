package elucent.albedo.event;

import java.util.ArrayList;

import elucent.albedo.lighting.Light;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GatherLightsEvent extends Event {
	private final ArrayList<Light> lights;

	public GatherLightsEvent(ArrayList<Light> lights) {
		super();
		this.lights = lights;
	}

	public ArrayList<Light> getLightList() {
		return lights;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}
}
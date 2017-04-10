package elucent.albedo.event;

import java.util.ArrayList;

import elucent.albedo.lighting.Light;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GatherLightsEvent extends Event{
	ArrayList<Light> lights = new ArrayList<Light>();
	public GatherLightsEvent(ArrayList<Light> lights){
		super();
		this.lights = lights;
	}
	
	public ArrayList<Light> getLightList(){
		return lights;
	}
	
	@Override
	public boolean isCancelable(){
		return false;
	}
}
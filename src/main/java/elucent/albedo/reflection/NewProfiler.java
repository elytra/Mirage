package elucent.albedo.reflection;

import elucent.albedo.event.ProfilerStartEvent;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;

public class NewProfiler extends Profiler{
	@Override
    public void startSection(String name)
    {
        super.startSection(name);
        MinecraftForge.EVENT_BUS.post(new ProfilerStartEvent(name));
    }
}

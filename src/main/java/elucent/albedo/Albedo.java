package elucent.albedo;

import elucent.albedo.reflection.NewProfiler;
import elucent.albedo.reflection.Fields;
import elucent.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Albedo.MODID, version = Albedo.VERSION, name = Albedo.NAME)
public class Albedo
{
    public static final String MODID = "albedo";
    public static final String VERSION = "1.0";
    public static final String NAME = "Albedo";
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
		MinecraftForge.EVENT_BUS.register(new EventManager());
		MinecraftForge.EVENT_BUS.register(new ConfigManager());
		ConfigManager.init(event.getSuggestedConfigurationFile());
		Fields.init();
		ShaderUtil.init();
		if (!(Minecraft.getMinecraft().mcProfiler instanceof NewProfiler)){
			try {
				Fields.Minecraft_mcProfiler.set(Minecraft.getMinecraft(), new NewProfiler());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
    }
}

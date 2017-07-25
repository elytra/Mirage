package elucent.albedo;

import elucent.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=Albedo.MODID, version=Albedo.VERSION, name=Albedo.NAME, guiFactory="elucent.albedo.gui.AlbedoGuiFactory", clientSideOnly=true)
public class Albedo {
	public static final String MODID = "albedo";
	public static final String VERSION = "1.0";
	public static final String NAME = "Albedo";
	
	public static boolean initialized = false;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((irm) -> {
			ShaderUtil.reload(irm);
		});
		
		MinecraftForge.EVENT_BUS.register(new EventManager());
		MinecraftForge.EVENT_BUS.register(new ConfigManager());
		
		ConfigManager.init(event.getSuggestedConfigurationFile());
	}
}

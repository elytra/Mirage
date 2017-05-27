package elucent.albedo.gui;

import java.util.List;

import elucent.albedo.Albedo;
import elucent.albedo.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiAlbedoConfig extends GuiConfig {

	public GuiAlbedoConfig(GuiScreen parentScreen) {
		super(parentScreen, 
				new ConfigElement(ConfigManager.config.getCategory("light")).getChildElements(), 
				Albedo.MODID, 
				false, 
				false, 
				"Albedo Config Options!");
	}

}

package elucent.albedo;

import org.lwjgl.opengl.GL20;

import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.event.ProfilerStartEvent;
import elucent.albedo.lighting.Light;
import elucent.albedo.lighting.LightManager;
import elucent.albedo.reflection.Fields;
import elucent.albedo.reflection.NewProfiler;
import elucent.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventManager {
	int ticks = 0;
	
	@SubscribeEvent
	public void onProfilerChange(ProfilerStartEvent event){
		if (event.getSection().compareTo("terrain") == 0){
			ShaderUtil.useProgram(ConfigManager.useFragmentLights ? ShaderUtil.lightProgram : ShaderUtil.fastLightProgram);
			int flickerLoc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "flickerMode");
			GL20.glUniform1i(flickerLoc,ConfigManager.blockLightFlickering ? 1 : 0);
			int tickLoc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "ticks");
			GL20.glUniform1f(tickLoc, (float)ticks + Minecraft.getMinecraft().getRenderPartialTicks());
			int texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "sampler");
			GL20.glUniform1i(texloc, 0);
			texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightmap");
			GL20.glUniform1i(texloc, 1);
			int playerPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "playerPos");
			GL20.glUniform3f(playerPos, (float)Minecraft.getMinecraft().player.posX, (float)Minecraft.getMinecraft().player.posY, (float)Minecraft.getMinecraft().player.posZ);
			LightManager.update(Minecraft.getMinecraft().world);
		}
		if (event.getSection().compareTo("entities") == 0){
			ShaderUtil.useProgram(0);
		}
		if (event.getSection().compareTo("litParticles") == 0){
			ShaderUtil.useProgram(0);
		}
		if (event.getSection().compareTo("aboveClouds") == 0){
			ShaderUtil.useProgram(0);
		}
		if (event.getSection().compareTo("destroyProgress") == 0){
			ShaderUtil.useProgram(ConfigManager.useFragmentLights ? ShaderUtil.lightProgram : ShaderUtil.fastLightProgram);
			int texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "sampler");
			GL20.glUniform1i(texloc, 0);
			texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightmap");
			GL20.glUniform1i(texloc, 1);
			int playerPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "playerPos");
			GL20.glUniform3f(playerPos, (float)Minecraft.getMinecraft().player.posX, (float)Minecraft.getMinecraft().player.posY, (float)Minecraft.getMinecraft().player.posZ);
		}
		if (event.getSection().compareTo("translucent") == 0){
			ShaderUtil.useProgram(ConfigManager.useFragmentLights ? ShaderUtil.lightProgram : ShaderUtil.fastLightProgram);
			int texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "sampler");
			GL20.glUniform1i(texloc, 0);
			texloc = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightmap");
			GL20.glUniform1i(texloc, 1);
			int playerPos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "playerPos");
			GL20.glUniform3f(playerPos, (float)Minecraft.getMinecraft().player.posX, (float)Minecraft.getMinecraft().player.posY, (float)Minecraft.getMinecraft().player.posZ);
		}
		if (event.getSection().compareTo("gui") == 0){
			ShaderUtil.useProgram(0);
		}
	}
	
	@SubscribeEvent
	public void onGatherLights(GatherLightsEvent event){
		event.getLightList().add(new Light((float)Minecraft.getMinecraft().player.posX, 
				(float)Minecraft.getMinecraft().player.posY, 
				(float)Minecraft.getMinecraft().player.posZ, 
				0.0625f, 0.75f, 1.0f, 1.0f, 
				4.0f));
	}
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event){
		if (event.phase == TickEvent.Phase.START){
			ticks ++;
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
}

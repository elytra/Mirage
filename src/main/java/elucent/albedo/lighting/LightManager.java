package elucent.albedo.lighting;

import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import elucent.albedo.util.ShaderUtil;
import elucent.albedo.ConfigManager;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.Light;

public class LightManager {
	public static ArrayList<Light> lights = new ArrayList<Light>();
	
	public static void addLight(Light l){
		if (l != null){
			lights.add(l);
		}
	}
	
	public static void uploadLights(){
		int max = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lightCount");
		GL20.glUniform1i(max, lights.size());
		for (int i = 0; i < Math.min(ConfigManager.maxLights, lights.size()); i ++){
			if (i < lights.size()){
				Light l = lights.get(i);
				int pos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].position");
				GL20.glUniform3f(pos, l.x, l.y, l.z);
				int color = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].color");
				GL20.glUniform4f(color, l.r, l.g, l.b, l.a);
				int radius = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].radius");
				GL20.glUniform1f(radius, l.radius);
			}
			else {
				int pos = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].position");
				GL20.glUniform3f(pos, 0, 0, 0);
				int color = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].color");
				GL20.glUniform4f(color, 0, 0, 0, 0);
				int radius = GL20.glGetUniformLocation(ShaderUtil.currentProgram, "lights["+i+"].radius");
				GL20.glUniform1f(radius, 0);
			}
		}
	}
	
	public static void update(World world){
		GatherLightsEvent event = new GatherLightsEvent(lights);
		MinecraftForge.EVENT_BUS.post(event);
		
		for (Entity e : world.getLoadedEntityList()){
			if (e instanceof ILightProvider){
				addLight(((ILightProvider)e).provideLight());
			}
		}
		for (TileEntity t : world.loadedTileEntityList){
			if (t instanceof ILightProvider){
				addLight(((ILightProvider)t).provideLight());
			}
		}
		
		lights.sort(distComparator);
		
		uploadLights();
		lights.clear();
	}
	
	public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2){
		return (Math.pow((x1-x2),2.0) + Math.pow((y1-y2),2.0) + Math.pow((z1-z2),2.0));
	}
	
	public static DistComparator distComparator = new DistComparator();
	
	public static class DistComparator implements Comparator<Light> {
		@Override
		public int compare(Light arg0, Light arg1) {
			double dist1 = distanceSquared(arg0.x,arg0.y,arg0.z,
					Minecraft.getMinecraft().player.posX,Minecraft.getMinecraft().player.posY,Minecraft.getMinecraft().player.posZ);
			double dist2 = distanceSquared(arg1.x,arg1.y,arg1.z,
					Minecraft.getMinecraft().player.posX,Minecraft.getMinecraft().player.posY,Minecraft.getMinecraft().player.posZ);
			return Double.compare(dist1, dist2);
		}
	}
}

package elucent.albedo.lighting;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import elucent.albedo.util.ShaderUtil;
import elucent.albedo.ConfigManager;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.Light;

public class LightManager {
	public static final ArrayList<Light> lights = Lists.newArrayList();
	
	public static void addLight(Light l) {
		if (l != null) {
			lights.add(l);
		}
	}
	
	public static void uploadLights() {
		ShaderUtil.currentProgram.getUniform("lightCount").setInt(lights.size());
		for (int i = 0; i < Math.min(ConfigManager.maxLights, lights.size()); i++) {
			if (i < lights.size()) {
				Light l = lights.get(i);
		
				ShaderUtil.currentProgram.getUniform("lights["+i+"].position").setFloat(l.x, l.y, l.z);
				ShaderUtil.currentProgram.getUniform("lights["+i+"].color").setFloat(l.r, l.g, l.b, l.a);
				ShaderUtil.currentProgram.getUniform("lights["+i+"].radius").setFloat(l.radius);
			} else {
				ShaderUtil.currentProgram.getUniform("lights["+i+"].position").setFloat(0, 0, 0);
				ShaderUtil.currentProgram.getUniform("lights["+i+"].color").setFloat(0, 0, 0, 0);
				ShaderUtil.currentProgram.getUniform("lights["+i+"].radius").setFloat(0);
			}
		}
	}
	
	public static void update(World world) {
		GatherLightsEvent event = new GatherLightsEvent(lights);
		MinecraftForge.EVENT_BUS.post(event);
		
		for (Entity e : world.getLoadedEntityList()) {
			if (e instanceof ILightProvider){
				addLight(((ILightProvider)e).provideLight());
			}
		}
		for (TileEntity t : world.loadedTileEntityList) {
			if (t instanceof ILightProvider) {
				addLight(((ILightProvider)t).provideLight());
			}
		}
		
		lights.sort(distComparator);
	}
	
	public static void clear() {
		lights.clear();
	}
	
	public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
		return (Math.pow((x1-x2),2.0) + Math.pow((y1-y2),2.0) + Math.pow((z1-z2),2.0));
	}
	
	public static DistComparator distComparator = new DistComparator();
	
	public static class DistComparator implements Comparator<Light> {
		@Override
		public int compare(Light a, Light b) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			
			double dist1 = distanceSquared(a.x, a.y, a.z, p.posX, p.posY, p.posZ);
			double dist2 = distanceSquared(b.x, b.y, b.z, p.posX, p.posY, p.posZ);
			return Double.compare(dist1, dist2);
		}
	}
}

package elucent.albedo;

import org.lwjgl.opengl.GL11;
import elucent.albedo.event.ProfilerStartEvent;
import elucent.albedo.event.RenderChunkUniformsEvent;
import elucent.albedo.event.RenderEntityEvent;
import elucent.albedo.event.RenderTileEntityEvent;
import elucent.albedo.gui.GuiAlbedoConfig;
import elucent.albedo.lighting.Light;
import elucent.albedo.lighting.LightManager;
import elucent.albedo.util.ShaderProgram;
import elucent.albedo.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventManager {
	int ticks = 0;
	boolean postedLights = false;
	boolean precedesEntities = true;
	public static boolean isGui = false;
	String section = "";

	@SubscribeEvent
	public void onProfilerChange(ProfilerStartEvent event) {
		section = event.getSection();
		if (ConfigManager.enableLights) {
			EntityPlayer p = Minecraft.getMinecraft().player;
			if (event.getSection().equals("terrain")) {
				isGui = false;
				precedesEntities = true;
				
				ShaderUtil.fastLightProgram.use();
				
				ShaderUtil.fastLightProgram.getUniform("ticks").setFloat(ticks + Minecraft.getMinecraft().getRenderPartialTicks());
				ShaderUtil.fastLightProgram.getUniform("sampler").setInt(0);
				ShaderUtil.fastLightProgram.getUniform("lightmap").setInt(1);
				ShaderUtil.fastLightProgram.getUniform("brightlayer").setInt(2);
				ShaderUtil.fastLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
				
				if (!postedLights) {
					LightManager.clear();
					LightManager.update(Minecraft.getMinecraft().world);
					LightManager.uploadLights();
					
					ShaderUtil.entityLightProgram.use();
					
					ShaderUtil.entityLightProgram.getUniform("sampler").setInt(0);
					ShaderUtil.entityLightProgram.getUniform("lightmap").setInt(1);
					ShaderUtil.entityLightProgram.getUniform("brightlayer").setInt(2);
					
					LightManager.uploadLights();
					
					ShaderUtil.entityLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
					ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(GL11.glIsEnabled(GL11.GL_LIGHTING) ? 1 : 0);
					
					ShaderUtil.fastLightProgram.use();
					postedLights = true;
				}
			}
			if (event.getSection().equals("sky")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("litParticles")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("weather")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("entities")) {
				if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
					ShaderUtil.entityLightProgram.use();
					
					ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(1);
					ShaderUtil.entityLightProgram.getUniform("fogIntensity").setFloat(Minecraft.getMinecraft().world.provider.getDimensionType() == DimensionType.NETHER ? 0.015625f : 1.0f);
				}
			}
			if (event.getSection().equals("blockEntities")) {
				if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
					ShaderUtil.entityLightProgram.use();
					ShaderUtil.entityLightProgram.getUniform("lightingEnabled").setInt(1);
				}
			}
			if (event.getSection().equals("outline")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("aboveClouds")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("destroyProgress")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("translucent")) {
				ShaderUtil.fastLightProgram.use();
				
				ShaderUtil.fastLightProgram.getUniform("sampler").setInt(0);
				ShaderUtil.fastLightProgram.getUniform("lightmap").setInt(1);
				ShaderUtil.fastLightProgram.getUniform("playerPos").setFloat((float)p.posX, (float)p.posY, (float)p.posZ);
				
				precedesEntities = true;
			}
			if (event.getSection().equals("hand")) {
				ShaderProgram.NONE.use();
			}
			if (event.getSection().equals("gui")) {
				isGui = true;
				ShaderProgram.NONE.use();
			}
		}
	}

	@SubscribeEvent
	public void onRenderEntity(RenderEntityEvent event) {
		if (ConfigManager.enableLights) {
			if (event.getEntity() instanceof EntityLightningBolt) {
				ShaderProgram.NONE.use();
			} else if (section.equalsIgnoreCase("entities") || section.equalsIgnoreCase("blockEntities")) {
				ShaderUtil.entityLightProgram.use();
			}
			
			if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
				ShaderUtil.entityLightProgram.getUniform("entityPos").setFloat((float)event.getEntity().posX, (float)event.getEntity().posY + event.getEntity().height / 2.0f, (float)event.getEntity().posZ);
				ShaderUtil.entityLightProgram.getUniform("colorMult").setFloat(1f, 1f, 1f, 0f);
				
				if (event.getEntity() instanceof EntityLivingBase) {
					EntityLivingBase e = (EntityLivingBase) event.getEntity();
					if (e.hurtTime > 0 || e.deathTime > 0) {
						ShaderUtil.entityLightProgram.getUniform("colorMult").setFloat(1f, 0f, 0f, 0.3f);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event) {
		if (ConfigManager.enableLights) {
			if ((event.getEntity()).isPotionActive(Potion.getPotionFromResourceLocation("glowing"))) {
				ShaderProgram.NONE.use();
			} else if (section.equalsIgnoreCase("entities") || section.equalsIgnoreCase("blockEntities")) {
				ShaderUtil.entityLightProgram.use();
			}
			if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
				ShaderUtil.entityLightProgram.getUniform("colorMult").setFloat(1f, 1f, 1f, 0f);
			}
		}
	}

	@SubscribeEvent
	public void onRenderTileEntity(RenderTileEntityEvent event) {
		if (ConfigManager.enableLights) {
			if (event.getTileEntity() instanceof TileEntityEndPortal || event.getTileEntity() instanceof TileEntityEndGateway) {
				ShaderProgram.NONE.use();
			} else if (section.equalsIgnoreCase("entities") || section.equalsIgnoreCase("blockEntities")) {
				ShaderUtil.entityLightProgram.use();
			}
			
			if (ShaderUtil.entityLightProgram.isCurrentProgram()) {
				BlockPos pos = event.getTileEntity().getPos();
				ShaderUtil.entityLightProgram.getUniform("entityPos").setFloat(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
				ShaderUtil.entityLightProgram.getUniform("colorMult").setFloat(1f, 1f, 1f, 0f);
			}
		}
	}

	@SubscribeEvent
	public void onRenderChunk(RenderChunkUniformsEvent event) {
		if (ConfigManager.enableLights) {
			if (ShaderUtil.fastLightProgram.isCurrentProgram()) {
				BlockPos pos = event.getChunk().getPosition();
				
				ShaderUtil.fastLightProgram.getUniform("chunkX").setInt(pos.getX());
				ShaderUtil.fastLightProgram.getUniform("chunkY").setInt(pos.getY());
				ShaderUtil.fastLightProgram.getUniform("chunkZ").setInt(pos.getZ());
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			ticks++;
		}
	}

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		postedLights = false;
		EntityPlayer p = Minecraft.getMinecraft().player;
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			double interpX = p.lastTickPosX + ((p.posX - p.lastTickPosX) * event.getPartialTicks());
			double interpY = p.lastTickPosY + ((p.posY - p.lastTickPosY) * event.getPartialTicks());
			double interpZ = p.lastTickPosZ + ((p.posZ - p.lastTickPosZ) * event.getPartialTicks());
			GlStateManager.disableDepth();
			Tessellator tess = Tessellator.getInstance();
			VertexBuffer vb = tess.getBuffer();
			GlStateManager.color(1, 1, 1);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GL11.glLineWidth(4f);
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			drawLightCaltrops(tess, vb, interpX, interpY, interpZ, 1);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GlStateManager.enableDepth();
		}
		if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
			GlStateManager.disableLighting();
			ShaderProgram.NONE.use();
		}
	}

	private void drawLightCaltrops(Tessellator tess, VertexBuffer vb, double interpX, double interpY, double interpZ, float a) {
		vb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		int steps = 6;
		for (Light l : LightManager.lights) {
			for (int i = 0; i < steps; i++) {
				for (int j = 0; j < steps; j++) {
					float theta = (i/(float)steps) * (float)(Math.PI*2);
					float phi = (j/(float)steps) * (float)(Math.PI*2);
					
					double x = MathHelper.cos(theta) * MathHelper.sin(phi) * (l.radius/2);
					double y = MathHelper.sin(theta) * MathHelper.sin(phi) * (l.radius/2);
					double z = -MathHelper.cos(phi) * (l.radius/2);
					
					vb.pos(l.x-interpX, l.y-interpY, l.z-interpZ).color(l.r, l.g, l.b, l.a*a).endVertex();
					vb.pos((l.x+x)-interpX, (l.y+y)-interpY, (l.z+z)-interpZ).color(l.r, l.g, l.b, 0).endVertex();
				}
			}
		}
		tess.draw();
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiConfig && ((GuiConfig)event.getGui()).modID.equals(Albedo.MODID)) {
			event.setGui(new GuiAlbedoConfig(((GuiConfig)event.getGui()).parentScreen));
		}
	}
}

package elucent.albedo.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import elucent.albedo.Albedo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ShaderUtil {

	public static ShaderProgram currentProgram = ShaderProgram.NONE;
	public static ShaderProgram fastLightProgram = ShaderProgram.NONE;
	public static ShaderProgram entityLightProgram = ShaderProgram.NONE;

	public static void reload(IResourceManager irm) {
		if (fastLightProgram != null) {
			fastLightProgram.delete();
			fastLightProgram = null;
		}
		if (entityLightProgram != null) {
			entityLightProgram.delete();
			entityLightProgram = null;
		}
		fastLightProgram = new ShaderProgram(loadProgram(irm, "shaders/fastlight.vs", "shaders/fastlight.fs"));
		LogManager.getLogger("Albedo").info("Loaded fastlight shader");
		entityLightProgram = new ShaderProgram(loadProgram(irm, "shaders/entitylight.vs", "shaders/entitylight.fs"));
		LogManager.getLogger("Albedo").info("Loaded entitylight shader");
	}

	public static int loadProgram(IResourceManager irm, String vsh, String fsh) {
		int vertexShader = createShader(irm, vsh, OpenGlHelper.GL_VERTEX_SHADER);
		int fragmentShader = createShader(irm, fsh, OpenGlHelper.GL_FRAGMENT_SHADER);

		int program = OpenGlHelper.glCreateProgram();

		OpenGlHelper.glAttachShader(program, vertexShader);
		OpenGlHelper.glAttachShader(program, fragmentShader);

		OpenGlHelper.glLinkProgram(program);
		
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new RuntimeException("Error linking program: " + GL20.glGetProgramInfoLog(program, 65536));
		}

		return program;
	}

	public static int createShader(IResourceManager irm, String filename, int shaderType) {
		int shader = OpenGlHelper.glCreateShader(shaderType);
		if (shader == 0) return 0;
		
		try {
			ByteSource bs = new ByteSource() {
				@Override
				public InputStream openStream() throws IOException {
					return irm.getResource(new ResourceLocation(Albedo.MODID, filename)).getInputStream();
				}
			};
			String src = bs.asCharSource(Charsets.UTF_8).read();
			ARBShaderObjects.glShaderSourceARB(shader, src);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read "+filename, e);
		}
		
		OpenGlHelper.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
			throw new RuntimeException("Error creating shader: " + getLogInfo(shader));

		return shader;
	}

	public static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
}

package elucent.albedo.util;

import java.util.Arrays;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.OpenGlHelper;

public class ShaderProgram {

	public static final ShaderProgram NONE = new ShaderProgram(0);
	
	public class Uniform {
		private int location;
		private Object lastValue;
		
		public Uniform(int location, Object lastValue) {
			this.location = location;
			this.lastValue = lastValue;
		}
		
		private boolean equal(Object a, Object b) {
			if (a instanceof int[] && b instanceof int[]) {
				return Arrays.equals((int[])a, (int[])b);
			}
			if (a instanceof float[] && b instanceof float[]) {
				return Arrays.equals((float[])a, (float[])b);
			}
			return Objects.equal(a, b);
		}
		
		public void setFloat(float val) {
			if (equal(val, lastValue)) return;
			GL20.glUniform1f(location, val);
		}
		public void setInt(int val) {
			if (equal(val, lastValue)) return;
			GL20.glUniform1i(location, val);
		}
		
		public void setFloat(float val1, float val2) {
			float[] val = new float[] {val1, val2};
			if (equal(val, lastValue)) return;
			GL20.glUniform2f(location, val1, val2);
			lastValue = val;
		}
		public void setInt(int val1, int val2) {
			int[] val = new int[] {val1, val2};
			if (equal(val, lastValue)) return;
			GL20.glUniform2i(location, val1, val2);
			lastValue = val;
		}
		
		public void setFloat(float val1, float val2, float val3) {
			float[] val = new float[] {val1, val2, val3};
			if (equal(val, lastValue)) return;
			GL20.glUniform3f(location, val1, val2, val3);
			lastValue = val;
		}
		public void setInt(int val1, int val2, int val3) {
			int[] val = new int[] {val1, val2, val3};
			if (equal(val, lastValue)) return;
			GL20.glUniform3i(location, val1, val2, val3);
			lastValue = val;
		}
		
		public void setFloat(float val1, float val2, float val3, float val4) {
			float[] val = new float[] {val1, val2, val3, val4};
			if (equal(val, lastValue)) return;
			GL20.glUniform4f(location, val1, val2, val3, val4);
			lastValue = val;
		}
		public void setInt(int val1, int val2, int val3, int val4) {
			int[] val = new int[] {val1, val2, val3, val4};
			if (equal(val, lastValue)) return;
			GL20.glUniform4i(location, val1, val2, val3, val4);
			lastValue = val;
		}
		
	}

	private int program;
	
	private final Map<String, Uniform> uniforms = Maps.newHashMap();
	
	public ShaderProgram(int program) {
		this.program = program;
	}
	
	public Uniform getUniform(String name) {
		if (!uniforms.containsKey(name)) {
			uniforms.put(name, new Uniform(GL20.glGetUniformLocation(program, name), null));
		}
		return uniforms.get(name);
	}
	
	public void use() {
		ShaderUtil.currentProgram = this;
		OpenGlHelper.glUseProgram(program);
	}
	
	public void delete() {
		OpenGlHelper.glDeleteProgram(program);
	}

	public int getId() {
		return program;
	}

	public boolean isCurrentProgram() {
		return ShaderUtil.currentProgram == this;
	}
	
}

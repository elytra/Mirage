/*
 * The MIT License
 *
 * Copyright (c) 2017 Elucent, Una Thompson (unascribed), and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.mirage.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLContext;

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
		public void setFloats(float[] floats) {
			FloatBuffer values = ByteBuffer.allocateDirect(floats.length*Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
			values.put(floats);
			GL20.glUniform1(location, values);
		}
	}

	private int program;
	
	private final Map<String, Uniform> uniforms = Maps.newConcurrentMap();
	
	public ShaderProgram(int program) {
		this.program = program;
	}
	
	public void refreshUniforms() {
		if (GLContext.getCapabilities().OpenGL31) {
			int numUniforms = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS);
			for(int i=0; i<numUniforms; i++) {
				String name = GL31.glGetActiveUniformName(program, i,32);
				int type = GL20.glGetActiveUniformType(program, i);
				int size = GL20.glGetActiveUniformSize(program, i);
				int idx = GL20.glGetUniformLocation(program, name);
				//System.out.println(i+": Discovered uniform '"+name+"' at index "+idx+" of type "+typeString(type)+" and size "+size);
				uniforms.put(name, new Uniform(idx,null));
			}
			
		} else {
			System.out.println("Can't query and pre-register uniform names; GL context is <3.1");
		}
	}
	
	public Uniform getUniform(String name) {
		if (!uniforms.containsKey(name)) {
			int loc = GL20.glGetUniformLocation(program, name);
			if (loc==GL11.GL_FALSE) {
				System.out.println("INVALID UNIFORM NAME:"+name);
			}
			
			uniforms.put(name, new Uniform(loc, null));
		}
		return uniforms.get(name);
	}
	
	public void use() {
		Shaders.currentProgram = this;
		OpenGlHelper.glUseProgram(program);
	}
	
	public void delete() {
		OpenGlHelper.glDeleteProgram(program);
	}

	public int getId() {
		return program;
	}

	public boolean isCurrentProgram() {
		return Shaders.currentProgram == this;
	}
	
	public static String typeString(int type) {
		switch(type) {
		case GL20.GL_FLOAT_VEC3: return "vec3";
		case GL20.GL_FLOAT_VEC4: return "vec4";
		case GL11.GL_FLOAT: return "float";
		default:
			return "Unknown("+type+")";
		}
	}
	
}

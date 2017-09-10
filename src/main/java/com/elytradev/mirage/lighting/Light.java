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

package com.elytradev.mirage.lighting;

import java.nio.FloatBuffer;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Light {
	public static final float DOT_15 = 0.966f;
	public static final float DOT_30 = 0.866f;
	public static final float DOT_45 = 0.707f;
	public static final float DOT_60 = 0.500f;
	public static final float DOT_90 = 0.000f;
	
	public float x;
	public float y;
	public float z;
	
	public float r;
	public float g;
	public float b;
	public float a;
	public float l;
	
	//cone vector and falloff
	public float sx;
	public float sy;
	public float sz;
	
	public float sf;

	@Deprecated
	public Light(float x, float y, float z, float r, float g, float b, float a, float radius) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.l = 1.0f;
		this.sx = radius;
		this.sy = 0;
		this.sz = 0;
		this.sf = -1; //180 degrees
	}
	
	/*
	 *  struct light {
	 *  	vec4 color
	 *  	vec3 position
	 *  	vec3 coneDirection
	 *  	float coneFalloff
	 *  	float intensity
	 *  }
	 */
	
	public void writeTo(float[] buf, int ofs) {
		buf[ofs+ 0] = r;
		buf[ofs+ 1] = g;
		buf[ofs+ 2] = b;
		buf[ofs+ 3] = a;
		buf[ofs+ 4] = x;
		buf[ofs+ 5] = y;
		buf[ofs+ 6] = z;
		buf[ofs+ 7] = sx;
		buf[ofs+ 8] = sy;
		buf[ofs+ 9] = sz;
		buf[ofs+10] = sf;
		buf[ofs+11] = l;
	}
	
	public void writeTo(FloatBuffer buf) {
		buf.put(r);
		buf.put(g);
		buf.put(b);
		buf.put(a);
		buf.put(x);
		buf.put(y);
		buf.put(z);
		buf.put(sx);
		buf.put(sy);
		buf.put(sz);
		buf.put(sf);
		buf.put(l);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private float x = Float.NaN;
		private float y = Float.NaN;
		private float z = Float.NaN;
		
		private float r = Float.NaN;
		private float g = Float.NaN;
		private float b = Float.NaN;
		private float a = Float.NaN;
		private float l = 1.0f;
		
		private float radius = Float.NaN; //synthetic
		
		private float sx = 1;
		private float sy = 0;
		private float sz = 0;
		private float sf = -1;
		
		public Builder pos(BlockPos pos) {
			return pos(pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f);
		}
		
		public Builder pos(Vec3d pos) {
			return pos(pos.x, pos.y, pos.z);
		}
		
		public Builder pos(Entity e) {
			return pos(e.posX, e.posY, e.posZ);
		}
		
		public Builder pos(double x, double y, double z) {
			return pos((float)x, (float)y, (float)z);
		}
		
		public Builder pos(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}
		
		public Builder color(int c, boolean hasAlpha) {
			return color(extract(c, 2), extract(c, 1), extract(c, 0), hasAlpha ? extract(c, 3) : 1);
		}
		
		private float extract(int i, int idx) {
			return ((i >> (idx*8)) & 0xFF)/255f;
		}

		public Builder color(float r, float g, float b) {
			return color(r, g, b, 1);
		}
		
		public Builder color(float r, float g, float b, float a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			return this;
		}
		
		public Builder radius(float radius) {
			this.radius = radius;
			return this;
		}
		
		/**
		 * <p>Sets this light's shape to a cone, and sets its direction.
		 * The direction vector will be normalized (magnitude is controlled with {@link #radius(float)}).
		 * 
		 * <p>Spread controls how broad the cone is, as the dot product of the cone vector with the incident vector. So
		 * 1.0f will be infinitesimally small, 0.707f will be 45 degrees, 0 will be 90, -0.707f is 130, and -1 is 180
		 * degrees (which behaves like a point light) - you can also use one of the DOT_X constants in Light.
		 */
		public Builder cone(float x, float y, float z, float spread) {
			float magnitude = (float)Math.sqrt(x*x + y*y + z*z);
			this.sx = x / magnitude;
			this.sy = y / magnitude;
			this.sz = z / magnitude;
			this.sf = spread; //Math.min(spread, 1.0f); //Cap it so negative (>1/0f) cone widths don't make weird behavior
			return this;
		}
		
		/**
		 * @see #cone(float, float, float, float)
		 */
		public Builder cone(Vec3d vec, float spread) {
			cone((float)vec.x, (float)vec.y, (float)vec.z, spread);
			return this;
		}
		
		public Builder intensity(float l) {
			this.l = l;
			return this;
		}
		
		public Light build() {
			if (Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(z) &&
					Float.isFinite(r) && Float.isFinite(g) && Float.isFinite(b) && Float.isFinite(a) &&
					Float.isFinite(radius)) {
				Light l = new Light(x, y, z, r, g, b, a, 1.0f);
				l.sx = sx*radius;
				l.sy = sy*radius;
				l.sz = sz*radius;
				l.sf = sf;
				l.l = this.l;
				return l;
			} else {
				throw new IllegalArgumentException("Position, color, and radius must be set, and cannot be infinite");
			}
		}
	}
	
}

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

package com.elytradev.mirage.event;

import java.util.ArrayList;

import com.elytradev.mirage.ConfigManager;
import com.elytradev.mirage.lighting.Light;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GatherLightsEvent extends Event {
	private final ArrayList<Light> lights;
	private final Vec3d cameraPos;
	private final ICamera camera;
	private final double maxDist;

	public GatherLightsEvent(ArrayList<Light> lights) {
		super();
		this.lights = lights;
		this.cameraPos = null;
		this.camera = null;
		this.maxDist = Double.POSITIVE_INFINITY;
	}

	public GatherLightsEvent(ArrayList<Light> lights, Vec3d cameraPos, ICamera camera, double maxDist) {
		super();
		this.lights = lights;
		this.cameraPos = cameraPos;
		this.camera = camera;
		this.maxDist = maxDist;
	}

	/** @deprecated use {@link #add(Light)} instead. This method will be maintained through the end of Minecraft 1.12,
	 *              but *will* be removed for 1.13.
	 */
	@Deprecated
	public ArrayList<Light> getLightList() {
		return lights;
	}
	
	/** Adds a light to render on this frame. Lights must be re-added each frame you wish to render them on. */
	public void add(Light l) {
		if (cameraPos != null && cameraPos.squareDistanceTo(l.x, l.y, l.z) > l.mag + maxDist) {
			return;
		}

		if (camera != null && !camera.isBoundingBoxInFrustum(new AxisAlignedBB(
				l.x - l.mag,
				l.y - l.mag,
				l.z - l.mag,
				l.x + l.mag,
				l.y + l.mag,
				l.z + l.mag
		))) {
			return;
		}

		lights.add(l);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}
}
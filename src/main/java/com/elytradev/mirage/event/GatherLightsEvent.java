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

import com.elytradev.mirage.lighting.Light;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GatherLightsEvent extends Event {
	private final ArrayList<Light> lights;

	public GatherLightsEvent(ArrayList<Light> lights) {
		super();
		this.lights = lights;
	}

	/** @deprecated use {@link #add(Light)} instead */
	@Deprecated
	public ArrayList<Light> getLightList() {
		return lights;
	}
	
	/** Adds a light to render on this frame. Lights must be re-added each frame you wish to render them on. */
	public void add(Light l) {
		lights.add(l);
	}

	@Override
	public boolean isCancelable() {
		return false;
	}
}
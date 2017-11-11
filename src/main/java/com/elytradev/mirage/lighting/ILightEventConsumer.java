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

import com.elytradev.mirage.event.GatherLightsEvent;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Identifies tiles which provide dynamic light. Implement this on your TileEntity, and gatherLights will be called each
 * frame for each block that has that tile, allowing you to add lights to the render for that frame.
 * 
 * <p>Classes that can be tagged with this interface:
 * <ul>
 *   <li>TileEntity
 *   <ul>
 *     <li>Lit when in placed in-world.
 *   </ul>
 * </ul>
 */
public interface ILightEventConsumer {
	/**
	 * Called to allow clients to provide zero or more colored lights. Lights must be added every frame, but the
	 * lighting system does not make any changes to your light; the same light can be safely added every frame.
	 * @param evt An object which can accept lights for display.
	 */
	@SideOnly(Side.CLIENT)
	public void gatherLights(GatherLightsEvent evt);
}

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

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Identifies entities or items which provide dynamic light. Implement this on any Entity or any Item, and gatherLights
 * will be called each frame for each one in the world, in an equipment slot, or in a hand, allowing you to add lights
 * to the render for that frame.
 * 
 * <p>Classes that can be tagged with this interface:
 * <ul>
 *   <li>Item
 *   <ul>
 *     <li>Lit when worn in an equipment slot
 *     <li>Lit when held in either hand
 *     <li>Lit when dropped on the ground
 *     <li><em>NOT</em> lit when in a Bauble slot.
 *   </ul>
 *   <li>Entity
 *   <ul>
 *     <li>Lit anywhere unless captured in a golden lasso, soul vial, or other non-entity form.
 *     <li>Stays lit while riding or being ridden by other entities.
 *   </ul>
 * </ul>
 * 
 * <p>
 */
public interface IEntityLightEventConsumer {
	@SideOnly(Side.CLIENT)
	public void gatherLights(GatherLightsEvent evt, Entity entity);
}

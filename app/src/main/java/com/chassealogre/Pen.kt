/*
 * Copyright 2026 Pierre Halipré
 *
 * This file is part of Chasse à l'ogre.
 *
 * Chasse à l'ogre is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Chasse à l'ogre is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Chasse à l'ogre. If not, see <https://www.gnu.org/licenses/>.
 */

package com.chassealogre

class Pen(
    device: Device,
) {
    var sprite: Sprite

    init {
        val graphic = device.graphic
        val rasters = graphic.pen
        sprite = Sprite(rasters)

        for (iSheet in 0..<4) {
            val jSprite = iSheet
            sprite.addSheet(jSprite, false, rasters, device)
        }
    }

    fun outlinePolygon(
        polygon: Polygon,
        canvas: Canvas,
        device: Device,
    ) {
        for (i in 0..<polygon.getSize()) {
            val shape = polygon.get(i)
            drawLineShape(shape, canvas, device)
        }
    }

    fun drawLineShape(
        shape: Shape,
        canvas: Canvas,
        device: Device,
    ) {
        if (shape.lines != Shape.NONE) {
            val images = sprite.get(device.design.mode).get(0)

            for (iImage in 0..<8) {
                val flag = Math.pow(2, iImage)

                if (Math.isFlag(shape.lines, flag)) {
                    val image = images.get(iImage)
                    val w = device.graphic.toWCases(shape.i)
                    val h = device.graphic.toHCases(shape.j)
                    val x = canvas.x + w
                    val y = canvas.y + h
                    image.onWindow(x, y, device.graphic.window)
                }
            }
        }
    }
}

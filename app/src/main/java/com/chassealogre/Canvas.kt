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

abstract class Canvas(
    rastersCanvas: Graphic.RastersCanvas,
    device: Device,
) {
    val x: Int = rastersCanvas.x
    val y: Int = rastersCanvas.y
    val nWCases: Int = rastersCanvas.nWCases
    val nHCases: Int = rastersCanvas.nHCases
    val sprite: Sprite = Sprite(rastersCanvas)

    init {
        sprite.addSheet(0, false, rastersCanvas, device)
        sprite.addSheet(0, true, rastersCanvas, device)
        sprite.addSheet(1, false, rastersCanvas, device)
        sprite.addSheet(1, true, rastersCanvas, device)
    }

    fun isIn(device: Device): Boolean {
        val xEvent = device.event.x
        val yEvent = device.event.y
        val w = device.graphic.toWCases(nWCases)
        val h = device.graphic.toHCases(nHCases)

        return xEvent >= x && xEvent < x + w && yEvent >= y && yEvent < y + h
    }

    fun draw(
        isForeground: Boolean,
        device: Device,
    ) {
        var iSheetStart: Int

        if (isForeground) {
            iSheetStart = 0
        } else {
            iSheetStart = 2
        }

        val iSheetLeft = iSheetStart
        val iSheetRight = iSheetStart + 1
        val ratioLeft = getSpriteRatio()
        val ratioRight = (ratioLeft + 1 / 2) % 1
        val xLeft = x
        val nWCasesHalf = Math.half(nWCases)
        val xMiddle = device.graphic.toWCases(nWCasesHalf)
        val xRight = xLeft + xMiddle
        val yTop = y
        sprite.draw(iSheetLeft, ratioLeft, xLeft, yTop, device)
        sprite.draw(iSheetRight, ratioRight, xRight, yTop, device)
    }

    abstract fun getSpriteRatio(): Double
}

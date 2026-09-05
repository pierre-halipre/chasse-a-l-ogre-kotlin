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

class Font(
    device: Device,
) {
    var sprite: Sprite
    val paragraph: Array<Array<Int>> = Array<Array<Int>>()

    init {
        val rasters = device.graphic.font
        sprite = Sprite(rasters)
        sprite.addSheet(0, false, rasters, device)

        var nSentences = 0

        while (nSentences < 3) {
            paragraph.add(Array())
            nSentences += 1
        }
    }

    fun setHome(device: Device) {
        val paragraphHome = device.text.home.get(device.design.mode)
        paragraph.set(0, paragraphHome.get(0))
        paragraph.set(1, paragraphHome.get(1))
        paragraph.set(2, paragraphHome.get(2))
    }

    fun setEnd(
        score: Int,
        device: Device,
    ) {
        device.text.setCustomEnd(score, device.design)
        val paragraphEnd = device.text.end.get(device.design.mode)
        paragraph.set(0, paragraphEnd.get(0))
        paragraph.set(1, paragraphEnd.get(1))
        paragraph.set(2, device.text.custom)
    }

    fun setPause(
        ticks: Int,
        device: Device,
    ) {
        val milliseconds: Int

        if (ticks == device.clock.getTicksMax()) {
            milliseconds = device.clock.timeMax - 1
        } else {
            milliseconds = ticks * device.clock.getRefreshTime()
        }

        device.text.setCustomPause(milliseconds, device.design)
        val paragraphPause = device.text.pause.get(device.design.mode)
        paragraph.set(0, paragraphPause.get(0))
        paragraph.set(1, paragraphPause.get(1))
        paragraph.set(2, device.text.custom)
    }

    fun draw(
        canvas: Canvas,
        device: Device,
    ) {
        for (line in 0..<paragraph.getSize()) {
            val sentence = paragraph.get(line)
            val yLine = device.graphic.toHCases(9 + line * 3)
            val y = canvas.y + yLine

            for (i in 0..<sentence.getSize()) {
                val iSheet = 0
                val unicode = sentence.get(i)
                val ratio = getSpriteRatio(unicode)
                val xWord = device.graphic.toWCases(1 + i)
                val x = canvas.x + xWord
                sprite.draw(iSheet, ratio, x, y, device)
            }
        }
    }

    fun getSpriteRatio(unicode: Int): Double {
        var iImage: Int

        if (unicode >= 48 && unicode <= 57) {
            iImage = unicode - 48
        } else if (unicode >= 65 && unicode <= 90) {
            iImage = unicode - 55
        } else if (unicode == 46) {
            iImage = 36
        } else if (unicode == 33) {
            iImage = 37
        } else if (unicode == 58) {
            iImage = 38
        } else if (unicode == 39) {
            iImage = 39
        } else {
            iImage = 40
        }

        return Math.toDouble(iImage) / 41
    }
}

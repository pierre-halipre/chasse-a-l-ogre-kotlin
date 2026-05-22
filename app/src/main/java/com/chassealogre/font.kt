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
        sprite.add_sheet(0, false, rasters, device)

        var n_sentences = 0

        while (n_sentences < 3) {
            paragraph.add(Array())
            n_sentences += 1
        }
    }

    fun set_home(device: Device) {
        val paragraph_home = device.text.home.get(device.design.mode)
        paragraph.set(0, paragraph_home.get(0))
        paragraph.set(1, paragraph_home.get(1))
        paragraph.set(2, paragraph_home.get(2))
    }

    fun set_end(
        score: Int,
        device: Device,
    ) {
        device.text.set_custom_end(score, device.design)
        val paragraph_end = device.text.end.get(device.design.mode)
        paragraph.set(0, paragraph_end.get(0))
        paragraph.set(1, paragraph_end.get(1))
        paragraph.set(2, device.text.custom)
    }

    fun set_pause(
        ticks: Int,
        device: Device,
    ) {
        val milliseconds = ticks * device.clock.get_refresh_time()
        device.text.set_custom_pause(milliseconds, device.design)
        val paragraph_pause = device.text.pause.get(device.design.mode)
        paragraph.set(0, paragraph_pause.get(0))
        paragraph.set(1, paragraph_pause.get(1))
        paragraph.set(2, device.text.custom)
    }

    fun draw(
        canvas: Canvas,
        device: Device,
    ) {
        for (line in 0..<paragraph.get_size()) {
            val sentence = paragraph.get(line)
            val y_line = device.graphic.to_h_cases(9 + line * 3)
            val y = canvas.y + y_line

            for (i in 0..<sentence.get_size()) {
                val i_sheet = 0
                val unicode = sentence.get(i)
                val ratio = get_ratio_sprite(unicode)
                val x_word = device.graphic.to_w_cases(1 + i)
                val x = canvas.x + x_word
                sprite.draw(i_sheet, ratio, x, y, device)
            }
        }
    }

    fun get_ratio_sprite(unicode: Int): Double {
        var i_image: Int

        if (unicode >= 48 && unicode <= 57) {
            i_image = unicode - 48
        } else if (unicode >= 65 && unicode <= 90) {
            i_image = unicode - 55
        } else if (unicode == 46) {
            i_image = 36
        } else if (unicode == 33) {
            i_image = 37
        } else if (unicode == 58) {
            i_image = 38
        } else if (unicode == 39) {
            i_image = 39
        } else {
            i_image = 40
        }

        return Math.to_double(i_image) / 41
    }
}

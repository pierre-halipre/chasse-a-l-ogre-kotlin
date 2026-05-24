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
    rasters_canvas: RastersCanvas,
    device: Device,
) {
    val x: Int = rasters_canvas.x
    val y: Int = rasters_canvas.y
    val n_w_cases: Int = rasters_canvas.n_w_cases
    val n_h_cases: Int = rasters_canvas.n_h_cases
    val sprite: Sprite = Sprite(rasters_canvas)

    init {
        sprite.add_sheet(0, false, rasters_canvas, device)
        sprite.add_sheet(0, true, rasters_canvas, device)
        sprite.add_sheet(1, false, rasters_canvas, device)
        sprite.add_sheet(1, true, rasters_canvas, device)
    }

    fun is_in(device: Device): Boolean {
        val x_event = device.event.x
        val y_event = device.event.y
        val w = device.graphic.to_w_cases(n_w_cases)
        val h = device.graphic.to_h_cases(n_h_cases)
        val in_w = x_event >= x && x_event < x + w
        val in_h = y_event >= y && y_event < y + h

        return in_w && in_h
    }

    fun draw(
        is_foreground: Boolean,
        device: Device,
    ) {
        var i_sheet_start: Int

        if (is_foreground) {
            i_sheet_start = 0
        } else {
            i_sheet_start = 2
        }

        val i_sheet_left = i_sheet_start
        val i_sheet_right = i_sheet_start + 1
        val ratio_left = get_ratio_sprite()
        val ratio_right = (ratio_left + 1 / 2) % 1
        val x_left = x
        val n_w_cases_half = Math.half(n_w_cases)
        val x_middle = device.graphic.to_w_cases(n_w_cases_half)
        val x_right = x_left + x_middle
        val y_top = y
        sprite.draw(i_sheet_left, ratio_left, x_left, y_top, device)
        sprite.draw(i_sheet_right, ratio_right, x_right, y_top, device)
    }

    abstract fun get_ratio_sprite(): Double
}

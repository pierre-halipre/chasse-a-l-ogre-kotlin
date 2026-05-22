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

class Cherry(
    board: Board,
    device: Device,
) {
    var sprite: Sprite

    init {
        val rasters_zone = device.graphic.cherry
        sprite = Sprite(rasters_zone)
        board.fill_sprite(sprite, 0, rasters_zone, device)
    }

    fun draw(
        ratio: Double,
        zone: Int,
        board: Board,
        device: Device,
    ) {
        val zone_inverse = board.get_zone_inverse(zone)
        val i_sheet = board.to_i_zone(zone_inverse)
        val zone_start = board.get_zone_center()
        val x_start = board.get_x_zone(zone_start, device)
        val y_start = board.get_y_zone(zone_start, device)
        val x_end = board.get_x_far_zone(zone, device)
        val y_end = board.get_y_far_zone(zone, device)
        val x = Math.distance(x_start, x_end, ratio)
        val y = Math.distance(y_start, y_end, ratio)
        sprite.draw(i_sheet, ratio, x, y, device)
    }
}

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

class Fence(
    board: Board,
    device: Device,
) {
    var sprite: Sprite

    init {
        val rasters = device.graphic.fence
        sprite = Sprite(rasters)
        board.fill_sprite(sprite, 0, rasters, device)
    }

    fun draw(
        level: Int,
        zone: Int,
        board: Board,
        device: Device,
    ) {
        val i_sheet = board.to_i_zone(zone)
        val ratio = Math.to_double(level - 1) / 3
        val x = board.get_x_near_zone(zone, device)
        val y = board.get_y_near_zone(zone, device)
        sprite.draw(i_sheet, ratio, x, y, device)
    }
}

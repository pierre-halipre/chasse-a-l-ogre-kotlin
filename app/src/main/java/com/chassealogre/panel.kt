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

class Panel(
    device: Device,
) : Layout(device.graphic.panel, device) {
    companion object {
        const val LEFT: Int = 8
        const val RIGHT: Int = 9
    }

    override fun add_zones() {
        zones.add(LEFT)
        zones.add(RIGHT)
    }

    override fun get_polygon(zone: Int): Polygon {
        val result = Polygon()
        result.fill(0, 0, Shape.FULL, Shape.TOP + Shape.LEFT)
        result.fill(0, 1, Shape.FULL, Shape.LEFT)
        result.fill(0, 2, Shape.FULL, Shape.LEFT)
        result.fill(0, 3, Shape.FULL, Shape.LEFT + Shape.BOTTOM)
        result.fill(1, 0, Shape.FULL, Shape.TOP)
        result.fill(1, 1, Shape.FULL, Shape.NONE)
        result.fill(1, 2, Shape.FULL, Shape.NONE)
        result.fill(1, 3, Shape.FULL, Shape.BOTTOM)
        result.fill(2, 0, Shape.FULL, Shape.TOP)
        result.fill(2, 1, Shape.FULL, Shape.NONE)
        result.fill(2, 2, Shape.FULL, Shape.NONE)
        result.fill(2, 3, Shape.FULL, Shape.BOTTOM)
        result.fill(3, 0, Shape.FULL, Shape.RIGHT + Shape.TOP)
        result.fill(3, 1, Shape.FULL, Shape.RIGHT)
        result.fill(3, 2, Shape.FULL, Shape.RIGHT)
        result.fill(3, 3, Shape.FULL, Shape.RIGHT + Shape.BOTTOM)

        val flip_w = !is_left_zone(zone)
        val flip_h = false
        flip_polygon(result, flip_w, flip_h)

        return result
    }

    override fun get_ratio_sprite(): Double = 0.0

    override fun get_i_case_zone(zone: Int): Int {
        var result: Int

        if (zone == LEFT) {
            result = 1
        } else {
            result = 5
        }

        return result
    }

    override fun get_j_case_zone(zone: Int): Int = 0

    fun is_left_zone(zone: Int): Boolean = zone == LEFT
}

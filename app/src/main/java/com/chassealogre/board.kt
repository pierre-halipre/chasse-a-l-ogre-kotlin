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

class Board(
    device: Device,
) : Layout(device.graphic.board, device) {
    companion object {
        const val NW: Int = 1
        const val W: Int = 2
        const val SW: Int = 3
        const val NE: Int = 4
        const val E: Int = 5
        const val SE: Int = 6
        const val C: Int = 7
    }

    val timer: Timer = Timer()
    val polygon_center: Polygon = Polygon()

    init {
        polygon_center.fill(3, 6, Shape.BOTTOM + Shape.RIGHT, Shape.NONE)
        polygon_center.fill(3, 7, Shape.FULL, Shape.NONE)
        polygon_center.fill(3, 8, Shape.FULL, Shape.NONE)
        polygon_center.fill(3, 9, Shape.TOP + Shape.RIGHT, Shape.NONE)
        polygon_center.fill(4, 6, Shape.BOTTOM + Shape.LEFT, Shape.NONE)
        polygon_center.fill(4, 7, Shape.FULL, Shape.NONE)
        polygon_center.fill(4, 8, Shape.FULL, Shape.NONE)
        polygon_center.fill(4, 9, Shape.TOP + Shape.LEFT, Shape.NONE)
    }

    override fun get_ratio_sprite(): Double = timer.get_ratio()

    override fun add_zones() {
        zones.add(NW)
        zones.add(W)
        zones.add(SW)
        zones.add(NE)
        zones.add(E)
        zones.add(SE)
    }

    override fun get_polygon(zone: Int): Polygon {
        var result: Polygon
        var flip_w: Boolean
        var flip_h: Boolean

        if (is_horizon_zone(zone)) {
            result = get_polygon_horizon()
            flip_w = !is_western_zone(zone)
            flip_h = false
        } else {
            result = get_polygon_corner()
            flip_w = is_eastern_zone(zone)
            flip_h = is_southern_zone(zone)
        }

        flip_polygon(result, flip_w, flip_h)

        return result
    }

    fun get_polygon_horizon(): Polygon {
        val result = Polygon()
        result.fill(0, 4, Shape.LEFT + Shape.BOTTOM, Shape.UP + Shape.LEFT)
        result.fill(0, 5, Shape.FULL, Shape.LEFT)
        result.fill(0, 6, Shape.FULL, Shape.LEFT)
        result.fill(0, 7, Shape.FULL, Shape.LEFT)
        result.fill(0, 8, Shape.FULL, Shape.LEFT)
        result.fill(0, 9, Shape.FULL, Shape.LEFT)
        result.fill(0, 10, Shape.FULL, Shape.LEFT)
        result.fill(0, 11, Shape.LEFT + Shape.TOP, Shape.DOWN + Shape.LEFT)
        result.fill(1, 5, Shape.LEFT + Shape.BOTTOM, Shape.UP)
        result.fill(1, 6, Shape.FULL, Shape.NONE)
        result.fill(1, 7, Shape.FULL, Shape.NONE)
        result.fill(1, 8, Shape.FULL, Shape.NONE)
        result.fill(1, 9, Shape.FULL, Shape.NONE)
        result.fill(1, 10, Shape.LEFT + Shape.TOP, Shape.DOWN)
        result.fill(2, 6, Shape.LEFT + Shape.BOTTOM, Shape.UP)
        result.fill(2, 7, Shape.FULL, Shape.RIGHT)
        result.fill(2, 8, Shape.FULL, Shape.RIGHT)
        result.fill(2, 9, Shape.LEFT + Shape.TOP, Shape.DOWN)

        return result
    }

    fun get_polygon_corner(): Polygon {
        val result = Polygon()
        result.fill(0, 3, Shape.RIGHT + Shape.BOTTOM, Shape.DOWN)
        result.fill(0, 4, Shape.RIGHT + Shape.TOP, Shape.NONE)
        result.fill(1, 2, Shape.RIGHT + Shape.BOTTOM, Shape.DOWN)
        result.fill(1, 3, Shape.FULL, Shape.NONE)
        result.fill(1, 4, Shape.FULL, Shape.NONE)
        result.fill(1, 5, Shape.RIGHT + Shape.TOP, Shape.NONE)
        result.fill(2, 1, Shape.RIGHT + Shape.BOTTOM, Shape.DOWN)
        result.fill(2, 2, Shape.FULL, Shape.NONE)
        result.fill(2, 3, Shape.FULL, Shape.NONE)
        result.fill(2, 4, Shape.FULL, Shape.NONE)
        result.fill(2, 5, Shape.FULL, Shape.NONE)
        result.fill(2, 6, Shape.RIGHT + Shape.TOP, Shape.NONE)
        result.fill(3, 0, Shape.RIGHT + Shape.BOTTOM, Shape.DOWN + Shape.RIGHT)
        result.fill(3, 1, Shape.FULL, Shape.RIGHT)
        result.fill(3, 2, Shape.FULL, Shape.RIGHT)
        result.fill(3, 3, Shape.FULL, Shape.RIGHT)
        result.fill(3, 4, Shape.FULL, Shape.RIGHT)
        result.fill(3, 5, Shape.FULL, Shape.RIGHT)
        result.fill(3, 6, Shape.LEFT + Shape.TOP, Shape.DOWN)

        return result
    }

    override fun find_zone(device: Device): Int {
        var result: Int

        if (is_center_zone(device)) {
            result = get_zone_center()
        } else {
            result = super.find_zone(device)
        }

        return result
    }

    fun is_center_zone(device: Device): Boolean {
        val x = device.event.x - x
        val y = device.event.y - y
        val i = device.graphic.to_i_case(x)
        val j = device.graphic.to_j_case(y)

        return is_in_polygon(polygon_center, x, y, i, j, device)
    }

    fun get_zone_center(): Int = C

    override fun get_i_case_zone(zone: Int): Int {
        var result: Int

        if (is_western_zone(zone)) {
            if (is_northern_zone(zone) || is_southern_zone(zone)) {
                result = 2
            } else {
                result = 1
            }
        } else if (is_northern_zone(zone) || is_southern_zone(zone)) {
            result = 4
        } else if (is_eastern_zone(zone)) {
            result = 5
        } else {
            result = 3
        }

        return result
    }

    override fun get_j_case_zone(zone: Int): Int {
        var result: Int

        if (is_northern_zone(zone)) {
            result = 3
        } else if (is_southern_zone(zone)) {
            result = 9
        } else {
            result = 6
        }

        return result
    }

    fun get_x_limit_zone(
        zone: Int,
        offset: Int,
        device: Device,
    ): Int {
        val i_case_zone = get_i_case_zone(zone)
        val i_case_zone_limit = i_case_zone + offset

        return get_x_case(i_case_zone_limit, device)
    }

    fun get_y_limit_zone(
        zone: Int,
        offset: Int,
        device: Device,
    ): Int {
        val j_case_zone = get_j_case_zone(zone)
        val j_case_zone_limit = j_case_zone + offset

        return get_y_case(j_case_zone_limit, device)
    }

    fun get_x_far_zone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (is_horizon_zone(zone)) {
            offset = 2
        } else {
            offset = 1
        }

        if (is_western_zone(zone)) {
            offset *= -1
        }

        return get_x_limit_zone(zone, offset, device)
    }

    fun get_y_far_zone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (is_horizon_zone(zone)) {
            offset = 0
        } else {
            offset = 3

            if (is_northern_zone(zone)) {
                offset *= -1
            }
        }

        return get_y_limit_zone(zone, offset, device)
    }

    fun get_x_near_zone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (is_horizon_zone(zone)) {
            offset = 1
        } else {
            offset = 0
        }

        if (!is_western_zone(zone)) {
            offset *= -1
        }

        return get_x_limit_zone(zone, offset, device)
    }

    fun get_y_near_zone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (is_horizon_zone(zone)) {
            offset = 0
        } else {
            offset = 1

            if (!is_northern_zone(zone)) {
                offset *= -1
            }
        }

        return get_y_limit_zone(zone, offset, device)
    }

    fun is_northern_zone(zone: Int): Boolean = zone == NW || zone == NE

    fun is_southern_zone(zone: Int): Boolean = zone == SW || zone == SE

    fun is_western_zone(zone: Int): Boolean {
        val result = zone == NW || zone == W || zone == SW

        return result
    }

    fun is_eastern_zone(zone: Int): Boolean {
        val result = zone == NE || zone == E || zone == SE

        return result
    }

    fun is_horizon_zone(zone: Int): Boolean = zone == W || zone == E

    fun get_zone_inverse(zone: Int): Int {
        var result: Int

        if (zone == NW) {
            result = SE
        } else if (zone == W) {
            result = E
        } else if (zone == SW) {
            result = NE
        } else if (zone == NE) {
            result = SW
        } else if (zone == E) {
            result = W
        } else {
            result = NW
        }

        return result
    }

    fun can_draw_zone(
        zone: Int,
        northern_zone: Boolean,
    ): Boolean = is_northern_zone(zone) == northern_zone

    fun fill_sprite(
        sprite: Sprite,
        j_start_sprite: Int,
        rasters: Rasters,
        device: Device,
    ) {
        val size_zones = zones.get_size()

        for (i in 0..<size_zones) {
            val zone = zones.get(i)
            val flip_w: Boolean
            val j_sprite: Int

            if (is_eastern_zone(zone)) {
                flip_w = true
                j_sprite = j_start_sprite + i - Math.half(size_zones)
            } else {
                flip_w = false
                j_sprite = j_start_sprite + i
            }

            sprite.add_sheet(j_sprite, flip_w, rasters, device)
        }
    }
}

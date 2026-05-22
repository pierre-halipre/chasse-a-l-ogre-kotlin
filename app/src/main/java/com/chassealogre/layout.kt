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

abstract class Layout(
    rasters_canvas: RastersCanvas,
    device: Device,
) : Canvas(rasters_canvas, device) {
    companion object {
        const val NONE: Int = 0
    }

    val zones: Array<Int> = Array<Int>()
    val polygons: Array<Polygon> = Array<Polygon>()

    init {
        add_zones()

        for (i in 0..<zones.get_size()) {
            val zone = zones.get(i)
            val polygon = get_polygon(zone)
            polygons.add(polygon)
        }
    }

    abstract fun add_zones()

    abstract fun get_polygon(zone: Int): Polygon

    fun flip_polygon(
        polygon: Polygon,
        flip_w: Boolean,
        flip_h: Boolean,
    ) {
        for (i in 0..<polygon.get_size()) {
            val shape = polygon.get(i)

            if (flip_w) {
                shape.i = n_w_cases - 1 - shape.i
            }

            if (flip_h) {
                shape.j = n_h_cases - 1 - shape.j
            }

            shape.track = shape.get_flags_inverse(shape.track, flip_w, flip_h)
            shape.lines = shape.get_flags_inverse(shape.lines, flip_w, flip_h)
        }
    }

    fun is_in_zones(zone: Int): Boolean {
        var result = false

        for (i in 0..<zones.get_size()) {
            if (zones.get(i) == zone) {
                result = true
            }
        }

        return result
    }

    open fun find_zone(device: Device): Int {
        var result = get_zone_none()

        val x = device.event.x - this.x
        val y = device.event.y - this.y
        val i = device.graphic.to_i_case(x)
        val j = device.graphic.to_j_case(y)

        for (k in 0..<zones.get_size()) {
            val zone = zones.get(k)
            val i_zone = to_i_zone(zone)
            val polygon = polygons.get(i_zone)

            if (is_in_polygon(polygon, x, y, i, j, device)) {
                result = zone
            }
        }

        return result
    }

    fun get_zone_none(): Int = NONE

    fun to_i_zone(zone: Int): Int {
        var result = NONE

        for (i in 0..<zones.get_size()) {
            if (zones.get(i) == zone) {
                result = i
            }
        }

        return result
    }

    fun is_in_polygon(
        polygon: Polygon,
        x: Int,
        y: Int,
        i: Int,
        j: Int,
        device: Device,
    ): Boolean {
        var result = false

        for (k in 0..<polygon.get_size()) {
            val shape = polygon.get(k)

            if (shape.i == i && shape.j == j && shape.is_in(x, y, device)) {
                result = true
            }
        }

        return result
    }

    fun get_x_zone(
        zone: Int,
        device: Device,
    ): Int {
        val i = get_i_case_zone(zone)

        return get_x_case(i, device)
    }

    fun get_x_case(
        i: Int,
        device: Device,
    ): Int {
        val w = device.graphic.to_w_cases(i)

        return x + w
    }

    fun get_y_zone(
        zone: Int,
        device: Device,
    ): Int {
        val j = get_j_case_zone(zone)

        return get_y_case(j, device)
    }

    fun get_y_case(
        j: Int,
        device: Device,
    ): Int {
        val h = device.graphic.to_h_cases(j)

        return y + h
    }

    abstract fun get_i_case_zone(zone: Int): Int

    abstract fun get_j_case_zone(zone: Int): Int
}

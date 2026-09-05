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
    rastersCanvas: Graphic.RastersCanvas,
    device: Device,
) : Canvas(rastersCanvas, device) {
    companion object {
        const val NONE: Int = 0
    }

    val zones: Array<Int> = Array<Int>()
    val polygons: Array<Polygon> = Array<Polygon>()

    init {
        addZones()

        for (i in 0..<zones.getSize()) {
            val zone = zones.get(i)
            val polygon = getPolygon(zone)
            polygons.add(polygon)
        }
    }

    abstract fun addZones()

    abstract fun getPolygon(zone: Int): Polygon

    fun flipPolygon(
        polygon: Polygon,
        flipW: Boolean,
        flipH: Boolean,
    ) {
        for (i in 0..<polygon.getSize()) {
            val shape = polygon.get(i)

            if (flipW) {
                shape.i = nWCases - 1 - shape.i
            }

            if (flipH) {
                shape.j = nHCases - 1 - shape.j
            }

            shape.track = shape.getFlagsInverse(shape.track, flipW, flipH)
            shape.lines = shape.getFlagsInverse(shape.lines, flipW, flipH)
        }
    }

    fun isInZones(zone: Int): Boolean {
        var result = false

        for (i in 0..<zones.getSize()) {
            if (zones.get(i) == zone) {
                result = true
            }
        }

        return result
    }

    open fun findZone(device: Device): Int {
        var result = getZoneNone()

        val x = device.event.x - this.x
        val y = device.event.y - this.y
        val i = device.graphic.toICase(x)
        val j = device.graphic.toJCase(y)

        for (k in 0..<zones.getSize()) {
            val zone = zones.get(k)
            val iZone = toIZone(zone)
            val polygon = polygons.get(iZone)

            if (isInPolygon(polygon, x, y, i, j, device)) {
                result = zone
            }
        }

        return result
    }

    fun getZoneNone(): Int = NONE

    fun toIZone(zone: Int): Int {
        var result = NONE

        for (i in 0..<zones.getSize()) {
            if (zones.get(i) == zone) {
                result = i
            }
        }

        return result
    }

    fun isInPolygon(
        polygon: Polygon,
        x: Int,
        y: Int,
        i: Int,
        j: Int,
        device: Device,
    ): Boolean {
        var result = false

        for (k in 0..<polygon.getSize()) {
            val shape = polygon.get(k)

            if (shape.i == i && shape.j == j && shape.isIn(x, y, device)) {
                result = true
            }
        }

        return result
    }

    fun getXZone(
        zone: Int,
        device: Device,
    ): Int {
        val i = getICaseZone(zone)

        return getXCase(i, device)
    }

    fun getXCase(
        i: Int,
        device: Device,
    ): Int {
        val w = device.graphic.toWCases(i)

        return x + w
    }

    fun getYZone(
        zone: Int,
        device: Device,
    ): Int {
        val j = getJCaseZone(zone)

        return getYCase(j, device)
    }

    fun getYCase(
        j: Int,
        device: Device,
    ): Int {
        val h = device.graphic.toHCases(j)

        return y + h
    }

    abstract fun getICaseZone(zone: Int): Int

    abstract fun getJCaseZone(zone: Int): Int
}

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
    val polygonCenter: Polygon = Polygon()

    init {
        polygonCenter.fill(3, 6, Shape.BOTTOM + Shape.RIGHT, Shape.NONE)
        polygonCenter.fill(3, 7, Shape.FULL, Shape.NONE)
        polygonCenter.fill(3, 8, Shape.FULL, Shape.NONE)
        polygonCenter.fill(3, 9, Shape.TOP + Shape.RIGHT, Shape.NONE)
        polygonCenter.fill(4, 6, Shape.BOTTOM + Shape.LEFT, Shape.NONE)
        polygonCenter.fill(4, 7, Shape.FULL, Shape.NONE)
        polygonCenter.fill(4, 8, Shape.FULL, Shape.NONE)
        polygonCenter.fill(4, 9, Shape.TOP + Shape.LEFT, Shape.NONE)
    }

    override fun getSpriteRatio(): Double = timer.getRatio()

    override fun addZones() {
        zones.add(NW)
        zones.add(W)
        zones.add(SW)
        zones.add(NE)
        zones.add(E)
        zones.add(SE)
    }

    override fun getPolygon(zone: Int): Polygon {
        var result: Polygon
        var flipW: Boolean
        var flipH: Boolean

        if (isHorizonZone(zone)) {
            result = getPolygonHorizon()
            flipW = !isWesternZone(zone)
            flipH = false
        } else {
            result = getPolygonCorner()
            flipW = isEasternZone(zone)
            flipH = isSouthernZone(zone)
        }

        flipPolygon(result, flipW, flipH)

        return result
    }

    fun getPolygonHorizon(): Polygon {
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

    fun getPolygonCorner(): Polygon {
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

    override fun findZone(device: Device): Int {
        var result: Int

        if (isCenterZone(device)) {
            result = getZoneCenter()
        } else {
            result = super.findZone(device)
        }

        return result
    }

    fun isCenterZone(device: Device): Boolean {
        val x = device.event.x - x
        val y = device.event.y - y
        val i = device.graphic.toICase(x)
        val j = device.graphic.toJCase(y)

        return isInPolygon(polygonCenter, x, y, i, j, device)
    }

    fun getZoneCenter(): Int = C

    override fun getICaseZone(zone: Int): Int {
        var result: Int

        if (isWesternZone(zone)) {
            if (isNorthernZone(zone) || isSouthernZone(zone)) {
                result = 2
            } else {
                result = 1
            }
        } else if (isNorthernZone(zone) || isSouthernZone(zone)) {
            result = 4
        } else if (isEasternZone(zone)) {
            result = 5
        } else {
            result = 3
        }

        return result
    }

    override fun getJCaseZone(zone: Int): Int {
        var result: Int

        if (isNorthernZone(zone)) {
            result = 3
        } else if (isSouthernZone(zone)) {
            result = 9
        } else {
            result = 6
        }

        return result
    }

    fun getXLimitZone(
        zone: Int,
        offset: Int,
        device: Device,
    ): Int {
        val iCaseZone = getICaseZone(zone)
        val iCaseZoneLimit = iCaseZone + offset

        return getXCase(iCaseZoneLimit, device)
    }

    fun getYLimitZone(
        zone: Int,
        offset: Int,
        device: Device,
    ): Int {
        val jCaseZone = getJCaseZone(zone)
        val jCaseZoneLimit = jCaseZone + offset

        return getYCase(jCaseZoneLimit, device)
    }

    fun getXFarZone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (isHorizonZone(zone)) {
            offset = 2
        } else {
            offset = 1
        }

        if (isWesternZone(zone)) {
            offset *= -1
        }

        return getXLimitZone(zone, offset, device)
    }

    fun getYFarZone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (isHorizonZone(zone)) {
            offset = 0
        } else {
            offset = 3

            if (isNorthernZone(zone)) {
                offset *= -1
            }
        }

        return getYLimitZone(zone, offset, device)
    }

    fun getXNearZone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (isHorizonZone(zone)) {
            offset = 1
        } else {
            offset = 0
        }

        if (!isWesternZone(zone)) {
            offset *= -1
        }

        return getXLimitZone(zone, offset, device)
    }

    fun getYNearZone(
        zone: Int,
        device: Device,
    ): Int {
        var offset: Int

        if (isHorizonZone(zone)) {
            offset = 0
        } else {
            offset = 1

            if (!isNorthernZone(zone)) {
                offset *= -1
            }
        }

        return getYLimitZone(zone, offset, device)
    }

    fun isNorthernZone(zone: Int): Boolean = zone == NW || zone == NE

    fun isSouthernZone(zone: Int): Boolean = zone == SW || zone == SE

    fun isWesternZone(zone: Int): Boolean {
        val result = zone == NW || zone == W || zone == SW

        return result
    }

    fun isEasternZone(zone: Int): Boolean {
        val result = zone == NE || zone == E || zone == SE

        return result
    }

    fun isHorizonZone(zone: Int): Boolean = zone == W || zone == E

    fun getZoneInverse(zone: Int): Int {
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

    fun canDrawZone(
        zone: Int,
        northernZone: Boolean,
    ): Boolean = isNorthernZone(zone) == northernZone

    fun fillSprite(
        sprite: Sprite,
        jStartSprite: Int,
        rasters: Graphic.Rasters,
        device: Device,
    ) {
        val sizeZones = zones.getSize()

        for (i in 0..<sizeZones) {
            val zone = zones.get(i)
            val flipW: Boolean
            val jSprite: Int

            if (isEasternZone(zone)) {
                flipW = true
                jSprite = jStartSprite + i - Math.half(sizeZones)
            } else {
                flipW = false
                jSprite = jStartSprite + i
            }

            sprite.addSheet(jSprite, flipW, rasters, device)
        }
    }
}

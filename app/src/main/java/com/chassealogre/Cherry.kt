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
        val rastersZone = device.graphic.cherry
        sprite = Sprite(rastersZone)
        board.fillSprite(sprite, 0, rastersZone, device)
    }

    fun draw(
        ratio: Double,
        zone: Int,
        board: Board,
        device: Device,
    ) {
        val zoneInverse = board.getZoneInverse(zone)
        val iSheet = board.toIZone(zoneInverse)
        val zoneStart = board.getZoneCenter()
        val xStart = board.getXZone(zoneStart, device)
        val yStart = board.getYZone(zoneStart, device)
        val xEnd = board.getXFarZone(zone, device)
        val yEnd = board.getYFarZone(zone, device)
        val x = Math.distance(xStart, xEnd, ratio)
        val y = Math.distance(yStart, yEnd, ratio)
        sprite.draw(iSheet, ratio, x, y, device)
    }
}

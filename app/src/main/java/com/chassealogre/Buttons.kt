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

class Buttons(
    device: Device,
) {
    var sprite: Sprite

    init {
        val rastersZone = device.graphic.buttons
        sprite = Sprite(rastersZone)

        for (iSheet in 0..<6) {
            val jSprite = iSheet
            sprite.addSheet(jSprite, false, rastersZone, device)
        }
    }

    fun drawSprites(
        layout: Layout,
        party: Party,
        device: Device,
    ) {
        layout as Panel
        val ratio = 0.0

        for (i in 0..<layout.zones.getSize()) {
            val zone = layout.zones.get(i)
            var iSheet: Int

            if (layout.isLeftZone(zone)) {
                if (party.isHome() || party.isQuit()) {
                    iSheet = 0
                } else if (party.isPlay()) {
                    iSheet = 1
                } else if (party.isPause()) {
                    iSheet = 2
                } else {
                    iSheet = 3
                }
            } else if (party.isHome() || party.isQuit()) {
                iSheet = 5
            } else {
                iSheet = 4
            }

            val x = layout.getXZone(zone, device)
            val y = layout.getYZone(zone, device)
            sprite.draw(iSheet, ratio, x, y, device)
        }
    }
}

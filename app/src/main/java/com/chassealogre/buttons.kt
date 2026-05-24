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
        val rasters_zone = device.graphic.buttons
        sprite = Sprite(rasters_zone)

        for (i_sheet in 0..<6) {
            val j_sprite = i_sheet
            sprite.add_sheet(j_sprite, false, rasters_zone, device)
        }
    }

    fun draw_sprites(
        layout: Layout,
        party: Party,
        device: Device,
    ) {
        layout as Panel
        val ratio = 0.0

        for (i in 0..<layout.zones.get_size()) {
            val zone = layout.zones.get(i)
            var i_sheet: Int

            if (layout.is_left_zone(zone)) {
                if (party.is_home() || party.is_quit()) {
                    i_sheet = 0
                } else if (party.is_play()) {
                    i_sheet = 1
                } else if (party.is_pause()) {
                    i_sheet = 2
                } else {
                    i_sheet = 3
                }
            } else if (party.is_home() || party.is_quit()) {
                i_sheet = 5
            } else {
                i_sheet = 4
            }

            val x = layout.get_x_zone(zone, device)
            val y = layout.get_y_zone(zone, device)
            sprite.draw(i_sheet, ratio, x, y, device)
        }
    }
}

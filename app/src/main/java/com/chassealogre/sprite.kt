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

class Sprite(
    rasters: Rasters,
) : Array<Array<Array<Image>>>() {
    init {
        var n_sheets = 0

        while (n_sheets < rasters.get_size()) {
            add(Array())
            n_sheets += 1
        }
    }

    fun add_sheet(
        j_sprite: Int,
        flip_w: Boolean,
        rasters: Rasters,
        device: Device,
    ) {
        for (mode in 0..<get_size()) {
            val sheet = Array<Image>()
            val sheets = get(mode)
            sheets.add(sheet)

            for (i_image in 0..<rasters.get_n_sprites(mode)) {
                val i_sprite = rasters.get_i_sprite(mode, i_image)
                val graphic = device.graphic
                val image: Image
                image = rasters.pick(mode, i_sprite, j_sprite, flip_w, graphic)
                sheet.add(image)
            }
        }
    }

    fun draw(
        i_sheet: Int,
        ratio: Double,
        x: Int,
        y: Int,
        device: Device,
    ) {
        val sheets = get(device.design.mode)
        val sheet = sheets.get(i_sheet)
        val n_images = sheet.get_size()
        val i_image = Math.floor(n_images * ratio)
        val image = sheet.get(i_image)
        image.on_window(x, y, device.graphic.window)
    }
}

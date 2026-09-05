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
    rasters: Graphic.Rasters,
) : Array<Array<Array<Image>>>() {
    init {
        var nSheets = 0

        while (nSheets < rasters.getSize()) {
            add(Array())
            nSheets += 1
        }
    }

    fun addSheet(
        jSprite: Int,
        flipW: Boolean,
        rasters: Graphic.Rasters,
        device: Device,
    ) {
        for (mode in 0..<getSize()) {
            val sheet = Array<Image>()
            val sheets = get(mode)
            sheets.add(sheet)
            var image: Image

            for (iImage in 0..<rasters.getNSprites(mode)) {
                val iSprite = rasters.getISprite(mode, iImage)
                val graphic = device.graphic
                image = rasters.pick(mode, iSprite, jSprite, flipW, graphic)
                sheet.add(image)
            }
        }
    }

    fun draw(
        iSheet: Int,
        ratio: Double,
        x: Int,
        y: Int,
        device: Device,
    ) {
        val sheets = get(device.design.mode)
        val sheet = sheets.get(iSheet)
        val nImages = sheet.getSize()
        val iImage = Math.floor(nImages * ratio)
        val image = sheet.get(iImage)
        image.onWindow(x, y, device.graphic.window)
    }
}

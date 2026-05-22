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

abstract class Gui(
    layout_init: Layout,
) {
    val layout: Layout = layout_init

    abstract fun reset()

    abstract fun update(
        zone: Int,
        device: Device,
    )

    fun draw(
        pen: Pen,
        device: Device,
    ) {
        layout.draw(false, device)
        draw_elements(device)
        layout.draw(true, device)

        for (i in 0..<layout.polygons.get_size()) {
            val polygon = layout.polygons.get(i)
            pen.outline_polygon(polygon, layout, device)
        }
    }

    abstract fun draw_elements(device: Device)

    fun play() {
        if (need_play()) {
            val audio = get_audio()
            audio.songs.play()
        }
    }

    abstract fun need_play(): Boolean

    abstract fun get_audio(): Audio
}

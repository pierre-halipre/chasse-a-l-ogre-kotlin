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

class Clock(
    frame_rate_init: Int,
) {
    val frame_rate: Int = frame_rate_init
    val thread: Thread = Thread()
    val time_animation_min: Int = 250
    val time_animation_max: Int = 750

    fun get_refresh_time(): Int = Math.ceil(1000 / frame_rate)

    fun to_ticks(milliseconds: Int): Int {
        val refresh_time = get_refresh_time()

        return Math.ceil(milliseconds / refresh_time)
    }

    fun get_n_animations_min(): Int = to_ticks(time_animation_min)
}

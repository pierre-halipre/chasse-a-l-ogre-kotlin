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

class Timer {
    var counts: Int = 0
    var threshold: Int = 0

    fun start(
        milliseconds: Int,
        device: Device,
    ) {
        counts = 0
        threshold = device.clock.to_ticks(milliseconds)
    }

    fun stop() {
        counts = 0
        threshold = 0
    }

    fun is_on(): Boolean = counts < threshold

    fun update() {
        counts += 1
    }

    fun get_ratio(): Double = Math.to_double(counts) / threshold

    fun get_ratio_inverse(): Double {
        val result = Math.to_double(threshold - 1 - counts) / threshold

        return result
    }

    fun is_blink(device: Device): Boolean {
        val ratio = get_ratio()
        val n_animations_min = device.clock.get_n_animations_min()
        val threshold_blinks = Math.to_double(threshold) * 2
        val n_blinks = Math.ceil(threshold_blinks / n_animations_min)

        return Math.floor(ratio * n_blinks) % 2 == 1
    }
}

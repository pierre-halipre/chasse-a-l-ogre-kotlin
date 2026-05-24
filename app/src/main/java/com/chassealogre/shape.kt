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

class Shape(
    i_init: Int,
    j_init: Int,
    track_init: Int,
    lines_init: Int,
) {
    companion object {
        const val NONE: Int = 0
        const val TOP: Int = 1
        const val BOTTOM: Int = 2
        const val LEFT: Int = 4
        const val RIGHT: Int = 8
        const val UP: Int = 16
        const val DOWN: Int = 32
        const val FULL: Int = 64
    }

    var i: Int = i_init
    var j: Int = j_init
    var track: Int = track_init
    var lines: Int = lines_init

    fun is_in(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val is_at_left = is_at_left(x, y, device)
        val is_track_left = is_track_left()
        val in_left = is_at_left && is_track_left
        val in_right = !is_at_left && !is_track_left

        return is_track_full() || in_left || in_right
    }

    fun is_at_left(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val is_at_bottom = is_at_bottom(x, y, device)
        val is_track_up = is_track_up()

        return (is_at_bottom && is_track_up) || (!is_at_bottom && !is_track_up)
    }

    fun is_at_bottom(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val graphic = device.graphic
        val w_case = graphic.w_case
        val h_case = graphic.h_case
        var a: Double
        var b: Int

        if (is_track_up()) {
            a = Math.to_double(h_case - 1) / (w_case - 1)
            b = 0
        } else {
            a = Math.to_double(1 - h_case) / (w_case - 1)
            b = h_case - 1
        }

        return Math.round(a * (x % w_case) + b) <= y % h_case
    }

    fun is_track_up(): Boolean {
        val result = is_track(BOTTOM + LEFT) || is_track(TOP + RIGHT)

        return result
    }

    fun is_track(track: Int): Boolean = Math.is_flag(track, track)

    fun is_track_left(): Boolean = is_track_full() || is_track(LEFT)

    fun is_track_full(): Boolean = is_track(FULL)

    fun get_flags_inverse(
        flags: Int,
        flip_w: Boolean,
        flip_h: Boolean,
    ): Int {
        var flags_flip = NONE

        if (flip_w && Math.has_flag(flags, LEFT + RIGHT)) {
            flags_flip += LEFT + RIGHT
        }

        if (flip_h && Math.has_flag(flags, TOP + BOTTOM)) {
            flags_flip += TOP + BOTTOM
        }

        val flip_diagonal = (flip_w && !flip_h) || (!flip_w && flip_h)

        if (flip_diagonal && Math.has_flag(flags, UP + DOWN)) {
            flags_flip += UP + DOWN
        }

        return flags.xor(flags_flip)
    }
}

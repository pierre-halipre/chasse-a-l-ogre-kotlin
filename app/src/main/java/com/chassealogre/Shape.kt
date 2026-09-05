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
    iInit: Int,
    jInit: Int,
    trackInit: Int,
    linesInit: Int,
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

    var i: Int = iInit
    var j: Int = jInit
    var track: Int = trackInit
    var lines: Int = linesInit

    fun isIn(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val isAtLeft = isAtLeft(x, y, device)
        val isTrackLeft = isTrackLeft()
        val inLeft = isAtLeft && isTrackLeft
        val inRight = !isAtLeft && !isTrackLeft

        return isTrackFull() || inLeft || inRight
    }

    fun isAtLeft(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val isAtBottom = isAtBottom(x, y, device)
        val isTrackUp = isTrackUp()

        return (isAtBottom && isTrackUp) || (!isAtBottom && !isTrackUp)
    }

    fun isAtBottom(
        x: Int,
        y: Int,
        device: Device,
    ): Boolean {
        val graphic = device.graphic
        val wCase = graphic.wCase
        val hCase = graphic.hCase
        var a: Double
        var b: Int

        if (isTrackUp()) {
            a = Math.toDouble(hCase - 1) / (wCase - 1)
            b = 0
        } else {
            a = Math.toDouble(1 - hCase) / (wCase - 1)
            b = hCase - 1
        }

        return Math.round(a * (x % wCase) + b) <= y % hCase
    }

    fun isTrackUp(): Boolean {
        val result = isTrack(BOTTOM + LEFT) || isTrack(TOP + RIGHT)

        return result
    }

    fun isTrack(track: Int): Boolean = Math.isFlag(track, track)

    fun isTrackLeft(): Boolean = isTrackFull() || isTrack(LEFT)

    fun isTrackFull(): Boolean = isTrack(FULL)

    fun getFlagsInverse(
        flags: Int,
        flipW: Boolean,
        flipH: Boolean,
    ): Int {
        var flagsFlip = NONE

        if (flipW && Math.hasFlag(flags, LEFT + RIGHT)) {
            flagsFlip += LEFT + RIGHT
        }

        if (flipH && Math.hasFlag(flags, TOP + BOTTOM)) {
            flagsFlip += TOP + BOTTOM
        }

        val flipDiagonal = (flipW && !flipH) || (!flipW && flipH)

        if (flipDiagonal && Math.hasFlag(flags, UP + DOWN)) {
            flagsFlip += UP + DOWN
        }

        return flags.xor(flagsFlip)
    }
}

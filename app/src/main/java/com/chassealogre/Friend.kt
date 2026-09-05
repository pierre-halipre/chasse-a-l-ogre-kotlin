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

class Friend(
    speedWalkInit: Double,
    speedWaitInit: Double,
    board: Board,
    rastersZone: Graphic.RastersZone,
    device: Device,
) : Monster(speedWalkInit, speedWaitInit, board, rastersZone, device) {
    override fun isEnemy(): Boolean = false

    override fun getAccuracyTally(): Double {
        var step: Int

        if (isCome()) {
            step = 0
        } else if (isWait()) {
            step = 1
        } else {
            step = 2
        }

        val ratio = timer.getRatio()

        return (step + ratio) / 3
    }

    override fun getScoreTally(): Int {
        var result: Int

        if (isCome()) {
            result = 0
        } else if (isWait()) {
            result = 1
        } else if (isLeave()) {
            result = 2
        } else {
            result = 3
        }

        return result
    }
}

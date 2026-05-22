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

class Enemy(
    speed_walk_init: Double,
    speed_wait_init: Double,
    board: Board,
    rasters_zone: RastersZone,
    device: Device,
) : Monster(speed_walk_init, speed_wait_init, board, rasters_zone, device) {
    override fun is_enemy(): Boolean = true

    override fun get_accuracy_tally(): Double {
        var step: Int

        if (is_come()) {
            step = 3
        } else if (is_wait()) {
            step = 2
        } else {
            step = 1
        }

        val ratio = timer.get_ratio()

        return (step - ratio) / 3
    }

    override fun get_score_tally(): Int {
        var result: Int

        if (is_come()) {
            result = 3
        } else if (is_wait()) {
            result = 2
        } else if (is_leave()) {
            result = 1
        } else {
            result = 0
        }

        return result
    }
}

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

class Tally {
    var counts: Int = 0
    var accuracy: Double = 0.0
    var score: Int = 0
    var progress: Double = 0.0
    val progress_max: Int = 6

    fun update_score(monster: Monster) {
        val accuracy_monster = monster.get_accuracy_tally()
        val sum_accuracy = accuracy * counts + accuracy_monster
        counts += 1
        accuracy = sum_accuracy / counts

        val score_monster = monster.get_score_tally()
        score += Math.to_int(Math.pow(score_monster, 2))
    }

    fun update_progress(n_updates: Int) {
        val progress_update = 1 - accuracy
        progress += Math.pow(progress_update, 2) * n_updates
    }

    fun is_end(): Boolean = progress >= progress_max

    fun get_level(): Int {
        var result: Int

        if (progress < progress_max / 10) {
            result = 1
        } else if (progress >= 6 * progress_max / 10) {
            result = 3
        } else {
            result = 2
        }

        return result
    }

    fun get_time_base(device: Device): Int {
        val clock = device.clock
        val time_range = clock.time_animation_max - clock.time_animation_min
        val ratio = accuracy * progress / progress_max

        return Math.ceil(clock.time_animation_max - time_range * ratio)
    }
}

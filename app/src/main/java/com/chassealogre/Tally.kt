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
    val progressMax: Int = 6

    fun updateScore(monster: Monster) {
        val accuracyMonster = monster.getAccuracyTally()
        val sumAccuracy = accuracy * counts + accuracyMonster
        counts += 1
        accuracy = sumAccuracy / counts

        val scoreMonster = monster.getScoreTally()
        score += Math.toInt(Math.pow(scoreMonster, 2))
    }

    fun updateProgress(nUpdates: Int) {
        val progressUpdate = 1 - accuracy
        progress += Math.pow(progressUpdate, 2) * nUpdates
    }

    fun isEnd(): Boolean = progress >= progressMax

    fun getLevel(): Int {
        var result: Int

        if (progress < progressMax / 10) {
            result = 1
        } else if (progress >= 6 * progressMax / 10) {
            result = 3
        } else {
            result = 2
        }

        return result
    }

    fun getTimeBase(device: Device): Int {
        val clock = device.clock
        val timeRange = clock.timeAnimationMax - clock.timeAnimationMin
        val ratio = accuracy * progress / progressMax

        return Math.ceil(clock.timeAnimationMax - timeRange * ratio)
    }
}

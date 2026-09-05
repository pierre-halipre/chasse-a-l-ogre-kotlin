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

class Action {
    var zone: Int = Layout.NONE
    var needQuit: Boolean = false
    var countsChange: Int = 0
    var ticksChange: Int = 0

    fun setQuit() {
        needQuit = true
    }

    fun checkChangeDesign(
        game: Game,
        device: Device,
    ) {
        game.layout as Board

        if (zone == game.layout.getZoneCenter()) {
            val clock = device.clock
            val timeRange = clock.timeAnimationMax - clock.timeAnimationMin
            val nCountsChange = clock.toTicks(timeRange)

            if (game.ticks - ticksChange <= nCountsChange) {
                countsChange += 1
            } else {
                countsChange = 1
            }

            ticksChange = game.ticks
        } else {
            countsChange = 0
        }
    }

    fun needChange(): Boolean = countsChange == 3

    fun setZoneEvent(
        frame: Frame,
        menu: Menu,
        game: Game,
        device: Device,
    ) {
        if (frame.border.isIn(device)) {
            var layout: Layout

            if (menu.layout.isIn(device)) {
                layout = menu.layout
            } else {
                layout = game.layout
            }

            zone = layout.findZone(device)
        }
    }

    fun getZoneDemo(game: Game): Int {
        var result = Layout.NONE

        if (canHitDemo(game.logger)) {
            var monsterBest: Monster? = null
            val nFriends = game.monsters.getNFriends()

            for (rank in 0..<game.monsters.ranks) {
                val monster = game.monsters.get(rank)!!
                val canFall = canFallDemo(monster, nFriends)
                val isBetter = isBetter(monsterBest, monster)

                if (canFall && isBetter) {
                    monsterBest = monster
                    result = monster.zone
                }
            }
        }

        return result
    }

    fun isBetter(
        monsterBest: Monster?,
        monster: Monster,
    ): Boolean {
        val isNull = monsterBest == null

        return isNull || monsterBest.getSpeed() > monster.getSpeed()
    }

    fun canHitDemo(logger: Logger): Boolean {
        val inWaiting = logger.isWait() && isBetweenDemo(logger)
        val inFalling = logger.isFall() && !isBetweenDemo(logger)

        return inWaiting || inFalling
    }

    fun isBetweenDemo(character: Character): Boolean {
        val result = !isBeginDemo(character) && !isFinishDemo(character)

        return result
    }

    fun isBeginDemo(character: Character): Boolean {
        val ratio = character.timer.getRatio()

        return ratio < 1 / 3
    }

    fun isFinishDemo(character: Character): Boolean {
        val ratio = character.timer.getRatio()

        return ratio >= 2 / 3
    }

    fun canFallDemo(
        monster: Monster,
        nFriends: Int,
    ): Boolean {
        val canFall = monster.isEnemy() || nFriends == 2
        val inComing = monster.isCome() && isFinishDemo(monster)
        val inWaiting = monster.isWait() && isBetweenDemo(monster)
        val inLeaving = monster.isLeave() && isBeginDemo(monster)

        return canFall && (inComing || inWaiting || inLeaving)
    }
}

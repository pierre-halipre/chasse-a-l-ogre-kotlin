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

class Logger(
    board: Board,
    device: Device,
) : Character(21.0 / 7, 14.0 / 7, board, device.graphic.logger, device) {
    companion object {
        const val HIT_GOOD: Int = 6
        const val HIT_BAD: Int = 7
    }

    fun setHitGood() {
        changeStatus(HIT_GOOD)
    }

    fun isHitGood(): Boolean = isStatus(HIT_GOOD)

    fun setHitBad() {
        changeStatus(HIT_BAD)
    }

    fun isHitBad(): Boolean = isStatus(HIT_BAD)

    fun isHit(): Boolean = isHitGood() || isHitBad()

    override fun update(device: Device) {
        if (timer.isOn()) {
            timer.update()

            if (!timer.isOn()) {
                if (isLeave()) {
                    setOut()
                } else {
                    setWait(device)
                }
            }
        }
    }

    override fun getZonePosition(board: Board): Int = board.getZoneCenter()

    override fun draw(
        board: Board,
        device: Device,
    ) {
        val zonePosition = getZonePosition(board)
        val x = board.getXZone(zonePosition, device)
        val y = board.getYZone(zonePosition, device)
        drawSprite(x, y, board, device)
    }

    override fun canDrawSprite(device: Device): Boolean {
        val isNotBad = !isHitBad() || !timer.isBlink(device)

        return super.canDrawSprite(device) && isNotBad
    }

    override fun getISheetState(): Int {
        var result: Int

        if (isWait()) {
            result = 0
        } else if (isHit() || isCome() || isLeave()) {
            result = 6
        } else {
            result = 12
        }

        return result
    }

    override fun getISheetZone(board: Board): Int {
        val zoneSheet = board.getZoneInverse(zone)

        return board.toIZone(zoneSheet)
    }
}

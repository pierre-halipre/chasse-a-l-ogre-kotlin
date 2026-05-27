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

abstract class Monster(
    speedWalkInit: Double,
    speedWaitInit: Double,
    board: Board,
    rastersZone: Graphic.RastersZone,
    device: Device,
) : Character(speedWalkInit, speedWaitInit, board, rastersZone, device) {
    companion object {
        const val TOUCH: Int = 8
    }

    var x: Int = 0
    var y: Int = 0
    var iSheetZone: Int = board.getZoneNone()

    fun setTouch() {
        changeStatus(TOUCH)
    }

    fun isTouch(): Boolean = isStatus(TOUCH)

    override fun isEnd(): Boolean = super.isEnd() || isTouch()

    fun isTouchable(): Boolean = !isEnd() && !isFall()

    override fun start(
        zone: Int,
        timeBase: Int,
        board: Board,
        device: Device,
    ) {
        super.start(zone, timeBase, board, device)
        x = 0
        y = 0
        iSheetZone = board.getZoneNone()
    }

    override fun update(device: Device) {
        if (timer.isOn()) {
            timer.update()

            if (!timer.isOn()) {
                if (isCome()) {
                    setWait(device)
                } else if (isWait()) {
                    setLeave(device)
                } else if (isLeave()) {
                    setOut()
                } else if (isFall()) {
                    setTouch()
                }
            }
        }
    }

    override fun getZonePosition(board: Board): Int = zone

    override fun draw(
        board: Board,
        device: Device,
    ) {
        if (!isFall()) {
            zone = getZonePosition(board)
            val xStart = board.getXFarZone(zone, device)
            val yStart = board.getYFarZone(zone, device)
            val xEnd = board.getXZone(zone, device)
            val yEnd = board.getYZone(zone, device)
            val ratio = getRatioPosition()
            x = Math.distance(xStart, xEnd, ratio)
            y = Math.distance(yStart, yEnd, ratio)
            iSheetZone = getISheetZone(board)
        }

        if (device.design.mode == 0) {
            drawSprites(board, device)
        } else {
            drawSprite(x, y, board, device)
        }
    }

    fun drawSprites(
        board: Board,
        device: Device,
    ) {
        val zone = getZonePosition(board)

        for (position in 0..<3) {
            var iCase: Int
            var jCase: Int

            if (board.isHorizonZone(zone)) {
                iCase = (position + 1) % 2

                if (board.isWesternZone(zone)) {
                    iCase *= -1
                }

                jCase = position - 1
            } else {
                iCase = (position + 2) % 2

                if (board.isWesternZone(zone)) {
                    iCase *= -1
                }

                jCase = position

                if (board.isNorthernZone(zone)) {
                    jCase -= 2
                }
            }

            val w = device.graphic.toWCases(iCase)
            val h = device.graphic.toHCases(jCase)
            val xSprite = x + w
            val ySprite = y + h
            drawSprite(xSprite, ySprite, board, device)
        }
    }

    override fun getISheetState(): Int {
        var result: Int

        if (isCome() || isLeave()) {
            result = 0
        } else if (isWait()) {
            result = 6
        } else {
            result = 12
        }

        return result
    }

    override fun getISheetZone(board: Board): Int {
        var result: Int

        if (isFall()) {
            result = iSheetZone
        } else {
            var zoneSheet: Int

            if (isLeave()) {
                zoneSheet = board.getZoneInverse(zone)
            } else {
                zoneSheet = zone
            }

            result = board.toIZone(zoneSheet)
        }

        return result
    }

    abstract fun isEnemy(): Boolean

    abstract fun getAccuracyTally(): Double

    abstract fun getScoreTally(): Int
}

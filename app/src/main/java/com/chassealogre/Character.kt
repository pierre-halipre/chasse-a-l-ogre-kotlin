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

abstract class Character(
    speedWalkInit: Double,
    speedWaitInit: Double,
    board: Board,
    rastersZone: Graphic.RastersZone,
    device: Device,
) : State() {
    companion object {
        const val COME: Int = 1
        const val WAIT: Int = 2
        const val LEAVE: Int = 3
        const val OUT: Int = 4
        const val FALL: Int = 5
        const val SPEED_FALL: Double = 7.0 / 7
    }

    val speedWalk: Double = speedWalkInit
    val speedWait: Double = speedWaitInit
    var zone: Int = board.getZoneNone()
    val timer: Timer = Timer()
    var timeBase: Int = 0
    var sprite: Sprite = Sprite(rastersZone)

    init {
        board.fillSprite(sprite, 0, rastersZone, device)
        board.fillSprite(sprite, 3, rastersZone, device)
        board.fillSprite(sprite, 6, rastersZone, device)
    }

    override fun setNone() {
        super.setNone()
        timer.stop()
    }

    fun setCome(device: Device) {
        changeStatus(COME)
        val time = getTime()
        timer.start(time, device)
    }

    fun isCome(): Boolean = isStatus(COME)

    fun setWait(device: Device) {
        changeStatus(WAIT)
        val time = getTime()
        timer.start(time, device)
    }

    fun isWait(): Boolean = isStatus(WAIT)

    fun setLeave(device: Device) {
        changeStatus(LEAVE)
        val time = getTime()
        timer.start(time, device)
    }

    fun isLeave(): Boolean = isStatus(LEAVE)

    fun setOut() {
        changeStatus(OUT)
    }

    fun isOut(): Boolean = isStatus(OUT)

    fun setFall(device: Device) {
        changeStatus(FALL)
        val time = getTime()
        timer.start(time, device)
    }

    fun isFall(): Boolean = isStatus(FALL)

    open fun isEnd(): Boolean = isNone() || isOut()

    fun getTime(): Int {
        val speed = getSpeed()
        val time = Math.ceil(speed * timeBase)
        val nAnimations = Math.round(speed)
        val timeAnimation = Math.ceil(time / nAnimations)

        return nAnimations * timeAnimation
    }

    fun getSpeed(): Double {
        var result: Double

        if (isCome() || isLeave()) {
            result = speedWalk
        } else if (isWait()) {
            result = speedWait
        } else {
            result = SPEED_FALL
        }

        return result
    }

    open fun start(
        zone: Int,
        timeBase: Int,
        board: Board,
        device: Device,
    ) {
        this.zone = zone
        this.timeBase = timeBase
        setCome(device)
    }

    abstract fun update(device: Device)

    abstract fun getZonePosition(board: Board): Int

    fun getRatioPosition(): Double {
        var result: Double

        if (isCome()) {
            result = timer.getRatio()
        } else if (isLeave()) {
            result = timer.getRatioInverse()
        } else {
            result = 1.0
        }

        return result
    }

    abstract fun draw(
        board: Board,
        device: Device,
    )

    fun drawSprite(
        x: Int,
        y: Int,
        board: Board,
        device: Device,
    ) {
        if (canDrawSprite(device)) {
            val iSheetState = getISheetState()
            val iSheetZone = getISheetZone(board)
            val iSheet = iSheetState + iSheetZone
            val ratio = getSpriteRatio()
            sprite.draw(iSheet, ratio, x, y, device)
        }
    }

    open fun canDrawSprite(device: Device): Boolean {
        val result = !isFall() || !timer.isBlink(device)

        return result
    }

    abstract fun getISheetState(): Int

    abstract fun getISheetZone(board: Board): Int

    fun getSpriteRatio(): Double {
        var result: Double

        if (isFall()) {
            val halfThreshold = Math.round(timer.threshold / 2)

            if (timer.counts < halfThreshold) {
                result = timer.getRatio()
            } else {
                result = Math.toDouble(halfThreshold) / timer.threshold
            }
        } else {
            val nAnimations = Math.round(getSpeed())
            val threshold = Math.ceil(timer.threshold / nAnimations)
            result = Math.toDouble(timer.counts % threshold) / threshold
        }

        return result
    }
}

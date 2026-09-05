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

class Game(
    device: Device,
) : Gui(Board(device)) {
    val greyedOut: Image = device.graphic.greyedOut
    val logger: Logger = Logger(layout as Board, device)
    val monsters: Monsters = Monsters(layout as Board, device)
    val fence: Fence = Fence(layout as Board, device)

    var loggerCome: Audio
    var loggerLeave: Audio
    var friendFall: Audio
    var enemyFall: Audio
    var friendCome: Audio
    var enemyCome: Audio

    val tally: Tally = Tally()
    var ticks: Int = 0

    init {
        val music = device.music
        loggerCome = Audio(music.loggerCome)
        loggerLeave = Audio(music.loggerLeave)
        friendFall = Audio(music.friendFall)
        enemyFall = Audio(music.enemyFall)
        friendCome = Audio(music.friendCome)
        enemyCome = Audio(music.enemyCome)
    }

    override fun reset() {
        layout as Board
        layout.timer.stop()
        logger.setNone()
        monsters.reset()

        tally.accuracy = 0.0
        tally.sequence = 0.0
        tally.score = 0
        tally.progress = 0.0

        ticks = 0
    }

    override fun update(
        zone: Int,
        device: Device,
    ) {
        if (!isCutscene()) {
            updateMonsters(zone, device)
        }

        logger.timeBase = tally.getTimeBase(device)
        val isEnd = tally.isEnd() && monsters.isEnd()

        if (!isCutscene() && !isEnd && layout.isInZones(zone)) {
            updateLoggerAction(zone, device)
        } else {
            updateLogger(device)
        }

        if (!isCutscene() && !tally.isEnd()) {
            updateMonstersStart(device)
        }

        updateLayout(device)

        if (ticks < device.clock.getTicksMax()) {
            ticks += 1
        }
    }

    fun isCutscene(): Boolean {
        val result = !logger.isEnd() && (logger.isCome() || logger.isLeave())

        return result
    }

    fun updateMonsters(
        zone: Int,
        device: Device,
    ) {
        var nUpdatesTally = 0

        for (rank in 0..<monsters.ranks) {
            val monster = monsters.get(rank)!!

            if (!monster.isEnd()) {
                if (!monster.isFall() && monster.zone == zone) {
                    tally.updateScore(monster)
                    nUpdatesTally += 1

                    monster.setFall(device)

                    setPlayAudioMonster(monster)
                } else {
                    monster.update(device)

                    if (monster.isOut()) {
                        tally.updateScore(monster)
                        nUpdatesTally += 1
                    }
                }
            }
        }

        tally.updateProgress(nUpdatesTally)
    }

    fun setPlayAudioMonster(monster: Monster) {
        var audio: Audio

        if (monster.isEnemy()) {
            if (monster.isCome()) {
                audio = enemyCome
            } else {
                audio = enemyFall
            }
        } else if (monster.isCome()) {
            audio = friendCome
        } else {
            audio = friendFall
        }

        audio.setPlay()
    }

    fun updateLoggerAction(
        zone: Int,
        device: Device,
    ) {
        logger.zone = zone
        var isHit = false

        for (rank in 0..<monsters.ranks) {
            val monster = monsters.get(rank)!!

            if (monster.isFall() && monster.zone == logger.zone) {
                if (monster.isEnemy()) {
                    logger.setHitGood()
                } else {
                    logger.setHitBad()
                }

                logger.timer.counts = monster.timer.counts
                logger.timer.threshold = monster.timer.threshold
                isHit = true
            }
        }

        if (!logger.isWait() && !logger.isFall() && !isHit) {
            logger.setWait(device)
        }
    }

    fun updateLogger(device: Device) {
        if (logger.isNone()) {
            logger.setCome(device)
            loggerCome.setPlay()
        } else if (!logger.isOut()) {
            logger.update(device)
            val isNotLeave = !logger.isOut() && !logger.isLeave()

            if (tally.isEnd() && monsters.isEnd() && isNotLeave) {
                logger.setLeave(device)
                loggerLeave.setPlay()
            } else if (logger.isWait()) {
                for (rank in 0..<monsters.ranks) {
                    val monster = monsters.get(rank)!!

                    if (monster.isWait() && monster.isEnemy()) {
                        logger.setFall(device)
                    }
                }
            }
        }

        if (isCutscene()) {
            val ratio = logger.getRatioPosition()
            val sizeZones = layout.zones.getSize()
            val i = Math.floor(ratio * sizeZones)
            logger.zone = layout.zones.get(i)
        }
    }

    fun updateMonstersStart(device: Device) {
        layout as Board
        val level = tally.getLevel()

        for (rank in 0..<monsters.getRankMax(level) + 1) {
            var monster = monsters.get(rank)
            val isAvailable = monster == null || monster.isEnd()

            if (isAvailable && monsters.canStartNext(level, device)) {
                val zone = monsters.getZoneNext(layout)
                val timeBase = tally.getTimeBase(device)
                monster = monsters.getNext(level)
                monster.start(zone, timeBase, layout, device)
                monsters.set(rank, monster)
                setPlayAudioMonster(monster)

                if (rank >= monsters.ranks) {
                    monsters.ranks += 1
                }
            }
        }
    }

    fun updateLayout(device: Device) {
        layout as Board

        if (layout.timer.isOn()) {
            layout.timer.update()
        }

        if (!layout.timer.isOn()) {
            val timeBase = tally.getTimeBase(device)
            layout.timer.start(timeBase, device)
        }
    }

    override fun drawElements(device: Device) {
        layout as Board

        if (!logger.isEnd()) {
            monsters.drawSprites(true, layout, device)
            drawFence(true, device)
            logger.draw(layout, device)
            drawFence(false, device)
            monsters.drawSprites(false, layout, device)
            monsters.drawCherry(layout, device)
        }
    }

    fun drawFence(
        northernZone: Boolean,
        device: Device,
    ) {
        layout as Board

        val level = tally.getLevel()
        val iZoneLogger = layout.toIZone(logger.zone)
        val isCutscene = isCutscene()
        val isBlink = layout.timer.isBlink(device)

        for (i in 0..<layout.zones.getSize()) {
            val zone = layout.zones.get(i)
            val isBuild = layout.toIZone(zone) < iZoneLogger
            val isConstruct = i == iZoneLogger && !isBlink
            val isAttack = monsters.isAttack(zone)
            val canDraw = layout.canDrawZone(zone, northernZone)
            val attacked = !isCutscene && (!isBlink || !isAttack)
            val constructed = isCutscene && (isBuild || isConstruct)

            if (canDraw && (attacked || constructed)) {
                fence.draw(level, zone, layout, device)
            }
        }
    }

    fun drawGreyedOut(device: Device) {
        greyedOut.onWindow(layout.x, layout.y, device.graphic.window)
    }

    fun clearBounds(device: Device) {
        val color = device.background.get(device.design.mode)
        val window = device.graphic.window
        window.setBackgroundColor(color.r, color.g, color.b)

        val w = device.graphic.toWCases(layout.nWCases)
        val h = device.graphic.toHCases(layout.nHCases)
        val left = layout.x
        val right = left + w
        val top = layout.y
        val bottom = top + h

        val xMin = 0
        val xMax = xMin + window.getW()
        val yMin = 0
        val yMax = yMin + window.getH()

        window.drawRectangle(xMin, yMin, left, yMax)
        window.drawRectangle(right, yMin, xMax, yMax)
        window.drawRectangle(left, yMin, right, top)
        window.drawRectangle(left, bottom, right, yMax)
    }

    override fun needPlay(): Boolean {
        val loggerNeedPlay = loggerCome.needPlay || loggerLeave.needPlay
        val friendNeedPlay = friendFall.needPlay || friendCome.needPlay
        val enemyNeedPlay = enemyFall.needPlay || enemyCome.needPlay

        return loggerNeedPlay || friendNeedPlay || enemyNeedPlay
    }

    override fun getAudio(): Audio {
        var result: Audio

        if (loggerCome.needPlay) {
            result = loggerCome
        } else if (loggerLeave.needPlay) {
            result = loggerLeave
        } else if (friendFall.needPlay) {
            result = friendFall
        } else if (enemyFall.needPlay) {
            result = enemyFall
        } else if (friendCome.needPlay) {
            result = friendCome
        } else {
            result = enemyCome
        }

        unsetPlayAudio()

        return result
    }

    fun unsetPlayAudio() {
        loggerCome.unsetPlay()
        loggerLeave.unsetPlay()
        friendFall.unsetPlay()
        enemyFall.unsetPlay()
        friendCome.unsetPlay()
        enemyCome.unsetPlay()
    }
}

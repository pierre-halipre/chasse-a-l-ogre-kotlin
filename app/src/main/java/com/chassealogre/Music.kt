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

import android.content.Context

class Music {
    open class Songs : Array<Sound>() {
        fun fill(
            idInit: Int,
            volumeMaxInit: Double,
            context: Context,
        ) {
            val sound = Sound(idInit, volumeMaxInit, context)
            add(sound)
        }

        fun play() {
            for (mode in 0..<getSize()) {
                val sound = get(mode)
                val isLoop = isLoop()
                sound.play(isLoop)
            }
        }

        fun stop() {
            for (mode in 0..<getSize()) {
                val sound = get(mode)
                sound.stop()
            }
        }

        fun setVolume(
            mode: Int,
            volumeInit: Double,
        ) {
            val sound = get(mode)
            sound.setVolume(volumeInit)
        }

        open fun isLoop(): Boolean = false
    }

    class SongsLoop : Songs() {
        override fun isLoop(): Boolean = true
    }

    val theme: SongsLoop = SongsLoop()
    val buttonPause: Songs = Songs()
    val buttonResume: Songs = Songs()

    val loggerCome: Songs = Songs()
    val loggerLeave: Songs = Songs()
    val friendFall: Songs = Songs()
    val enemyFall: Songs = Songs()
    val friendCome: Songs = Songs()
    val enemyCome: Songs = Songs()

    val volumeMax: Double = 1.0

    fun changeVolume(design: Design) {
        for (mode in 0..<design.nModes) {
            var volume: Double

            if (mode == design.mode) {
                volume = volumeMax
            } else {
                volume = 0.0
            }

            theme.setVolume(mode, volume)
            buttonPause.setVolume(mode, volume)
            buttonResume.setVolume(mode, volume)

            loggerCome.setVolume(mode, volume)
            loggerLeave.setVolume(mode, volume)
            friendFall.setVolume(mode, volume)
            enemyFall.setVolume(mode, volume)
            friendCome.setVolume(mode, volume)
            enemyCome.setVolume(mode, volume)
        }
    }

    fun stop() {
        theme.stop()
        buttonPause.stop()
        buttonResume.stop()

        loggerCome.stop()
        loggerLeave.stop()
        friendFall.stop()
        enemyFall.stop()
        friendCome.stop()
        enemyCome.stop()
    }
}

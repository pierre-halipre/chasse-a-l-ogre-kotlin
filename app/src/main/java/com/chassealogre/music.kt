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

class Music {
    val theme: SongsLoop = SongsLoop()
    val buttons_pause: Songs = Songs()
    val buttons_resume: Songs = Songs()

    val logger_come: Songs = Songs()
    val logger_leave: Songs = Songs()
    val friend_fall: Songs = Songs()
    val enemy_fall: Songs = Songs()
    val friend_come: Songs = Songs()
    val enemy_come: Songs = Songs()

    val volume_max: Double = 1.0

    fun change_volume(design: Design) {
        for (mode in 0..<design.n_modes) {
            var volume: Double

            if (mode == design.mode) {
                volume = volume_max
            } else {
                volume = 0.0
            }

            theme.set_volume(mode, volume)
            buttons_pause.set_volume(mode, volume)
            buttons_resume.set_volume(mode, volume)

            logger_come.set_volume(mode, volume)
            logger_leave.set_volume(mode, volume)
            friend_fall.set_volume(mode, volume)
            enemy_fall.set_volume(mode, volume)
            friend_come.set_volume(mode, volume)
            enemy_come.set_volume(mode, volume)
        }
    }

    fun stop() {
        theme.stop()
        buttons_pause.stop()
        buttons_resume.stop()

        logger_come.stop()
        logger_leave.stop()
        friend_fall.stop()
        enemy_fall.stop()
        friend_come.stop()
        enemy_come.stop()
    }
}

open class Songs : Array<Sound>() {
    fun fill(
        id_init: Int,
        volume_max_init: Double,
    ) {
        val sound = Sound(id_init, volume_max_init)
        add(sound)
    }

    fun play() {
        for (mode in 0..<get_size()) {
            val sound = get(mode)
            val is_loop = is_loop()
            sound.play(is_loop)
        }
    }

    fun stop() {
        for (mode in 0..<get_size()) {
            val sound = get(mode)
            sound.stop()
        }
    }

    fun set_volume(
        mode: Int,
        volume_init: Double,
    ) {
        val sound = get(mode)
        sound.set_volume(volume_init)
    }

    open fun is_loop(): Boolean = false
}

class SongsLoop : Songs() {
    override fun is_loop(): Boolean = true
}

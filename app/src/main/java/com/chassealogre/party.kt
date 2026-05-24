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

class Party : State() {
    companion object {
        const val HOME: Int = 1
        const val PLAY: Int = 2
        const val PAUSE: Int = 3
        const val END: Int = 4
        const val QUIT: Int = 5
    }

    fun set_home() {
        set_state(HOME)
    }

    fun is_home(): Boolean = is_state(HOME)

    fun set_play() {
        set_state(PLAY)
    }

    fun is_play(): Boolean = is_state(PLAY)

    fun set_pause() {
        set_state(PAUSE)
    }

    fun is_pause(): Boolean = is_state(PAUSE)

    fun set_end() {
        set_state(END)
    }

    fun set_quit() {
        set_state(QUIT)
    }

    fun is_quit(): Boolean = is_state(QUIT)
}

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

open class State {
    companion object {
        const val NONE: Int = 0
    }

    var status: Int = NONE

    fun changeStatus(statusInit: Int) {
        status = statusInit
    }

    fun isStatus(statusInit: Int): Boolean = status == statusInit

    open fun setNone() {
        changeStatus(NONE)
    }

    fun isNone(): Boolean = isStatus(NONE)
}

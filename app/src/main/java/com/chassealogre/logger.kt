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

    fun set_hit_good() {
        set_state(HIT_GOOD)
    }

    fun is_hit_good(): Boolean = is_state(HIT_GOOD)

    fun set_hit_bad() {
        set_state(HIT_BAD)
    }

    fun is_hit_bad(): Boolean = is_state(HIT_BAD)

    fun is_hit(): Boolean = is_hit_good() || is_hit_bad()

    override fun update(device: Device) {
        if (timer.is_on()) {
            timer.update()

            if (!timer.is_on()) {
                if (is_leave()) {
                    set_out()
                } else {
                    set_wait(device)
                }
            }
        }
    }

    override fun get_zone_position(board: Board): Int = board.get_zone_center()

    override fun draw(
        board: Board,
        device: Device,
    ) {
        val zone_position = get_zone_position(board)
        val x = board.get_x_zone(zone_position, device)
        val y = board.get_y_zone(zone_position, device)
        draw_sprite(x, y, board, device)
    }

    override fun can_draw_sprite(device: Device): Boolean {
        val is_not_bad = !is_hit_bad() || !timer.is_blink(device)

        return super.can_draw_sprite(device) && is_not_bad
    }

    override fun get_i_sheet_state(): Int {
        var result: Int

        if (is_wait()) {
            result = 0
        } else if (is_hit() || is_come() || is_leave()) {
            result = 6
        } else {
            result = 12
        }

        return result
    }

    override fun get_i_sheet_zone(board: Board): Int {
        val zone_sheet = board.get_zone_inverse(zone)

        return board.to_i_zone(zone_sheet)
    }
}

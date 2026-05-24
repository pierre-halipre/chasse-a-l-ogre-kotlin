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
    speed_walk_init: Double,
    speed_wait_init: Double,
    board: Board,
    rasters_zone: RastersZone,
    device: Device,
) : Character(speed_walk_init, speed_wait_init, board, rasters_zone, device) {
    companion object {
        const val TOUCH: Int = 8
    }

    var x: Int = 0
    var y: Int = 0
    var i_sheet_zone: Int = board.get_zone_none()

    fun set_touch() {
        set_state(TOUCH)
    }

    fun is_touch(): Boolean = is_state(TOUCH)

    override fun is_end(): Boolean = super.is_end() || is_touch()

    fun is_touchable(): Boolean = !is_end() && !is_fall()

    override fun start(
        zone: Int,
        time_base: Int,
        board: Board,
        device: Device,
    ) {
        super.start(zone, time_base, board, device)
        x = 0
        y = 0
        i_sheet_zone = board.get_zone_none()
    }

    override fun update(device: Device) {
        if (timer.is_on()) {
            timer.update()

            if (!timer.is_on()) {
                if (is_come()) {
                    set_wait(device)
                } else if (is_wait()) {
                    set_leave(device)
                } else if (is_leave()) {
                    set_out()
                } else if (is_fall()) {
                    set_touch()
                }
            }
        }
    }

    override fun get_zone_position(board: Board): Int = zone

    override fun draw(
        board: Board,
        device: Device,
    ) {
        if (!is_fall()) {
            zone = get_zone_position(board)
            val x_start = board.get_x_far_zone(zone, device)
            val y_start = board.get_y_far_zone(zone, device)
            val x_end = board.get_x_zone(zone, device)
            val y_end = board.get_y_zone(zone, device)
            val ratio = get_ratio_position()
            x = Math.distance(x_start, x_end, ratio)
            y = Math.distance(y_start, y_end, ratio)
            i_sheet_zone = get_i_sheet_zone(board)
        }

        if (device.design.mode == 0) {
            draw_sprites(board, device)
        } else {
            draw_sprite(x, y, board, device)
        }
    }

    fun draw_sprites(
        board: Board,
        device: Device,
    ) {
        val zone = get_zone_position(board)

        for (position in 0..<3) {
            var i_case: Int
            var j_case: Int

            if (board.is_horizon_zone(zone)) {
                i_case = (position + 1) % 2

                if (board.is_western_zone(zone)) {
                    i_case *= -1
                }

                j_case = position - 1
            } else {
                i_case = (position + 2) % 2

                if (board.is_western_zone(zone)) {
                    i_case *= -1
                }

                j_case = position

                if (board.is_northern_zone(zone)) {
                    j_case -= 2
                }
            }

            val w = device.graphic.to_w_cases(i_case)
            val h = device.graphic.to_h_cases(j_case)
            val x_sprite = x + w
            val y_sprite = y + h
            draw_sprite(x_sprite, y_sprite, board, device)
        }
    }

    override fun get_i_sheet_state(): Int {
        var result: Int

        if (is_come() || is_leave()) {
            result = 0
        } else if (is_wait()) {
            result = 6
        } else {
            result = 12
        }

        return result
    }

    override fun get_i_sheet_zone(board: Board): Int {
        var result: Int

        if (is_fall()) {
            result = i_sheet_zone
        } else {
            var zone: Int

            if (is_leave()) {
                zone = board.get_zone_inverse(this.zone)
            } else {
                zone = this.zone
            }

            result = board.to_i_zone(zone)
        }

        return result
    }

    abstract fun is_enemy(): Boolean

    abstract fun get_accuracy_tally(): Double

    abstract fun get_score_tally(): Int
}

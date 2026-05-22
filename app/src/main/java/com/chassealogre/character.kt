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
    speed_walk_init: Double,
    speed_wait_init: Double,
    board: Board,
    rasters_zone: RastersZone,
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

    val speed_walk: Double = speed_walk_init
    val speed_wait: Double = speed_wait_init
    var zone: Int = board.get_zone_none()
    val timer: Timer = Timer()
    var time_base: Int = 0
    var sprite: Sprite = Sprite(rasters_zone)

    init {
        board.fill_sprite(sprite, 0, rasters_zone, device)
        board.fill_sprite(sprite, 3, rasters_zone, device)
        board.fill_sprite(sprite, 6, rasters_zone, device)
    }

    override fun set_none() {
        super.set_none()
        timer.stop()
    }

    fun set_come(device: Device) {
        set_state(COME)
        val time = get_time()
        timer.start(time, device)
    }

    fun is_come(): Boolean = is_state(COME)

    fun set_wait(device: Device) {
        set_state(WAIT)
        val time = get_time()
        timer.start(time, device)
    }

    fun is_wait(): Boolean = is_state(WAIT)

    fun set_leave(device: Device) {
        set_state(LEAVE)
        val time = get_time()
        timer.start(time, device)
    }

    fun is_leave(): Boolean = is_state(LEAVE)

    fun set_out() {
        set_state(OUT)
    }

    fun is_out(): Boolean = is_state(OUT)

    fun set_fall(device: Device) {
        set_state(FALL)
        val time = get_time()
        timer.start(time, device)
    }

    fun is_fall(): Boolean = is_state(FALL)

    open fun is_end(): Boolean = is_none() || is_out()

    fun get_time(): Int {
        val speed = get_speed()
        val time = Math.ceil(speed * time_base)
        val n_animations = Math.round(speed)
        val time_animation = Math.ceil(time / n_animations)

        return n_animations * time_animation
    }

    fun get_speed(): Double {
        var result: Double

        if (is_come() || is_leave()) {
            result = speed_walk
        } else if (is_wait()) {
            result = speed_wait
        } else {
            result = SPEED_FALL
        }

        return result
    }

    open fun start(
        zone: Int,
        time_base: Int,
        board: Board,
        device: Device,
    ) {
        this.zone = zone
        this.time_base = time_base
        set_come(device)
    }

    abstract fun update(device: Device)

    abstract fun get_zone_position(board: Board): Int

    fun get_ratio_position(): Double {
        var result: Double

        if (is_come()) {
            result = timer.get_ratio()
        } else if (is_leave()) {
            result = timer.get_ratio_inverse()
        } else {
            result = 1.0
        }

        return result
    }

    abstract fun draw(
        board: Board,
        device: Device,
    )

    fun draw_sprite(
        x: Int,
        y: Int,
        board: Board,
        device: Device,
    ) {
        if (can_draw_sprite(device)) {
            val i_sheet_state = get_i_sheet_state()
            val i_sheet_zone = get_i_sheet_zone(board)
            val i_sheet = i_sheet_state + i_sheet_zone
            val ratio = get_ratio_sprite()
            sprite.draw(i_sheet, ratio, x, y, device)
        }
    }

    open fun can_draw_sprite(device: Device): Boolean {
        val result = !is_fall() || !timer.is_blink(device)

        return result
    }

    abstract fun get_i_sheet_state(): Int

    abstract fun get_i_sheet_zone(board: Board): Int

    fun get_ratio_sprite(): Double {
        var result: Double

        if (is_fall()) {
            val half_threshold = Math.round(timer.threshold / 2)

            if (timer.counts < half_threshold) {
                result = timer.get_ratio()
            } else {
                result = Math.to_double(half_threshold) / timer.threshold
            }
        } else {
            val n_animations = Math.round(get_speed())
            val threshold = Math.ceil(timer.threshold / n_animations)
            result = Math.to_double(timer.counts % threshold) / threshold
        }

        return result
    }
}

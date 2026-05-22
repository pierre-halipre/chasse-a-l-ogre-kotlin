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

class Action {
    var zone: Int = Layout.NONE
    var need_quit: Boolean = false
    var counts_change: Int = 0
    var ticks_change: Int = 0

    fun set_quit() {
        need_quit = true
    }

    fun check_change_design(
        game: Game,
        device: Device,
    ) {
        game.layout as Board

        if (zone == game.layout.get_zone_center()) {
            val clock = device.clock
            val time_animation_max = clock.time_animation_max
            val time_animation_min = clock.time_animation_min
            val time_range = time_animation_max - time_animation_min
            val n_counts_change = clock.to_ticks(time_range)

            if (game.ticks - ticks_change <= n_counts_change) {
                counts_change += 1
            } else {
                counts_change = 1
            }

            ticks_change = game.ticks
        } else {
            counts_change = 0
        }
    }

    fun need_change(): Boolean = counts_change == 3

    fun set_zone_event(
        frame: Frame,
        menu: Menu,
        game: Game,
        device: Device,
    ) {
        if (frame.border.is_in(device)) {
            var layout: Layout

            if (menu.layout.is_in(device)) {
                layout = menu.layout
            } else {
                layout = game.layout
            }

            zone = layout.find_zone(device)
        }
    }

    fun get_zone_demo(game: Game): Int {
        var result = Layout.NONE

        if (can_hit_demo(game.logger)) {
            var monster_best: Monster? = null
            val n_friends = game.monsters.get_n_friends()

            for (rank in 0..<game.monsters.ranks) {
                val monster = game.monsters.get(rank)!!
                val can_fall = can_fall_demo(monster, n_friends)
                val is_better = is_better(monster_best, monster)

                if (can_fall && is_better) {
                    monster_best = monster
                    result = monster.zone
                }
            }
        }

        return result
    }

    fun is_better(
        monster_best: Monster?,
        monster: Monster,
    ): Boolean {
        val is_null = monster_best == null

        return is_null || monster_best.get_speed() > monster.get_speed()
    }

    fun can_hit_demo(logger: Logger): Boolean {
        val in_waiting = logger.is_wait() && is_between_demo(logger)
        val in_falling = logger.is_fall() && !is_between_demo(logger)

        return in_waiting || in_falling
    }

    fun is_between_demo(character: Character): Boolean {
        val is_not_begin = !is_begin_demo(character)
        val is_not_finish = !is_finish_demo(character)

        return is_not_begin && is_not_finish
    }

    fun is_begin_demo(character: Character): Boolean {
        val ratio = character.timer.get_ratio()

        return ratio < 1 / 3
    }

    fun is_finish_demo(character: Character): Boolean {
        val ratio = character.timer.get_ratio()

        return ratio >= 2 / 3
    }

    fun can_fall_demo(
        monster: Monster,
        n_friends: Int,
    ): Boolean {
        val can_fall = monster.is_enemy() || n_friends == 2
        val in_coming = monster.is_come() && is_finish_demo(monster)
        val in_waiting = monster.is_wait() && is_between_demo(monster)
        val in_leaving = monster.is_leave() && is_begin_demo(monster)

        return can_fall && (in_coming || in_waiting || in_leaving)
    }
}

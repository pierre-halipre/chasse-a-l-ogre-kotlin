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
    val greyed_out: Image = device.graphic.greyed_out
    val logger: Logger = Logger(layout as Board, device)
    val monsters: Monsters = Monsters(layout as Board, device)
    val fence: Fence = Fence(layout as Board, device)

    var logger_come: Audio
    var logger_leave: Audio
    var friend_fall: Audio
    var enemy_fall: Audio
    var friend_come: Audio
    var enemy_come: Audio

    val tally: Tally = Tally()
    var ticks: Int = 0

    init {
        val music = device.music
        logger_come = Audio(music.logger_come)
        logger_leave = Audio(music.logger_leave)
        friend_fall = Audio(music.friend_fall)
        enemy_fall = Audio(music.enemy_fall)
        friend_come = Audio(music.friend_come)
        enemy_come = Audio(music.enemy_come)
    }

    override fun reset() {
        layout as Board
        layout.timer.stop()
        logger.set_none()
        monsters.reset()

        tally.accuracy = 0.0
        tally.counts = 0
        tally.score = 0
        tally.progress = 0.0

        ticks = 0
    }

    override fun update(
        zone: Int,
        device: Device,
    ) {
        if (!is_cutscene()) {
            update_monsters(zone, device)
        }

        logger.time_base = tally.get_time_base(device)
        val is_end = tally.is_end() && monsters.is_end()

        if (!is_cutscene() && !is_end && layout.is_in_zones(zone)) {
            update_logger_action(zone, device)
        } else {
            update_logger(device)
        }

        if (!is_cutscene() && !tally.is_end()) {
            update_monsters_start(device)
        }

        update_layout(device)

        ticks += 1
    }

    fun is_cutscene(): Boolean {
        val is_walk = logger.is_come() || logger.is_leave()

        return !logger.is_end() && is_walk
    }

    fun update_monsters(
        zone: Int,
        device: Device,
    ) {
        var n_updates_tally = 0

        for (rank in 0..<monsters.ranks) {
            val monster = monsters.get(rank)!!

            if (!monster.is_end()) {
                if (!monster.is_fall() && monster.zone == zone) {
                    tally.update_score(monster)
                    n_updates_tally += 1

                    monster.set_fall(device)

                    set_play_audio_monster(monster)
                } else {
                    monster.update(device)

                    if (monster.is_out()) {
                        tally.update_score(monster)
                        n_updates_tally += 1
                    }
                }
            }
        }

        tally.update_progress(n_updates_tally)
    }

    fun set_play_audio_monster(monster: Monster) {
        var audio: Audio

        if (monster.is_enemy()) {
            if (monster.is_come()) {
                audio = enemy_come
            } else {
                audio = enemy_fall
            }
        } else if (monster.is_come()) {
            audio = friend_come
        } else {
            audio = friend_fall
        }

        audio.set_play()
    }

    fun update_logger_action(
        zone: Int,
        device: Device,
    ) {
        logger.zone = zone
        var is_hit = false

        for (rank in 0..<monsters.ranks) {
            val monster = monsters.get(rank)!!

            if (monster.is_fall() && monster.zone == logger.zone) {
                if (monster.is_enemy()) {
                    logger.set_hit_good()
                } else {
                    logger.set_hit_bad()
                }

                logger.timer.counts = monster.timer.counts
                logger.timer.threshold = monster.timer.threshold
                is_hit = true
            }
        }

        if (!logger.is_wait() && !logger.is_fall() && !is_hit) {
            logger.set_wait(device)
        }
    }

    fun update_logger(device: Device) {
        if (logger.is_none()) {
            logger.set_come(device)
            logger_come.set_play()
        } else if (!logger.is_out()) {
            logger.update(device)
            val is_not_leave = !logger.is_out() && !logger.is_leave()

            if (tally.is_end() && monsters.is_end() && is_not_leave) {
                logger.set_leave(device)
                logger_leave.set_play()
            } else if (logger.is_wait()) {
                for (rank in 0..<monsters.ranks) {
                    val monster = monsters.get(rank)!!

                    if (monster.is_wait() && monster.is_enemy()) {
                        logger.set_fall(device)
                    }
                }
            }
        }

        if (is_cutscene()) {
            val ratio = logger.get_ratio_position()
            val size_zones = layout.zones.get_size()
            val i = Math.floor(ratio * size_zones)
            logger.zone = layout.zones.get(i)
        }
    }

    fun update_monsters_start(device: Device) {
        layout as Board
        val level = tally.get_level()

        for (rank in 0..<monsters.get_rank_max(level) + 1) {
            var monster = monsters.get(rank)
            val is_available = monster == null || monster.is_end()
            val can_start = monsters.can_start_next(level, device)

            if (is_available && can_start) {
                val zone = monsters.get_zone_next(layout)
                val time_base = tally.get_time_base(device)
                monster = monsters.get_next(level)
                monster.start(zone, time_base, layout, device)
                monsters.set(rank, monster)
                set_play_audio_monster(monster)

                if (rank >= monsters.ranks) {
                    monsters.ranks += 1
                }
            }
        }
    }

    fun update_layout(device: Device) {
        layout as Board

        if (layout.timer.is_on()) {
            layout.timer.update()
        }

        if (!layout.timer.is_on()) {
            val time_base = tally.get_time_base(device)
            layout.timer.start(time_base, device)
        }
    }

    override fun draw_elements(device: Device) {
        layout as Board

        if (!logger.is_end()) {
            monsters.draw_sprites(true, layout, device)
            draw_fence(true, device)
            logger.draw(layout, device)
            draw_fence(false, device)
            monsters.draw_sprites(false, layout, device)
            monsters.draw_cherry(layout, device)
        }
    }

    fun draw_fence(
        northern_zone: Boolean,
        device: Device,
    ) {
        layout as Board

        val level = tally.get_level()
        val i_zone_logger = layout.to_i_zone(logger.zone)
        val is_cutscene = is_cutscene()
        val is_blink = layout.timer.is_blink(device)

        for (i in 0..<layout.zones.get_size()) {
            val zone = layout.zones.get(i)
            val is_build = layout.to_i_zone(zone) < i_zone_logger
            val is_construct = i == i_zone_logger && !is_blink
            val is_attack = monsters.is_attack(zone)
            val can_draw = layout.can_draw_zone(zone, northern_zone)
            val attacked = !is_cutscene && (!is_blink || !is_attack)
            val constructed = is_cutscene && (is_build || is_construct)

            if (can_draw && (attacked || constructed)) {
                fence.draw(level, zone, layout, device)
            }
        }
    }

    fun draw_greyed_out(device: Device) {
        greyed_out.on_window(layout.x, layout.y, device.graphic.window)
    }

    fun clear_bounds(device: Device) {
        val color = device.background.get(device.design.mode)
        val window = device.graphic.window
        window.set_background_color(color.r, color.g, color.b)

        val w = device.graphic.to_w_cases(layout.n_w_cases)
        val h = device.graphic.to_h_cases(layout.n_h_cases)
        val left = layout.x
        val right = left + w
        val top = layout.y
        val bottom = top + h

        val x_min = 0
        val x_max = x_min + window.get_w()
        val y_min = 0
        val y_max = y_min + window.get_h()

        window.draw_rectangle(x_min, y_min, left, y_max)
        window.draw_rectangle(right, y_min, x_max, y_max)
        window.draw_rectangle(left, y_min, right, top)
        window.draw_rectangle(left, bottom, right, y_max)
    }

    override fun need_play(): Boolean {
        val logger_play = logger_come.need_play || logger_leave.need_play
        val friend_play = friend_fall.need_play || friend_come.need_play
        val enemy_play = enemy_fall.need_play || enemy_come.need_play

        return logger_play || friend_play || enemy_play
    }

    override fun get_audio(): Audio {
        var result: Audio

        if (logger_come.need_play) {
            result = logger_come
        } else if (logger_leave.need_play) {
            result = logger_leave
        } else if (friend_fall.need_play) {
            result = friend_fall
        } else if (enemy_fall.need_play) {
            result = enemy_fall
        } else if (friend_come.need_play) {
            result = friend_come
        } else {
            result = enemy_come
        }

        unset_play_audio()

        return result
    }

    fun unset_play_audio() {
        logger_come.unset_play()
        logger_leave.unset_play()
        friend_fall.unset_play()
        enemy_fall.unset_play()
        friend_come.unset_play()
        enemy_come.unset_play()
    }
}

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

class Device(
    w_window: Int,
    h_window: Int,
    frame_rate_init: Int,
) {
    val event: Event = Event()
    val clock: Clock = Clock(frame_rate_init)
    val design: Design = Design()
    val text: Text = Text()
    var graphic: Graphic = Graphic(w_window, h_window)
    val music: Music = Music()
    val background: Background = Background()

    init {
        graphic.fill(R.raw.greyed_out)

        add_mode_0()
        add_mode_1()
        add_mode_2()
        add_mode_3()

        music.change_volume(design)
    }

    fun add_mode_0() {
        design.add()

        text.fill(R.raw.screen_play)

        graphic.font.fill(R.raw.printer, 41)
        graphic.pen.fill(R.raw.form, 6)

        graphic.border.fill(R.raw.frame, 1)

        graphic.panel.fill(R.raw.scroll, 1)
        graphic.buttons.fill(R.raw.items, 1)

        graphic.board.fill(R.raw.kingdom, 4)
        graphic.logger.fill(R.raw.archer, 4)
        graphic.zombie.fill(R.raw.orc_blue, 4)
        graphic.vampire.fill(R.raw.ogre_red, 4)
        graphic.skeleton.fill(R.raw.orc_red, 4)
        graphic.ghost.fill(R.raw.ogre_blue, 4)
        graphic.deer.fill(R.raw.mage_blue, 4)
        graphic.rabbit.fill(R.raw.mage_red, 4)
        graphic.cherry.fill(R.raw.magic, 4)
        graphic.fence.fill(R.raw.palisade, 3)

        music.theme.fill(R.raw.kingdom_theme_cut, 1.0)
        music.buttons_pause.fill(R.raw.item_pause, 0.5)
        music.buttons_resume.fill(R.raw.item_resume, 0.5)

        music.logger_come.fill(R.raw.archer_come, 0.5)
        music.logger_leave.fill(R.raw.archer_leave, 0.5)
        music.friend_fall.fill(R.raw.mage_fall, 0.25)
        music.enemy_fall.fill(R.raw.orc_fall, 0.25)
        music.friend_come.fill(R.raw.mage_come, 0.25)
        music.enemy_come.fill(R.raw.orc_come, 0.25)

        background.fill(255, 241, 210)
    }

    fun add_mode_1() {
        design.add()

        text.fill(R.raw.book)

        graphic.font.fill(R.raw.press, 41)
        graphic.pen.fill(R.raw.cast, 6)

        graphic.border.fill(R.raw.mount, 1)

        graphic.panel.fill(R.raw.parchment, 1)
        graphic.buttons.fill(R.raw.knobs, 1)

        graphic.board.fill(R.raw.laboratory, 1)
        graphic.logger.fill(R.raw.wizard, 3)
        graphic.zombie.fill(R.raw.goblin_hair, 3)
        graphic.vampire.fill(R.raw.goblin_horn, 3)
        graphic.skeleton.fill(R.raw.goblin_helmet, 3)
        graphic.ghost.fill(R.raw.goblin_hood, 3)
        graphic.deer.fill(R.raw.child, 3)
        graphic.rabbit.fill(R.raw.old_man, 3)
        graphic.cherry.fill(R.raw.spell, 5)
        graphic.fence.fill(R.raw.barricade, 3)

        music.theme.fill(R.raw.laboratory_theme_cut, 0.75)
        music.buttons_pause.fill(R.raw.knob_pause, 0.75)
        music.buttons_resume.fill(R.raw.knob_resume, 0.75)

        music.logger_come.fill(R.raw.wizard_come, 0.75)
        music.logger_leave.fill(R.raw.wizard_leave, 0.75)
        music.friend_fall.fill(R.raw.child_fall, 0.25)
        music.enemy_fall.fill(R.raw.goblin_fall, 0.25)
        music.friend_come.fill(R.raw.child_come, 0.25)
        music.enemy_come.fill(R.raw.goblin_come, 0.25)

        background.fill(192, 232, 148)
    }

    fun add_mode_2() {
        design.add()

        text.fill(R.raw.scratch)

        graphic.font.fill(R.raw.typography, 41)
        graphic.pen.fill(R.raw.pencil, 6)

        graphic.border.fill(R.raw.edge, 1)

        graphic.panel.fill(R.raw.shell, 1)
        graphic.buttons.fill(R.raw.toggles, 1)

        graphic.board.fill(R.raw.village, 1)
        graphic.logger.fill(R.raw.witch, 4)
        graphic.zombie.fill(R.raw.cockroach, 4)
        graphic.vampire.fill(R.raw.spirit, 4)
        graphic.skeleton.fill(R.raw.hermit_crab, 4)
        graphic.ghost.fill(R.raw.robot, 4)
        graphic.deer.fill(R.raw.owl, 4)
        graphic.rabbit.fill(R.raw.balloon, 4)
        graphic.cherry.fill(R.raw.bobble, 2)
        graphic.fence.fill(R.raw.hedge, 3)

        music.theme.fill(R.raw.village_theme_cut, 0.75)
        music.buttons_pause.fill(R.raw.toggle_pause, 1.0)
        music.buttons_resume.fill(R.raw.toggle_resume, 1.0)

        music.logger_come.fill(R.raw.witch_come, 0.75)
        music.logger_leave.fill(R.raw.witch_leave, 0.75)
        music.friend_fall.fill(R.raw.owl_fall, 0.75)
        music.enemy_fall.fill(R.raw.cockroach_fall, 0.75)
        music.friend_come.fill(R.raw.owl_come, 0.75)
        music.enemy_come.fill(R.raw.cockroach_come, 0.75)

        background.fill(255, 155, 201)
    }

    fun add_mode_3() {
        design.add()

        text.fill(R.raw.scripts)

        graphic.font.fill(R.raw.font, 41)
        graphic.pen.fill(R.raw.pen, 6)

        graphic.border.fill(R.raw.border, 1)

        graphic.panel.fill(R.raw.panel, 1)
        graphic.buttons.fill(R.raw.buttons, 1)

        graphic.board.fill(R.raw.board, 3)
        graphic.logger.fill(R.raw.logger, 3)
        graphic.zombie.fill(R.raw.zombie, 3)
        graphic.vampire.fill(R.raw.vampire, 3)
        graphic.skeleton.fill(R.raw.skeleton, 3)
        graphic.ghost.fill(R.raw.ghost, 3)
        graphic.deer.fill(R.raw.deer, 3)
        graphic.rabbit.fill(R.raw.rabbit, 3)
        graphic.cherry.fill(R.raw.cherry, 3)
        graphic.fence.fill(R.raw.fence, 3)

        music.theme.fill(R.raw.theme_cut, 1.0)
        music.buttons_pause.fill(R.raw.buttons_pause, 1.0)
        music.buttons_resume.fill(R.raw.buttons_resume, 1.0)

        music.logger_come.fill(R.raw.logger_come, 1.0)
        music.logger_leave.fill(R.raw.logger_leave, 1.0)
        music.friend_fall.fill(R.raw.friend_fall, 1.0)
        music.enemy_fall.fill(R.raw.enemy_fall, 1.0)
        music.friend_come.fill(R.raw.friend_come, 1.0)
        music.enemy_come.fill(R.raw.enemy_come, 1.0)

        background.fill(255, 255, 204)
    }
}

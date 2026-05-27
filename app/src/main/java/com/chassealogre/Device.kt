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

class Device(
    wWindow: Int,
    hWindow: Int,
    frameRateInit: Int,
    context: Context,
) {
    val event: Event = Event()
    val clock: Clock = Clock(frameRateInit)
    val design: Design = Design()
    val text: Text = Text()
    var graphic: Graphic = Graphic(wWindow, hWindow)
    val music: Music = Music()
    val background: Background = Background()

    init {
        graphic.fill(R.raw.greyed_out, context)

        addMode0(context)
        addMode1(context)
        addMode2(context)
        addMode3(context)

        music.changeVolume(design)
    }

    fun addMode0(context: Context) {
        design.add()

        text.fill(R.raw.screen_play, context)

        graphic.font.fill(R.raw.printer, 41, context)
        graphic.pen.fill(R.raw.form, 6, context)

        graphic.border.fill(R.raw.frame, 1, context)

        graphic.panel.fill(R.raw.scroll, 1, context)
        graphic.buttons.fill(R.raw.items, 1, context)

        graphic.board.fill(R.raw.kingdom, 4, context)
        graphic.logger.fill(R.raw.archer, 4, context)
        graphic.zombie.fill(R.raw.orc_blue, 4, context)
        graphic.vampire.fill(R.raw.ogre_red, 4, context)
        graphic.skeleton.fill(R.raw.orc_red, 4, context)
        graphic.ghost.fill(R.raw.ogre_blue, 4, context)
        graphic.deer.fill(R.raw.mage_blue, 4, context)
        graphic.rabbit.fill(R.raw.mage_red, 4, context)
        graphic.cherry.fill(R.raw.magic, 4, context)
        graphic.fence.fill(R.raw.palisade, 3, context)

        music.theme.fill(R.raw.kingdom_theme_cut, 1.0, context)
        music.buttonPause.fill(R.raw.item_pause, 0.5, context)
        music.buttonResume.fill(R.raw.item_resume, 0.5, context)

        music.loggerCome.fill(R.raw.archer_come, 0.5, context)
        music.loggerLeave.fill(R.raw.archer_leave, 0.5, context)
        music.friendFall.fill(R.raw.mage_fall, 0.25, context)
        music.enemyFall.fill(R.raw.orc_fall, 0.25, context)
        music.friendCome.fill(R.raw.mage_come, 0.25, context)
        music.enemyCome.fill(R.raw.orc_come, 0.25, context)

        background.fill(255, 241, 210)
    }

    fun addMode1(context: Context) {
        design.add()

        text.fill(R.raw.book, context)

        graphic.font.fill(R.raw.press, 41, context)
        graphic.pen.fill(R.raw.cast, 6, context)

        graphic.border.fill(R.raw.mount, 1, context)

        graphic.panel.fill(R.raw.parchment, 1, context)
        graphic.buttons.fill(R.raw.knobs, 1, context)

        graphic.board.fill(R.raw.laboratory, 1, context)
        graphic.logger.fill(R.raw.wizard, 3, context)
        graphic.zombie.fill(R.raw.goblin_hair, 3, context)
        graphic.vampire.fill(R.raw.goblin_horn, 3, context)
        graphic.skeleton.fill(R.raw.goblin_helmet, 3, context)
        graphic.ghost.fill(R.raw.goblin_hood, 3, context)
        graphic.deer.fill(R.raw.child, 3, context)
        graphic.rabbit.fill(R.raw.old_man, 3, context)
        graphic.cherry.fill(R.raw.spell, 5, context)
        graphic.fence.fill(R.raw.barricade, 3, context)

        music.theme.fill(R.raw.laboratory_theme_cut, 0.75, context)
        music.buttonPause.fill(R.raw.knob_pause, 0.75, context)
        music.buttonResume.fill(R.raw.knob_resume, 0.75, context)

        music.loggerCome.fill(R.raw.wizard_come, 0.75, context)
        music.loggerLeave.fill(R.raw.wizard_leave, 0.75, context)
        music.friendFall.fill(R.raw.child_fall, 0.25, context)
        music.enemyFall.fill(R.raw.goblin_fall, 0.25, context)
        music.friendCome.fill(R.raw.child_come, 0.25, context)
        music.enemyCome.fill(R.raw.goblin_come, 0.25, context)

        background.fill(192, 232, 148)
    }

    fun addMode2(context: Context) {
        design.add()

        text.fill(R.raw.scratch, context)

        graphic.font.fill(R.raw.typography, 41, context)
        graphic.pen.fill(R.raw.pencil, 6, context)

        graphic.border.fill(R.raw.edge, 1, context)

        graphic.panel.fill(R.raw.shell, 1, context)
        graphic.buttons.fill(R.raw.toggles, 1, context)

        graphic.board.fill(R.raw.village, 1, context)
        graphic.logger.fill(R.raw.witch, 4, context)
        graphic.zombie.fill(R.raw.cockroach, 4, context)
        graphic.vampire.fill(R.raw.spirit, 4, context)
        graphic.skeleton.fill(R.raw.hermit_crab, 4, context)
        graphic.ghost.fill(R.raw.robot, 4, context)
        graphic.deer.fill(R.raw.owl, 4, context)
        graphic.rabbit.fill(R.raw.balloon, 4, context)
        graphic.cherry.fill(R.raw.bobble, 2, context)
        graphic.fence.fill(R.raw.hedge, 3, context)

        music.theme.fill(R.raw.village_theme_cut, 0.75, context)
        music.buttonPause.fill(R.raw.toggle_pause, 1.0, context)
        music.buttonResume.fill(R.raw.toggle_resume, 1.0, context)

        music.loggerCome.fill(R.raw.witch_come, 0.75, context)
        music.loggerLeave.fill(R.raw.witch_leave, 0.75, context)
        music.friendFall.fill(R.raw.owl_fall, 0.75, context)
        music.enemyFall.fill(R.raw.cockroach_fall, 0.75, context)
        music.friendCome.fill(R.raw.owl_come, 0.75, context)
        music.enemyCome.fill(R.raw.cockroach_come, 0.75, context)

        background.fill(255, 155, 201)
    }

    fun addMode3(context: Context) {
        design.add()

        text.fill(R.raw.scripts, context)

        graphic.font.fill(R.raw.font, 41, context)
        graphic.pen.fill(R.raw.pen, 6, context)

        graphic.border.fill(R.raw.border, 1, context)

        graphic.panel.fill(R.raw.panel, 1, context)
        graphic.buttons.fill(R.raw.buttons, 1, context)

        graphic.board.fill(R.raw.board, 3, context)
        graphic.logger.fill(R.raw.logger, 3, context)
        graphic.zombie.fill(R.raw.zombie, 3, context)
        graphic.vampire.fill(R.raw.vampire, 3, context)
        graphic.skeleton.fill(R.raw.skeleton, 3, context)
        graphic.ghost.fill(R.raw.ghost, 3, context)
        graphic.deer.fill(R.raw.deer, 3, context)
        graphic.rabbit.fill(R.raw.rabbit, 3, context)
        graphic.cherry.fill(R.raw.cherry, 3, context)
        graphic.fence.fill(R.raw.fence, 3, context)

        music.theme.fill(R.raw.theme_cut, 1.0, context)
        music.buttonPause.fill(R.raw.buttons_pause, 1.0, context)
        music.buttonResume.fill(R.raw.buttons_resume, 1.0, context)

        music.loggerCome.fill(R.raw.logger_come, 1.0, context)
        music.loggerLeave.fill(R.raw.logger_leave, 1.0, context)
        music.friendFall.fill(R.raw.friend_fall, 1.0, context)
        music.enemyFall.fill(R.raw.enemy_fall, 1.0, context)
        music.friendCome.fill(R.raw.friend_come, 1.0, context)
        music.enemyCome.fill(R.raw.enemy_come, 1.0, context)

        background.fill(255, 255, 204)
    }
}

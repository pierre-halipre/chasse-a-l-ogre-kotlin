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

class Menu(
    device: Device,
) : Gui(Panel(device)) {
    val party: Party = Party()

    val buttons: Buttons = Buttons(device)

    var theme: Audio = Audio(device.music.theme)
    var buttons_pause: Audio = Audio(device.music.buttons_pause)
    var buttons_resume: Audio = Audio(device.music.buttons_resume)

    var need_reset: Boolean = false
    var need_resume: Boolean = false

    override fun reset() {
        party.set_home()

        need_reset = true
        need_resume = false

        theme.set_play()
    }

    override fun update(
        zone: Int,
        device: Device,
    ) {
        layout as Panel

        if (layout.is_in_zones(zone)) {
            val is_left_zone = layout.is_left_zone(zone)

            if (is_left_zone) {
                if (party.is_play()) {
                    party.set_pause()
                    buttons_pause.set_play()
                } else {
                    if (party.is_pause()) {
                        need_resume = true
                        buttons_resume.set_play()
                    } else {
                        need_reset = true
                    }

                    party.set_play()
                }
            } else if (party.is_home()) {
                party.set_quit()
            } else {
                party.set_home()
                need_reset = true
                theme.set_play()
            }
        }
    }

    override fun draw_elements(device: Device) {
        buttons.draw_sprites(layout, party, device)
    }

    override fun need_play(): Boolean {
        val buttons_play = buttons_pause.need_play || buttons_resume.need_play

        return theme.need_play || buttons_play
    }

    override fun get_audio(): Audio {
        var result: Audio

        if (theme.need_play) {
            result = theme
        } else if (buttons_pause.need_play) {
            result = buttons_pause
        } else {
            result = buttons_resume
        }

        theme.unset_play()
        buttons_pause.unset_play()
        buttons_resume.unset_play()

        return result
    }
}

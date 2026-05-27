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
    var buttonPause: Audio = Audio(device.music.buttonPause)
    var buttonResume: Audio = Audio(device.music.buttonResume)

    var needReset: Boolean = false
    var needResume: Boolean = false

    override fun reset() {
        party.setHome()

        needReset = true
        needResume = false

        theme.setPlay()
    }

    override fun update(
        zone: Int,
        device: Device,
    ) {
        layout as Panel

        if (layout.isInZones(zone)) {
            val isLeftZone = layout.isLeftZone(zone)

            if (isLeftZone) {
                if (party.isPlay()) {
                    party.setPause()
                    buttonPause.setPlay()
                } else {
                    if (party.isPause()) {
                        needResume = true
                        buttonResume.setPlay()
                    } else {
                        needReset = true
                    }

                    party.setPlay()
                }
            } else if (party.isHome()) {
                party.setQuit()
            } else {
                party.setHome()
                needReset = true
                theme.setPlay()
            }
        }
    }

    override fun drawElements(device: Device) {
        buttons.drawSprites(layout, party, device)
    }

    override fun needPlay(): Boolean {
        val buttonsNeedPlay = buttonPause.needPlay || buttonResume.needPlay

        return theme.needPlay || buttonsNeedPlay
    }

    override fun getAudio(): Audio {
        var result: Audio

        if (theme.needPlay) {
            result = theme
        } else if (buttonPause.needPlay) {
            result = buttonPause
        } else {
            result = buttonResume
        }

        theme.unsetPlay()
        buttonPause.unsetPlay()
        buttonResume.unsetPlay()

        return result
    }
}

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
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceView
import kotlin.system.exitProcess

class Main(
    context: Context,
) : SurfaceView(context),
    Runnable {
    var thread: java.lang.Thread = Thread(this)

    lateinit var device: Device
    lateinit var frame: Frame
    lateinit var menu: Menu
    lateinit var game: Game
    lateinit var pen: Pen
    lateinit var font: Font
    lateinit var action: Action

    var ready: Boolean = false
    var watch_time: Long = 0

    init {
        start()
    }

    fun start() {
        try {
            thread.start()
        } catch (_: IllegalThreadStateException) {
            exitProcess(0)
        }
    }

    fun stop() {
        try {
            thread.join()
        } catch (_: InterruptedException) {
            exitProcess(0)
        }
    }

    fun watch_start() {
        watch_time = Config.get_time()
    }

    fun watch_check() {
        if (Config.get_time() - watch_time > 5000) {
            exitProcess(0)
        }
    }

    override fun run() {
        Config.set_context(context)
        show_loading()
        Config.load_sounds()
        val w_window = Config.get_w_desktop()
        val h_window = Config.get_h_desktop()
        val frame_rate = Config.get_frame_rate()

        device = Device(w_window, h_window, frame_rate)
        frame = Frame(device)
        menu = Menu(device)
        game = Game(device)
        pen = Pen(device)
        font = Font(device)
        action = Action()

        wait_sounds()

        ready = true

        while (device.clock.thread.is_run()) {
            val refresh_time = device.clock.get_refresh_time()

            if (device.clock.thread.is_tick(refresh_time)) {
                if (device.event.is_clic()) {
                    action.set_zone_event(frame, menu, game, device)
                    action.check_change_design(game, device)
                    device.event.kind = 0
                }

                if (menu.party.is_none()) {
                    menu.reset()
                }

                menu.update(action.zone, device)

                val reset_demo = game.logger.is_end() && menu.party.is_home()

                if (menu.need_reset || reset_demo) {
                    game.reset()
                    menu.need_reset = false
                } else if (game.logger.is_end()) {
                    menu.party.set_end()
                }

                val need_update = menu.party.is_home() || menu.party.is_play()

                if (need_update && !menu.need_resume) {
                    var zone: Int

                    if (menu.party.is_home()) {
                        zone = action.get_zone_demo(game)
                    } else {
                        zone = action.zone
                    }

                    game.update(zone, device)
                } else {
                    menu.need_resume = false
                }

                if (!action.need_quit) {
                    action.zone = Layout.NONE

                    if (action.need_change()) {
                        device.design.change()
                        device.music.change_volume(device.design)
                        action.counts_change = 0
                    }

                    if (menu.party.is_home()) {
                        game.unset_play_audio()
                    }

                    game.play()
                    menu.play()

                    frame.draw_background(device)
                    game.draw(pen, device)
                    game.clear_bounds(device)
                    menu.draw(pen, device)

                    if (!menu.party.is_play()) {
                        game.draw_greyed_out(device)

                        if (menu.party.is_home()) {
                            font.set_home(device)
                        } else if (menu.party.is_pause()) {
                            font.set_pause(game.ticks, device)
                        } else {
                            font.set_end(game.tally.score, device)
                        }

                        font.draw(frame.border, device)
                    }

                    frame.draw_foreground(device)

                    draw()
                } else {
                    device.clock.thread.stop()
                }
            }
        }

        device.music.release()
        Config.release_sounds()
    }

    fun show_loading() {
        watch_start()

        while (true) {
            watch_check()

            try {
                if (holder.surface.isValid) {
                    val drawing = holder.lockHardwareCanvas()
                    val paint = Paint()
                    val w_desktop = Config.get_w_desktop()
                    val h_desktop = Config.get_h_desktop()
                    val top = Math.half(h_desktop - w_desktop)
                    val n_images = Math.ceil(Math.to_double(top) / w_desktop)

                    val icon_foreground = Image()
                    icon_foreground.load(R.raw.icon_foreground)
                    icon_foreground.scale(w_desktop, w_desktop)

                    val icon_background = Image()
                    icon_background.load(R.raw.icon_background)
                    icon_background.scale(w_desktop, w_desktop)

                    for (i in -n_images..<n_images + 1) {
                        var drawable = icon_background.drawable
                        val y = Math.to_float(top - i * w_desktop)
                        drawing.drawBitmap(drawable, 0F, y, paint)

                        if (i == 0) {
                            var drawable = icon_foreground.drawable
                            drawing.drawBitmap(drawable, 0F, y, paint)
                        }
                    }

                    holder.unlockCanvasAndPost(drawing)
                    break
                }
            } catch (_: Exception) {
                exitProcess(0)
            }
        }
    }

    fun wait_sounds() {
        watch_start()

        while (Config.n_sounds < 36) {
            watch_check()
        }
    }

    private fun draw() {
        try {
            if (holder.surface.isValid) {
                val drawing = holder.lockHardwareCanvas()
                val window = device.graphic.window
                drawing.drawBitmap(window.drawable, 0F, 0F, window.paint)
                holder.unlockCanvasAndPost(drawing)
            }
        } catch (_: Exception) {
            exitProcess(0)
        }
    }

    fun quit() {
        action.set_quit()
        stop()
    }

    override fun onTouchEvent(motion_event: MotionEvent): Boolean {
        val is_clic = motion_event.action == MotionEvent.ACTION_DOWN

        if (ready && !device.event.is_clic() && is_clic) {
            device.event.kind = 1
            device.event.x = Math.to_int(motion_event.x)
            device.event.y = Math.to_int(motion_event.y)
            val is_right = menu.layout.find_zone(device) == Panel.RIGHT

            if (menu.party.is_home() && is_right) {
                quit()
            }
        }

        performClick()

        return true
    }

    override fun performClick(): Boolean {
        val value = super.performClick()

        return value
    }
}

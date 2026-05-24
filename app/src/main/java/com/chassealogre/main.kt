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
import android.view.MotionEvent
import android.view.SurfaceView

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

    init {
        start()
    }

    fun start() {
        try {
            thread.start()
        } catch (_: IllegalThreadStateException) {
            Config.activity.finish()
        }
    }

    fun stop() {
        try {
            thread.join()
        } catch (_: InterruptedException) {
            Config.activity.finish()
        }
    }

    override fun run() {
        load_screen()

        device = Device(Config.w_drawing, Config.h_drawing, Config.frame_rate)
        frame = Frame(device)
        menu = Menu(device)
        game = Game(device)
        pen = Pen(device)
        font = Font(device)
        action = Action()

        load_sounds()

        Config.set_ready()

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

        device.music.stop()
    }

    fun load_screen() {
        Config.watch_start()

        while (true) {
            Config.watch_check()

            try {
                if (holder.surface.isValid) {
                    val drawing = holder.lockHardwareCanvas()
                    val w_drawing = drawing.width
                    val h_drawing = drawing.height

                    val is_portrait: Boolean
                    val size_icon: Int
                    val x_left: Int
                    val y_top: Int
                    val n_images: Int

                    if (w_drawing > h_drawing) {
                        is_portrait = false
                        size_icon = h_drawing
                        x_left = Math.half(w_drawing - h_drawing)
                        y_top = 0
                        n_images =
                            Math.ceil(Math.to_double(x_left) / h_drawing)
                    } else {
                        is_portrait = true
                        size_icon = w_drawing
                        x_left = 0
                        y_top = Math.half(h_drawing - w_drawing)
                        n_images = Math.ceil(Math.to_double(y_top) / w_drawing)
                    }

                    val icon_foreground = Image()
                    icon_foreground.load(R.raw.icon_foreground)
                    icon_foreground.scale(size_icon, size_icon)

                    val icon_background = Image()
                    icon_background.load(R.raw.icon_background)
                    icon_background.scale(size_icon, size_icon)

                    for (i in -n_images..<n_images + 1) {
                        val x: Float
                        val y: Float

                        if (is_portrait) {
                            x = Math.to_float(x_left)
                            y = Math.to_float(y_top + i * size_icon)
                        } else {
                            x = Math.to_float(x_left + i * size_icon)
                            y = Math.to_float(y_top)
                        }

                        val drawable = icon_background.drawable
                        drawing.drawBitmap(drawable, x, y, Config.paint)

                        if (i == 0) {
                            val drawable = icon_foreground.drawable
                            drawing.drawBitmap(drawable, x, y, Config.paint)
                        }
                    }

                    val copyright = Image()
                    copyright.load(R.raw.copyright)
                    val w = Math.to_double(copyright.get_w())
                    val h = Math.to_double(copyright.get_h())
                    val w_copyright = size_icon
                    val h_copyright = Math.to_int(size_icon * h / w)

                    copyright.scale(w_copyright, h_copyright)
                    val drawable = copyright.drawable
                    val x = Math.to_float(w_drawing - 1 - copyright.get_w())
                    val y = Math.to_float(h_drawing - 1 - copyright.get_h())
                    drawing.drawBitmap(drawable, x, y, Config.paint)

                    holder.unlockCanvasAndPost(drawing)

                    Config.set_size_drawing(w_drawing, h_drawing)
                    break
                }
            } catch (_: Exception) {
                Config.activity.finish()
            }
        }
    }

    fun load_sounds() {
        Config.set_sounds()
        Config.watch_start()

        while (Config.n_sounds < Config.sounds.get_size()) {
            Config.watch_check()
        }
    }

    private fun draw() {
        try {
            if (holder.surface.isValid) {
                val drawing = holder.lockHardwareCanvas()
                val window = device.graphic.window
                drawing.drawBitmap(window.drawable, 0F, 0F, Config.paint)
                holder.unlockCanvasAndPost(drawing)
            }
        } catch (_: Exception) {
            Config.activity.finish()
        }
    }

    override fun onTouchEvent(motion_event: MotionEvent): Boolean {
        val is_clic = motion_event.action == MotionEvent.ACTION_DOWN

        if (Config.ready && !device.event.is_clic() && is_clic) {
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

    fun quit() {
        action.set_quit()
        stop()
        Config.activity.finish()
    }

    override fun performClick(): Boolean {
        val value = super.performClick()

        return value
    }
}

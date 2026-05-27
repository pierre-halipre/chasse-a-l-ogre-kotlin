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
import com.chassealogre.Config.Companion.soundPool

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
    var focus: Boolean = true

    fun begin() {
        start()
    }

    fun end() {
        (context as Activity).finish()
    }

    fun start() {
        try {
            thread.start()
        } catch (_: IllegalThreadStateException) {
            end()
        }
    }

    fun stop() {
        try {
            thread.join()
        } catch (_: InterruptedException) {
            end()
        }
    }

    fun changeFocus(hasFocus: Boolean) {
        focus = hasFocus

        if (ready) {
            if (focus) {
                soundPool.autoResume()
            } else {
                soundPool.autoPause()
            }
        }
    }

    override fun run() {
        showLoading()

        device = Device(width, height, 60, context)
        frame = Frame(device)
        menu = Menu(device)
        game = Game(device)
        pen = Pen(device)
        font = Font(device)
        action = Action()

        waitLoading()

        ready = true

        while (device.clock.thread.isRun()) {
            val clock = device.clock
            val refreshTime = clock.getRefreshTime()

            if (clock.thread.isTick(refreshTime) && focus) {
                if (device.event.isClic()) {
                    action.setZoneEvent(frame, menu, game, device)
                    action.checkChangeDesign(game, device)
                    device.event.kind = 0
                }

                if (menu.party.isNone()) {
                    menu.reset()
                }

                menu.update(action.zone, device)

                val resetDemo = game.logger.isEnd() && menu.party.isHome()

                if (menu.needReset || resetDemo) {
                    game.reset()
                    menu.needReset = false
                } else if (game.logger.isEnd()) {
                    menu.party.setEnd()
                }

                val needUpdate = menu.party.isHome() || menu.party.isPlay()

                if (needUpdate && !menu.needResume) {
                    var zone: Int

                    if (menu.party.isHome()) {
                        zone = action.getZoneDemo(game)
                    } else {
                        zone = action.zone
                    }

                    game.update(zone, device)
                } else {
                    menu.needResume = false
                }

                if (!action.needQuit) {
                    action.zone = Layout.NONE

                    if (action.needChange()) {
                        device.design.change()
                        device.music.changeVolume(device.design)
                        action.countsChange = 0
                    }

                    if (menu.party.isHome()) {
                        game.unsetPlayAudio()
                    }

                    game.play()
                    menu.play()

                    frame.drawBackground(device)
                    game.draw(pen, device)
                    game.clearBounds(device)
                    menu.draw(pen, device)

                    if (!menu.party.isPlay()) {
                        game.drawGreyedOut(device)

                        if (menu.party.isHome()) {
                            font.setHome(device)
                        } else if (menu.party.isPause()) {
                            font.setPause(game.ticks, device)
                        } else {
                            font.setEnd(game.tally.score, device)
                        }

                        font.draw(frame.border, device)
                    }

                    frame.drawForeground(device)

                    drawWindow()
                } else {
                    clock.thread.stop()
                }
            }
        }

        device.music.stop()
    }

    fun showLoading() {
        while (true) {
            if (holder.surface.isValid) {
                val surface = holder.lockHardwareCanvas()

                val isPortrait: Boolean
                val size: Int
                val xLeft: Int
                val yTop: Int
                val nImages: Int

                if (width > height) {
                    isPortrait = false
                    size = height
                    xLeft = Math.half(width - height)
                    yTop = 0
                    nImages = Math.ceil(Math.toDouble(xLeft) / height)
                } else {
                    isPortrait = true
                    size = width
                    xLeft = 0
                    yTop = Math.half(height - width)
                    nImages = Math.ceil(Math.toDouble(yTop) / width)
                }

                val iconForeground = Image()
                iconForeground.load(R.raw.icon_foreground, context)
                iconForeground.scale(size, size)

                val iconBackground = Image()
                iconBackground.load(R.raw.icon_background, context)
                iconBackground.scale(size, size)

                for (i in -nImages..<nImages + 1) {
                    val x: Int
                    val y: Int

                    if (isPortrait) {
                        x = xLeft
                        y = yTop + i * size
                    } else {
                        x = xLeft + i * size
                        y = yTop
                    }

                    drawOnSurface(iconBackground, x, y, surface)

                    if (i == 0) {
                        drawOnSurface(iconForeground, x, y, surface)
                    }
                }

                val copyright = Image()
                copyright.load(R.raw.copyright, context)
                val w = Math.toDouble(copyright.getW())
                val h = Math.toDouble(copyright.getH())
                val wCopyright = size
                val hCopyright = Math.toInt(size * h / w)

                copyright.scale(wCopyright, hCopyright)
                val x = width - 1 - copyright.getW()
                val y = height - 1 - copyright.getH()

                drawOnSurface(copyright, x, y, surface)

                holder.unlockCanvasAndPost(surface)
                break
            }
        }
    }

    fun waitLoading() {
        while (Config.nSounds < 9 * device.design.nModes) {
        }
    }

    fun drawWindow() {
        if (holder.surface.isValid) {
            val surface = holder.lockHardwareCanvas()
            drawOnSurface(device.graphic.window, 0, 0, surface)
            holder.unlockCanvasAndPost(surface)
        }
    }

    fun drawOnSurface(
        image: Image,
        x: Int,
        y: Int,
        surface: android.graphics.Canvas,
    ) {
        val drawable = image.drawable
        val paint = Config.paint
        surface.drawBitmap(drawable, Math.toFloat(x), Math.toFloat(y), paint)
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        val isClic = motionEvent.action == MotionEvent.ACTION_DOWN

        if (ready && !device.event.isClic() && isClic) {
            device.event.kind = 1
            device.event.x = Math.toInt(motionEvent.x)
            device.event.y = Math.toInt(motionEvent.y)
            val isRight = menu.layout.findZone(device) == Panel.RIGHT

            if (menu.party.isHome() && isRight) {
                action.setQuit()
                stop()
                end()
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

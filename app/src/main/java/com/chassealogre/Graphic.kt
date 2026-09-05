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

class Graphic(
    wWindow: Int,
    hWindow: Int,
) {
    open class Rasters(
        nWCasesInit: Int,
        nHCasesInit: Int,
        nSheetsInit: Int,
    ) : Array<SpriteSheet>() {
        val nWCases: Int = nWCasesInit
        val nHCases: Int = nHCasesInit
        val nSheets: Int = nSheetsInit

        open fun getW(graphic: Graphic): Int = graphic.toWCases(nWCases)

        fun getH(graphic: Graphic): Int = graphic.toHCases(nHCases)

        open fun getNSprites(mode: Int): Int {
            val spriteSheet = get(mode)

            return spriteSheet.nImages
        }

        open fun getISprite(
            mode: Int,
            iImage: Int,
        ): Int = iImage

        fun fill(
            id: Int,
            nImages: Int,
            context: Context,
        ) {
            add(SpriteSheet(id, nImages, context))
        }

        fun pick(
            mode: Int,
            iSprite: Int,
            jSprite: Int,
            flipW: Boolean,
            graphic: Graphic,
        ): Image {
            val sprite = Image()
            val spriteSheet = get(mode)
            val nImages = spriteSheet.nImages
            val wSpriteSheet = spriteSheet.getW()
            val hSpriteSheet = spriteSheet.getH()
            var wSprite = Math.floor(wSpriteSheet / nImages)
            var hSprite = Math.floor(hSpriteSheet / nSheets)
            sprite.setSize(wSprite, hSprite)
            val xSprite = iSprite * wSprite
            val ySprite = jSprite * hSprite
            sprite.from(0, 0, spriteSheet, xSprite, ySprite, wSprite, hSprite)
            sprite.flip(flipW)
            scaleSprite(sprite, graphic)

            val result = Image()
            val w = getW(graphic)
            val h = getH(graphic)
            result.setSize(w, h)
            wSprite = sprite.getW()
            hSprite = sprite.getH()
            val x = Math.half(w - wSprite)
            val y = Math.half(h - hSprite)
            result.from(x, y, sprite, 0, 0, wSprite, hSprite)

            return result
        }

        open fun scaleSprite(
            sprite: Image,
            graphic: Graphic,
        ) {
            val wSprite = getW(graphic)
            val hSprite = getH(graphic)
            sprite.scale(wSprite, hSprite)
        }
    }

    open class RastersLoop(
        nWCasesInit: Int,
        nHCasesInit: Int,
        nSheetsInit: Int,
    ) : Rasters(nWCasesInit, nHCasesInit, nSheetsInit) {
        override fun getNSprites(mode: Int): Int {
            var result: Int
            val spriteSheet = get(mode)

            if (spriteSheet.nImages > 1) {
                result = (spriteSheet.nImages - 1) * 2
            } else {
                result = super.getNSprites(mode)
            }

            return result
        }

        override fun getISprite(
            mode: Int,
            iImage: Int,
        ): Int {
            var result: Int
            val spriteSheet = get(mode)

            if (iImage >= spriteSheet.nImages) {
                result = getNSprites(mode) - iImage
            } else {
                result = super.getISprite(mode, iImage)
            }

            return result
        }
    }

    class RastersZone(
        nSheetsInit: Int,
    ) : RastersLoop(2, 4, nSheetsInit) {
        override fun scaleSprite(
            sprite: Image,
            graphic: Graphic,
        ) {
            val wCase = graphic.wCase
            val hCase = graphic.hCase
            val slope = Math.toDouble(hCase - 1) / (wCase - 1)
            val origin = Math.toDouble(hCase - 1)
            var a = -slope
            var b = origin
            val xTopLeft = Math.round(((wCase - 2 * hCase + b) / (1 - a)))
            val yTopLeft = Math.round(a * xTopLeft + b)

            a = slope
            b = 0.0
            val xTopRight = Math.round((2 * hCase - b - 1) / (1 + a))
            val yTopRight = Math.round(a * xTopRight + b)

            a = slope
            b = 0.0
            val xBottomLeft = Math.round((wCase - hCase - 1 - b) / (1 + a))
            val yBottomLeft = Math.round(a * xBottomLeft + b)

            a = -slope
            b = origin
            val xBottomRight = Math.round((hCase + b) / (1 - a))
            val yBottomRight = Math.round(a * xBottomRight + b)
            val xLeft = Math.max(xTopLeft, xBottomLeft)
            val xRight = wCase - 1 - Math.min(xTopRight, xBottomRight)
            val yTop = Math.max(yTopLeft, yTopRight)
            val yBottom = hCase - 1 - Math.min(yBottomLeft, yBottomRight) + 1
            val w = getW(graphic)
            val h = getH(graphic)
            val wSprite = w - xLeft - xRight
            val hSprite = h - yTop - yBottom
            sprite.scale(wSprite, hSprite)
        }
    }

    class RastersCanvas(
        nWCasesInit: Int,
        nHCasesInit: Int,
        xInit: Int,
        yInit: Int,
    ) : RastersLoop(nWCasesInit, nHCasesInit, 2) {
        val x: Int = xInit
        val y: Int = yInit

        override fun getW(graphic: Graphic): Int {
            val w = super.getW(graphic)

            return Math.half(w)
        }
    }

    class SpriteSheet(
        id: Int,
        nImagesInit: Int,
        context: Context,
    ) : Image() {
        val nImages: Int = nImagesInit

        init {
            load(id, context)
        }
    }

    val window: Window = Window(wWindow, hWindow)
    var wCase: Int = 0
    var hCase: Int = 0
    var border: RastersCanvas
    var panel: RastersCanvas
    val buttons: RastersZone = RastersZone(6)
    var board: RastersCanvas
    val logger: RastersZone = RastersZone(9)
    val zombie: RastersZone = RastersZone(9)
    val vampire: RastersZone = RastersZone(9)
    val skeleton: RastersZone = RastersZone(9)
    val ghost: RastersZone = RastersZone(9)
    val deer: RastersZone = RastersZone(9)
    val rabbit: RastersZone = RastersZone(9)
    val cherry: RastersZone = RastersZone(3)
    val fence: Rasters = Rasters(2, 4, 3)
    val pen: Rasters = Rasters(1, 1, 1)
    val font: Rasters = Rasters(1, 2, 1)
    val greyedOut: Image = Image()

    init {
        setSizeCase()
        val xBorder = getXWindow()
        val yBorder = getYWindow()
        border = RastersCanvas(10, 22, xBorder, yBorder)
        val xPanel = xBorder + wCase
        val yPanel = yBorder + hCase
        panel = RastersCanvas(8, 4, xPanel, yPanel)
        val xBoard = xPanel
        val yBoard = yPanel + 4 * hCase
        board = RastersCanvas(8, 16, xBoard, yBoard)
    }

    fun setSizeCase() {
        val wWindow = window.getW()
        val hWindow = window.getH()
        val ratioWCase = Math.sqrt(3) / 2
        val ratioHCase = 1.0 / 2
        val ratioWWindow = 8 * ratioWCase + 2 * ratioHCase
        val ratioHWindow = 22 * ratioHCase
        val diagonalCaseFromW = Math.floor(wWindow / ratioWWindow)
        val diagonalCaseFromH = Math.floor(hWindow / ratioHWindow)
        val diagonalCase = Math.min(diagonalCaseFromW, diagonalCaseFromH)
        wCase = Math.floor(diagonalCase * ratioWCase)
        hCase = Math.floor(diagonalCase * ratioHCase)
    }

    fun fill(
        id: Int,
        context: Context,
    ) {
        greyedOut.load(id, context)
        greyedOut.scale(8 * wCase, 16 * hCase)
    }

    fun getXWindow(): Int {
        val wWindow = window.getW()

        return Math.half(wWindow - 10 * wCase)
    }

    fun getYWindow(): Int {
        val hWindow = window.getH()

        return Math.half(hWindow - 22 * hCase)
    }

    fun toWCases(n: Int): Int = n * wCase

    fun toHCases(n: Int): Int = n * hCase

    fun toICase(x: Int): Int = Math.floor(x / wCase)

    fun toJCase(y: Int): Int = Math.floor(y / hCase)
}

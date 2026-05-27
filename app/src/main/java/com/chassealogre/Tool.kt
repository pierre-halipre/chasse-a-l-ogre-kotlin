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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.media.SoundPool
import java.io.BufferedReader
import kotlin.math.pow
import kotlin.random.Random

class Math {
    companion object {
        fun toDouble(x: Int): Double = x.toDouble()

        fun toInt(x: Double): Int = x.toInt()

        fun toInt(x: Float): Int = x.toInt()

        fun toFloat(x: Int): Float = x.toFloat()

        fun toFloat(x: Double): Float = x.toFloat()

        fun abs(x: Double): Double = kotlin.math.abs(x)

        fun floor(x: Double): Int = toInt(kotlin.math.floor(x))

        fun floor(x: Int): Int = floor(toDouble(x))

        fun ceil(x: Double): Int = toInt(kotlin.math.ceil(x))

        fun ceil(x: Int): Int = ceil(toDouble(x))

        fun round(x: Double): Int {
            var result: Int
            val xRest = abs(x) % 1

            if ((x >= 0 && xRest < 1 / 2) || (x < 0 && xRest >= 1 / 2)) {
                result = floor(x)
            } else {
                result = ceil(x)
            }

            return result
        }

        fun round(x: Int): Int = round(toDouble(x))

        fun sqrt(x: Int): Double = kotlin.math.sqrt(toDouble(x))

        fun pow(
            x: Double,
            e: Int,
        ): Double = x.pow(e)

        fun pow(
            x: Int,
            e: Int,
        ): Double = pow(toDouble(x), e)

        fun rand(m: Int): Int = Random.nextInt(m)

        fun half(x: Int): Int = floor(x / 2)

        fun max(
            x1: Int,
            x2: Int,
        ): Int {
            var result: Int

            if (x1 > x2) {
                result = x1
            } else {
                result = x2
            }

            return result
        }

        fun min(
            x1: Int,
            x2: Int,
        ): Int {
            var result: Int

            if (x1 < x2) {
                result = x1
            } else {
                result = x2
            }

            return result
        }

        fun distance(
            start: Int,
            end: Int,
            ratio: Double,
        ): Int = start + floor((end - start) * ratio)

        fun isFlag(
            x: Int,
            flag: Int,
        ): Boolean = x.and(flag) == flag

        fun isFlag(
            x: Int,
            flag: Double,
        ): Boolean = isFlag(x, toInt(flag))

        fun hasFlag(
            x: Int,
            flag: Int,
        ): Boolean = x.and(flag) != 0
    }
}

class File(
    id: Int,
    context: Context,
) {
    val buffer: BufferedReader

    init {
        val resources = context.resources
        val charsets = Charsets.US_ASCII
        buffer = resources.openRawResource(id).bufferedReader(charsets)
    }

    fun read(): Int {
        val data = buffer.read()

        if (data == -1) {
            buffer.close()
        }

        return data
    }
}

open class Array<T> {
    var elements: MutableList<T> = mutableListOf()

    fun getSize(): Int = elements.size

    fun set(
        i: Int,
        value: T,
    ) {
        elements[i] = value
    }

    fun get(i: Int): T = elements[i]

    fun add(value: T) {
        elements.add(value)
    }
}

class Config {
    companion object {
        val options: BitmapFactory.Options = createOptions()
        val paint: Paint = createPaint()
        val soundPool: SoundPool = createSoundPool()
        var nSounds: Int = 0

        fun createPaint(): Paint {
            val paint = Paint()
            paint.isAntiAlias = false
            paint.isDither = false
            paint.isFilterBitmap = false

            return paint
        }

        fun createOptions(): BitmapFactory.Options {
            val options = BitmapFactory.Options()
            options.inScaled = false

            return options
        }

        fun createSoundPool(): SoundPool {
            val builder = SoundPool.Builder()
            builder.setMaxStreams(4)
            val soundPool = builder.build()
            soundPool.setOnLoadCompleteListener { _, _, _ -> nSounds += 1 }

            return soundPool
        }

        fun getTime(): Long = System.currentTimeMillis()
    }
}

class Thread {
    var run: Boolean = true
    var time: Long = 0

    fun isRun(): Boolean = run

    fun stop() {
        run = false
    }

    fun isTick(refreshRate: Int): Boolean {
        var result = false
        val timeCurrent = Config.getTime()

        if (timeCurrent - time >= refreshRate) {
            result = true
            time = timeCurrent
        }

        return result
    }
}

class Event {
    var kind: Int = 0
    var x: Int = 0
    var y: Int = 0

    fun isClic(): Boolean = kind != 0
}

open class Image {
    lateinit var drawable: Bitmap

    fun load(
        id: Int,
        context: Context,
    ) {
        val resources = context.resources
        val options = Config.options
        val resource = BitmapFactory.decodeResource(resources, id, options)
        drawable = Bitmap.createBitmap(resource)
        setDensity()
    }

    fun setDensity() {
        drawable.density = Bitmap.DENSITY_NONE
    }

    fun setSize(
        w: Int,
        h: Int,
    ) {
        val config = Bitmap.Config.ARGB_4444
        drawable = Bitmap.createBitmap(w, h, config)
        setDensity()
    }

    fun getW(): Int = drawable.getWidth()

    fun getH(): Int = drawable.getHeight()

    fun scale(
        w: Int,
        h: Int,
    ) {
        drawable = Bitmap.createScaledBitmap(drawable, w, h, false)
    }

    fun flip(flipW: Boolean) {
        val matrix = Matrix()
        val sx: Float

        if (flipW) {
            sx = -1F
        } else {
            sx = 1F
        }

        matrix.setScale(sx, 1F)
        val w = getW()
        val h = getH()
        drawable = Bitmap.createBitmap(drawable, 0, 0, w, h, matrix, false)
    }

    fun onWindow(
        x: Int,
        y: Int,
        window: Window,
    ) {
        val xImage = Math.toFloat(x)
        val yImage = Math.toFloat(y)
        window.drawing.drawBitmap(drawable, xImage, yImage, Config.paint)
    }

    fun from(
        x: Int,
        y: Int,
        image: Image,
        xImage: Int,
        yImage: Int,
        w: Int,
        h: Int,
    ) {
        val source = Rect(xImage, yImage, xImage + w, yImage + h)
        val destination = Rect(x, y, x + w, y + h)
        val drawing = android.graphics.Canvas(drawable)
        drawing.drawBitmap(image.drawable, source, destination, null)
    }
}

class Window(
    w: Int,
    h: Int,
) : Image() {
    val drawing: android.graphics.Canvas

    init {
        setSize(w, h)
        drawing = android.graphics.Canvas(drawable)
    }

    fun setBackgroundColor(
        r: Int,
        g: Int,
        b: Int,
    ) {
        Config.paint.setARGB(255, r, g, b)
    }

    fun drawRectangle(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val xMin = Math.toFloat(left)
        val xMax = Math.toFloat(right)
        val yMin = Math.toFloat(top)
        val yMax = Math.toFloat(bottom)
        drawing.drawRect(xMin, yMin, xMax, yMax, Config.paint)
    }
}

class Sound(
    idResource: Int,
    volumeMaxInit: Double,
    context: Context,
) {
    var listenable: Int = 0
    var id: Int = -1
    var volumeMax: Double = volumeMaxInit
    var volumeCurrent: Double = 0.0

    init {
        listenable = Config.soundPool.load(context, idResource, 1)
    }

    fun play(isLoop: Boolean) {
        var loop: Int

        if (isLoop) {
            loop = -1
        } else {
            loop = 0
        }

        val volume = Math.toFloat(volumeCurrent)
        val soundPool = Config.soundPool
        id = soundPool.play(listenable, volume, volume, 1, loop, 1F)
    }

    fun stop() {
        if (id != -1) {
            Config.soundPool.stop(id)
            id = -1
        }
    }

    fun setVolume(volumeInit: Double) {
        volumeCurrent = volumeInit * volumeMax

        if (id != -1) {
            val volume = Math.toFloat(volumeCurrent)
            Config.soundPool.setVolume(id, volume, volume)
        }
    }
}

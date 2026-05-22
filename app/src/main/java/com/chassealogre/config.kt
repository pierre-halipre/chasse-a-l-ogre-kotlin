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
import android.content.res.Resources
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
        fun to_double(x: Int): Double = x.toDouble()

        fun to_int(x: Double): Int = x.toInt()

        fun to_int(x: Float): Int = x.toInt()

        fun to_float(x: Int): Float = x.toFloat()

        fun to_float(x: Double): Float = x.toFloat()

        fun abs(x: Double): Double = kotlin.math.abs(x)

        fun floor(x: Double): Int = to_int(kotlin.math.floor(x))

        fun floor(x: Int): Int = floor(to_double(x))

        fun ceil(x: Double): Int = to_int(kotlin.math.ceil(x))

        fun ceil(x: Int): Int = ceil(to_double(x))

        fun round(x: Double): Int {
            var result: Int
            val x_rest = abs(x) % 1

            if ((x >= 0 && x_rest < 1 / 2) || (x < 0 && x_rest >= 1 / 2)) {
                result = floor(x)
            } else {
                result = ceil(x)
            }

            return result
        }

        fun round(x: Int): Int = round(to_double(x))

        fun sqrt(x: Int): Double = kotlin.math.sqrt(to_double(x))

        fun pow(
            x: Double,
            e: Int,
        ): Double = x.pow(e)

        fun pow(
            x: Int,
            e: Int,
        ): Double = pow(to_double(x), e)

        fun rand(m: Int): Int = Random.nextInt(m)

        fun half(x: Int): Int = floor(x / 2)

        fun max(
            x_1: Int,
            x_2: Int,
        ): Int {
            var result: Int

            if (x_1 > x_2) {
                result = x_1
            } else {
                result = x_2
            }

            return result
        }

        fun min(
            x_1: Int,
            x_2: Int,
        ): Int {
            var result: Int

            if (x_1 < x_2) {
                result = x_1
            } else {
                result = x_2
            }

            return result
        }

        fun distance(
            start: Int,
            end: Int,
            ratio: Double,
        ): Int = start + floor((end - start) * ratio)

        fun is_flag(
            x: Int,
            flag: Int,
        ): Boolean = x.and(flag) == flag

        fun is_flag(
            x: Int,
            flag: Double,
        ): Boolean = is_flag(x, to_int(flag))

        fun has_flag(
            x: Int,
            flag: Int,
        ): Boolean = x.and(flag) != 0
    }
}

class File(
    id: Int,
) {
    val buffer: BufferedReader

    init {
        val resources = Config.context.resources
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

    fun get_size(): Int = elements.size

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
        lateinit var context: Context
        val options: BitmapFactory.Options = BitmapFactory.Options()
        lateinit var sound_pool: SoundPool
        var n_sounds: Int = 0

        fun set_context(context_application: Context) {
            context = context_application
            options.inScaled = false
        }

        fun load_sounds() {
            val builder = SoundPool.Builder()
            builder.setMaxStreams(4)
            sound_pool = builder.build()
            sound_pool.setOnLoadCompleteListener { _, _, _ -> n_sounds += 1 }
        }

        fun release_sounds() {
            sound_pool.release()
        }

        fun get_w_desktop(): Int {
            val result: Int = Resources.getSystem().displayMetrics.widthPixels

            return result
        }

        fun get_h_desktop(): Int {
            val result = Resources.getSystem().displayMetrics.heightPixels

            return result
        }

        fun get_frame_rate(): Int = 60

        fun get_time(): Long = System.currentTimeMillis()
    }
}

class Thread {
    var run: Boolean = true
    var time: Long = 0

    fun is_run(): Boolean = run

    fun stop() {
        run = false
    }

    fun is_tick(refresh_rate: Int): Boolean {
        var result = false
        val time_current = Config.get_time()

        if (time_current - time >= refresh_rate) {
            result = true
            time = time_current
        }

        return result
    }
}

class Event {
    var kind: Int = 0
    var x: Int = 0
    var y: Int = 0

    fun is_clic(): Boolean = kind != 0
}

open class Image {
    lateinit var drawable: Bitmap

    fun load(id: Int) {
        val resources = Config.context.resources
        val options = Config.options
        val resource = BitmapFactory.decodeResource(resources, id, options)
        drawable = Bitmap.createBitmap(resource)
        set_density()
    }

    fun set_density() {
        drawable.density = Bitmap.DENSITY_NONE
    }

    fun set_size(
        w: Int,
        h: Int,
    ) {
        val config = Bitmap.Config.ARGB_4444
        drawable = Bitmap.createBitmap(w, h, config)
        set_density()
    }

    fun get_w(): Int = drawable.getWidth()

    fun get_h(): Int = drawable.getHeight()

    fun scale(
        w: Int,
        h: Int,
    ) {
        drawable = Bitmap.createScaledBitmap(drawable, w, h, false)
    }

    fun flip(flip_w: Boolean) {
        val matrix = Matrix()
        val sx: Float

        if (flip_w) {
            sx = -1F
        } else {
            sx = 1F
        }

        matrix.setScale(sx, 1F)
        val w = get_w()
        val h = get_h()
        drawable = Bitmap.createBitmap(drawable, 0, 0, w, h, matrix, false)
    }

    fun on_window(
        x: Int,
        y: Int,
        window: Window,
    ) {
        val x_image = Math.to_float(x)
        val y_image = Math.to_float(y)
        window.drawing.drawBitmap(drawable, x_image, y_image, window.paint)
    }

    fun from(
        x: Int,
        y: Int,
        image: Image,
        x_image: Int,
        y_image: Int,
        w: Int,
        h: Int,
    ) {
        val source = Rect(x_image, y_image, x_image + w, y_image + h)
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
    val paint: Paint = Paint()

    init {
        set_size(w, h)
        drawing = android.graphics.Canvas(drawable)
        paint.isAntiAlias = false
        paint.isDither = false
        paint.isFilterBitmap = false
    }

    fun set_background_color(
        r: Int,
        g: Int,
        b: Int,
    ) {
        paint.setARGB(255, r, g, b)
    }
}

class Sound(
    id_resource: Int,
    volume_max_init: Double,
) {
    var listenable: Int = 0
    var id: Int = -1
    var volume_max: Double = volume_max_init
    var volume_current: Double = 0.0

    init {
        listenable = Config.sound_pool.load(Config.context, id_resource, 1)
    }

    fun play(is_loop: Boolean) {
        var loop: Int

        if (is_loop) {
            loop = -1
        } else {
            loop = 0
        }

        val volume = Math.to_float(volume_current)
        id = Config.sound_pool.play(listenable, volume, volume, 1, loop, 1F)
    }

    fun stop() {
        if (id != -1) {
            Config.sound_pool.stop(id)
            id = -1
        }
    }

    fun set_volume(volume_init: Double) {
        volume_current = volume_init * volume_max

        if (id != -1) {
            val volume = Math.to_float(volume_current)
            Config.sound_pool.setVolume(id, volume, volume)
        }
    }

    fun release() {
        Config.sound_pool.unload(listenable)
    }
}

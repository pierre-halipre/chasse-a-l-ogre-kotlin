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

class Graphic(
    w_window: Int,
    h_window: Int,
) {
    val window: Window = Window(w_window, h_window)
    var w_case: Int = 0
    var h_case: Int = 0
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
    val greyed_out: Image = Image()

    init {
        set_size_case()
        val x_border = get_x_screen()
        val y_border = get_y_screen()
        border = RastersCanvas(10, 22, x_border, y_border)
        val x_panel = x_border + w_case
        val y_panel = y_border + h_case
        panel = RastersCanvas(8, 4, x_panel, y_panel)
        val x_board = x_panel
        val y_board = y_panel + 4 * h_case
        board = RastersCanvas(8, 16, x_board, y_board)
    }

    fun set_size_case() {
        val w_window = window.get_w()
        val h_window = window.get_h()
        val ratio_w_case = Math.sqrt(3) / 2
        val ratio_h_case = 1.0 / 2
        val ratio_w_screen = 8 * ratio_w_case + 2 * ratio_h_case
        val ratio_h_screen = 22 * ratio_h_case
        val diagonal_case_in_w = Math.floor(w_window / ratio_w_screen)
        val diagonal_case_in_h = Math.floor(h_window / ratio_h_screen)
        val diagonal_case = Math.min(diagonal_case_in_w, diagonal_case_in_h)
        w_case = Math.floor(diagonal_case * ratio_w_case)
        h_case = Math.floor(diagonal_case * ratio_h_case)
    }

    fun fill(id: Int) {
        greyed_out.load(id)
        greyed_out.scale(8 * w_case, 16 * h_case)
    }

    fun get_x_screen(): Int {
        val w_window = window.get_w()

        return Math.half(w_window - 10 * w_case)
    }

    fun get_y_screen(): Int {
        val h_window = window.get_h()

        return Math.half(h_window - 22 * h_case)
    }

    fun to_w_cases(n: Int): Int = n * w_case

    fun to_h_cases(n: Int): Int = n * h_case

    fun to_i_case(x: Int): Int = Math.floor(x / w_case)

    fun to_j_case(y: Int): Int = Math.floor(y / h_case)
}

open class Rasters(
    n_w_cases_init: Int,
    n_h_cases_init: Int,
    n_sheets_init: Int,
) : Array<SpriteSheet>() {
    val n_w_cases: Int = n_w_cases_init
    val n_h_cases: Int = n_h_cases_init
    val n_sheets: Int = n_sheets_init

    open fun get_w(graphic: Graphic): Int = graphic.to_w_cases(n_w_cases)

    fun get_h(graphic: Graphic): Int = graphic.to_h_cases(n_h_cases)

    open fun get_n_sprites(mode: Int): Int {
        val sprite_sheet = get(mode)

        return sprite_sheet.n_images
    }

    open fun get_i_sprite(
        mode: Int,
        i_image: Int,
    ): Int = i_image

    fun fill(
        id: Int,
        n_images: Int,
    ) {
        val sprite_sheet = SpriteSheet(id, n_images)
        add(sprite_sheet)
    }

    fun pick(
        mode: Int,
        i_sprite: Int,
        j_sprite: Int,
        flip_w: Boolean,
        graphic: Graphic,
    ): Image {
        val sprite = Image()
        val sprite_sheet = get(mode)
        val n_images = sprite_sheet.n_images
        val w_sprite_sheet = sprite_sheet.get_w()
        val h_sprite_sheet = sprite_sheet.get_h()
        var w_sprite = Math.floor(w_sprite_sheet / n_images)
        var h_sprite = Math.floor(h_sprite_sheet / n_sheets)
        sprite.set_size(w_sprite, h_sprite)
        val x_sprite = i_sprite * w_sprite
        val y_sprite = j_sprite * h_sprite
        sprite.from(0, 0, sprite_sheet, x_sprite, y_sprite, w_sprite, h_sprite)
        sprite.flip(flip_w)
        scale_sprite(sprite, graphic)

        val result = Image()
        val w = get_w(graphic)
        val h = get_h(graphic)
        result.set_size(w, h)
        w_sprite = sprite.get_w()
        h_sprite = sprite.get_h()
        val x = Math.half(w - w_sprite)
        val y = Math.half(h - h_sprite)
        result.from(x, y, sprite, 0, 0, w_sprite, h_sprite)

        return result
    }

    open fun scale_sprite(
        sprite: Image,
        graphic: Graphic,
    ) {
        val w_sprite = get_w(graphic)
        val h_sprite = get_h(graphic)
        sprite.scale(w_sprite, h_sprite)
    }
}

open class RastersLoop(
    n_w_cases_init: Int,
    n_h_cases_init: Int,
    n_sheets_init: Int,
) : Rasters(n_w_cases_init, n_h_cases_init, n_sheets_init) {
    override fun get_n_sprites(mode: Int): Int {
        var result: Int
        val sprite_sheet = get(mode)

        if (sprite_sheet.n_images > 1) {
            result = (sprite_sheet.n_images - 1) * 2
        } else {
            result = super.get_n_sprites(mode)
        }

        return result
    }

    override fun get_i_sprite(
        mode: Int,
        i_image: Int,
    ): Int {
        var result: Int
        val sprite_sheet = get(mode)

        if (i_image >= sprite_sheet.n_images) {
            result = get_n_sprites(mode) - i_image
        } else {
            result = super.get_i_sprite(mode, i_image)
        }

        return result
    }
}

class RastersZone(
    n_sheets_init: Int,
) : RastersLoop(2, 4, n_sheets_init) {
    override fun scale_sprite(
        sprite: Image,
        graphic: Graphic,
    ) {
        val w_case = graphic.w_case
        val h_case = graphic.h_case
        val slope = Math.to_double(h_case - 1) / (w_case - 1)
        val origin = Math.to_double(h_case - 1)
        var a = -slope
        var b = origin
        val x_top_left = Math.round(((w_case - 2 * h_case + b) / (1 - a)))
        val y_top_left = Math.round(a * x_top_left + b)

        a = slope
        b = 0.0
        val x_top_right = Math.round((2 * h_case - b - 1) / (1 + a))
        val y_top_right = Math.round(a * x_top_right + b)

        a = slope
        b = 0.0
        val x_bottom_left = Math.round((w_case - h_case - 1 - b) / (1 + a))
        val y_bottom_left = Math.round(a * x_bottom_left + b)

        a = -slope
        b = origin
        val x_bottom_right = Math.round((h_case + b) / (1 - a))
        val y_bottom_right = Math.round(a * x_bottom_right + b)
        val x_left = Math.max(x_top_left, x_bottom_left)
        val x_right = w_case - 1 - Math.min(x_top_right, x_bottom_right)
        val y_top = Math.max(y_top_left, y_top_right)
        val y_bottom = h_case - 1 - Math.min(y_bottom_left, y_bottom_right) + 1
        val w = get_w(graphic)
        val h = get_h(graphic)
        val w_sprite = w - x_left - x_right
        val h_sprite = h - y_top - y_bottom
        sprite.scale(w_sprite, h_sprite)
    }
}

class RastersCanvas(
    n_w_cases_init: Int,
    n_h_cases_init: Int,
    x_init: Int,
    y_init: Int,
) : RastersLoop(n_w_cases_init, n_h_cases_init, 2) {
    val x: Int = x_init
    val y: Int = y_init

    override fun get_w(graphic: Graphic): Int {
        val w = super.get_w(graphic)

        return Math.half(w)
    }
}

class SpriteSheet(
    id: Int,
    n_images_init: Int,
) : Image() {
    val n_images: Int = n_images_init

    init {
        load(id)
    }
}

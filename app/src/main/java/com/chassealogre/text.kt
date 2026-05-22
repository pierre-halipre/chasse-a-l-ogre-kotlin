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

class Text {
    val numerals: Array<Sentence> = Array<Sentence>()
    val home: Array<Paragraph> = Array<Paragraph>()
    val pause: Array<Paragraph> = Array<Paragraph>()
    val end: Array<Paragraph> = Array<Paragraph>()
    val custom: Array<Int> = Array<Int>()

    init {
        var n_characters = 0

        while (n_characters < 8) {
            custom.add(-1)
            n_characters += 1
        }
    }

    fun fill(id: Int) {
        val file = File(id)
        numerals.add(Sentence(file, 13))
        home.add(Paragraph(file, 3))
        pause.add(Paragraph(file, 2))
        end.add(Paragraph(file, 2))
    }

    fun set_custom_numeral(
        i: Int,
        numeral: Int,
        design: Design,
    ) {
        val numerals: Sentence = numerals.get(design.mode)
        val unicode = numerals.get(numeral)
        custom.set(i, unicode)
    }

    fun set_custom_pause(
        milliseconds: Int,
        design: Design,
    ) {
        val milliseconds_final: Int

        if (milliseconds >= 3600000) {
            milliseconds_final = 3599999
        } else {
            milliseconds_final = milliseconds
        }

        val minutes = Math.floor(milliseconds_final / 60000)
        val minutes_ten = Math.floor(minutes / 10)
        val minutes_unit = minutes % 10
        val minutes_separator = 10
        val seconds = Math.floor((milliseconds_final % 60000) / 1000)
        val seconds_ten = Math.floor(seconds / 10)
        val seconds_unit = seconds % 10
        val seconds_separator = 11
        val centiseconds = Math.floor((milliseconds_final % 1000) / 10)
        val centiseconds_ten = Math.floor(centiseconds / 10)
        val centiseconds_unit = centiseconds % 10

        set_custom_numeral(0, minutes_ten, design)
        set_custom_numeral(1, minutes_unit, design)
        set_custom_numeral(2, minutes_separator, design)
        set_custom_numeral(3, seconds_ten, design)
        set_custom_numeral(4, seconds_unit, design)
        set_custom_numeral(5, seconds_separator, design)
        set_custom_numeral(6, centiseconds_ten, design)
        set_custom_numeral(7, centiseconds_unit, design)
    }

    fun set_custom_end(
        score: Int,
        design: Design,
    ) {
        val score_final: Int

        if (score >= 10000) {
            score_final = 9999
        } else {
            score_final = score
        }

        val thousand = Math.floor(score_final / 1000)
        val hundred = Math.floor((score_final % 1000) / 100)
        val ten = Math.floor((score_final % 100) / 10)
        val unit = score % 10
        val space = 12

        set_custom_numeral(0, space, design)
        set_custom_numeral(1, space, design)
        set_custom_numeral(2, thousand, design)
        set_custom_numeral(3, hundred, design)
        set_custom_numeral(4, ten, design)
        set_custom_numeral(5, unit, design)
        set_custom_numeral(6, space, design)
        set_custom_numeral(7, space, design)
    }
}

class Sentence(
    file: File,
    n_characters: Int,
) : Array<Int>() {
    init {
        var n = 0

        while (n < n_characters) {
            val character = file.read()
            add(character)
            n += 1
        }

        val end_of_file = file.read()

        if (end_of_file != -1) {
            file.read()
        }
    }
}

class Paragraph(
    file: File,
    n_sentences: Int,
) : Array<Sentence>() {
    init {
        var n = 0

        while (n < n_sentences) {
            add(Sentence(file, 8))
            n += 1
        }
    }
}

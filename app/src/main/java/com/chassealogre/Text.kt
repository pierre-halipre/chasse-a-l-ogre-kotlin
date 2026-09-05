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

class Text {
    class Sentence(
        file: File,
        nCharacters: Int,
    ) : Array<Int>() {
        init {
            var n = 0

            while (n < nCharacters) {
                val character = file.read()
                add(character)
                n += 1
            }

            val endOfFile = file.read()

            if (endOfFile != -1) {
                file.read()
            }
        }
    }

    class Paragraph(
        file: File,
        nSentences: Int,
    ) : Array<Sentence>() {
        init {
            var n = 0

            while (n < nSentences) {
                add(Sentence(file, 8))
                n += 1
            }
        }
    }

    val numerals: Array<Sentence> = Array<Sentence>()
    val home: Array<Paragraph> = Array<Paragraph>()
    val pause: Array<Paragraph> = Array<Paragraph>()
    val end: Array<Paragraph> = Array<Paragraph>()
    val custom: Array<Int> = Array<Int>()

    init {
        var nCharacters = 0

        while (nCharacters < 8) {
            custom.add(-1)
            nCharacters += 1
        }
    }

    fun fill(
        id: Int,
        context: Context,
    ) {
        val file = File(id, context)
        numerals.add(Sentence(file, 13))
        home.add(Paragraph(file, 3))
        pause.add(Paragraph(file, 2))
        end.add(Paragraph(file, 2))
    }

    fun setCustomNumeral(
        i: Int,
        numeral: Int,
        design: Design,
    ) {
        val numerals: Sentence = numerals.get(design.mode)
        val unicode = numerals.get(numeral)
        custom.set(i, unicode)
    }

    fun setCustomPause(
        milliseconds: Int,
        design: Design,
    ) {
        val minutes = Math.floor(milliseconds / 60000)
        val minutesTen = Math.floor(minutes / 10)
        val minutesUnit = minutes % 10
        val minutesSeparator = 10
        val seconds = Math.floor((milliseconds % 60000) / 1000)
        val secondsTen = Math.floor(seconds / 10)
        val secondsUnit = seconds % 10
        val secondsSeparator = 11
        val centiseconds = Math.floor((milliseconds % 1000) / 10)
        val centisecondsTen = Math.floor(centiseconds / 10)
        val centisecondsUnit = centiseconds % 10

        setCustomNumeral(0, minutesTen, design)
        setCustomNumeral(1, minutesUnit, design)
        setCustomNumeral(2, minutesSeparator, design)
        setCustomNumeral(3, secondsTen, design)
        setCustomNumeral(4, secondsUnit, design)
        setCustomNumeral(5, secondsSeparator, design)
        setCustomNumeral(6, centisecondsTen, design)
        setCustomNumeral(7, centisecondsUnit, design)
    }

    fun setCustomEnd(
        score: Int,
        design: Design,
    ) {
        val thousand = Math.floor(score / 1000)
        val hundred = Math.floor((score % 1000) / 100)
        val ten = Math.floor((score % 100) / 10)
        val unit = score % 10
        val space = 12

        setCustomNumeral(0, space, design)
        setCustomNumeral(1, space, design)
        setCustomNumeral(2, thousand, design)
        setCustomNumeral(3, hundred, design)
        setCustomNumeral(4, ten, design)
        setCustomNumeral(5, unit, design)
        setCustomNumeral(6, space, design)
        setCustomNumeral(7, space, design)
    }
}

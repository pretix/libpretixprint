package eu.pretix.libpretixprint.templating

import com.lowagie.text.Font
import com.lowagie.text.Utilities
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.BidiLine
import com.lowagie.text.pdf.PdfChunk
import com.lowagie.text.pdf.PdfWriter

fun linesRequiredHardBreak(content: String, font: Font, rectangularWidth: Float): Boolean {
    // assuming no indents
    val currentValues = arrayOfNulls<Any>(2)
    currentValues[1] = 0.0f

    val bidiProcessor = BidiProcessor(content, font.baseFont, font.size)

    while (true) {
        return when (bidiProcessor.processLine(rectangularWidth)) {
            BidiProcessor.LineResult.TEXT_END -> false
            BidiProcessor.LineResult.LINE_SPLIT_GRACEFULLY -> continue
            BidiProcessor.LineResult.LINE_SPLIT_FORCED -> true
        }
    }
}

class BidiProcessor(val text: String, val font: BaseFont, val fontSize: Float, val charSpacing: Float = 0f) {
    var currentChar = 0

    fun getCharWidth(c: Int): Float {
        if (PdfChunk.noPrint(c)) return 0f
        return font.getWidthPoint(c, fontSize) + charSpacing
    }

    fun trimRightEx(startIdx: Int, endIdx: Int): Int {
        var idx = endIdx
        var c: Char
        while (idx >= startIdx) {
            c = font.getUnicodeEquivalent(text[idx].code).toChar()
            if (!BidiLine.isWS(c) && !PdfChunk.noPrint(c.code)) break
            --idx
        }
        return idx
    }

    fun trimLeftEx(startIdx: Int, endIdx: Int): Int {
        var idx = startIdx
        var c: Char
        while (idx <= endIdx) {
            c = font.getUnicodeEquivalent(text[idx].code).toChar()
            if (!BidiLine.isWS(c) && !PdfChunk.noPrint(c.code)) break
            ++idx
        }
        return idx
    }

    fun isSplitCharacter(c: Char): Boolean {
        if (c <= ' ' || c == '-' || c == '\u2010') {
            return true
        }
        return if (c.code < 0x2002) false else c.code <= 0x200b ||
                c.code in 0x2e80..0xd79f ||
                c.code in 0xf900..0xfaff ||
                c.code in 0xfe30..0xfe4f ||
                c.code in 0xff61..0xff9f
    }

    enum class LineResult {
        LINE_SPLIT_GRACEFULLY,
        LINE_SPLIT_FORCED,
        TEXT_END
    }

    fun processLine(width: Float): LineResult {
        // This is a *highly simplified* implementation of BidiLine.processLine() that returns what type of split was
        // made.
        if (currentChar >= text.length) {
            return LineResult.TEXT_END
        }
        if (currentChar != 0) currentChar = trimLeftEx(currentChar, text.length - 1)
        var width = width
        var lastSplit = -1
        val oldCurrentChar: Int = currentChar
        var uniC: Int
        var charWidth: Float
        var anyValid = false
        var splitChar: Boolean
        var surrogate = false

        while (currentChar < text.length) {
            surrogate = Utilities.isSurrogatePair(text, currentChar)
            uniC = if (surrogate) font.getUnicodeEquivalent(Utilities.convertToUtf32(text, currentChar)) else font.getUnicodeEquivalent(text[currentChar].code)
            if (PdfChunk.noPrint(uniC)) {
                ++currentChar
                continue
            }
            charWidth = if (surrogate) getCharWidth(uniC) else getCharWidth(text[currentChar].code)
            splitChar = isSplitCharacter(text[currentChar])
            if (splitChar && Character.isWhitespace(uniC.toChar())) lastSplit = currentChar
            if (width - charWidth < 0) break
            if (splitChar) lastSplit = currentChar
            width -= charWidth
            anyValid = true
            if (surrogate) ++currentChar
            ++currentChar
        }
        if (!anyValid) {
            // not even a single char fit; must output the first char
            ++currentChar
            if (surrogate) ++currentChar
            return LineResult.LINE_SPLIT_GRACEFULLY
        }
        if (currentChar >= text.length) {
            // there was more line than text
            return LineResult.LINE_SPLIT_GRACEFULLY
        }
        var newCurrentChar: Int = trimRightEx(oldCurrentChar, currentChar - 1)
        if (newCurrentChar < oldCurrentChar) {
            // only WS
            return LineResult.LINE_SPLIT_GRACEFULLY
        }
        if (lastSplit == -1) {
            return LineResult.LINE_SPLIT_FORCED
        } else if (lastSplit >= newCurrentChar) {
            // no split point or split point ahead of end
            return LineResult.LINE_SPLIT_GRACEFULLY
        }
        // standard split
        currentChar = lastSplit + 1
        newCurrentChar = trimRightEx(oldCurrentChar, lastSplit);
        if (newCurrentChar < oldCurrentChar) {
            // only WS again
            newCurrentChar = currentChar - 1;
        }
        return LineResult.LINE_SPLIT_GRACEFULLY
    }
}